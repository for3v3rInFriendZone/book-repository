package com.repository.books.serviceimpl;

import com.google.gson.Gson;
import com.repository.books.exception.BookNotFoundException;
import com.repository.books.exception.FileWriterFailedException;
import com.repository.books.model.Book;
import com.repository.books.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  @Value("${books.file.path}")
  private String booksFilePath;

  private final Gson gson;

  @Override
  public List<Book> getAllBooks() {

    log.debug("Trying to get all books from *books.json* file...");

    try {
      InputStream input =  new FileInputStream(booksFilePath);
      Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);

      return Stream.of(gson.fromJson(reader, Book[].class)).collect(toList());
    } catch (IOException e) {
      log.error("Error while reading *books.json* file: {}", e.getMessage());

      return null;
    }
  }

  @Override
  public Book getBookById(String id) {

    log.debug("Getting book by an id: {}", id);

    return this.getAllBooks().stream()
        .filter(book -> book.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new BookNotFoundException(id));
  }

  @Override
  public Book saveBook(Book book) {

    log.debug("Trying to save the new book: {}", book);

    List<Book> books = getAllBooks();
    book.setId(UUID.randomUUID().toString());
    books.add(book);

    writeBookToFile(books);

    return book;
  }

  @Override
  public Book updateBook(String bookId, Book updatedBook) {

    log.debug("Trying to update the book: {}", updatedBook);

    checkIfBookExists(bookId);

    List<Book> updatedBooks =
        getAllBooks().stream()
            .map(book -> book.getId().equals(bookId) ? book = updatedBook : book)
            .collect(toList());

    writeBookToFile(updatedBooks);

    return updatedBook;
  }

  @Override
  public Boolean deleteBook(String bookId) {

    log.debug("Trying to delete the book with an id: {}", bookId);

    checkIfBookExists(bookId);

    List<Book> books = this.getAllBooks();
    Boolean deleteResponse = books.removeIf(book -> book.getId().equals(bookId));
    writeBookToFile(books);

    return deleteResponse;
  }

  private void writeBookToFile(List<Book> books) {

    log.info("Trying to write books to *books.json* file");

    try {
      Writer writer = new FileWriter(booksFilePath);
      gson.toJson(books, writer);

      writer.flush();
      writer.close();
    } catch (IOException e) {
      log.error(
          "There was an error while trying to write to a file *books.json* : {}", e.getMessage());

      throw new FileWriterFailedException("*books.json*", e);
    }
  }

  private void checkIfBookExists(String bookId) {

    if (this.getAllBooks().stream().noneMatch(book -> book.getId().equals(bookId))) {
      throw new BookNotFoundException(bookId);
    }
  }
}

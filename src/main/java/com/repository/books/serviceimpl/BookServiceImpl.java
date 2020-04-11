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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    @Value("${books.file.path}")
    private String booksFilePath;

    private final Gson gson;

    @Override
    public List<Book> getAll() {

        log.debug("Trying to get all books from *books.json* file...");

        try (Reader reader =
                     new InputStreamReader(new FileInputStream(booksFilePath), StandardCharsets.UTF_8)) {
            return Stream.of(gson.fromJson(reader, Book[].class)).collect(toList());
        } catch (IOException e) {
            log.error("Error while reading *books.json* file: {}", e.getMessage());

            return null;
        }
    }

    @Override
    public Book getById(String id) {

        log.debug("Getting book by an id: {}", id);

        return this.getAll().stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    public Book save(Book book) {

        log.debug("Trying to save the new book: {}", book);

        List<Book> books = getAll();
        book.setId(UUID.randomUUID().toString());
        book.setCreatedAt(getFormattedCreatedAt());
        books.add(book);

        writeBooksToFile(books);

        return book;
    }

    @Override
    public Book update(String id, Book changedBook) {

        log.debug("Trying to update the book: {}", changedBook);

        checkIfExists(id);
        changedBook.setId(id);

        List<Book> updatedBooks =
                getAll().stream()
                        .map(book -> book.getId().equals(id) ? book = changedBook : book)
                        .collect(toList());

        writeBooksToFile(updatedBooks);

        return changedBook;
    }

    @Override
    public Boolean remove(String id) {

        log.debug("Trying to delete the book with an id: {}", id);

        checkIfExists(id);

        List<Book> books = this.getAll();
        Boolean deleteResponse = books.removeIf(book -> book.getId().equals(id));
        writeBooksToFile(books);

        return deleteResponse;
    }

    private void writeBooksToFile(List<Book> books) {

        log.info("Trying to write books to *books.json* file");

        try {
            Writer writer =
                    new OutputStreamWriter(new FileOutputStream(booksFilePath), StandardCharsets.UTF_8);
            gson.toJson(books, writer);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error(
                    "There was an error while trying to write to a file *books.json* : {}", e.getMessage());

            throw new FileWriterFailedException("*books.json*", e);
        }
    }

    private void checkIfExists(String bookId) {

        if (this.getAll().stream().noneMatch(book -> book.getId().equals(bookId))) {
            throw new BookNotFoundException(bookId);
        }
    }

    private String getFormattedCreatedAt() {

        LocalDate date = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

        return date.format(formatters);
    }
}

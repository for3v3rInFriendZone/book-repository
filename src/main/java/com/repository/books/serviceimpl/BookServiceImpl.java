package com.repository.books.serviceimpl;

import com.google.gson.Gson;
import com.repository.books.exception.BookNotFoundException;
import com.repository.books.exception.FileWriterFailedException;
import com.repository.books.model.Book;
import com.repository.books.model.SortingDirection;
import com.repository.books.model.SortingType;
import com.repository.books.service.BookService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.Collator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

import static com.repository.books.model.SortingDirection.ASC;
import static com.repository.books.model.SortingType.CREATED_AT;
import static com.repository.books.model.SortingType.TITLE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  @Value("${books.file.path}")
  private String booksFilePath;

  @Value("${base.image.url}")
  private String baseImageUrl;

  private final Gson gson;

  @Override
  public List<Book> getAll(SortingType sortingType, SortingDirection sortingDirection) {
    log.debug("Trying to get all books from *books.json* file...");

    try (Reader reader = new InputStreamReader(new FileInputStream(booksFilePath), UTF_8)) {
      return sorted(
          Stream.of(gson.fromJson(reader, Book[].class)).collect(toList()),
          sortingType,
          sortingDirection);
    } catch (IOException e) {
      log.error("Error while reading *books.json* file: {}", e.getMessage());

      return null;
    }
  }

  @Override
  public List<Book> search(String searchTerm) {
    final String lowercaseTerm = searchTerm.toLowerCase(getSerbianLocale());

    return getAllBooksSorted().stream()
        .filter(
            book ->
                book.getTitle().toLowerCase(getSerbianLocale()).contains(lowercaseTerm)
                    || book.getAuthors().contains(lowercaseTerm)
                    || book.getInventoryNumber().toString().contains(lowercaseTerm))
        .collect(toList());
  }

  @Override
  public Book getById(String id) {
    log.debug("Getting book by an id: {}", id);

    return getAllBooksSorted().stream()
        .filter(book -> book.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new BookNotFoundException(id));
  }

  @Override
  public Book save(Book book) {
    log.debug("Trying to save the new book: {}", book);

    List<Book> books = getAllBooksSorted();
    book.setId(UUID.randomUUID().toString());
    book.setCreatedAt(getFormattedCreatedAt());
    book.setImage(getBookImage(book.getImage()));
    books.add(book);

    writeBooksToFile(books);

    return book;
  }

  @Override
  public Book update(String id, Book changedBook) {
    log.debug("Trying to update the book: {}", changedBook);

    checkIfExists(id);
    changedBook.setId(id);
    changedBook.setImage(getBookImage(changedBook.getImage()));

    List<Book> updatedBooks =
        getAllBooksSorted().stream()
            .map(book -> book.getId().equals(id) ? book = changedBook : book)
            .collect(toList());

    writeBooksToFile(updatedBooks);

    return changedBook;
  }

  @Override
  public Boolean remove(String id) {
    log.debug("Trying to delete the book with an id: {}", id);

    checkIfExists(id);

    List<Book> books = getAllBooksSorted();
    Boolean deleteResponse = books.removeIf(book -> book.getId().equals(id));
    writeBooksToFile(books);

    return deleteResponse;
  }

  private void writeBooksToFile(List<Book> books) {
    log.info("Trying to write books to *books.json* file");

    try {
      Writer writer = new OutputStreamWriter(new FileOutputStream(booksFilePath), UTF_8);
      gson.toJson(books, writer);

      writer.flush();
      writer.close();
    } catch (IOException e) {
      log.error(
          "There was an error while trying to write to a file *books.json* : {}", e.getMessage());

      throw new FileWriterFailedException("*books.json*", e);
    }
  }

  private List<Book> getAllBooksSorted() {
    return getAll(TITLE, ASC);
  }

  private void checkIfExists(String bookId) {
    if (getAllBooksSorted().stream().noneMatch(book -> book.getId().equals(bookId))) {
      throw new BookNotFoundException(bookId);
    }
  }

  private String getFormattedCreatedAt() {
    LocalDate date = LocalDate.now();
    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    return date.format(formatters);
  }

  private String getBookImage(String bookImage) {
    return StringUtils.isBlank(bookImage)
        ? null
        : this.baseImageUrl + parseBookIdFromLink(bookImage);
  }

  private String parseBookIdFromLink(String bookImageLink) {
    return bookImageLink.split("=")[1];
  }

  private List<Book> sorted(
      List<Book> books, SortingType sortingType, SortingDirection sortingDirection) {

    if (sortingType == CREATED_AT) {
      return books.stream().sorted(getComparatorForCreatedAt(sortingDirection)).collect(toList());
    }

    return books.stream().sorted(getComparatorForTitle(sortingDirection)).collect(toList());
  }

  private Comparator<Book> getComparatorForTitle(SortingDirection sortingDirection) {
    Locale locale = getSerbianLocale();

    Collator collator = Collator.getInstance(locale);

    return sortingDirection == ASC
        ? Comparator.comparing(Book::getTitle, collator)
        : Comparator.comparing(Book::getTitle, collator).reversed();
  }

  private Comparator<Book> getComparatorForCreatedAt(SortingDirection sortingDirection) {

    return sortingDirection == ASC
        ? Comparator.comparing(Book::getCreatedAt)
        : Comparator.comparing(Book::getCreatedAt).reversed();
  }

  private Locale getSerbianLocale() {
    return new Locale.Builder().setLanguage("sr").setScript("Cyrl").build();
  }
}

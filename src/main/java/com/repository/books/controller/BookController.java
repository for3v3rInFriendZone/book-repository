package com.repository.books.controller;

import com.repository.books.model.Book;
import com.repository.books.model.SortingDirection;
import com.repository.books.model.SortingType;
import com.repository.books.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
@Slf4j
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping
  public List<Book> getAllBooks(
      @RequestParam(required = false) SortingType sortingType,
      @RequestParam(required = false) SortingDirection sortingDirection) {
    log.info("*getAllBooks* API:");

    return this.bookService.getAll(sortingType, sortingDirection);
  }

  @GetMapping("/{id}")
  public Book getBookById(@PathVariable String id) {
    return this.bookService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book saveBook(@RequestBody Book book) {
    log.debug("*saveBook* API");

    return this.bookService.save(book);
  }

  @PutMapping("/{id}")
  public Book updateBook(@PathVariable String id, @RequestBody Book updatedBook) {

    return this.bookService.update(id, updatedBook);
  }

  @DeleteMapping("/{id}")
  public Boolean deleteBook(@PathVariable String id) {

    return this.bookService.remove(id);
  }
}

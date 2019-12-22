package com.repository.books.controller;

import com.repository.books.model.Book;
import com.repository.books.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/book")
@Slf4j
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping
  public List<Book> getAllBooks() {

    log.info("*getAllBooks* API:");

    return this.bookService.getAll();
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

package com.repository.books.exception;

public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(String bookId) {
    super(String.format("Book with an ID: %s Not Found", bookId));
  }
}

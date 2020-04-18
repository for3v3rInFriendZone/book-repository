package com.repository.books.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
  public CategoryAlreadyExistsException(String categoryName) {
    super(String.format("Област са називом: %s већ постоји.", categoryName));
  }
}

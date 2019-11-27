package com.repository.books.exception;

public class CategoryNotFoundException extends RuntimeException {

  public CategoryNotFoundException(String categoryId) {
    super(String.format("Category with an ID: %s Not Found", categoryId));
  }
}

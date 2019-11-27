package com.repository.books.exception;

public class FileWriterFailedException extends RuntimeException {

  public FileWriterFailedException(String file, Throwable cause) {
    super(String.format("FileWriter failed while saving: %s", file), cause);
  }
}

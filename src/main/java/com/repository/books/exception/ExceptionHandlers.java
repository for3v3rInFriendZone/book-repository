package com.repository.books.exception;

import com.repository.books.model.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlers {

  @ExceptionHandler(BookNotFoundException.class)
  public ResponseEntity<ErrorMessage> bookNotFoundExceptionHandler(BookNotFoundException exception) {

    log.warn("Book Not Found exception, thrown with message: {}", exception.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorMessage(exception.getMessage()));
  }

  @ExceptionHandler(BookWriterFailedException.class)
  public ResponseEntity<ErrorMessage> bookWriterExceptionHandler(BookWriterFailedException exception) {

    log.warn("Writing book to *books.json* file failed, thrown with message: {}", exception.getMessage());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorMessage(exception.getMessage()));
  }
}

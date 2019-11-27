package com.repository.books.service;

import com.repository.books.model.Book;

import java.io.IOException;
import java.util.List;

public interface BookService {

  List<Book> getAllBooks();

  Book getBookById(String id);

  Book saveBook(Book book);

  Book updateBook(String bookId, Book updatedBook);

  Boolean deleteBook(String bookId);
}

package com.repository.books.service;

import com.repository.books.model.Book;

import java.io.IOException;
import java.util.List;

public interface BookService {

  List<Book> getAll();

  Book getById(String id);

  Book save(Book book);

  Book update(String id, Book changedBook);

  Boolean remove(String id);
}

package com.repository.books.service;

import com.repository.books.model.Book;
import com.repository.books.model.SortingDirection;
import com.repository.books.model.SortingType;

import java.io.IOException;
import java.util.List;

public interface BookService {

  List<Book> getAll(SortingType sortingType, SortingDirection sortingDirection);

  List<Book> search(String searchTerm);

  Book getById(String id);

  Book save(Book book);

  Book update(String id, Book changedBook);

  Boolean remove(String id);
}

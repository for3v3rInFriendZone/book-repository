package com.repository.books.service;

import com.repository.books.model.Book;
import com.repository.books.model.Category;

import java.util.List;

public interface CategoryService {

  List<Category> getAll();

  Category getById(String id);

  Category save(Category category);

  Category update(String id, Category changedCategory);

  Boolean remove(String id);
}

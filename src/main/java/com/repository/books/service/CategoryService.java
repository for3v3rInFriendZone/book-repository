package com.repository.books.service;

import com.repository.books.model.Category;

import java.util.List;

public interface CategoryService {

  List<Category> getAllCategories();

  Category getCategoryById(String id);

  Category saveCategory(Category category);
}

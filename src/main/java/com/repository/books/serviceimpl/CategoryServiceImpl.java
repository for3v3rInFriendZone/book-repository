package com.repository.books.serviceimpl;

import com.google.gson.Gson;
import com.repository.books.exception.CategoryNotFoundException;
import com.repository.books.exception.FileWriterFailedException;
import com.repository.books.model.Book;
import com.repository.books.model.Category;
import com.repository.books.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

  @Value("${categories.file.path}")
  private String categoriesFilePath;

  private final Gson gson;

  @Override
  public List<Category> getAllCategories() {
    log.debug("Trying to get all categories from *categories.json* file...");

    try (Reader reader = new FileReader(categoriesFilePath)) {
      return Stream.of(gson.fromJson(reader, Category[].class)).collect(toList());

    } catch (IOException e) {
      log.error("Error while reading *categories.json* file: {}", e.getMessage());

      return null;
    }
  }

  @Override
  public Category getCategoryById(String id) {

    log.debug("Getting category by an id: {}", id);

    return this.getAllCategories().stream()
        .filter(category -> category.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new CategoryNotFoundException(id));
  }

  @Override
  public Category saveCategory(Category category) {
    log.debug("Trying to save the new category: {}", category);

    List<Category> categories = getAllCategories();
    category.setId(UUID.randomUUID().toString());
    categories.add(category);

    writeCategoryToFile(categories);

    return category;
  }

  private void writeCategoryToFile(List<Category> categories) {

    log.info("Trying to write books to *books.json* file");

    try {
      Writer writer = new FileWriter(categoriesFilePath);
      gson.toJson(categories, writer);

      writer.flush();
      writer.close();
    } catch (IOException e) {
      log.error("There was an error while trying to write to a file *books.json* : {}", e.getMessage());

      throw new FileWriterFailedException("*categories.json*", e);
    }
  }
}

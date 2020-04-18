package com.repository.books.serviceimpl;

import com.google.gson.Gson;
import com.repository.books.exception.CategoryAlreadyExistsException;
import com.repository.books.exception.CategoryNotFoundException;
import com.repository.books.exception.FileWriterFailedException;
import com.repository.books.model.Category;
import com.repository.books.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
  public List<Category> getAll() {
    log.debug("Trying to get all categories from *categories.json* file...");

    try (Reader reader =
        new InputStreamReader(new FileInputStream(categoriesFilePath), StandardCharsets.UTF_8)) {
      return Stream.of(gson.fromJson(reader, Category[].class)).collect(toList());

    } catch (IOException e) {
      log.error("Error while reading *categories.json* file: {}", e.getMessage());

      return null;
    }
  }

  @Override
  public Category getById(String id) {

    log.debug("Getting category by an id: {}", id);

    return this.getAll().stream()
        .filter(category -> category.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new CategoryNotFoundException(id));
  }

  @Override
  public Category save(Category category) {
    log.debug("Trying to save the new category: {}", category);

    List<Category> categories = getAll();
    categoryAlreadyExists(categories, category.getName());

    category.setId(UUID.randomUUID().toString());
    categories.add(category);

    writeCategoriesToFile(categories);

    return category;
  }

  @Override
  public Category update(String id, Category changedCategory) {

    log.debug("Trying to update the category: {}", changedCategory);

    checkIfExists(id);
    changedCategory.setId(id);

    List<Category> updatedCategories =
        getAll().stream()
            .map(category -> category.getId().equals(id) ? category = changedCategory : category)
            .collect(toList());

    writeCategoriesToFile(updatedCategories);

    return changedCategory;
  }

  @Override
  public Boolean remove(String id) {

    log.debug("Trying to delete the category with an id: {}", id);

    checkIfExists(id);

    List<Category> categories = this.getAll();
    Boolean deleteResponse = categories.removeIf(category -> category.getId().equals(id));
    writeCategoriesToFile(categories);

    return deleteResponse;
  }

  private void writeCategoriesToFile(List<Category> categories) {

    log.info("Trying to write categories to *categories.json* file");

    try {
      Writer writer =
          new OutputStreamWriter(new FileOutputStream(categoriesFilePath), StandardCharsets.UTF_8);
      gson.toJson(categories, writer);

      writer.flush();
      writer.close();
    } catch (IOException e) {
      log.error(
          "There was an error while trying to write to a file *categories.json* : {}",
          e.getMessage());

      throw new FileWriterFailedException("*categories.json*", e);
    }
  }

  private void checkIfExists(String categoryId) {

    if (this.getAll().stream().noneMatch(category -> category.getId().equals(categoryId))) {
      throw new CategoryNotFoundException(categoryId);
    }
  }

  private void categoryAlreadyExists(List<Category> categories, String name) {
    boolean exists =
        categories.stream()
            .map(Category::getName)
            .anyMatch(categoryName -> categoryName.equals(name));

    if (exists) {
      throw new CategoryAlreadyExistsException(name);
    }
  }
}

package com.repository.books.controller;

import com.repository.books.model.Category;
import com.repository.books.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public List<Category> getAllCategories() {

    log.info("*getAllBooks* API:");

    return this.categoryService.getAllCategories();
  }

  @GetMapping("/{id}")
  public Category getCategoryById(@PathVariable String id) {
    return this.categoryService.getCategoryById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Category saveCategory(@RequestBody Category category) {

    log.debug("*saveBook* API");

    return this.categoryService.saveCategory(category);
  }
}

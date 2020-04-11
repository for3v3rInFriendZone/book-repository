package com.repository.books.controller;

import com.repository.books.model.Category;
import com.repository.books.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategories() {

        log.info("*getAllCategories* API:");

        return this.categoryService.getAll();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable String id) {

        log.debug("*getCategoryById: {}* API", id);

        return this.categoryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category saveCategory(@RequestBody Category category) {

        log.debug("*saveCategory* API");

        return this.categoryService.save(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable String id, @RequestBody Category changedCategory) {

        return this.categoryService.update(id, changedCategory);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteCategory(@PathVariable String id) {

        return this.categoryService.remove(id);
    }
}

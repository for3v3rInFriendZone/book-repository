package com.repository.books.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Book implements Serializable {

  private static final long serialVersionUID = 5703523454260669753L;

  private String id;
  private String title;
  private List<String> authors;
  private String publisher;
  private Integer publishedYear;
  private String publication;
  private Integer numberOfPages;
  private String publicationLanguage;
  private String form;
  private String keepingPlace;
  private List<String> categories;
  private Long inventoryNumber;
  private String image;
  private String createdAt;
}

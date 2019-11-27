package com.repository.books.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Category implements Serializable {

  private static final long serialVersionUID = 648301428082717652L;

  private String id;
  private String name;
}

package com.repository.books.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class Category implements Serializable {

  private static final long serialVersionUID = 648301428082717652L;

  private String id;
  private String name;
}

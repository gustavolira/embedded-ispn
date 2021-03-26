package com.example;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

@Indexed
public class Book {

   @FullTextField
   private String title;

   public Book(String title) {
      this.title = title;
   }

   public Book() {
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   public String toString() {
      return "Book{" + title + '}';
   }
}
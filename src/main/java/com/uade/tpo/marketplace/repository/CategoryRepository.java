package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Category;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoryRepository {
    public ArrayList<Category> categories = new ArrayList<>(
        Arrays.asList(Category.builder().name("empanadas").id(1).build(),
               Category.builder().name("pizza").id(2).build(),
               Category.builder().name("chocotorta").id(3).build())
    );

    public ArrayList<Category> getCategories() {
        return this.categories;
    }

    public String getCategoryById( int categoryID) {
        return null;
    }

    public String createCategory( String entity) {
        return null;
    }
}

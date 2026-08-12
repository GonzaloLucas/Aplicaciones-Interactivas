package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.repository.CategoryRepository;

import java.util.ArrayList;

public class CategoryService {

    public ArrayList<Category> getCategories() {
        return new CategoryRepository().getCategories();
    }

    public String getCategoryById( int categoryID) {
        return "";
    }

    public String createCategory( String entity) {
        return entity;
    }
}

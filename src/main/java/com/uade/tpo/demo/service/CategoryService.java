
package com.uade.tpo.demo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;

public interface CategoryService { 
    public Page<Category> getCategories(PageRequest pageRequest);
    //paginacion 
    public Optional<Category> getCategoryById(Long categoryId);

    public Category createCategory(String name, String description) throws CategoryDuplicateException;
}
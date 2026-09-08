package com.uade.tpo.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired //inyeccion de categoryRepository
    private CategoryRepository categoryRepository;

    public Page<Category> getCategories(PageRequest pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public Category createCategory(String name, String description) throws CategoryDuplicateException {
        List<Category> categories = categoryRepository.findByNameAndDescription(name, description);
        if (categories.isEmpty())
            return categoryRepository.save(new Category(name, description));
        throw new CategoryDuplicateException();
    }

    @Transactional
    public Category updateCategory(Long categoryId, String name, String description) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException());

        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }
}

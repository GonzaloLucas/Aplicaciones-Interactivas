package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("categories") //localhost:8080/categories
public class CategoriesController {

    //localhost:8080/categories - GET
    @GetMapping
    public ArrayList<Category> getCategories() {
        return new CategoryService().getCategories();
    }

    //localhost:8080/categories/<id>
    @GetMapping("{categoryID}")
    public String getCategoryById(@PathVariable int categoryID) {
        return new CategoryService().getCategoryById(categoryID);
    }

    @PostMapping
    public String createCategory(@RequestBody String categoryID) {
        return new CategoryService().createCategory(categoryID);
    }
}

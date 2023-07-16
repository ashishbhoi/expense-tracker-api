package com.ashishbhoi.expensetrackerapi.services;

import com.ashishbhoi.expensetrackerapi.models.Category;
import com.ashishbhoi.expensetrackerapi.exceptions.EtBadRequestException;
import com.ashishbhoi.expensetrackerapi.exceptions.EtResourceNotFoundException;

import java.util.List;

public interface CategoryService {

    List<Category> fetchAllCategories(Integer userId) throws EtResourceNotFoundException;

    Category fetchCategoryById(Integer userId, Integer categoryId) throws EtResourceNotFoundException;

    Category addCategory(Integer userId, String title, String description) throws EtBadRequestException;

    void updateCategory(Integer userId, Integer categoryId, Category category) throws EtBadRequestException;

    void removeCategoryWithAllTransactions(Integer userId, Integer categoryId) throws EtResourceNotFoundException;
}

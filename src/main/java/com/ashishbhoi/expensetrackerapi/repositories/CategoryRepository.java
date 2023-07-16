package com.ashishbhoi.expensetrackerapi.repositories;

import com.ashishbhoi.expensetrackerapi.exceptions.EtBadRequestException;
import com.ashishbhoi.expensetrackerapi.exceptions.EtResourceNotFoundException;
import com.ashishbhoi.expensetrackerapi.models.Category;

import java.util.List;

public interface CategoryRepository {

    List<Category> findAll(Integer userId) throws EtResourceNotFoundException;

    Category findById(Integer userId, Integer categoryId) throws EtResourceNotFoundException;

    Integer create(Integer userId, String title, String description) throws EtBadRequestException;

    void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException;

    void removeById(Integer userId, Integer categoryId) throws EtResourceNotFoundException;
}

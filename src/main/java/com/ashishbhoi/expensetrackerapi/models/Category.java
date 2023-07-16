package com.ashishbhoi.expensetrackerapi.models;

public record Category(Integer categoryId, Integer userId, String title, String description, Double totalExpense) {
}

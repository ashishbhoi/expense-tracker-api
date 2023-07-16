package com.ashishbhoi.expensetrackerapi.models;

public record Transaction(Integer transactionId, Integer categoryId, Integer userId, Double amount, String note,
                          Long transactionDate) {
}

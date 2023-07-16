package com.ashishbhoi.expensetrackerapi.services;

import com.ashishbhoi.expensetrackerapi.exceptions.EtBadRequestException;
import com.ashishbhoi.expensetrackerapi.exceptions.EtResourceNotFoundException;
import com.ashishbhoi.expensetrackerapi.models.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> fetchAllTransactions(Integer userId, Integer categoryId) throws EtResourceNotFoundException;

    Transaction fetchTransactionById(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException;

    Transaction addTransaction(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate)
            throws EtBadRequestException;

    void updateTransaction(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction)
            throws EtBadRequestException;

    void removeTransaction(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException;

}

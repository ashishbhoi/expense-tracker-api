package com.ashishbhoi.expensetrackerapi.services;

import com.ashishbhoi.expensetrackerapi.exceptions.EtBadRequestException;
import com.ashishbhoi.expensetrackerapi.exceptions.EtResourceNotFoundException;
import com.ashishbhoi.expensetrackerapi.models.Transaction;
import com.ashishbhoi.expensetrackerapi.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> fetchAllTransactions(Integer userId, Integer categoryId)
            throws EtResourceNotFoundException {
        return transactionRepository.findAll(userId, categoryId);
    }

    @Override
    public Transaction fetchTransactionById(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException {
        return transactionRepository.findById(userId, categoryId, transactionId);
    }

    @Override
    public Transaction addTransaction(Integer userId, Integer categoryId, Double amount, String note,
                                      Long transactionDate) throws EtBadRequestException {
        int transactionId = transactionRepository.create(userId, categoryId, amount, note, transactionDate);
        return transactionRepository.findById(userId, categoryId, transactionId);
    }

    @Override
    public void updateTransaction(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction)
            throws EtBadRequestException {
        transactionRepository.update(userId, categoryId, transactionId, transaction);
    }

    @Override
    public void removeTransaction(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException {
        transactionRepository.removeById(userId, categoryId, transactionId);
    }
}

package com.ashishbhoi.expensetrackerapi.repositories;

import com.ashishbhoi.expensetrackerapi.domain.Transaction;
import com.ashishbhoi.expensetrackerapi.exceptions.EtBadRequestException;
import com.ashishbhoi.expensetrackerapi.exceptions.EtResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final String SQL_FIND_ALL = "SELECT TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, " +
            "TRANSACTION_DATE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ?";

    private static final String SQL_FIND_BY_ID = "SELECT TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, " +
            "TRANSACTION_DATE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?";

    private static final String SQL_CREATE = "INSERT INTO ET_TRANSACTIONS(TRANSACTION_ID, CATEGORY_ID, USER_ID, " +
            "AMOUNT, NOTE, TRANSACTION_DATE) VALUES(NEXTVAL('ET_TRANSACTIONS_SEQ'), ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE ET_TRANSACTIONS SET AMOUNT = ?, NOTE = ?, TRANSACTION_DATE = ? " +
            "WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?";

    private static final String SQL_DELETE = "DELETE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ? " +
            "AND TRANSACTION_ID = ?";

    private final RowMapper<Transaction> transactionRowMapper = ((rs, rowNum) -> new Transaction(
            rs.getInt("TRANSACTION_ID"),
            rs.getInt("CATEGORY_ID"),
            rs.getInt("USER_ID"),
            rs.getDouble("AMOUNT"),
            rs.getString("NOTE"),
            rs.getLong("TRANSACTION_DATE")
    ));


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Transaction> findAll(Integer userId, Integer categoryId) throws EtResourceNotFoundException {
        try {
            List<Transaction> transactions = jdbcTemplate.query(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_FIND_ALL);
                ps.setInt(1, userId);
                ps.setInt(2, categoryId);
                return ps;
            }, transactionRowMapper);
            if (transactions.isEmpty()) {
                throw new EtResourceNotFoundException("No transactions found");
            }
            return transactions;
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public Transaction findById(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException {
        try {
            List<Transaction> transactions = jdbcTemplate.query(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_ID);
                ps.setInt(1, userId);
                ps.setInt(2, categoryId);
                ps.setInt(3, transactionId);
                return ps;
            }, transactionRowMapper);
            if (transactions.isEmpty()) {
                throw new EtResourceNotFoundException("Transaction not found");
            }
            return transactions.get(0);
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public Integer create(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate)
            throws EtBadRequestException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, categoryId);
                ps.setInt(2, userId);
                ps.setDouble(3, amount);
                ps.setString(4, note);
                ps.setLong(5, transactionDate);
                return ps;
            }, keyHolder);
            return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("TRANSACTION_ID");
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void update(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction)
            throws EtBadRequestException {
        try {
            int count = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_UPDATE);
                ps.setDouble(1, transaction.getAmount());
                ps.setString(2, transaction.getNote());
                ps.setLong(3, transaction.getTransactionDate());
                ps.setInt(4, userId);
                ps.setInt(5, categoryId);
                ps.setInt(6, transactionId);
                return ps;
            });
            if (count == 0) {
                throw new EtResourceNotFoundException("Transaction not found");
            }
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void removeById(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException {
        try {
            int count = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_DELETE);
                ps.setInt(1, userId);
                ps.setInt(2, categoryId);
                ps.setInt(3, transactionId);
                return ps;
            });
            if (count == 0) {
                throw new EtResourceNotFoundException("Transaction not found");
            }
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }
}

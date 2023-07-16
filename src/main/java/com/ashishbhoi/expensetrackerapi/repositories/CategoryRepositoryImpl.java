package com.ashishbhoi.expensetrackerapi.repositories;

import com.ashishbhoi.expensetrackerapi.exceptions.EtBadRequestException;
import com.ashishbhoi.expensetrackerapi.exceptions.EtResourceNotFoundException;
import com.ashishbhoi.expensetrackerapi.models.Category;
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
public class CategoryRepositoryImpl implements CategoryRepository {


    private static final String SQL_FIND_ALL = "SELECT C.CATEGORY_ID, C.USER_ID, C.TITLE, C.DESCRIPTION, " +
            "COALESCE(SUM(T.AMOUNT), 0) TOTAL_EXPENSE " +
            "FROM ET_TRANSACTIONS T RIGHT OUTER JOIN ET_CATEGORIES C " +
            "ON C.CATEGORY_ID = T.CATEGORY_ID " +
            "WHERE C.USER_ID = ? GROUP BY C.CATEGORY_ID";

    private static final String SQL_FIND_BY_ID = "SELECT C.CATEGORY_ID, C.USER_ID, C.TITLE, C.DESCRIPTION, " +
            "COALESCE(SUM(T.AMOUNT), 0) TOTAL_EXPENSE " +
            "FROM ET_TRANSACTIONS T RIGHT OUTER JOIN ET_CATEGORIES C " +
            "ON C.CATEGORY_ID = T.CATEGORY_ID " +
            "WHERE C.USER_ID = ? AND C.CATEGORY_ID = ? " +
            "GROUP BY C.CATEGORY_ID";

    private static final String SQL_CREATE = "INSERT INTO ET_CATEGORIES(CATEGORY_ID, USER_ID, TITLE, DESCRIPTION) " +
            "VALUES(NEXTVAL('ET_CATEGORIES_SEQ'), ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE ET_CATEGORIES SET TITLE = ?, DESCRIPTION = ? " +
            "WHERE USER_ID = ? AND CATEGORY_ID = ?";

    private static final String SQL_DELETE_CATEGORY = "DELETE FROM ET_CATEGORIES WHERE USER_ID = ? AND CATEGORY_ID = ?";

    private static final String SQL_DELETE_ALL_TRANSACTIONS = "DELETE FROM ET_TRANSACTIONS WHERE CATEGORY_ID = ?";

    private final RowMapper<Category> categoryRowMapper = ((rs, rowNum) -> new Category(
            rs.getInt("CATEGORY_ID"),
            rs.getInt("USER_ID"),
            rs.getString("TITLE"),
            rs.getString("DESCRIPTION"),
            rs.getDouble("TOTAL_EXPENSE")
    ));

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Category> findAll(Integer userId) throws EtResourceNotFoundException {
        try {
            List<Category> categories = jdbcTemplate.query(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_FIND_ALL);
                ps.setInt(1, userId);
                return ps;
            }, categoryRowMapper);
            if (categories.isEmpty()) {
                throw new EtResourceNotFoundException("Category not found");
            } else {
                return categories;
            }
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public Category findById(Integer userId, Integer categoryId) throws EtResourceNotFoundException {
        try {
            List<Category> categories = jdbcTemplate.query(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_ID);
                ps.setInt(1, userId);
                ps.setInt(2, categoryId);
                return ps;
            }, categoryRowMapper);
            if (categories.isEmpty()) {
                throw new EtResourceNotFoundException("Category not found");
            } else {
                return categories.get(0);
            }
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public Integer create(Integer userId, String title, String description) throws EtBadRequestException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, description);
                return ps;
            }, keyHolder);
            return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("CATEGORY_ID");
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException {
        try {
            int count = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_UPDATE);
                ps.setString(1, category.getTitle());
                ps.setString(2, category.getDescription());
                ps.setInt(3, userId);
                ps.setInt(4, categoryId);
                return ps;
            });
            if (count == 0) {
                throw new EtResourceNotFoundException("Category not found");
            }
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void removeById(Integer userId, Integer categoryId) throws EtResourceNotFoundException {
        try {
            deleteAllTransactions(categoryId);
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_DELETE_CATEGORY);
                ps.setInt(1, userId);
                ps.setInt(2, categoryId);
                return ps;
            });
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    private void deleteAllTransactions(Integer categoryId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_DELETE_ALL_TRANSACTIONS);
            ps.setInt(1, categoryId);
            return ps;
        });
    }
}

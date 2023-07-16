package com.ashishbhoi.expensetrackerapi.repositories;

import com.ashishbhoi.expensetrackerapi.exceptions.EtAuthException;
import com.ashishbhoi.expensetrackerapi.models.User;
import org.mindrot.jbcrypt.BCrypt;
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
public class UserRepositoryImpl implements UserRepository {

    private static final String SQL_CREATE = "INSERT INTO ET_USERS(USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) " +
            "VALUES(NEXTVAL('ET_USERS_SEQ'), ?, ?, ?, ?)";
    private static final String SQL_FIND_BY_EMAIL_AND_PASSWORD = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL," +
            " PASSWORD FROM ET_USERS WHERE EMAIL = ?";
    private static final String SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM ET_USERS WHERE EMAIL = ?";
    private static final String SQL_FIND_BY_ID = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL," +
            " PASSWORD FROM ET_USERS WHERE USER_ID = ?";
    private final RowMapper<User> userRowMapper = ((rs, rowNum) -> new User(
            rs.getInt("USER_ID"),
            rs.getString("FIRST_NAME"),
            rs.getString("LAST_NAME"),
            rs.getString("EMAIL"),
            rs.getString("PASSWORD")
    ));
    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Integer create(String firstName, String lastName, String email, String password) throws EtAuthException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);
                ps.setString(4, hashedPassword);
                return ps;
            }, keyHolder);
            return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("USER_ID");
        } catch (Exception e) {
            throw new EtAuthException("Invalid details. Failed to create account");
        }
    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws EtAuthException {
        List<User> users = jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_EMAIL_AND_PASSWORD);
            ps.setString(1, email);
            return ps;
        }, userRowMapper);
        if (users.size() == 0) {
            throw new EtAuthException("Invalid email/password");
        }
        User user = users.get(0);
        if (!BCrypt.checkpw(password, user.password())) {
            throw new EtAuthException("Invalid email/password");
        }
        return user;
    }

    @Override
    public Integer getCountByEmail(String email) {
        List<Integer> count = jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_COUNT_BY_EMAIL);
            ps.setString(1, email);
            return ps;
        }, (rs, rowNum) -> rs.getInt(1));
        return count.get(0);
    }

    @Override
    public User findById(Integer userId) {
        List<User> users = jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_ID);
            ps.setInt(1, userId);
            return ps;
        }, userRowMapper);
        return users.get(0);
    }
}

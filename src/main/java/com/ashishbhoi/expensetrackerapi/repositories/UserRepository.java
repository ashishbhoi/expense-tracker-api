package com.ashishbhoi.expensetrackerapi.repositories;

import com.ashishbhoi.expensetrackerapi.exceptions.EtAuthException;
import com.ashishbhoi.expensetrackerapi.models.User;

public interface UserRepository {

    Integer create(String firstName, String lastName, String email, String password) throws EtAuthException;

    User findByEmailAndPassword(String email, String password) throws EtAuthException;

    Integer getCountByEmail(String email);

    User findById(Integer userId);
}

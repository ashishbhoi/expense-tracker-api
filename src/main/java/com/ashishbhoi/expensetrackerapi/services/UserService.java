package com.ashishbhoi.expensetrackerapi.services;

import com.ashishbhoi.expensetrackerapi.domain.User;
import com.ashishbhoi.expensetrackerapi.exceptions.EtAuthException;

public interface UserService {

    User validateUser(String email, String password) throws EtAuthException;

    User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException;


}

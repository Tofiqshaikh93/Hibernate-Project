package com.atm.service;

import com.atm.dao.UserDao;
import com.atm.entity.User;
import com.atm.exception.ATMException;

import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser(User user) {
        userDao.save(user);
    }

    public User getUserById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new ATMException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new ATMException("User not found"));
    }

       
}


package com.atm.dao;

import com.atm.entity.User;


import java.util.Optional;

public interface UserDao extends GenericDao<User> {
    Optional<User> findByUsername(String username);
}


package com.atm.dao;

import com.atm.entity.Account;

import java.util.Optional;

public interface AccountDao extends GenericDao<Account> {
    Optional<Account> findByAccountNumber(String accountNumber);
}


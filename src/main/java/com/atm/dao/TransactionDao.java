package com.atm.dao;

import com.atm.entity.Account;
import com.atm.entity.Transaction;

import java.util.List;

public interface TransactionDao extends GenericDao<Transaction> {
    List<Transaction> findByAccount(Account account);
    List<Transaction> findRecentByAccount(Account account, int limit);
}


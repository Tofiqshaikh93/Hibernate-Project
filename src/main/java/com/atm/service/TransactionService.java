package com.atm.service;

import com.atm.dao.TransactionDao;
import com.atm.entity.Transaction;

public class TransactionService {
    private final TransactionDao transactionDao;

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public void createTransaction(Transaction transaction) {
        transactionDao.save(transaction);
    }

        
}


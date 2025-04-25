package com.atm.service;

import com.atm.dao.AccountDao;

import com.atm.entity.Account;
import com.atm.exception.ATMException;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountService {
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void createAccount(Account account) {
        accountDao.save(account);
    }

    public Account getAccountById(Long id) {
        return accountDao.findById(id)
                .orElseThrow(() -> new ATMException("Account not found"));
    }

    public void deposit(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountDao.update(account);
    }

    public void withdraw(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) >= 0) {
            account.setBalance(account.getBalance().subtract(amount));
            accountDao.update(account);
        } else {
            throw new ATMException("Insufficient funds");
        }
    }

   
}


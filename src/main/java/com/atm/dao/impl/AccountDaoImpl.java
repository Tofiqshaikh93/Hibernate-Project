package com.atm.dao.impl;

import com.atm.dao.AccountDao;
import com.atm.entity.Account;

import util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Optional;

public class AccountDaoImpl extends GenericDaoImpl<Account> implements AccountDao {
    public AccountDaoImpl() {
        super(Account.class);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Account> query = session.createQuery("FROM Account WHERE accountNumber = :accountNumber", Account.class);
            query.setParameter("accountNumber", accountNumber);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}


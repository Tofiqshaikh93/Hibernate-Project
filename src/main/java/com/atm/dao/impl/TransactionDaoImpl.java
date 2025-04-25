package com.atm.dao.impl;

import com.atm.dao.TransactionDao;
import com.atm.entity.Account;
import com.atm.entity.Transaction;

import util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class TransactionDaoImpl extends GenericDaoImpl<Transaction> implements TransactionDao {
    public TransactionDaoImpl() {
        super(Transaction.class);
    }

    @Override
    public List<Transaction> findByAccount(Account account) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Transaction> query = session.createQuery("FROM Transaction WHERE account = :account ORDER BY timestamp DESC", Transaction.class);
            query.setParameter("account", account);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<Transaction> findRecentByAccount(Account account, int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Transaction> query = session.createQuery("FROM Transaction WHERE account = :account ORDER BY timestamp DESC", Transaction.class);
            query.setParameter("account", account);
            query.setMaxResults(limit);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}


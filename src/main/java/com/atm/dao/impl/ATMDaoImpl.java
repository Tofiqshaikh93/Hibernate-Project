package com.atm.dao.impl;

import com.atm.dao.ATMDao;

import com.atm.entity.ATM;

import util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Optional;

public class ATMDaoImpl extends GenericDaoImpl<ATM> implements ATMDao {
    public ATMDaoImpl() {
        super(ATM.class);
    }

    @Override
    public Optional<ATM> findByAtmId(String atmId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ATM> query = session.createQuery("FROM ATM WHERE atmId = :atmId", ATM.class);
            query.setParameter("atmId", atmId);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<ATM> findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ATM> query = session.createQuery("FROM ATM WHERE name = :name", ATM.class);
            query.setParameter("name", name);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}


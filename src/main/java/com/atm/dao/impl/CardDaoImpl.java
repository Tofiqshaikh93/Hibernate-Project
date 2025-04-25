package com.atm.dao.impl;

import com.atm.dao.CardDao;

import com.atm.entity.Card;

import util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Optional;

public class CardDaoImpl extends GenericDaoImpl<Card> implements CardDao {
    public CardDaoImpl() {
        super(Card.class);
    }

    @Override
    public Optional<Card> findByCardNumberWithAccount(String cardNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Card> query = session.createQuery(
                "SELECT c FROM Card c JOIN FETCH c.account WHERE c.cardNumber = :cardNumber",
                Card.class
            );
            query.setParameter("cardNumber", cardNumber);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Card> findByCardNumber(String cardNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Card> query = session.createQuery("FROM Card WHERE cardNumber = :cardNumber", Card.class);
            query.setParameter("cardNumber", cardNumber);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}


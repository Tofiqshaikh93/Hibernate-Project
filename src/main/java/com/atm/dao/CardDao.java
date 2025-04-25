package com.atm.dao;

import com.atm.entity.Card;

import java.util.Optional;

public interface CardDao extends GenericDao<Card> {
    Optional<Card> findByCardNumberWithAccount(String cardNumber);
    Optional<Card> findByCardNumber(String cardNumber);
}


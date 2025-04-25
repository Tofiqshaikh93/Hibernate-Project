package com.atm.service;

import com.atm.dao.CardDao;
import com.atm.entity.Card;

public class CardService {
    private final CardDao cardDao;

    public CardService(CardDao cardDao) {
        this.cardDao = cardDao;
    }

    public void createCard(Card card) {
        cardDao.save(card);
    }

    public Card getCardByNumber(String cardNumber) {
      
        return null;
    }

           
}


package com.atm.dao;

import com.atm.entity.ATM;

import java.util.Optional;

public interface ATMDao extends GenericDao<ATM> {
    Optional<ATM> findByAtmId(String atmId);
    Optional<ATM> findByName(String name);
}


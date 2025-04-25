package com.atm.service;

import com.atm.dao.*;

import com.atm.dao.impl.*;
import com.atm.entity.*;
import com.atm.exception.ATMException;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ATMService {
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final ATMDao atmDao;

    public ATMService() {
        this.userDao = new UserDaoImpl();
        this.accountDao = new AccountDaoImpl();
        this.transactionDao = new TransactionDaoImpl();
        this.atmDao = new ATMDaoImpl();
    }

    public void initializeSystem() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {
                        
                Optional<User> adminUser = userDao.findByUsername("Tofiq");
                if (!adminUser.isPresent()) {
                    User newAdminUser = new User();
                    newAdminUser.setName("Tofiq");
                    newAdminUser.setUsername("Tofiq");
                    newAdminUser.setPassword("TOFIQ@123");
                    newAdminUser.setAdmin(true);
                    session.save(newAdminUser);
                }

                   
                Optional<ATM> existingATM = atmDao.findByAtmId("BAR123");
                if (!existingATM.isPresent()) {
                    ATM atm = new ATM();
                    atm.setAtmId("BAR123");
                    atm.setName("BARISTA");
                    atm.setLocation("Main Branch");
                    atm.setCashAvailable(new BigDecimal("80000.00"));
                    session.save(atm);
                }

                       
                createCustomerIfNotExists(session, "Tabrez", "1234567890", "1234", new BigDecimal("20000.00"));
                createCustomerIfNotExists(session, "Zayn", "0987654321", "1234", new BigDecimal("20000.00"));

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw new ATMException("Error initializing system: " + e.getMessage());
            }
        }
    }

    private void createCustomerIfNotExists(Session session, String name, String accountNumber, String pin, BigDecimal initialBalance) {
        Optional<Account> existingAccount = accountDao.findByAccountNumber(accountNumber);
        if (!existingAccount.isPresent()) {
            User user = new User();
            user.setName(name);
            user.setUsername(accountNumber);
            user.setPassword(pin);
            user.setAdmin(false);
            session.save(user);

            Account account = new Account();
            account.setAccountNumber(accountNumber);
            account.setBalance(initialBalance);
            account.setPin(pin);
            account.setUser(user);
            session.save(account);
        }
    }

    public User authenticateUser(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Optional<User> userOptional = userDao.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getPassword().equals(password)) {
                    return user;
                }
            }
            return null;
        }
    }

    public Account findAccountByAccountNumber(String accountNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {
                Query<Account> query = session.createQuery(
                    "SELECT a FROM Account a JOIN FETCH a.user WHERE a.accountNumber = :accountNumber",
                    Account.class
                );
                query.setParameter("accountNumber", accountNumber);
                Account account = query.uniqueResult();
                session.getTransaction().commit();
                if (account == null) {
                    throw new ATMException("Account not found");
                }
                return account;
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw new ATMException("Error finding account: " + e.getMessage());
            }
        }
    }

    public void addCustomer(String name, String accountNumber, String pin, BigDecimal initialBalance) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {
                if (accountDao.findByAccountNumber(accountNumber).isPresent()) {
                    throw new ATMException("Account number already exists");
                }

                User user = new User();
                user.setName(name);
                user.setUsername(accountNumber);
                user.setPassword(pin);
                user.setAdmin(false);
                session.save(user);

                Account account = new Account();
                account.setAccountNumber(accountNumber);
                account.setBalance(initialBalance);
                account.setPin(pin);
                account.setUser(user);
                session.save(account);

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw new ATMException("Error adding customer: " + e.getMessage());
            }
        }
    }

    public String generateATMPin() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    public void updateAccount(Account account) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {
                session.update(account);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw new ATMException("Error updating account: " + e.getMessage());
            }
        }
    }

    public void deleteAccount(Account account) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {
                session.delete(account);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw new ATMException("Error deleting account: " + e.getMessage());
            }
        }
    }

    public List<com.atm.entity.Transaction> getAllTransactions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return transactionDao.findAll();
        }
    }

    public List<com.atm.entity.Transaction> getTransactionsByAccount(Account account) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return transactionDao.findByAccount(account);
        }
    }

    public ATM getATMByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return atmDao.findByName(name).orElseThrow(() -> new ATMException("ATM not found"));
        }
    }

    public void addCashToATM(ATM atm, BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            try {
                atm.setCashAvailable(atm.getCashAvailable().add(amount));
                session.update(atm);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw new ATMException("Error adding cash to ATM: " + e.getMessage());
            }
        }
    }

    public Account getAccountById(Long accountId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Query<Account> query = session.createQuery(
                    "SELECT a FROM Account a JOIN FETCH a.user WHERE a.id = :id",
                    Account.class
                );
                query.setParameter("id", accountId);
                Account account = query.uniqueResult();
                if (account == null) {
                    throw new ATMException("Account not found");
                }
                tx.commit();
                return account;
            } catch (Exception e) {
                tx.rollback();
                throw new ATMException("Error finding account: " + e.getMessage());
            }
        }
    }

    public Account withdraw(Long accountId, BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Query<Account> query = session.createQuery(
                    "SELECT a FROM Account a JOIN FETCH a.user WHERE a.id = :id",
                    Account.class
                );
                query.setParameter("id", accountId);
                Account account = query.uniqueResult();
                if (account == null) {
                    throw new ATMException("Account not found");
                }

                ATM atm = atmDao.findByName("BARISTA").orElseThrow(() -> new ATMException("ATM not found"));

                if (account.getBalance().compareTo(amount) < 0) {
                    throw new ATMException("Insufficient funds");
                }

                if (atm.getCashAvailable().compareTo(amount) < 0) {
                    throw new ATMException("Insufficient cash in ATM");
                }

                account.setBalance(account.getBalance().subtract(amount));
                atm.setCashAvailable(atm.getCashAvailable().subtract(amount));

                com.atm.entity.Transaction transactionEntity = new com.atm.entity.Transaction();
                transactionEntity.setAccount(account);
                transactionEntity.setAtm(atm);
                transactionEntity.setAmount(amount);
                transactionEntity.setType(com.atm.entity.Transaction.TransactionType.WITHDRAWAL);
                transactionEntity.setTimestamp(LocalDateTime.now());

                session.update(account);
                session.update(atm);
                session.save(transactionEntity);

                tx.commit();
                return account;
            } catch (Exception e) {
                tx.rollback();
                throw new ATMException("Error during withdrawal: " + e.getMessage());
            }
        }
    }

    public Account deposit(Long accountId, BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Query<Account> query = session.createQuery(
                    "SELECT a FROM Account a JOIN FETCH a.user WHERE a.id = :id",
                    Account.class
                );
                query.setParameter("id", accountId);
                Account account = query.uniqueResult();
                if (account == null) {
                    throw new ATMException("Account not found");
                }

                ATM atm = atmDao.findByName("BARISTA").orElseThrow(() -> new ATMException("ATM not found"));

                account.setBalance(account.getBalance().add(amount));
                atm.setCashAvailable(atm.getCashAvailable().add(amount));

                com.atm.entity.Transaction transactionEntity = new com.atm.entity.Transaction();
                transactionEntity.setAccount(account);
                transactionEntity.setAtm(atm);
                transactionEntity.setAmount(amount);
                transactionEntity.setType(com.atm.entity.Transaction.TransactionType.DEPOSIT);
                transactionEntity.setTimestamp(LocalDateTime.now());

                session.update(account);
                session.update(atm);
                session.save(transactionEntity);

                tx.commit();
                return account;
            } catch (Exception e) {
                tx.rollback();
                throw new ATMException("Error during deposit: " + e.getMessage());
            }
        }
    }

    public List<com.atm.entity.Transaction> getRecentTransactions(Account account, int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return transactionDao.findRecentByAccount(account, limit);
        }
    }
}


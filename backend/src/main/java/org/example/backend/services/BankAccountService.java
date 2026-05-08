package org.example.backend.services;


import org.example.backend.entities.BankAccount;
import org.example.backend.entities.Customer;

import java.util.List;

public interface BankAccountService {
    Customer saveCustomer(Customer customer);
    BankAccount saveBankAccount(double initialBalance,String type,Long customerId);
    List<Customer> listCustomers();
    BankAccount getBankAccountById(Long customerId);
    void debit(String accountId , double amount ,String description);
    void credit(String accountId , double amount ,String description);
    void transfer(String accountIdSource , String accountIdDestination , double amount, String description);

}

package org.example.backend.services;


import org.example.backend.dtos.*;
import org.example.backend.entities.BankAccount;
import org.example.backend.entities.CurrentAccount;
import org.example.backend.entities.SavingAccount;

import java.util.List;

public interface BankAccountService {
    CustomerDto saveCustomer(CustomerDto customer);
    CurrentBankAccountDto saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId);
    SavingBankAccountDto saveSavingBankAccount(double initialBalance, double interestRate, Long customerId);
    List<CustomerDto> listCustomers();
    BankAccountDto getBankAccountById(String accountId);

    List<BankAccountDto> getAllBankAccounts();

    void debit(String accountId , double amount , String description);
    void credit(String accountId , double amount ,String description);
    void transfer(String accountIdSource , String accountIdDestination , double amount, String description);

    CustomerDto getCustomerById(Long id);

    CustomerDto updateCustomer(CustomerDto customerDto);

    void deleteCustomer(long customerId);

    List<AccountOperationDto> getHistory(String accountId);

    AccountHistoryDto getAccountHistory(String accountId, int page, int size);

}

package org.example.backend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dtos.*;
import org.example.backend.entities.*;
import org.example.backend.entities.enums.OperationType;
import org.example.backend.mappers.BankAccountMapperImpl;
import org.example.backend.repository.AccountOperationRepository;
import org.example.backend.repository.BankAccountRepository;
import org.example.backend.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDto saveCustomer(CustomerDto customerDto) {
        log.info("Saving customer {}", customerDto.getName());
        Customer customer = dtoMapper.fromCustomerDto(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDto saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        CurrentAccount currentaccount = new CurrentAccount();
        currentaccount.setId(UUID.randomUUID().toString());
        currentaccount.setCreatedAt(new Date());
        currentaccount.setBalance(initialBalance);
        currentaccount.setCustomer(customer);
        currentaccount.setOverDraft(overDraft);
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentaccount);
        return dtoMapper.fromCurrentBankAccount(savedCurrentAccount);
    }

    @Override
    public SavingBankAccountDto saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedSavingAccount);
    }

    @Override
    public List<CustomerDto> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).toList();
    }

    @Override
    public BankAccountDto getBankAccountById(String accountId) {
      BankAccount bankAccount=bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Bank account not found"));
      if (bankAccount instanceof SavingAccount) {
          return dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);
      } else if (bankAccount instanceof CurrentAccount) {
          return dtoMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
      } else {
          throw new RuntimeException("Unknown bank account type");
      }

    }

    @Override
    public List<BankAccountDto> getAllBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        return bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                return dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);
            } else if (bankAccount instanceof CurrentAccount) {
                return dtoMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
            } else {
                throw new RuntimeException("Unknown bank account type");
            }
        }).toList();
    }

    @Override
    public void debit(String accountId, double amount, String description) {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
            if(bankAccount.getBalance() < amount){
                throw new RuntimeException("Insufficient balance");
            }
            else {
                AccountOperation accountOperation = new AccountOperation();
                accountOperation.setType(OperationType.DEBIT);
                accountOperation.setAmount(amount);
                accountOperation.setDate(new Date());
                accountOperation.setBankAccount(bankAccount);
                accountOperationRepository.save(accountOperation);
                bankAccount.setBalance(bankAccount.getBalance() - amount);
                bankAccountRepository.save(bankAccount);
            }

    }

    @Override
    public void credit(String accountId, double amount, String description) {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
            AccountOperation accountOperation = new AccountOperation();
            accountOperation.setType(OperationType.CREDIT);
            accountOperation.setAmount(amount);
            accountOperation.setDate(new Date());
            accountOperation.setBankAccount(bankAccount);
            accountOperationRepository.save(accountOperation);
            bankAccount.setBalance(bankAccount.getBalance() + amount);
            bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount, String description) {
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource );
    }

    @Override
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        Customer customer = dtoMapper.fromCustomerDto(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(long customerId){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepository.delete(customer);
    }

    @Override
    public List<AccountOperationDto> getHistory(String accountId) {
        bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Bank account not found"));
        List<AccountOperation> operations = accountOperationRepository.findByBankAccountId(accountId);
        if (operations == null) {
            throw new RuntimeException("this account does not have any operation yet ");
        }else {
            return operations.stream().map(operation -> dtoMapper.fromAccountOperation(operation)).toList();
        }

    }

    @Override
    public AccountHistoryDto getAccountHistory(String accountId, int page, int size) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Bank account not found"));

        Page<AccountOperation> accountOperations=accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page,size));
        AccountHistoryDto accountHistoryDto=new AccountHistoryDto();
        List<AccountOperationDto>  accountOperationDto = accountOperations.getContent().stream().map(operation -> dtoMapper.fromAccountOperation(operation)).toList();
        accountHistoryDto.setAccountOperationDto(accountOperationDto);
        accountHistoryDto.setAccountId(accountId);
        //
        accountHistoryDto.setBalance(bankAccount.getBalance());
        accountHistoryDto.setAccountId(bankAccount.getId());
        accountHistoryDto.setCurrentPage(page);
        accountHistoryDto.setPageSize(size);
        accountHistoryDto.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDto;
    }

}

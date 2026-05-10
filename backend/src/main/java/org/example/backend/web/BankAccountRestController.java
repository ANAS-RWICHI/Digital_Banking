package org.example.backend.web;

import lombok.AllArgsConstructor;
import org.example.backend.dtos.AccountHistoryDto;
import org.example.backend.dtos.AccountOperationDto;
import org.example.backend.dtos.BankAccountDto;
import org.example.backend.services.BankAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class BankAccountRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/accounts/{accountId}")
    public BankAccountDto getBankAccount(@PathVariable String accountId) {
        return bankAccountService.getBankAccountById(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDto> getBankAccounts(){
        return bankAccountService.getAllBankAccounts();
    }
    @GetMapping("account/{accountId}/operations")
    public List<AccountOperationDto> getHistory (@PathVariable String accountId){
        return bankAccountService.getHistory(accountId);
    }

    @GetMapping("account/{accountId}/pageOperations")
    public AccountHistoryDto getAccountHistory (@PathVariable String accountId , @RequestParam(name = "page",defaultValue = "0") int page , @RequestParam(name = "size",defaultValue = "5") int size){
        return bankAccountService.getAccountHistory(accountId,page,size);
    }


}

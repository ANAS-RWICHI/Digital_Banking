package org.example.backend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dtos.CustomerDto;
import org.example.backend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Slf4j
public class CustomerRestController {
    private BankAccountService bankAccountService;
    @GetMapping("/customers")
    public List<CustomerDto> getAllCustomers() {
        return bankAccountService.listCustomers();
    }
    @GetMapping("/customer/{id}")
    public CustomerDto getCustomerById(@PathVariable Long id){
        return bankAccountService.getCustomerById(id);
    }
    @PostMapping("/customer")
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto){
        return bankAccountService.saveCustomer(customerDto);
    }
    @PutMapping("customer/{id}")
    public CustomerDto updateCustomer(@RequestBody CustomerDto customerDto, @PathVariable Long id){
        customerDto.setId(id);
        return bankAccountService.updateCustomer(customerDto);
    }
    @DeleteMapping("customer/{id}")
    public void deleteCustomer(@PathVariable Long id){
        bankAccountService.deleteCustomer(id);
    }
}

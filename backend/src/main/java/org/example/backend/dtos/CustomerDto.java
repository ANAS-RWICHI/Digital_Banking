package org.example.backend.dtos;

import lombok.Data;
import org.example.backend.entities.BankAccount;

import java.util.List;

@Data
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
}

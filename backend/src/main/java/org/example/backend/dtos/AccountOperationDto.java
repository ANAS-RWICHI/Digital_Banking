package org.example.backend.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.entities.BankAccount;
import org.example.backend.entities.enums.OperationType;

import java.util.Date;

@Data
public class AccountOperationDto {
    private Long id;
    private Date date;
    private double amount;
    private OperationType type;
    private String description;
}

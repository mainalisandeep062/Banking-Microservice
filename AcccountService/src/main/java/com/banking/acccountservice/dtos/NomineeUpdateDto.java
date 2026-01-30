package com.banking.acccountservice.dtos;

import lombok.Data;

@Data
public class NomineeUpdateDto {
    private String nomineeName;
    private String nomineeEmail;
    private String nomineeRelationship;
}

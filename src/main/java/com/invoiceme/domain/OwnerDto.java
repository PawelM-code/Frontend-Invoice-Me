package com.invoiceme.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDto {
    private Long id;
    private String name;
    private Long nip;
    private Long regon;
    private String workingAddress;
    private String bankAccount;
    private String email;

    public OwnerDto(String name, Long nip, Long regon, String workingAddress, String bankAccount, String email) {
        this.name = name;
        this.nip = nip;
        this.regon = regon;
        this.workingAddress = workingAddress;
        this.bankAccount = bankAccount;
        this.email = email;
    }
}

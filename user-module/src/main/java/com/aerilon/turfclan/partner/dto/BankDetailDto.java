package com.aerilon.turfclan.partner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankDetailDto {

    @NotBlank(message = "Business Name is required")
    private String accountHolderName;
    private String accountNumber;
    private String bankName;
    private String ifscCode;
    private String branchName;
    private String cancelledChequeUrl;
}

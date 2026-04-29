package com.aerilon.turfclan.partner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankDetailRequestDto {

    @NotBlank(message = "Account Holder Name is required")
    private String accountHolderName;
    @NotBlank(message = "Account Number is required")
    private String accountNumber;
    @NotBlank(message = "Confirm Account Number is required")
    private String confirmAccountNumber;
    @NotBlank(message = "Bank Name is required")
    private String bankName;
    @NotBlank(message = "IFSC code is required")
    private String ifscCode;
    @NotBlank(message = "Branch Name is required")
    private String branchName;
    @NotBlank(message = "Cancelled cheque is required")
    private String cancelledChequeUrl;
}

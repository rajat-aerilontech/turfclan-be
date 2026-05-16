package com.aerilon.turfclan.partner.dto;

import com.aerilon.turfclan.partner.enums.SignatureType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ContractRequestDto {
    @NotBlank(message = "Agreement Version is required")
    private String agreementVersion;
    @NotNull(message = "You must agree to the terms")
    private Boolean isAgreed;
    @NotNull(message = "Signature Type is required")
    private SignatureType signatureType;
    private String typedSignatureName;
    private MultipartFile uploadedSignature;
}

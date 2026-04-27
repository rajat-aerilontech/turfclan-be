package com.aerilon.turfclan.partner.dto;

import com.aerilon.turfclan.partner.enums.SignatureType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractDto {
    private String agreementVersion;
    private Boolean isAgreed;
    private SignatureType signatureType;
    private String typedSignatureName;
    private String uploadedSignatureUrl;
}

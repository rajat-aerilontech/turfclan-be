package com.aerilon.turfclan.partner.dto;

import com.aerilon.turfclan.enums.IdProofType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerDetailDto {
    private String fullName;
    private String phonenumber;
    private String emailId;
    private String designation;
    private String profileImageUrl;
    private String aadharNumber;
    private String panNumber;
    private IdProofType idProofType;
    private String idDocumentUrl;
}

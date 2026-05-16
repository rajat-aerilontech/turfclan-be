package com.aerilon.turfclan.partner.dto;

import com.aerilon.turfclan.dto.S3ImageModelDto;
import com.aerilon.turfclan.dto.S3ImageResponseDto;
import com.aerilon.turfclan.enums.IdProofType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PartnerDetailRequestDto {
    @NotBlank(message = "Full Name is required")
    private String fullName;
    @NotBlank(message = "Phone Number is required")
    private String phonenumber;
    @NotBlank(message = "Email Name is required")
    private String email;
    @NotBlank(message = "Designation is required")
    private String designation;
    private MultipartFile profileImage;
    private String aadharNumber;
    private String panNumber;
    @NotNull(message = "ID Proof type is required")
    private IdProofType idProofType;
    private MultipartFile idDocument;
}

package com.aerilon.turfclan.partner.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private String partnerName;
    private boolean partnerStatus;
    // Business Detail
    private String businessName;
    private String designation;
    private String email;
    private String mobile;
    private String gstNumber;
    private String panNumber;
    // Bank Detail
    private String accountHolderName;
    private String accountNumber;
    private String bankName;
    private String ifscCode;
    // Brand Detail
    private String brandName;
    private String tagline;
    private String brandLogoUrl;
    private List<String> bannerImageUrls;
    private String description;
    private String longDescription;
    // Site
    private String brandWebsite;
    private String instagram;
    private String youtube;
    private String facebook;
    private String x;
    private String linkedIn;
}

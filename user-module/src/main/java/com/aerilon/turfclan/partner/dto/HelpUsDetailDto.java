package com.aerilon.turfclan.partner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelpUsDetailDto {
    private String referralSource;
    private String programUnderstanding;
    private String reasonToJoin;
    private String activelyInvolved;
    private String timeCommitment;
    private Boolean isAssociatedWithEmployee;
    private Integer totalPartnersCount;
}

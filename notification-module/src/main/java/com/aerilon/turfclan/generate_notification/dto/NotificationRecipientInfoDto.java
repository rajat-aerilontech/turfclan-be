package com.aerilon.turfclan.generate_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRecipientInfoDto {
    private String userEmail;
    private String languageIsoCode;
    private String countryIsoCode;
    private String userRole;
}

package com.aerilon.turfclan.user.dto;

import com.aerilon.turfclan.dto.S3ImageResponseDto;
import com.aerilon.turfclan.user.enums.Gender;
import com.aerilon.turfclan.user.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    private String id;
    private String userName;
    @JsonAlias({"email", "user_email"})
    private String userEmail;
    private String firstName;
    private String lastName;
    private String phoneCountryCode;
    private String phoneNumber;
    private S3ImageResponseDto profilePictureUrl;
    private String bio;
    private String dateOfBirth;
    private Gender gender;
    @JsonProperty("city")
    private String location;
    private boolean isVerified;
    @JsonAlias("countryCode")
    private String countryIsoCode;
    private boolean isProfileComplete;
    private UserStatus status;

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneCountryCode='" + phoneCountryCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", location='" + location + '\'' +
                ", isVerified=" + isVerified +
                ", countryIsoCode='" + countryIsoCode + '\'' +
                ", isProfileComplete=" + isProfileComplete +
                ", status=" + status +
                '}';
    }
}

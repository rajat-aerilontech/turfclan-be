package com.aerilon.turfclan.user;

import com.aerilon.turfclan.user.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

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
    private String profilePictureUrl;
    private String bio;
    private String dateOfBirth;
    private String gender;
    private String location;
    private boolean isVerified;
    @JsonAlias("countryCode")
    private String countryIsoCode;
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
                ", status=" + status +
                '}';
    }
}

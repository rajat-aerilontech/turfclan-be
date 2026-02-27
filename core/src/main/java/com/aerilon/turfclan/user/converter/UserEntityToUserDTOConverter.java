package com.aerilon.turfclan.user.converter;

import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUserDTOConverter implements Converter<UserEntity, UserDTO> {
    @Override
    public UserDTO convert(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId().toString());
        userDTO.setUserName(userEntity.getUserName());
        userDTO.setUserEmail(userEntity.getUserEmail());
        userDTO.setFirstName(userEntity.getFirstName());
        userDTO.setLastName(userEntity.getLastName());
        userDTO.setPhoneCountryCode(userEntity.getPhoneCountryCode());
        userDTO.setPhoneNumber(userEntity.getPhoneNumber());
        userDTO.setProfilePictureUrl(userEntity.getProfilePictureUrl());
        userDTO.setBio(userEntity.getBio());
        userDTO.setDateOfBirth(userEntity.getDateOfBirth() != null ? userEntity.getDateOfBirth().toString() : null);
        userDTO.setGender(userEntity.getGender());
        userDTO.setLocation(userEntity.getLocation());
        userDTO.setVerified(userEntity.isVerified());
        userDTO.setCountryIsoCode(userEntity.getCountryIsoCode());
        userDTO.setStatus(userEntity.getStatus());
        return userDTO;
    }
}

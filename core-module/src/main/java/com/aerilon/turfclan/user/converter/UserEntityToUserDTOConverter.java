package com.aerilon.turfclan.user.converter;

import com.aerilon.turfclan.dto.S3ImageModelDto;
import com.aerilon.turfclan.service.S3Service;
import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUserDTOConverter implements Converter<UserEntity, UserDTO> {

    @Autowired
    private S3Service s3Service;

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
        if (userEntity.getUserProfileImage() != null) {
            S3ImageModelDto img = userEntity.getUserProfileImage();
            com.aerilon.turfclan.dto.S3ImageResponseDto image = new com.aerilon.turfclan.dto.S3ImageResponseDto(
                    img.getKey(),
                    s3Service.preSignedUrl(img.getKey(), 10)
            );
            userDTO.setProfilePictureUrl(image);
        }
        userDTO.setBio(userEntity.getBio());
        userDTO.setDateOfBirth(userEntity.getDateOfBirth() != null ? userEntity.getDateOfBirth().toString() : null);
        userDTO.setGender(userEntity.getGender());
        userDTO.setLocation(userEntity.getLocation());
        userDTO.setVerified(userEntity.isVerified());
        userDTO.setCountryIsoCode(userEntity.getCountryIsoCode());
        userDTO.setProfileComplete(userEntity.isProfileComplete());
        userDTO.setStatus(userEntity.getStatus());
        return userDTO;
    }
}

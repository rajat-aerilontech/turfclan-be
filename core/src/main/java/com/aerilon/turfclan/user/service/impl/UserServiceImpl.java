package com.aerilon.turfclan.user.service.impl;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.UserNotFoundException;
import com.aerilon.turfclan.user.converter.UserEntityToUserDTOConverter;
import com.aerilon.turfclan.user.dto.SignupRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import com.aerilon.turfclan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEntityToUserDTOConverter userConverter;

    @Value("${app.upload.profile-pic-dir:uploads/profile-pics}")
    private String profilePicUploadDir;

    @Override
    public UserDTO getUserByEmail(String emailId) {
        return userRepository.findByUserEmail(emailId)
                             .map(userConverter::convert)
                             .orElseThrow(() -> new UserNotFoundException("User not found with email: " + emailId));
    }

    @Override
    @Transactional
    public UserDTO signup(String userId, SignupRequestDTO request, MultipartFile profilePic) {
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserEmail(request.getEmail());
        user.setLocation(request.getLocation());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setSport(request.getSport());

        if (profilePic != null && !profilePic.isEmpty()) {
            String pictureUrl = saveProfilePicture(userId, profilePic);
            user.setProfilePictureUrl(pictureUrl);
        }

        UserEntity saved = userRepository.save(user);
        log.info("User profile completed for userId={}", userId);
        return userConverter.convert(saved);
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private String saveProfilePicture(String userId, MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "profile");
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String filename = userId + extension;
        try {
            Path uploadPath = Paths.get(profilePicUploadDir);
            Files.createDirectories(uploadPath);
            Path targetPath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Profile picture saved to {}", targetPath);
            return profilePicUploadDir + "/" + filename;
        } catch (IOException e) {
            log.error("Failed to store profile picture for userId={}", userId, e);
            throw new InvalidRequestException("Could not store profile picture. Please try again.");
        }
    }
}

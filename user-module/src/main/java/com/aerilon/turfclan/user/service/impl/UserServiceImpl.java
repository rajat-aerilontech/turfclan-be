package com.aerilon.turfclan.user.service.impl;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.UserNotFoundException;
import com.aerilon.turfclan.user.converter.UserEntityToUserDTOConverter;
import com.aerilon.turfclan.user.dto.SignupPersonalDTO;
import com.aerilon.turfclan.user.dto.SignupRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.enums.UserStatus;
import com.aerilon.turfclan.user.repository.UserRepository;
import com.aerilon.turfclan.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEntityToUserDTOConverter userConverter;

    @Override
    public UserDTO getUserByEmail(String emailId) {
        return userRepository.findByUserEmail(emailId)
                             .map(userConverter::convert)
                             .orElseThrow(() -> new UserNotFoundException("User not found with email: " + emailId));
    }

    @Override
    @Transactional
    public UserDTO signup(String userId, SignupRequestDTO request) {
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        SignupPersonalDTO personal = request.getPersonal();

        user.setFirstName(personal.getFirstName());
        user.setLastName(personal.getLastName());

        // Validate email uniqueness before saving
        String email = personal.getEmail();
        if (email != null && !email.isBlank()) {
            userRepository.findByUserEmail(email)
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new InvalidRequestException("Email '" + email + "' is already registered to another account");
                    });
            user.setUserEmail(email);
        }

        user.setLocation(personal.getCity());
        user.setGender(personal.getGender());
        user.setDateOfBirth(personal.getDob());
        user.setSport(personal.getSports());
        user.setSportProfile(request.getSport());
        user.setProfileComplete(true);
        user.setStatus(UserStatus.ACTIVE);

        UserEntity saved = userRepository.save(user);
        log.info("User profile completed for userId={}", userId);
        return userConverter.convert(saved);
    }

    private JsonNode buildSportPayload(JsonNode sportDetails, JsonNode selectedSports) {
        ObjectNode sportPayload = JsonNodeFactory.instance.objectNode();

        if (sportDetails != null && !sportDetails.isNull()) {
            if (sportDetails.isObject()) {
                sportPayload.setAll((ObjectNode) sportDetails.deepCopy());
            } else {
                sportPayload.set("details", sportDetails);
            }
        }

        if (selectedSports != null && !selectedSports.isNull()) {
            sportPayload.set("sports", selectedSports);
        }

        return sportPayload.isEmpty() ? null : sportPayload;
    }
}

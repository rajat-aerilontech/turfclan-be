package com.aerilon.turfclan.user.service.impl;

import com.aerilon.turfclan.dto.S3ImageModelDto;
import com.aerilon.turfclan.exception.AwsRuntimeException;
import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.service.S3Service;
import com.aerilon.turfclan.user.converter.UserEntityToUserDTOConverter;
import com.aerilon.turfclan.user.dto.DashboardResponseDTO;
import com.aerilon.turfclan.user.dto.SignupPersonalDTO;
import com.aerilon.turfclan.user.dto.SignupRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.enums.UserStatus;
import com.aerilon.turfclan.user.repository.UserRepository;
import com.aerilon.turfclan.user.repository.UserSportAssociationRepository;
import com.aerilon.turfclan.user.entity.UserSportAssociationEntity;
import com.aerilon.turfclan.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEntityToUserDTOConverter userConverter;
    private final UserSportAssociationRepository userSportAssociationRepository;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;
    private static final String USER_FOLDER_NAME = "users";

    @Override
    public UserDTO getUserByEmail(String emailId) {
        return userRepository.findByUserEmail(emailId)
                             .map(userConverter::convert)
                             .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + emailId));
    }

    @Override
    @Transactional
    public UserDTO signup(String userId, SignupRequestDTO request) {
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
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
        user.setProfileComplete(true);
        user.setStatus(UserStatus.ACTIVE);
        if (personal.getProfileImage() != null && !personal.getProfileImage().isEmpty()) {
            try {
                String key = s3Service.uploadFile(
                        personal.getProfileImage(),
                        "profile",
                        USER_FOLDER_NAME + "/" + user.getId(),
                        true
                );
                user.setUserProfileImage(new S3ImageModelDto(key));
            } catch (IOException e) {
                log.error("Failed to upload profile image", e);
                throw new AwsRuntimeException("Profile image upload failed", e);
            }
        }
        UserEntity saved = userRepository.save(user);
        if (personal.getSports() != null || request.getSport() != null) {
            UserSportAssociationEntity sportAssoc = userSportAssociationRepository.findByUserId(saved.getId())
                    .orElse(new UserSportAssociationEntity());
            sportAssoc.setUserId(saved.getId());
            if (personal.getSports() != null && !personal.getSports().isBlank()) {
                sportAssoc.setSportId(UUID.randomUUID());
            } else if (sportAssoc.getSportId() == null) {
                sportAssoc.setSportId(UUID.randomUUID());
            }
            if (request.getSport() != null && !request.getSport().isBlank()) {
                try {
                    JsonNode sportNode = objectMapper.readTree(request.getSport());
                    sportAssoc.setSportProfile(sportNode);
                } catch (Exception e) {
                    log.error("Invalid sport JSON", e);
                    throw new InvalidRequestException("Invalid sport JSON format");
                }
            }
            userSportAssociationRepository.save(sportAssoc);
        }
        log.info("User profile completed for userId={}", userId);
        return userConverter.convert(saved);
    }

    @Override
    public DashboardResponseDTO getDashboard(String userId, String selectedSportExperience) {
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String experienceKey = selectedSportExperience == null ? null : selectedSportExperience.trim();
        
        JsonNode sportProfile = null;
        userSportAssociationRepository.findByUserId(user.getId())
                .ifPresent(assoc -> {
                    // we can't easily assign to outer variable inside lambda without making it array or just using Optional
                });
        
        var sportAssocOpt = userSportAssociationRepository.findByUserId(user.getId());
        if (sportAssocOpt.isPresent()) {
            sportProfile = sportAssocOpt.get().getSportProfile();
        }

        JsonNode experienceNode = findExperienceNode(sportProfile, experienceKey);

        DashboardResponseDTO response = new DashboardResponseDTO();
        response.setUserId(userId);
        response.setSelectedSportExperience(experienceKey);
        response.setUserPerformance(extractUserPerformance(sportProfile, experienceNode, experienceKey));
        response.setSportSpecificDetail(extractSportSpecificDetail(user, sportProfile, experienceNode, experienceKey));
        return response;
    }

    private JsonNode findExperienceNode(JsonNode sportProfile, String selectedSportExperience) {
        if (sportProfile == null || sportProfile.isNull() || selectedSportExperience == null || selectedSportExperience.isBlank()) {
            return null;
        }

        JsonNode directExperience = getCaseInsensitiveField(sportProfile, selectedSportExperience);
        if (directExperience != null && !directExperience.isMissingNode() && !directExperience.isNull()) {
            return directExperience;
        }

        JsonNode experiences = getCaseInsensitiveField(sportProfile, "experiences");
        if (experiences == null || experiences.isNull() || experiences.isMissingNode()) {
            return null;
        }

        if (experiences.isObject()) {
            JsonNode keyedExperience = getCaseInsensitiveField(experiences, selectedSportExperience);
            if (keyedExperience != null && !keyedExperience.isMissingNode() && !keyedExperience.isNull()) {
                return keyedExperience;
            }
        }

        if (!experiences.isArray()) {
            return null;
        }

        ArrayNode experiencesArray = (ArrayNode) experiences;
        for (JsonNode candidate : experiencesArray) {
            if (!candidate.isObject()) {
                continue;
            }

            String name = readTextField(candidate, "name");
            String type = readTextField(candidate, "type");
            String level = readTextField(candidate, "level");
            if (selectedSportExperience.equalsIgnoreCase(name)
                    || selectedSportExperience.equalsIgnoreCase(type)
                    || selectedSportExperience.equalsIgnoreCase(level)) {
                return candidate;
            }
        }

        return null;
    }

    private JsonNode extractUserPerformance(JsonNode sportProfile, JsonNode experienceNode, String selectedSportExperience) {
        if (experienceNode != null) {
            JsonNode experiencePerformance = getCaseInsensitiveField(experienceNode, "performance");
            if (experiencePerformance != null && !experiencePerformance.isMissingNode() && !experiencePerformance.isNull()) {
                return experiencePerformance;
            }
        }

        JsonNode profilePerformance = getCaseInsensitiveField(sportProfile, "performance");
        if (profilePerformance == null || profilePerformance.isMissingNode() || profilePerformance.isNull()) {
            return null;
        }

        if (selectedSportExperience != null && !selectedSportExperience.isBlank() && profilePerformance.isObject()) {
            JsonNode selectedPerformance = getCaseInsensitiveField(profilePerformance, selectedSportExperience);
            if (selectedPerformance != null && !selectedPerformance.isMissingNode() && !selectedPerformance.isNull()) {
                return selectedPerformance;
            }
        }

        return profilePerformance;
    }

    private JsonNode extractSportSpecificDetail(UserEntity user,
                                                JsonNode sportProfile,
                                                JsonNode experienceNode,
                                                String selectedSportExperience) {
        if (experienceNode != null && experienceNode.isObject()) {
            JsonNode explicitDetail = getCaseInsensitiveField(experienceNode, "sportSpecificDetail");
            if (explicitDetail != null && !explicitDetail.isMissingNode() && !explicitDetail.isNull()) {
                return explicitDetail;
            }

            JsonNode detail = getCaseInsensitiveField(experienceNode, "details");
            if (detail != null && !detail.isMissingNode() && !detail.isNull()) {
                return detail;
            }

            ObjectNode fallback = ((ObjectNode) experienceNode).deepCopy();
            removeCaseInsensitiveField(fallback, "performance");
            if (!fallback.isEmpty()) {
                return fallback;
            }
        }

        ObjectNode detail = JsonNodeFactory.instance.objectNode();
        
        var sportAssocOpt = userSportAssociationRepository.findByUserId(user.getId());
        // Currently we don't store sport name in the association, only sportId.
        // We will just skip setting sport name or set it if needed later.

        if (selectedSportExperience != null && !selectedSportExperience.isBlank()) {
            detail.put("selectedSportExperience", selectedSportExperience);
        }

        JsonNode profileDetails = getCaseInsensitiveField(sportProfile, "sportSpecificDetail");
        if (profileDetails != null && !profileDetails.isMissingNode() && !profileDetails.isNull()) {
            detail.set("profile", profileDetails);
        }

        return detail.isEmpty() ? null : detail;
    }

    private JsonNode getCaseInsensitiveField(JsonNode node, String key) {
        if (node == null || key == null || !node.isObject()) {
            return null;
        }

        JsonNode exact = node.get(key);
        if (exact != null) {
            return exact;
        }

        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (fieldName.equalsIgnoreCase(key)) {
                return node.get(fieldName);
            }
        }

        return null;
    }

    private void removeCaseInsensitiveField(ObjectNode node, String key) {
        if (node == null || key == null) {
            return;
        }

        String toRemove = null;
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (fieldName.equalsIgnoreCase(key)) {
                toRemove = fieldName;
                break;
            }
        }

        if (toRemove != null) {
            node.remove(toRemove);
        }
    }

    private String readTextField(JsonNode node, String fieldName) {
        JsonNode field = getCaseInsensitiveField(node, fieldName);
        if (field == null || field.isNull()) {
            return null;
        }
        return field.asText().toLowerCase(Locale.ROOT);
    }
}

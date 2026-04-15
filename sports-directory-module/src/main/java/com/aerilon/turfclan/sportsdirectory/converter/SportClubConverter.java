package com.aerilon.turfclan.sportsdirectory.converter;

import com.aerilon.turfclan.enums.RecordStatus;
import com.aerilon.turfclan.sportsdirectory.dto.AssociationContactDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubDetailDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubSummaryDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.entity.SportClubEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SportClubConverter {

    private final ObjectMapper objectMapper;

    public SportClubSummaryDTO toSummary(SportClubEntity entity) {
        SportClubSummaryDTO summary = new SportClubSummaryDTO();
        summary.setId(entity.getId() != null ? entity.getId().toString() : null);
        summary.setSportCategory(entity.getSportCategory());
        summary.setImages(entity.getImages());
        summary.setName(entity.getName());
        summary.setShortName(entity.getShortName());
        summary.setBoard(entity.getBoard());
        summary.setMembersNumber(entity.getMembersNumber());
        summary.setFoundedYear(entity.getFoundedYear());
        summary.setState(entity.getState());
        summary.setLocationName(entity.getLocationName());
        summary.setMapLocation(entity.getMapLocation());
        return summary;
    }

    public SportClubDetailDTO toDetail(SportClubEntity entity) {
        SportClubDetailDTO detail = new SportClubDetailDTO();
        detail.setId(entity.getId() != null ? entity.getId().toString() : null);
        detail.setSportCategory(entity.getSportCategory());
        detail.setImages(entity.getImages());
        detail.setName(entity.getName());
        detail.setShortName(entity.getShortName());
        detail.setBoard(entity.getBoard());
        detail.setMembersNumber(entity.getMembersNumber());
        detail.setFoundedYear(entity.getFoundedYear());
        detail.setState(entity.getState());
        detail.setLocationName(entity.getLocationName());
        detail.setMapLocation(entity.getMapLocation());
        detail.setAbout(entity.getAbout());
        detail.setAchievements(entity.getAchievements());
        detail.setContactDetails(parseContactDetails(entity.getContactDetails()));
        return detail;
    }

    public void applyUpsertRequest(SportClubEntity entity, SportClubUpsertRequestDTO request) {
        entity.setSportCategory(request.getSportCategory());
        entity.setImages(request.getImages());
        entity.setName(request.getName() != null ? request.getName().trim() : null);
        entity.setShortName(request.getShortName());
        entity.setBoard(request.getBoard());
        entity.setMembersNumber(request.getMembersNumber());
        entity.setFoundedYear(request.getFoundedYear());
        entity.setState(request.getState());
        entity.setLocationName(request.getLocationName());
        entity.setMapLocation(request.getMapLocation());
        entity.setAbout(request.getAbout());
        entity.setAchievements(request.getAchievements());
        entity.setContactDetails(toContactDetailsJson(request.getContactDetails()));
        entity.setStatus(RecordStatus.ACTIVE);
        entity.setCreatedAt(LocalDateTime.now());
    }

    private JsonNode toContactDetailsJson(AssociationContactDTO contactDetails) {
        if (contactDetails == null) {
            return null;
        }
        return objectMapper.valueToTree(contactDetails);
    }

    private AssociationContactDTO parseContactDetails(JsonNode contactDetailsNode) {
        if (contactDetailsNode == null || contactDetailsNode.isNull()) {
            return new AssociationContactDTO();
        }

        try {
            return objectMapper.treeToValue(contactDetailsNode, AssociationContactDTO.class);
        } catch (Exception ex) {
            AssociationContactDTO fallback = new AssociationContactDTO();
            JsonNode email = contactDetailsNode.get("email");
            JsonNode phone = contactDetailsNode.get("phone");
            JsonNode website = contactDetailsNode.get("website");
            fallback.setEmail(email != null && !email.isNull() ? email.asText() : null);
            fallback.setPhone(phone != null && !phone.isNull() ? phone.asText() : null);
            fallback.setWebsite(website != null && !website.isNull() ? website.asText() : null);
            return fallback;
        }
    }
}
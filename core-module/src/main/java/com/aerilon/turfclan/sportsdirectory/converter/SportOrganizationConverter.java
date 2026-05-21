package com.aerilon.turfclan.sportsdirectory.converter;

import com.aerilon.turfclan.enums.RecordStatus;
import com.aerilon.turfclan.sportsdirectory.dto.OrganizationContactDto;
import com.aerilon.turfclan.sportsdirectory.dto.OrganizationSummaryDto;
import com.aerilon.turfclan.sportsdirectory.dto.SportOrganizationDetailDto;
import com.aerilon.turfclan.sportsdirectory.dto.SportOrganizationUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.entity.SportOrganizationEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SportOrganizationConverter {

    private final ObjectMapper objectMapper;

    public OrganizationSummaryDto toSummary(SportOrganizationEntity entity) {
        OrganizationSummaryDto summary = new OrganizationSummaryDto();
        summary.setId(entity.getId() != null ? entity.getId().toString() : null);
        summary.setOrganizationType(entity.getOrganizationType());
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
        // Map the JTS Point to Double coordinates safely
        if (entity.getLocation() != null) {
            summary.setLongitude(entity.getLocation().getX());
            summary.setLatitude(entity.getLocation().getY());
        }
        return summary;
    }

    public SportOrganizationDetailDto toDetail(SportOrganizationEntity entity) {
        SportOrganizationDetailDto detail = new SportOrganizationDetailDto();
//        detail.setId(entity.getId() != null ? entity.getId().toString() : null);
//        detail.setSportCategory(entity.getSportCategory());
//        detail.setImages(entity.getImages());
//        detail.setName(entity.getName());
//        detail.setShortName(entity.getShortName());
//        detail.setBoard(entity.getBoard());
//        detail.setMembersNumber(entity.getMembersNumber());
//        detail.setFoundedYear(entity.getFoundedYear());
//        detail.setState(entity.getState());
//        detail.setLocationName(entity.getLocationName());
//        detail.setMapLocation(entity.getMapLocation());
        detail.setAbout(entity.getAbout());
        detail.setAchievements(entity.getAchievements());
        return detail;
    }

    public void applyUpsertRequest(SportOrganizationEntity entity, SportOrganizationUpsertRequestDTO request) {
        entity.setSportCategory(request.getSportCategory());
        entity.setOrganizationType(request.getOrganizationType());
        entity.setName(request.getName() != null ? request.getName().trim() : null);
        entity.setShortName(request.getShortName());
        entity.setBoard(request.getBoard());
        entity.setMembersNumber(request.getMembersNumber());
        entity.setFoundedYear(request.getFoundedYear());
        entity.setState(request.getState());
        entity.setLocationName(request.getLocationName());
        entity.setMapLocation(request.getMapLocation());
        entity.setAbout(request.getAbout());
        entity.setCreatedAt(LocalDateTime.now());
    }

    private JsonNode toContactDetailsJson(OrganizationContactDto contactDetails) {
        if (contactDetails == null) {
            return null;
        }
        return objectMapper.valueToTree(contactDetails);
    }

    private OrganizationContactDto parseContactDetails(JsonNode contactDetailsNode) {
        if (contactDetailsNode == null || contactDetailsNode.isNull()) {
            return new OrganizationContactDto();
        }

        try {
            return objectMapper.treeToValue(contactDetailsNode, OrganizationContactDto.class);
        } catch (Exception ex) {
            OrganizationContactDto fallback = new OrganizationContactDto();
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

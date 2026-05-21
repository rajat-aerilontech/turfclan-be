package com.aerilon.turfclan.sportsdirectory.service.impl;

import com.aerilon.turfclan.enums.RecordStatus;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.pagination.CursorPageResponse;
import com.aerilon.turfclan.pagination.CursorPageToken;
import com.aerilon.turfclan.pagination.CursorPaginationUtils;
import com.aerilon.turfclan.service.S3Service;
import com.aerilon.turfclan.sportsdirectory.converter.SportOrganizationConverter;
import com.aerilon.turfclan.sportsdirectory.dto.OrganizationSummaryDto;
import com.aerilon.turfclan.sportsdirectory.dto.SportOrganizationDetailDto;
import com.aerilon.turfclan.sportsdirectory.dto.SportOrganizationUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.entity.SportOrganizationEntity;
import com.aerilon.turfclan.sportsdirectory.enums.OrganizationType;
import com.aerilon.turfclan.sportsdirectory.repository.SportOrganizationRepository;
import com.aerilon.turfclan.sportsdirectory.resolver.OrganizationResolver;
import com.aerilon.turfclan.sportsdirectory.service.SportOrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SportOrganizationServiceImpl implements SportOrganizationService {

    private final SportOrganizationRepository sportOrganizationRepository;
    private final SportOrganizationConverter sportOrganizationConverter;
    private final OrganizationResolver organizationResolver;
    private final ObjectMapper objectMapper;
    private final S3Service s3Service;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private static final String ORG_FOLDER_NAME = "organizations";

    @Override
    public CursorPageResponse<OrganizationSummaryDto> getOrganizationBySport(OrganizationType organizationType,
                                                                             String cursor,
                                                                             Integer limit) {
        int normalizedLimit = CursorPaginationUtils.normalizeLimit(limit);
        CursorPageToken cursorToken = CursorPaginationUtils.decodeCursor(cursor);
        PageRequest pageRequest = PageRequest.of(0, normalizedLimit + 1);

        List<SportOrganizationEntity> entities;

        if (cursorToken == null) {
            // Initial Page
            if (organizationType == null) {
                entities = sportOrganizationRepository.findAllByOrderByCreatedAtDescIdDesc(pageRequest);
            } else {
                entities = sportOrganizationRepository.findByOrganizationTypeOrderByCreatedAtDescIdDesc(organizationType, pageRequest);
            }
        } else {
            // Subsequent Pages using Cursor
            if (organizationType == null) {
                entities = sportOrganizationRepository.findAllAfterCursor(
                        cursorToken.createdAt(),
                        cursorToken.id(),
                        pageRequest
                );
            } else {
                entities = sportOrganizationRepository.findByOrganizationTypeAfterCursor(
                        organizationType,
                        cursorToken.createdAt(),
                        cursorToken.id(),
                        pageRequest
                );
            }
        }

        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No sports associations found");
        }

        // Assuming BaseAuditableEntity has getCreatedAt()
        return CursorPaginationUtils.buildResponse(
                entities,
                normalizedLimit,
                sportOrganizationConverter::toSummary,
                SportOrganizationEntity::getCreatedAt,  // Must return LocalDateTime
                SportOrganizationEntity::getId          // Must return UUID
        );
    }

//    @Override
//    public SportAssociationDetailDTO getAssociationDetail(String associationId) {
//        SportAssociationEntity entity = sportAssociationResolver.requireByAssociationIdentifier(associationId);
//        return sportAssociationConverter.toDetail(entity);
//    }
//
    @Override
    @Transactional
    public void createAssociation(SportOrganizationUpsertRequestDTO request) {
        SportOrganizationEntity entity = new SportOrganizationEntity();
        UUID organizationId = UUID.randomUUID();
        entity.setId(organizationId);
        sportOrganizationConverter.applyUpsertRequest(entity, request);
        List<MultipartFile> images = request.getImages();
        if (images != null && !images.isEmpty()) {
            List<String> uploadedImages = new ArrayList<>();
            for (MultipartFile file : images) {
                try {
                    String key = s3Service.uploadFile(
                            file,
                            "org_image",
                            ORG_FOLDER_NAME + "/" + organizationId,
                            false
                    );
                    uploadedImages.add(key);
                } catch (IOException e) {
                    log.error("Failed to upload organization image: {}", file.getOriginalFilename(), e);
                    throw new RuntimeException("Organization image upload failed", e);
                }
            }
            entity.setImages(uploadedImages);
        }
        if (request.getLatitude() != null && request.getLongitude() != null) {
            Point locationPoint = geometryFactory.createPoint(
                    new Coordinate(request.getLongitude(), request.getLatitude())
            );
            entity.setLocation(locationPoint);
        }
        if (request.getContactDetails() != null) {
            entity.setContactDetails(objectMapper.valueToTree(request.getContactDetails()));
        }
        if (request.getAchievements() != null && !request.getAchievements().isBlank()) {
            try {
                entity.setAchievements(objectMapper.readTree(request.getAchievements()));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.error("Failed to parse achievements JSON string", e);
                throw new IllegalArgumentException("Invalid JSON format for achievements");
            }
        }
        entity.setStatus(RecordStatus.ACTIVE);
        sportOrganizationRepository.save(entity);
    }
//
//    @Override
//    @Transactional
//    public SportAssociationDetailDTO updateAssociation(String associationId,
//                                                       SportAssociationUpsertRequestDTO request) {
//        sportAssociationResolver.validateUpsertRequest(request);
//        SportAssociationEntity entity =
//                sportAssociationResolver.requireByAssociationIdentifierGlobal(associationId);
//
//        sportAssociationConverter.applyUpsertRequest(entity, request);
//
//        SportAssociationEntity saved = sportAssociationRepository.save(entity);
//        return sportAssociationConverter.toDetail(saved);
//    }
//
//    @Override
//    @Transactional
//    public void deleteAssociation(String associationId) {
//        SportAssociationEntity entity =
//                sportAssociationResolver.requireByAssociationIdentifierGlobal(associationId);
//        sportAssociationRepository.delete(entity);
//    }
}

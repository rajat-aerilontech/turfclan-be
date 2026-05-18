package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.BrandDetailsRequestDto;
import com.aerilon.turfclan.partner.dto.BusinessInfoDto;
import com.aerilon.turfclan.partner.entity.BrandDetailEntity;
import com.aerilon.turfclan.service.S3Service;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

@Component
public class BrandDetailConverter implements Converter<BusinessInfoDto, BrandDetailEntity> {

    @Autowired
    private S3Service s3Service;

    @Override
    public BrandDetailEntity convert(BusinessInfoDto source) {
        if (source.getBrandDetails() == null) {
            return null;
        }
        BrandDetailsRequestDto detail = source.getBrandDetails();
        BrandDetailEntity entity = new BrandDetailEntity();
        entity.setBrandName(detail.getBrandName());
        entity.setTagline(detail.getTagline());
        entity.setBrandLogoUrl(detail.getBrandLogoUrl());
        entity.setBannerImageUrls(detail.getBannerImageUrls() != null ? new HashSet<>(detail.getBannerImageUrls()) : new HashSet<>());
        entity.setDescription(detail.getDescription());
        entity.setLongDescription(detail.getLongDescription());
        entity.setBrandWebsite(detail.getBrandWebsite());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public BrandDetailsRequestDto toDto(BrandDetailEntity entity) {
        if (entity == null) return null;
        BrandDetailsRequestDto dto = new BrandDetailsRequestDto();
        dto.setBrandName(entity.getBrandName());
        dto.setTagline(entity.getTagline());
        if (entity.getBrandLogoUrl() != null && !entity.getBrandLogoUrl().isBlank()) {
            dto.setBrandLogoUrl(s3Service.preSignedUrl(entity.getBrandLogoUrl(), 10));
        }
        dto.setBannerImageUrls(entity.getBannerImageUrls() != null ?
                entity.getBannerImageUrls().stream()
                        .map(key -> s3Service.preSignedUrl(key, 10))
                        .toList() : new ArrayList<>());
        dto.setDescription(entity.getDescription());
        dto.setLongDescription(entity.getLongDescription());
        dto.setBrandWebsite(entity.getBrandWebsite());
        return dto;
    }
}

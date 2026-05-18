package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.BankDetailRequestDto;
import com.aerilon.turfclan.partner.entity.BankDetailEntity;
import com.aerilon.turfclan.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BankDetailConverter implements Converter<BankDetailRequestDto, BankDetailEntity> {

    @Autowired
    private S3Service s3Service;

    @Override
    public BankDetailEntity convert(BankDetailRequestDto source) {
        BankDetailEntity entity = new BankDetailEntity();
        entity.setAccountHolderName(source.getAccountHolderName());
        entity.setAccountNumber(source.getAccountNumber());
        entity.setBankName(source.getBankName());
        entity.setIfscCode(source.getIfscCode());
        entity.setBranchName(source.getBranchName());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public BankDetailRequestDto toDto(BankDetailEntity entity) {
        if (entity == null) return null;

        BankDetailRequestDto dto = new BankDetailRequestDto();
        dto.setAccountHolderName(entity.getAccountHolderName());
        dto.setAccountNumber(entity.getAccountNumber());
        dto.setConfirmAccountNumber(entity.getAccountNumber());
        dto.setBankName(entity.getBankName());
        dto.setIfscCode(entity.getIfscCode());
        dto.setBranchName(entity.getBranchName());
        if (entity.getCancelledChequeUrl() != null && !entity.getCancelledChequeUrl().isBlank()) {
            dto.setCancelledChequeUrl(s3Service.preSignedUrl(entity.getCancelledChequeUrl(), 10));
        }
        return dto;
    }
}

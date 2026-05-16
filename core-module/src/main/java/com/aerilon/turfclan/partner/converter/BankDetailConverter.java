package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.BankDetailRequestDto;
import com.aerilon.turfclan.partner.entity.BankDetailEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BankDetailConverter implements Converter<BankDetailRequestDto, BankDetailEntity> {

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
        return dto;
    }
}

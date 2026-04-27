package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.BankDetailDto;
import com.aerilon.turfclan.partner.entity.BankDetailEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BankDetailConverter implements Converter<BankDetailDto, BankDetailEntity> {

    @Override
    public BankDetailEntity convert(BankDetailDto source) {
        BankDetailEntity entity = new BankDetailEntity();
        entity.setAccountHolderName(source.getAccountHolderName());
        entity.setAccountNumber(source.getAccountNumber());
        entity.setBankName(source.getBankName());
        entity.setIfscCode(source.getIfscCode());
        entity.setBranchName(source.getBranchName());
        entity.setCancelledChequeUrl(source.getCancelledChequeUrl());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}

package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.BusinessInfoDto;
import com.aerilon.turfclan.partner.dto.HelpUsDetailDto;
import com.aerilon.turfclan.partner.entity.HelpUsEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HelpUsConverter implements Converter<BusinessInfoDto, HelpUsEntity> {

    @Override
    public HelpUsEntity convert(BusinessInfoDto source) {
        if (source.getHelpUsDetail() == null) {
            return null;
        }
        HelpUsDetailDto detail = source.getHelpUsDetail();
        HelpUsEntity entity = new HelpUsEntity();
        entity.setReferralSource(detail.getReferralSource());
        entity.setProgramUnderstanding(detail.getProgramUnderstanding());
        entity.setReasonToJoin(detail.getReasonToJoin());
        entity.setActivelyInvolved(detail.getActivelyInvolved());
        entity.setTimeCommitment(detail.getTimeCommitment());
        entity.setIsAssociatedWithEmployee(detail.getIsAssociatedWithEmployee());
        entity.setTotalPartnersCount(detail.getTotalPartnersCount());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public HelpUsDetailDto toDto(HelpUsEntity entity) {
        if (entity == null) return null;
        HelpUsDetailDto dto = new HelpUsDetailDto();
        dto.setReferralSource(entity.getReferralSource());
        dto.setProgramUnderstanding(entity.getProgramUnderstanding());
        dto.setReasonToJoin(entity.getReasonToJoin());
        dto.setActivelyInvolved(entity.getActivelyInvolved());
        dto.setTimeCommitment(entity.getTimeCommitment());
        dto.setIsAssociatedWithEmployee(entity.getIsAssociatedWithEmployee());
        dto.setTotalPartnersCount(entity.getTotalPartnersCount());
        return dto;
    }
}

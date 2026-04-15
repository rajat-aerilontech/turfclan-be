package com.aerilon.turfclan.tournament.converter;

import com.aerilon.turfclan.enums.RecordStatus;
import com.aerilon.turfclan.tournament.dto.TournamentAdminDTO;
import com.aerilon.turfclan.tournament.dto.TournamentCreateRequestDTO;
import com.aerilon.turfclan.tournament.dto.TournamentSummaryDTO;
import com.aerilon.turfclan.tournament.entity.TournamentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TournamentConverter {

    public TournamentSummaryDTO toSummary(TournamentEntity entity) {
        TournamentSummaryDTO summary = createBaseSummary(entity);
        summary.setAdmin(toAdmin(entity));
        return summary;
    }

    private TournamentSummaryDTO createBaseSummary(TournamentEntity entity) {
        TournamentSummaryDTO summary = new TournamentSummaryDTO();
        summary.setId(entity.getId() != null ? entity.getId().toString() : null);
        summary.setSportCategory(entity.getSportCategory());
        summary.setName(entity.getName());
        summary.setLocationName(entity.getLocationName());
        summary.setGeolocation(entity.getGeolocation());
        summary.setEntryFee(entity.getEntryFee());
        summary.setPrizePool(entity.getPrizePool());
        summary.setAbout(entity.getAbout());
        summary.setFormat(entity.getFormat());
        summary.setRegisterBy(entity.getRegisterBy() != null ? entity.getRegisterBy().toString() : null);
        summary.setWhyJoin(entity.getWhyJoin());
        summary.setRules(entity.getRules());
        return summary;
    }

    private TournamentAdminDTO toAdmin(TournamentEntity entity) {
        if (entity.getAdmin() == null) {
            return null;
        }

        TournamentAdminDTO admin = new TournamentAdminDTO();
        admin.setId(entity.getAdmin().getId() != null ? entity.getAdmin().getId().toString() : null);
        admin.setUserName(entity.getAdmin().getUserName());
        admin.setFirstName(entity.getAdmin().getFirstName());
        admin.setLastName(entity.getAdmin().getLastName());
        admin.setProfilePictureUrl(entity.getAdmin().getProfilePictureUrl());
        return admin;
    }

    public void applyCreateRequest(TournamentEntity entity,
                                   TournamentCreateRequestDTO request,
                                   String sportCategory) {
        entity.setSportCategory(sportCategory);
        entity.setName(request.getName() != null ? request.getName().trim() : null);
        entity.setLocationName(request.getLocationName() != null ? request.getLocationName().trim() : null);
        entity.setGeolocation(request.getGeolocation() != null ? request.getGeolocation().trim() : null);
        entity.setEntryFee(request.getEntryFee());
        entity.setPrizePool(request.getPrizePool());
        entity.setAbout(request.getAbout() != null ? request.getAbout().trim() : null);
        entity.setFormat(request.getFormat() != null ? request.getFormat().trim() : null);
        entity.setRegisterBy(request.getRegisterBy());
        entity.setWhyJoin(request.getWhyJoin() != null ? request.getWhyJoin().trim() : null);
        entity.setRules(request.getRules() != null ? request.getRules().trim() : null);
        entity.setStatus(RecordStatus.ACTIVE);
        entity.setCreatedAt(LocalDateTime.now());
    }
}
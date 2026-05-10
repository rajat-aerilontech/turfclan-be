package com.aerilon.turfclan.tournament.converter;

import com.aerilon.turfclan.enums.RecordStatus;
import com.aerilon.turfclan.tournament.dto.TournamentAdminDTO;
import com.aerilon.turfclan.tournament.dto.TournamentCreateRequestDTO;
import com.aerilon.turfclan.tournament.dto.TournamentSummaryDTO;
import com.aerilon.turfclan.tournament.entity.TournamentEntity;
import com.aerilon.turfclan.tournament.enums.TournamentStatus;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
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
        summary.setName(entity.getTournamentName());
        summary.setLocationName(entity.getLocationName());
//        Double lat = entity.get();
//        Double lon = entity.getLongitude();
//        if (lat != null && lon != null) {
//            // Coordinate order is (X, Y) which is (Longitude, Latitude)
//            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
//            Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
//            entity.setGeoLocation(point);
//        }
        summary.setEntryFee(entity.getEntryFee());
        summary.setPrizePool(entity.getPrizePool());
        summary.setAbout(entity.getAbout());
        summary.setFormat(entity.getFormat());
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
//        admin.setProfilePictureUrl(entity.getAdmin().getProfilePictureUrl());
        return admin;
    }

    public void applyCreateRequest(TournamentEntity entity,
                                   TournamentCreateRequestDTO request,
                                   String sportCategory) {
        entity.setSportCategory(sportCategory);
        entity.setTournamentName(request.getName() != null ? request.getName().trim() : null);
        entity.setLocationName(request.getLocationName() != null ? request.getLocationName().trim() : null);
//        entity.setGeolocation(request.getGeolocation() != null ? request.getGeolocation().trim() : null);
        entity.setEntryFee(request.getEntryFee());
        entity.setPrizePool(request.getPrizePool());
        entity.setAbout(request.getAbout() != null ? request.getAbout().trim() : null);
        entity.setFormat(request.getFormat() != null ? request.getFormat().trim() : null);
        entity.setWhyJoin(request.getWhyJoin() != null ? request.getWhyJoin().trim() : null);
        entity.setRules(request.getRules() != null ? request.getRules().trim() : null);
        entity.setTournamentStatus(TournamentStatus.REGISTRATION);
        entity.setCreatedAt(LocalDateTime.now());
    }
}
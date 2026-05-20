package com.aerilon.turfclan.converter;

import com.aerilon.turfclan.dto.BookingRequestDTO;
import com.aerilon.turfclan.dto.BookingResponseDTO;
import com.aerilon.turfclan.entity.BookingEntity;
import com.aerilon.turfclan.facility.dto.SubFacilityRequestDto;
import com.aerilon.turfclan.facility.entity.SubFacilityEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingConverter {
    public BookingEntity toEntity(BookingRequestDTO dto, SubFacilityEntity sport, UserEntity user) {
        BookingEntity entity = new BookingEntity();
        entity.setSport(sport);
        entity.setUser(user);
        entity.setBookingDate(dto.getBookingDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setFinalPrice(sport.getPricePerHour().doubleValue());
        entity.setIsPrimeTime(false);
        entity.setUserNote(dto.getUserNote());
        entity.setPlayerCount(dto.getPlayerCount());
        return entity;
    }

    public BookingResponseDTO toDto(BookingEntity entity) {
        return BookingResponseDTO.builder()
                .bookingId(entity.getId())
                .facilityName(entity.getSport().getFacility().getFacilityName())
                .subFacility(toSubFacilityDto(entity.getSport()))
                .sportType(entity.getSport().getSportType())
                .subType(entity.getSport().getSubType())
                .bookingDate(entity.getBookingDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getBookingStatus())
                .totalAmount(entity.getTotalAmount())
            .playerCount(entity.getPlayerCount())
                .userName(entity.getUser().getFirstName())
                .userNote(entity.getUserNote())
                .build();
    }

    private SubFacilityRequestDto toSubFacilityDto(SubFacilityEntity sport) {
        if (sport == null) {
            return null;
        }
        SubFacilityRequestDto dto = new SubFacilityRequestDto();
        dto.setSportType(sport.getSportType());
        dto.setSubType(sport.getSubType());
        dto.setNumberOfUnits(sport.getNumberOfUnits());
        dto.setMaxPlayersPerUnit(sport.getMaxPlayersPerUnit());
        dto.setCurrency(sport.getCurrency());
        dto.setPricePerHour(sport.getPricePerHour());
        dto.setPricePerSession(sport.getPricePerSession());
        dto.setOpenTime(sport.getOpenTime());
        dto.setCloseTime(sport.getCloseTime());
        dto.setSlotDurationMinutes(sport.getSlotDurationMinutes());
        dto.setBufferDuration(sport.getBufferDuration());
        dto.setPrimeTimeSurgePercentage(sport.getPrimeTimeSurgePercentage());
        dto.setPrimeTimeWindows(sport.getPrimeTimeWindows());
        dto.setAvailableDays(sport.getAvailableDays());
        dto.setLength(sport.getLength());
        dto.setWidth(sport.getWidth());
        dto.setSurfaceType(sport.getSurfaceType());
        dto.setAmenities(sport.getAmenities());
        return dto;
    }
}

package com.aerilon.turfclan.converter;

import com.aerilon.turfclan.dto.BookingRequestDTO;
import com.aerilon.turfclan.dto.BookingResponseDTO;
import com.aerilon.turfclan.entity.BookingEntity;
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
        return entity;
    }

    public BookingResponseDTO toDto(BookingEntity entity) {
        return BookingResponseDTO.builder()
                .bookingId(entity.getId())
                .facilityName(entity.getSport().getFacility().getFacilityName())
                .sportType(entity.getSport().getSportType())
                .subType(entity.getSport().getSubType())
                .bookingDate(entity.getBookingDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getBookingStatus())
                .totalAmount(entity.getTotalAmount())
                .userName(entity.getUser().getFirstName())
                .userNote(entity.getUserNote())
                .build();
    }
}

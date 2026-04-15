package com.aerilon.turfclan.tournament.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TournamentSummaryDTO {

    private String id;
    private String sportCategory;
    private String name;
    private String locationName;
    private String geolocation;
    private BigDecimal entryFee;
    private BigDecimal prizePool;
    private String about;
    private String format;
    private String registerBy;
    private String whyJoin;
    private String rules;
    private TournamentAdminDTO admin;
}
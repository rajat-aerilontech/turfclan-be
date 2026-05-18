package com.aerilon.turfclan.partner.service;

import com.aerilon.turfclan.partner.dto.DashboardDto;
import com.aerilon.turfclan.partner.dto.FacilityDto;

import java.util.List;

public interface PartnerService {
    DashboardDto  getFullDashboardData(String userId);
    List<FacilityDto> getFullFacilityData(String userId);
}

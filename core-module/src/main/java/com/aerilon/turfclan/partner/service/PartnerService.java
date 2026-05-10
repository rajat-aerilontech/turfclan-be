package com.aerilon.turfclan.partner.service;

import com.aerilon.turfclan.partner.dto.DashboardDto;

public interface PartnerService {
    DashboardDto  getFullDashboardData(String userId);
}

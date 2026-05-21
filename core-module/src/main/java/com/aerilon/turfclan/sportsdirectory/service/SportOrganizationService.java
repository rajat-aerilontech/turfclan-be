package com.aerilon.turfclan.sportsdirectory.service;

import com.aerilon.turfclan.pagination.CursorPageResponse;
import com.aerilon.turfclan.sportsdirectory.dto.OrganizationSummaryDto;
import com.aerilon.turfclan.sportsdirectory.dto.SportOrganizationDetailDto;
import com.aerilon.turfclan.sportsdirectory.dto.SportOrganizationUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.enums.OrganizationType;

import java.util.List;

public interface SportOrganizationService {
    CursorPageResponse<OrganizationSummaryDto> getOrganizationBySport(OrganizationType organizationType, String cursor, Integer limit);
    void createOrganization(SportOrganizationUpsertRequestDTO request);
    SportOrganizationDetailDto getOrganizationDetail(String organizationId);
    SportOrganizationDetailDto updateOrganization(String organizationId, SportOrganizationUpsertRequestDTO request);
    void deleteOrganization(String organizationId);
}

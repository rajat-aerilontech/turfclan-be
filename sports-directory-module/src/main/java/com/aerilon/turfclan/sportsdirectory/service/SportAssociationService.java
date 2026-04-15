package com.aerilon.turfclan.sportsdirectory.service;

import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationDetailDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationSummaryDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationUpsertRequestDTO;

import java.util.List;

public interface SportAssociationService {

    List<SportAssociationSummaryDTO> getAssociations();

    SportAssociationDetailDTO getAssociationDetail(String associationId);

    SportAssociationDetailDTO createAssociation(SportAssociationUpsertRequestDTO request);

    SportAssociationDetailDTO updateAssociation(String associationId, SportAssociationUpsertRequestDTO request);

    void deleteAssociation(String associationId);
}

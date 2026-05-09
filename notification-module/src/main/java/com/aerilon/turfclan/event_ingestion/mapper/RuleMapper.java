package com.aerilon.turfclan.event_ingestion.mapper;

import com.aerilon.turfclan.event_ingestion.dto.RuleDto;
import com.aerilon.turfclan.event_ingestion.dto.RuleSets;
import com.aerilon.turfclan.event_ingestion.entity.RuleEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RuleMapper {

    ObjectMapper objectMapper = new ObjectMapper();


    @Mapping(source="ruleSets", target = "ruleSets", qualifiedByName = "deserializeRuleSets")
    RuleDto toDto(RuleEntity ruleEntity);

    @Mapping(source="ruleSets", target = "ruleSets", qualifiedByName = "serializeRuleSets")
    RuleEntity toEntity(RuleDto ruleDto);

    @Named("deserializeRuleSets")
    default RuleSets deserializeRuleSets(String json) {
        try {
            return objectMapper.readValue(json, RuleSets.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize rule sets", e);
        }
    }

    @Named("serializeRuleSets")
    default String serializeRuleSets(RuleSets ruleSets) {
        try {
            return objectMapper.writeValueAsString(ruleSets);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize rule sets", e);
        }
    }
}

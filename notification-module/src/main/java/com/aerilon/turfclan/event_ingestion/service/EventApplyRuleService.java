package com.aerilon.turfclan.event_ingestion.service;

import com.aerilon.turfclan.event_ingestion.dto.EventDto;
import reactor.core.publisher.Mono;

public interface EventApplyRuleService {
    Mono<Void> applyRules(EventDto event);
}

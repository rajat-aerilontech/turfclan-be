package com.aerilon.turfclan.generate_notification.service;

import com.aerilon.turfclan.event_ingestion.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class EmailContentFactory {

    private final Map<EventType, EmailContentStrategy> strategyMap = new EnumMap<>(EventType.class);

    @Autowired
    public EmailContentFactory(List<EmailContentStrategy> strategies) {
        strategies.forEach(strategy -> strategyMap.put(strategy.getEventType(), strategy));
    }

    public EmailContentStrategy getStrategy(EventType type) { return strategyMap.get(type); }
}

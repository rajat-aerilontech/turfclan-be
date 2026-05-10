package com.aerilon.turfclan.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

public class JsonMapUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonMapUtil() {}

    public static JsonNode toJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to JsonNode", e);
        }
    }

    /**
     * Converts JSON string → Map<String, Object>
     */
    public static Map<String, Object> jsonToMap(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to Map", e);
        }
    }

    /**
     * Converts Map → JSON string
     */
    public static String mapToJson(Map<String, Object> map) {
        if (map == null) return null;
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Map to JSON", e);
        }
    }
}
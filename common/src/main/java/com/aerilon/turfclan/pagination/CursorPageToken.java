package com.aerilon.turfclan.pagination;

import java.time.LocalDateTime;
import java.util.UUID;

public record CursorPageToken(LocalDateTime createdAt, UUID id) {
}
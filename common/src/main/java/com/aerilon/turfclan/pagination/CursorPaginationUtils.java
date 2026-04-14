package com.aerilon.turfclan.pagination;

import com.aerilon.turfclan.exception.InvalidRequestException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class CursorPaginationUtils {

    public static final int DEFAULT_LIMIT = 10;
    public static final int MAX_LIMIT = 50;

    private CursorPaginationUtils() {
    }

    public static int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        if (limit < 1) {
            throw new InvalidRequestException("limit must be greater than 0");
        }
        if (limit > MAX_LIMIT) {
            throw new InvalidRequestException("limit must be less than or equal to " + MAX_LIMIT);
        }
        return limit;
    }

    public static CursorPageToken decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|", 2);
            if (parts.length != 2) {
                throw new InvalidRequestException("Invalid cursor");
            }

            long epochMillis = Long.parseLong(parts[0]);
            LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
            UUID id = UUID.fromString(parts[1]);
            return new CursorPageToken(createdAt, id);
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException("Invalid cursor");
        }
    }

    public static String encodeCursor(LocalDateTime createdAt, UUID id) {
        if (createdAt == null || id == null) {
            return null;
        }

        String raw = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli() + "|" + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static <E, T> CursorPageResponse<T> buildResponse(List<E> fetchedEntities,
                                                             int limit,
                                                             Function<E, T> mapper,
                                                             Function<E, LocalDateTime> createdAtExtractor,
                                                             Function<E, UUID> idExtractor) {
        boolean hasNext = fetchedEntities.size() > limit;
        int visibleCount = hasNext ? limit : fetchedEntities.size();

        List<T> items = new ArrayList<>(visibleCount);
        for (int index = 0; index < visibleCount; index++) {
            items.add(mapper.apply(fetchedEntities.get(index)));
        }

        String nextCursor = null;
        if (hasNext && visibleCount > 0) {
            E lastVisible = fetchedEntities.get(visibleCount - 1);
            nextCursor = encodeCursor(createdAtExtractor.apply(lastVisible), idExtractor.apply(lastVisible));
        }

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }
}
package com.aerilon.turfclan.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CursorPageResponse<T> {

    private List<T> items;
    private String nextCursor;
    private boolean hasNext;
}
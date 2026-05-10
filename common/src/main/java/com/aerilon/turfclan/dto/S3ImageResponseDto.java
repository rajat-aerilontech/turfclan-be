package com.aerilon.turfclan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class S3ImageResponseDto {
    private String key;
    private String url;
}

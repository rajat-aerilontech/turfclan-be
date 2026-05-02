package com.aerilon.turfclan.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebResponseDto {
    private String status;
    private String message;
}

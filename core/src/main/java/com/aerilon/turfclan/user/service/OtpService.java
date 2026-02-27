package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.OtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;

public interface OtpService {
    OtpResponseDTO requestOtp(OtpRequestDTO request);
}

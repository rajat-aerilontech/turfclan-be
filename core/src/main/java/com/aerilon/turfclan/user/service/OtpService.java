package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.AuthResponseDTO;
import com.aerilon.turfclan.user.dto.OtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;
import com.aerilon.turfclan.user.dto.OtpVerifyRequestDTO;

public interface OtpService {
    OtpResponseDTO requestOtp(OtpRequestDTO request);
    AuthResponseDTO verifyOtp(OtpVerifyRequestDTO request);
}

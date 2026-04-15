package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.AuthResponseDTO;
import com.aerilon.turfclan.user.dto.SendOtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;
import com.aerilon.turfclan.user.dto.OtpVerifyRequestDTO;

public interface OtpService {
    OtpResponseDTO requestOtp(SendOtpRequestDTO request);
    AuthResponseDTO verifyOtp(OtpVerifyRequestDTO request);
}

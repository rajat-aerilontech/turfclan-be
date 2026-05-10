package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.AuthResponseDTO;
import com.aerilon.turfclan.user.dto.SendOtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;
import com.aerilon.turfclan.user.dto.OtpVerifyRequestDTO;

public interface OtpService {
    OtpResponseDTO requestOtp(SendOtpRequestDTO request, String sourceApp);
    AuthResponseDTO verifyOtp(OtpVerifyRequestDTO request, String sourceApp);
}

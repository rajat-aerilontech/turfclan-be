package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.OtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;
import com.aerilon.turfclan.user.dto.OtpVerifyRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;

public interface OtpService {
    OtpResponseDTO requestOtp(OtpRequestDTO request);
    UserDTO verifyOtp(OtpVerifyRequestDTO request);
}

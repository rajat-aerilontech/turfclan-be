package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.SignupRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;

public interface UserService {
    UserDTO getUserByEmail(String emailId);
    UserDTO signup(String userId, SignupRequestDTO request);
}

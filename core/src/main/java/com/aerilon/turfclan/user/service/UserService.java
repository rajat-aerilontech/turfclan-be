package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.SignupRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserDTO getUserByEmail(String emailId);
    UserDTO signup(String userId, SignupRequestDTO request, MultipartFile profilePic);
}

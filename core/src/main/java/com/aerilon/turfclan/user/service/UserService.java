package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.UserDTO;

public interface UserService {
    UserDTO getUserByEmail(String emailId);
}

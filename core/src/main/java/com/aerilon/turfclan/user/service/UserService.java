package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.UserDTO;

import java.util.Optional;

public interface UserService {
    Optional<UserDTO> getUserByEmail(String emailId);
}

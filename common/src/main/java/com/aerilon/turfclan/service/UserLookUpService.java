package com.aerilon.turfclan.service;

import com.aerilon.turfclan.dto.UserDto;

import java.util.List;

public interface UserLookUpService {
    List<UserDto> findByRoles(List<String> roles);
}

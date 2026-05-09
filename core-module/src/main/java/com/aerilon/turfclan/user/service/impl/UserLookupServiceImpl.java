package com.aerilon.turfclan.user.service.impl;

import com.aerilon.turfclan.dto.UserDto;
import com.aerilon.turfclan.service.UserLookUpService;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLookupServiceImpl implements UserLookUpService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findByRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            log.warn("findByRoles called with empty/null roles");
            return Collections.emptyList();
        }
        log.info("Finding users with roles: {}", roles);
        List<UserEntity> users = userRepository.findByUserRoleIn(roles);
        if (users == null || users.isEmpty()) {
            log.warn("No users found for roles: {}", roles);
            return Collections.emptyList();
        }
        return users.stream()
                .map(this::mapToDto)
                .toList();
    }

    private UserDto mapToDto(UserEntity user) {
        UserDto dto = new UserDto();
        dto.setUserEmail(user.getUserEmail());
        dto.setUserRole(user.getUserRole().name());
        dto.setCountryIsoCode(user.getCountryIsoCode());
        dto.setLanguageIsoCode(user.getLanguageIsoCode());
        return dto;
    }
}

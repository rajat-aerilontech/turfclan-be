package com.aerilon.turfclan.user.service.impl;

import com.aerilon.turfclan.user.UserDTO;
import com.aerilon.turfclan.user.converter.UserEntityToUserDTOConverter;
import com.aerilon.turfclan.user.repository.UserRepository;
import com.aerilon.turfclan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEntityToUserDTOConverter userConverter;

    @Override
    public Optional<UserDTO> getUserByEmail(String emailId) {
        return userRepository.findByUserEmail(emailId)
                             .map(userConverter::convert);
    }
}

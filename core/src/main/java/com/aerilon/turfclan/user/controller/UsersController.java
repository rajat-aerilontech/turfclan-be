package com.aerilon.turfclan.user.controller;

import com.aerilon.turfclan.user.UserDTO;
import com.aerilon.turfclan.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping("/{emailId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("emailId") String emailId) {
        return userService.getUserByEmail(emailId)
                          .map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }
}


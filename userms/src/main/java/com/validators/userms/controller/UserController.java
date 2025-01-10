package com.validators.userms.controller;

import com.validators.userms.dto.UserDTO;
import com.validators.userms.exception.CustomException;
import com.validators.userms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) throws CustomException {
        return new ResponseEntity<>(userService.registerUser(userDTO), HttpStatus.OK);
    }

    @PostMapping("/getuser")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) throws CustomException {
        return new ResponseEntity<>(userService.getUserDTO(userDTO), HttpStatus.OK);
    }

}

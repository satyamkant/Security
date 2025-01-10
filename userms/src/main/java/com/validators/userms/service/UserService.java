package com.validators.userms.service;

import com.validators.userms.dto.UserDTO;
import com.validators.userms.exception.CustomException;

public interface UserService {
    UserDTO registerUser(UserDTO userDto) throws CustomException;
    UserDTO getUserDTO(UserDTO userDto) throws CustomException;
}

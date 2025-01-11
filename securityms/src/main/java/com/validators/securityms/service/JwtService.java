package com.validators.securityms.service;

import com.validators.securityms.dto.UserDTO;
import com.validators.securityms.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {
    UserDTO verifyUser(UserDTO userDto) throws CustomException;

    String getJwtFromHeader(HttpServletRequest request);

    String generateTokenFromUsername(UserDTO userDto);

    String getUserNameFromJwtToken(String token);

    boolean validateJwtToken(String authToken) throws CustomException;

    String getClaimFromJwtToken(String token);
}

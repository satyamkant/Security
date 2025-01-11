package com.validators.securityms.controller;

import com.validators.securityms.dto.ResponseDTO;
import com.validators.securityms.dto.UserDTO;
import com.validators.securityms.exception.CustomException;
import com.validators.securityms.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/security")
public class JwtController {

    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final String USER_DB_URL_REGISTER_USER;
    private final long EXPIRATION_TIME;
    private final String DOMAIN;

    @Autowired
    public JwtController(RestTemplate restTemplate, JwtService jwtService,
                         @Value("${USER_DB_URL_REGISTER_USER}") String userDbUrlRegisterUser,
                         @Value("${EXPIRATION_TIME}") long expirationTime,
                         @Value("${DOMAIN}") String domain) {
        this.restTemplate = restTemplate;
        this.jwtService = jwtService;
        USER_DB_URL_REGISTER_USER = userDbUrlRegisterUser;
        EXPIRATION_TIME = expirationTime;
        DOMAIN = domain;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDto,
                                   HttpServletResponse response) throws CustomException {

        // Create a cookie with the JWT token
        String jwtToken = jwtService.verifyUser(userDto).getJwtToken();
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);  // Make sure it's HttpOnly
        cookie.setSecure(true);    // Make sure it's Secure (use in production)
        cookie.setPath("/");       // The path where the cookie is valid
        cookie.setMaxAge((int) TimeUnit.HOURS.toSeconds(EXPIRATION_TIME)); // Set cookie expiration time (in seconds)
        cookie.setDomain(DOMAIN);

        response.addCookie(cookie); // Add cookie to response



        userDto.setPasswordHash(null);
        userDto.setJwtToken(null);
        userDto.setName(jwtService.getClaimFromJwtToken(jwtToken));

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setData(userDto);
        responseDTO.setMessage("Successfully logged in");
        responseDTO.setStatus(HttpStatus.OK.toString());

        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Create a cookie with the JWT token
        String jwtToken = "";
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);  // Make sure it's HttpOnly
        cookie.setSecure(true);    // Make sure it's Secure (use in production)
        cookie.setPath("/");       // The path where the cookie is valid
        cookie.setMaxAge(0); // Set cookie expiration time (in seconds)
        cookie.setDomain(DOMAIN);
        response.addCookie(cookie); // Add cookie to response
        ResponseDTO responseDTO = new ResponseDTO();

        responseDTO.setMessage("Successfully Logged out");
        responseDTO.setStatus(HttpStatus.OK.toString());

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated UserDTO userDto) throws CustomException {

        // Set HTTP headers (if needed, e.g., Authorization)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<UserDTO> requestEntity = new HttpEntity<>(userDto, headers);

        try {
            // Make the REST call

            ResponseDTO responseDTO = new ResponseDTO();

            restTemplate.exchange(
                    USER_DB_URL_REGISTER_USER,
                    HttpMethod.POST,
                    requestEntity,
                    UserDTO.class
            );

            userDto.setPasswordHash(null);

            responseDTO.setData(userDto);
            responseDTO.setMessage("User registered successfully");
            responseDTO.setStatus(HttpStatus.OK.toString());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new CustomException("Http exception",e);
        } catch (ResourceAccessException e) {
            throw new CustomException("Server not available",e);
        } catch (Exception e) {
            throw new CustomException("Internal server error",e);
        }
    }

    @GetMapping("/isAuthenticated")
    public ResponseEntity<?> isAuthenticated(HttpServletRequest request) {
        String jwt = jwtService.getJwtFromHeader(request);
        String userName = jwtService.getUserNameFromJwtToken(jwt);




        UserDTO userDto = new UserDTO();
        userDto.setEmail(userName);
        userDto.setName(jwtService.getClaimFromJwtToken(jwt));

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Authenticated user");
        responseDTO.setData(userDto);

        return new  ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}

package com.validators.securityms.service.impl;

import com.validators.securityms.dto.UserDTO;
import com.validators.securityms.dto.UserDetails;
import com.validators.securityms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceImpl implements UserService {

    private final RestTemplate restTemplate;
    private final String USER_DB_URL_GET_USER;

    @Autowired
    public UserServiceImpl(RestTemplate restTemplate,@Value("${USER_DB_URL_GET_USER}") String userDbUrlGetUser) {
        this.restTemplate = restTemplate;
        USER_DB_URL_GET_USER = userDbUrlGetUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Set HTTP headers (if needed, e.g., Authorization)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");



        // Create the request entity with headers and body
        UserDTO userDto = new UserDTO();
        userDto.setEmail(username);

        HttpEntity<UserDTO> requestEntity = new HttpEntity<>(userDto, headers);

        try {
            // Make the REST call
            ResponseEntity<UserDTO> responseEntity = restTemplate.exchange(
                    USER_DB_URL_GET_USER,
                    HttpMethod.POST,
                    requestEntity,
                    UserDTO.class
            );

            userDto = responseEntity.getBody();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new UserDetails(userDto);
    }
}

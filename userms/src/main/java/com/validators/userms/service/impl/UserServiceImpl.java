package com.validators.userms.service.impl;


import com.validators.userms.dto.UserDTO;
import com.validators.userms.entity.User;
import com.validators.userms.exception.CustomException;
import com.validators.userms.repository.UserRepository;
import com.validators.userms.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDTO registerUser(UserDTO userDto) throws CustomException {
        if(userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new CustomException("User Already Exists");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setBio(userDto.getBio());
        user.setPasswordHash(bCryptPasswordEncoder.encode(userDto.getPasswordHash()));
        user.setProfilePicturePath(userDto.getProfilePicturePath());
        userRepository.save(user);

        userDto.setPasswordHash("");
        return userDto;
    }

    @Override
    public UserDTO getUserDTO(UserDTO userDto) throws CustomException {
        Optional<User> userOptional = userRepository.findByEmail(userDto.getEmail());
        if(userOptional.isEmpty()) {
            throw new CustomException("User Not Found");
        }

        User user = userOptional.get();

        UserDTO response = new UserDTO();
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setPasswordHash(user.getPasswordHash());
        response.setName(user.getName());
        response.setBio(user.getBio());
        response.setProfilePicturePath(user.getProfilePicturePath());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setIsActive(user.getIsActive());
        response.setAccountLocked(user.getAccountLocked());

        return response;
    }

}

package com.validators.userms.dto;


import com.validators.userms.enums.UserRole;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {

    private Long userId;
    private String name;
    private String email;
    private String passwordHash;
    private String bio;
    private UserRole role;
    private Boolean isActive;
    private Boolean emailVerified;
    private String profilePicturePath;
    private LocalDateTime lastLogin;
    private Integer loginAttempts;
    private Boolean accountLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String jwtToken;
}

package com.validators.securityms.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO implements Serializable {
    private String message;
    private String status;
    private String error;
    private Object data;
}

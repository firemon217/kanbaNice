package com.kanbanice.backend.dto;

import com.kanbanice.backend.entity.type.UserType;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
    private String username;
    private String password;
    private String name;
    private UserType userType;
}

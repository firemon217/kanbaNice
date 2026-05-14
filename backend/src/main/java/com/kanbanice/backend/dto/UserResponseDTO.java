package com.kanbanice.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.kanbanice.backend.entity.type.UserType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private  Long id;
    private  String name;
    private  String email;
    private UserType userType;
}

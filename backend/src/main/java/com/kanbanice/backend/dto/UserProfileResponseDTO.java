package com.kanbanice.backend.dto;

import com.kanbanice.backend.entity.type.AuthProviderType;
import com.kanbanice.backend.entity.type.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.kanbanice.backend.entity.type.UserType;

import java.util.Set;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private UserType userType;
    private AuthProviderType providerType;
}

package com.kanbanice.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDto {

    private String name;

    private String username;

    @Email(message = "Invalid email")
    private String email;

    private String newPassword;

    private String confirmPassword;
}

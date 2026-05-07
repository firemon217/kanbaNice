package com.kanbanice.backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
    private String username;
    private String password;
    private String name;
}

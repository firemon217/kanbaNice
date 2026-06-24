package com.kanbanice.backend.controller;

import com.kanbanice.backend.dto.LoginRequestDTO;
import com.kanbanice.backend.dto.LoginResponseDTO;
import com.kanbanice.backend.dto.SignupRequestDTO;
import com.kanbanice.backend.dto.SignupResponseDTO;
import com.kanbanice.backend.security.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
   public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));
   }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> login(@RequestBody SignupRequestDTO SignUprequest){
        return ResponseEntity.ok(authService.SignUp(SignUprequest));
    }
}

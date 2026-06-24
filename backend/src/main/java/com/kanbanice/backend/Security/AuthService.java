package com.kanbanice.backend.security;

import com.kanbanice.backend.entity.type.AuthProviderType;
import com.kanbanice.backend.repository.UserRepository;
import com.kanbanice.backend.dto.LoginRequestDTO;
import com.kanbanice.backend.dto.LoginResponseDTO;
import com.kanbanice.backend.dto.SignupRequestDTO;
import com.kanbanice.backend.dto.SignupResponseDTO;
import com.kanbanice.backend.entity.User;

import com.kanbanice.backend.entity.type.RoleType;
import com.kanbanice.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.kanbanice.backend.entity.type.UserType;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public LoginResponseDTO login(LoginRequestDTO request) {
     Authentication authentication;
        try {
             authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        User user = (User)authentication.getPrincipal();
        String token=authUtil.generateAccessToken(user);
        return new LoginResponseDTO(token,user.getId());
    }

    public User SignUpInternal( SignupRequestDTO signupRequestDTO,String providerId, AuthProviderType authProviderType){
        User user=userRepository.findByUsername(signupRequestDTO.getUsername()).orElse(null);
        if(user!=null){
            throw new IllegalArgumentException("User already exists");
        }

        if (userRepository.existsByUsername(signupRequestDTO.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (signupRequestDTO.getEmail() == null || signupRequestDTO.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userRepository.existsByEmail(signupRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }

         user = User.builder()
                .name(signupRequestDTO.getName())
                .username(signupRequestDTO.getUsername())
                .email(signupRequestDTO.getEmail())
                .providerId(providerId)
                .providerType(authProviderType)
                .roles(Set.of(RoleType.USER))
                .userType(signupRequestDTO.getUserType())
                .build();


        if(authProviderType == AuthProviderType.EMAIL){
              user.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));
        } else {
            user.setPassword(UUID.randomUUID().toString());
        }
       userRepository.save(user);
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        } catch (Exception e) {
            // Игнорируем ошибки отправки email при регистрации
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
        return user;
    }
    public SignupResponseDTO SignUp(SignupRequestDTO signupRequestDTO) {
       User user=SignUpInternal(signupRequestDTO,null,AuthProviderType.EMAIL);

      return new SignupResponseDTO(user.getId(),user.getUsername());
    }

    public ResponseEntity<LoginResponseDTO> HandleOAuth2LoginRequest(String registrationId, OAuth2User oAuth2User){

        AuthProviderType providerType=authUtil.FindProviderType(registrationId);
        String providerId=authUtil.FindProviderId(oAuth2User,registrationId);

        User user=userRepository.findByProviderTypeAndProviderId(providerType, providerId).orElse(null);

        String email=oAuth2User.getAttribute("email");
        String name=oAuth2User.getAttribute("name");

        if (user == null && email != null) {
            user = userRepository.findByEmail(email).orElse(null); 
        }

        if (user == null) {
            String userName = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            user = SignUpInternal(new SignupRequestDTO(userName, email, null, name, UserType.WORKER), providerId, providerType);
        } else {
            if (user.getProviderId() == null || !user.getProviderId().equals(providerId)) {
                user.setProviderId(providerId);
                user.setProviderType(providerType);
                userRepository.save(user);
            }
        }

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(authUtil.generateAccessToken(user), user.getId());
        return ResponseEntity.ok(loginResponseDTO);
    }

}

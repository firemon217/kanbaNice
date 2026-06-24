package com.kanbanice.backend.security;

import com.kanbanice.backend.repository.UserRepository;
import com.kanbanice.backend.dto.LoginRequestDTO;
import com.kanbanice.backend.dto.LoginResponseDTO;
import com.kanbanice.backend.dto.SignupRequestDTO;
import com.kanbanice.backend.dto.SignupResponseDTO;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.type.AuthProviderType;
import com.kanbanice.backend.entity.type.UserType;
import com.kanbanice.backend.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — unit tests")
class AuthServiceTest {

    @Mock AuthUtil authUtil;
    @Mock AuthenticationManager authenticationManager;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock EmailService emailService;
    @InjectMocks AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).name("Alice").username("alice")
                .email("alice@test.com").password("hashedPwd")
                .providerType(AuthProviderType.EMAIL)
                .userType(UserType.WORKER)
                .roles(Set.of()).build();
    }

    // ─── login ────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: возвращает токен при корректных данных")
    void login_success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(authUtil.generateAccessToken(user)).thenReturn("jwt-token");

        LoginResponseDTO result = authService.login(new LoginRequestDTO("alice", "pass"));

        assertThat(result.getAccessToken()).isEqualTo("jwt-token");
        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("login: пробрасывает BadCredentialsException при неверных данных")
    void login_badCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(new LoginRequestDTO("alice", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ─── SignUp ───────────────────────────────────────────────────────

    @Test
    @DisplayName("SignUp: успешная регистрация нового пользователя")
    void signUp_success() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        SignupRequestDTO dto = new SignupRequestDTO("newuser", "new@test.com", "password123", "New User", UserType.WORKER);
        SignupResponseDTO result = authService.SignUp(dto);

        assertThat(result.getUsername()).isEqualTo("newuser");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("SignUp: IllegalArgumentException если username занят")
    void signUp_usernameTaken() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        SignupRequestDTO dto = new SignupRequestDTO("alice", "new@test.com", "pass", "Name", UserType.WORKER);

        assertThatThrownBy(() -> authService.SignUp(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("SignUp: IllegalArgumentException если email пустой")
    void signUp_emailBlank() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        SignupRequestDTO dto = new SignupRequestDTO("newuser", "", "pass", "Name", UserType.WORKER);

        assertThatThrownBy(() -> authService.SignUp(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    @DisplayName("SignUp: IllegalArgumentException если email занят")
    void signUp_emailTaken() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        SignupRequestDTO dto = new SignupRequestDTO("newuser", "alice@test.com", "pass", "Name", UserType.WORKER);

        assertThatThrownBy(() -> authService.SignUp(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already taken");
    }

    @Test
    @DisplayName("SignUp: email-ошибка не прерывает регистрацию")
    void signUp_emailSendFails_stillRegisters() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(5L);
            return u;
        });
        doThrow(new RuntimeException("SMTP error")).when(emailService)
                .sendWelcomeEmail(anyString(), anyString());

        SignupRequestDTO dto = new SignupRequestDTO("newuser", "new@test.com", "pass", "Name", UserType.WORKER);
        SignupResponseDTO result = authService.SignUp(dto);

        assertThat(result.getUsername()).isEqualTo("newuser");
    }
}

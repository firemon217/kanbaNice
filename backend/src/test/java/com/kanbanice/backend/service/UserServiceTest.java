package com.kanbanice.backend.service;

import com.kanbanice.backend.Repository.UserRepository;
import com.kanbanice.backend.dto.ChangePasswordDto;
import com.kanbanice.backend.dto.UpdateProfileDto;
import com.kanbanice.backend.dto.UserProfileResponseDTO;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.type.AuthProviderType;
import com.kanbanice.backend.entity.type.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — unit tests")
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock EmailService emailService;
    @InjectMocks UserService userService;

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

    // ─── getProfile ───────────────────────────────────────────────────

    @Test
    @DisplayName("getProfile: возвращает DTO пользователя")
    void getProfile_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileResponseDTO dto = userService.getProfile(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("alice");
        assertThat(dto.getEmail()).isEqualTo("alice@test.com");
    }

    @Test
    @DisplayName("getProfile: IllegalArgumentException при отсутствии пользователя")
    void getProfile_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ─── updateProfile ────────────────────────────────────────────────

    @Test
    @DisplayName("updateProfile: меняет имя пользователя")
    void updateProfile_changeName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileDto dto = new UpdateProfileDto();
        dto.setName("Bob");

        UserProfileResponseDTO result = userService.updateProfile(1L, dto);
        assertThat(result.getName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("updateProfile: меняет username если не занят")
    void updateProfile_changeUsername_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newname")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileDto dto = new UpdateProfileDto();
        dto.setUsername("newname");

        UserProfileResponseDTO result = userService.updateProfile(1L, dto);
        assertThat(result.getUsername()).isEqualTo("newname");
    }

    @Test
    @DisplayName("updateProfile: RuntimeException если username занят")
    void updateProfile_usernameTaken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        UpdateProfileDto dto = new UpdateProfileDto();
        dto.setUsername("taken");

        assertThatThrownBy(() -> userService.updateProfile(1L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    @DisplayName("updateProfile: RuntimeException если пароли не совпадают")
    void updateProfile_passwordMismatch() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateProfileDto dto = new UpdateProfileDto();
        dto.setNewPassword("newpass1");
        dto.setConfirmPassword("newpass2");

        assertThatThrownBy(() -> userService.updateProfile(1L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("do not match");
    }

    @Test
    @DisplayName("updateProfile: RuntimeException если пароль слишком короткий")
    void updateProfile_passwordTooShort() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateProfileDto dto = new UpdateProfileDto();
        dto.setNewPassword("abc");
        dto.setConfirmPassword("abc");

        assertThatThrownBy(() -> userService.updateProfile(1L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("at least 6");
    }

    @Test
    @DisplayName("updateProfile: успешно меняет пароль")
    void updateProfile_changePassword_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass1")).thenReturn("newHashedPass");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileDto dto = new UpdateProfileDto();
        dto.setNewPassword("newPass1");
        dto.setConfirmPassword("newPass1");

        userService.updateProfile(1L, dto);

        verify(passwordEncoder).encode("newPass1");
    }

    // ─── forgotPassword ───────────────────────────────────────────────

    @Test
    @DisplayName("forgotPassword: отправляет письмо и сохраняет токен")
    void forgotPassword_success() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        String result = userService.forgotPassword("alice@test.com");

        assertThat(result).contains("alice@test.com");
        assertThat(user.getResetToken()).isNotNull();
        assertThat(user.getResetTokenExpiry()).isAfter(LocalDateTime.now());
        verify(emailService).sendPasswordResetEmail(eq("alice@test.com"), eq("Alice"), anyString());
    }

    @Test
    @DisplayName("forgotPassword: IllegalArgumentException если email не найден")
    void forgotPassword_emailNotFound() {
        when(userRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.forgotPassword("nobody@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No account found");
    }

    @Test
    @DisplayName("forgotPassword: IllegalStateException для не-EMAIL провайдера")
    void forgotPassword_googleUser() {
        user.setProviderType(AuthProviderType.GOOGLE);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.forgotPassword("alice@test.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("GOOGLE");
    }

    @Test
    @DisplayName("forgotPassword: IllegalStateException при rate limit (5 минут)")
    void forgotPassword_rateLimited() {
        user.setLastPasswordResetRequest(LocalDateTime.now().minusMinutes(2));
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.forgotPassword("alice@test.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("wait 5 minutes");
    }

    // ─── resetPassword ────────────────────────────────────────────────

    @Test
    @DisplayName("resetPassword: успешно сбрасывает пароль")
    void resetPassword_success() {
        user.setResetToken("valid-token");
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        when(userRepository.findByResetToken("valid-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("newPass1", "hashedPwd")).thenReturn(false);
        when(passwordEncoder.encode("newPass1")).thenReturn("newHash");
        when(userRepository.save(any())).thenReturn(user);

        String result = userService.resetPassword("valid-token", "newPass1");

        assertThat(result).contains("successful");
        assertThat(user.getResetToken()).isNull();
        verify(emailService).sendPasswordChangedNotificationEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("resetPassword: IllegalArgumentException при недействительном токене")
    void resetPassword_invalidToken() {
        when(userRepository.findByResetToken("bad-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resetPassword("bad-token", "newPass1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid");
    }

    @Test
    @DisplayName("resetPassword: IllegalStateException при истёкшем токене")
    void resetPassword_expiredToken() {
        user.setResetToken("expired-token");
        user.setResetTokenExpiry(LocalDateTime.now().minusHours(1));
        when(userRepository.findByResetToken("expired-token")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        assertThatThrownBy(() -> userService.resetPassword("expired-token", "newPass1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("resetPassword: IllegalArgumentException если новый пароль совпадает со старым")
    void resetPassword_samePassword() {
        user.setResetToken("valid-token");
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        when(userRepository.findByResetToken("valid-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("samePass", "hashedPwd")).thenReturn(true);

        assertThatThrownBy(() -> userService.resetPassword("valid-token", "samePass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different");
    }

    @Test
    @DisplayName("resetPassword: IllegalArgumentException если пароль слишком короткий")
    void resetPassword_tooShort() {
        user.setResetToken("valid-token");
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        when(userRepository.findByResetToken("valid-token")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.resetPassword("valid-token", "abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 6");
    }

    // ─── deleteAccount ────────────────────────────────────────────────

    @Test
    @DisplayName("deleteAccount: успешно удаляет аккаунт")
    void deleteAccount_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String result = userService.deleteAccount(1L);

        assertThat(result).contains("deleted");
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteAccount: IllegalArgumentException если пользователь не найден")
    void deleteAccount_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteAccount(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ─── changePassword ───────────────────────────────────────────────

    @Test
    @DisplayName("changePassword: успешно меняет пароль")
    void changePassword_success() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setCurrentPassword("oldPass");
        dto.setNewPassword("newPass123");
        dto.setConfirmPassword("newPass123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "hashedPwd")).thenReturn(true);
        when(passwordEncoder.matches("newPass123", "hashedPwd")).thenReturn(false);
        when(passwordEncoder.encode("newPass123")).thenReturn("newHash");
        when(userRepository.save(any())).thenReturn(user);

        String result = userService.changePassword(1L, dto);

        assertThat(result).contains("successfully");
        verify(emailService).sendPasswordChangedNotificationEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("changePassword: IllegalArgumentException если текущий пароль неверен")
    void changePassword_wrongCurrent() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setCurrentPassword("wrong");
        dto.setNewPassword("newPass123");
        dto.setConfirmPassword("newPass123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashedPwd")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("incorrect");
    }
}

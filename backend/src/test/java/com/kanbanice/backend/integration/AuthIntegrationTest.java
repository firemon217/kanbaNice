package com.kanbanice.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanbanice.backend.dto.LoginRequestDTO;
import com.kanbanice.backend.dto.SignupRequestDTO;
import com.kanbanice.backend.entity.type.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Auth — интеграционные тесты (H2)")
class AuthIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean com.kanbanice.backend.service.EmailService emailService;

    // ─── signup ───────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /auth/signup: успешная регистрация → 200 + username в ответе")
    void signup_success() throws Exception {
        SignupRequestDTO dto = new SignupRequestDTO(
                "integrationuser", "integration@test.com", "password123", "Integration User", UserType.WORKER
        );

        MvcResult result = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("integrationuser");
    }

    @Test
    @DisplayName("POST /auth/signup: повторная регистрация с тем же username → 400/500")
    void signup_duplicateUsername() throws Exception {
        SignupRequestDTO dto = new SignupRequestDTO(
                "dupuser", "dup@test.com", "password123", "Dup User", UserType.WORKER
        );

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        SignupRequestDTO dup = new SignupRequestDTO(
                "dupuser", "other@test.com", "password123", "Other", UserType.WORKER
        );

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dup)))
                .andExpect(status().is4xxClientError());
    }

    // ─── login ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /auth/login: успешный вход → 200 + JWT токен")
    void login_success() throws Exception {
        // Сначала регистрация
        SignupRequestDTO signup = new SignupRequestDTO(
                "loginuser", "login@test.com", "securepass", "Login User", UserType.WORKER
        );
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isOk());

        // Затем логин
        LoginRequestDTO login = new LoginRequestDTO("loginuser", "securepass");
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("accessToken");
    }

    @Test
    @DisplayName("POST /auth/login: неверный пароль → 5xx (BadCredentials)")
    void login_wrongPassword() throws Exception {
        SignupRequestDTO signup = new SignupRequestDTO(
                "wrongpassuser", "wrongpass@test.com", "correctpass", "User", UserType.WORKER
        );
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isOk());

        LoginRequestDTO login = new LoginRequestDTO("wrongpassuser", "wrongpass");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /auth/login: несуществующий пользователь → 5xx")
    void login_unknownUser() throws Exception {
        LoginRequestDTO login = new LoginRequestDTO("nobody", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().is4xxClientError());
    }
}

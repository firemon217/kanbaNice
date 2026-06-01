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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("UserProfile — интеграционные тесты (H2)")
class UserProfileIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean com.kanbanice.backend.service.EmailService emailService;

    private String registerAndLogin(String username, String email, String password) throws Exception {
        SignupRequestDTO signup = new SignupRequestDTO(
                username, email, password, "Test User", UserType.WORKER
        );
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isOk());

        LoginRequestDTO login = new LoginRequestDTO(username, password);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String body = loginResult.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("accessToken").asText();
    }

    @Test
    @DisplayName("GET /api/users/profile: авторизованный пользователь получает профиль")
    void getProfile_authenticated() throws Exception {
        String token = registerAndLogin("profileuser", "profile@test.com", "pass1234");

        MvcResult result = mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("profileuser");
        assertThat(body).contains("profile@test.com");
    }

    @Test
    @DisplayName("GET /api/users/profile: без токена → 403 или 401")
    void getProfile_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().is4xxClientError());
    }
}

package com.kanbanice.backend.security;

import com.kanbanice.backend.Security.AuthUtil;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.type.AuthProviderType;
import com.kanbanice.backend.entity.type.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AuthUtil — unit tests")
class AuthUtilTest {

    private AuthUtil authUtil;
    private User user;

    @BeforeEach
    void setUp() {
        authUtil = new AuthUtil();
        ReflectionTestUtils.setField(authUtil, "jwtSecretKey",
                "TestSecretKeyThatIsAtLeast32CharsLong!!!");

        user = User.builder()
                .id(1L).name("Alice").username("alice")
                .email("alice@test.com").password("hash")
                .providerType(AuthProviderType.EMAIL)
                .userType(UserType.WORKER)
                .roles(Set.of()).build();
    }

    @Test
    @DisplayName("generateAccessToken: возвращает непустую строку")
    void generateAccessToken_returnsToken() {
        String token = authUtil.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // JWT: header.payload.signature
    }

    @Test
    @DisplayName("findUsernameFromToken: возвращает username из валидного токена")
    void findUsernameFromToken_validToken() {
        String token = authUtil.generateAccessToken(user);

        String username = authUtil.findUsernameFromToken(token);

        assertThat(username).isEqualTo("alice");
    }

    @Test
    @DisplayName("findUsernameFromToken: возвращает null для невалидного токена")
    void findUsernameFromToken_invalidToken() {
        String result = authUtil.findUsernameFromToken("not.a.valid.token");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("findUsernameFromToken: возвращает null для пустой строки")
    void findUsernameFromToken_emptyString() {
        String result = authUtil.findUsernameFromToken("");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("FindProviderType: GOOGLE для 'google'")
    void findProviderType_google() {
        assertThat(authUtil.FindProviderType("google"))
                .isEqualTo(AuthProviderType.GOOGLE);
    }

    @Test
    @DisplayName("FindProviderType: IllegalArgumentException для неизвестного провайдера")
    void findProviderType_unknown() {
        assertThatThrownBy(() -> authUtil.FindProviderType("facebook"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

package com.gui.authAPI.Service;

import com.gui.authAPI.Controller.dto.LoginRequest;
import com.gui.authAPI.Controller.dto.LoginResponse;
import com.gui.authAPI.Entity.User;
import com.gui.authAPI.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_requestNulo_lancaBadCredentials() {
        assertThrows(BadCredentialsException.class, () -> authService.login(null));
    }

    @Test
    void login_userNameNulo_lancaBadCredentials() {
        var request = new LoginRequest(null, "senha");

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_senhaNula_lancaBadCredentials() {
        var request = new LoginRequest("joao", null);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_usuarioNaoEncontrado_lancaBadCredentials() {
        when(userRepository.findByUserName("joao")).thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(new LoginRequest("joao", "senha123"))
        );
    }

    @Test
    void login_senhaIncorreta_lancaBadCredentials() {
        User user = usuario("joao", "USER");
        when(userRepository.findByUserName("joao")).thenReturn(Optional.of(user));
        when(userService.loginCorrect(any(), eq(user))).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(new LoginRequest("joao", "errada"))
        );
    }

    @Test
    void login_credenciaisValidas_retornaToken() {
        User user = usuario("joao", "USER");
        when(userRepository.findByUserName("joao")).thenReturn(Optional.of(user));
        when(userService.loginCorrect(any(), eq(user))).thenReturn(true);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token-jwt");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        LoginResponse response = authService.login(new LoginRequest("joao", "senha123"));

        assertEquals("token-jwt", response.accessToken());
        assertEquals(300L, response.expiresIn());
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    private static User usuario(String userName, String role) {
        User user = new User();
        user.setId(1L);
        user.setUserName(userName);
        user.setPassword("hash");
        user.setRole(role);
        return user;
    }
}

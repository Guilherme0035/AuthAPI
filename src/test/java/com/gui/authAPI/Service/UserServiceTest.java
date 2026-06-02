package com.gui.authAPI.Service;

import com.gui.authAPI.Controller.dto.CreateUserRequest;
import com.gui.authAPI.Controller.dto.LoginRequest;
import com.gui.authAPI.Entity.User;
import com.gui.authAPI.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
    }

    @Test
    void createUser_salvaUsuarioComSenhaCriptografada() {
        var request = new CreateUserRequest("joao", "senha123", "USER");
        when(userRepository.findByUserName("joao")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User result = userService.createUser(request);

        assertEquals(1L, result.getId());
        assertEquals("joao", result.getUserName());
        assertEquals("USER", result.getRole());
        assertTrue(passwordEncoder.matches("senha123", result.getPassword()));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertNotEquals("senha123", captor.getValue().getPassword());
    }

    @Test
    void createUser_usuarioJaExistente_lancaConflict() {
        var request = new CreateUserRequest("joao", "senha123", "USER");
        when(userRepository.findByUserName("joao")).thenReturn(Optional.of(new User()));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.createUser(request)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Usuário já existente", ex.getReason());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginCorrect_senhaCorreta_retornaTrue() {
        String hash = passwordEncoder.encode("senha123");
        User user = new User();
        user.setPassword(hash);

        boolean result = userService.loginCorrect(
                new LoginRequest("joao", "senha123"),
                user
        );

        assertTrue(result);
    }

    @Test
    void loginCorrect_senhaIncorreta_retornaFalse() {
        String hash = passwordEncoder.encode("senha123");
        User user = new User();
        user.setPassword(hash);

        boolean result = userService.loginCorrect(
                new LoginRequest("joao", "errada"),
                user
        );

        assertFalse(result);
    }
}

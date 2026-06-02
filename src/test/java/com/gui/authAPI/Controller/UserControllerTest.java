package com.gui.authAPI.Controller;

import com.gui.authAPI.Controller.dto.CreateUserRequest;
import com.gui.authAPI.Entity.User;
import com.gui.authAPI.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createUser_retorna200() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUserName("joao");
        user.setRole("USER");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userName":"joao","password":"senha123","role":"USER"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("joao"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void createUser_usuarioDuplicado_retorna409() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existente"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userName":"joao","password":"senha123","role":"USER"}
                                """))
                .andExpect(status().isConflict());
    }
}

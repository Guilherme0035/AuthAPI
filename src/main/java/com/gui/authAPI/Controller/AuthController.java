package com.gui.authAPI.Controller;

import com.gui.authAPI.Controller.dto.LoginRequest;
import com.gui.authAPI.Controller.dto.LoginResponse;
import com.gui.authAPI.Service.AuthService;
import com.gui.authAPI.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        var result = authService.login(request);
        return ResponseEntity.ok().body(result);
    }
}

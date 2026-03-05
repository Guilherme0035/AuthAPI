package com.gui.authAPI.Service;

import com.gui.authAPI.Controller.dto.LoginRequest;
import com.gui.authAPI.Controller.dto.LoginResponse;
import com.gui.authAPI.Entity.User;
import com.gui.authAPI.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public LoginResponse login(LoginRequest request){

        if (request == null || request.username() == null || request.password() == null){
            throw new BadCredentialsException("Login ou senha inválidos");
        }

        Optional <User> user = userRepository.findByUserName(request.username());

        if (user.isEmpty() || !userService.loginCorrect(request,user.get())){
            throw new BadCredentialsException("Credencias inválidas");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var claims = JwtClaimsSet.builder()
                .issuer("Auth API")
                .subject(user.get().getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(jwtValue,expiresIn);
    }
}

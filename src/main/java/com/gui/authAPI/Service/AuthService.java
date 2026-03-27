package com.gui.authAPI.Service;

import com.gui.authAPI.Controller.dto.LoginRequest;
import com.gui.authAPI.Controller.dto.LoginResponse;
import com.gui.authAPI.Entity.User;
import com.gui.authAPI.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
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

        if (request == null || request.userName() == null || request.password() == null){
            throw new BadCredentialsException("Login ou senha inválidos");
        }

        Optional<User> user = userRepository.findByUserName(request.userName());

        if (user.isEmpty() || !userService.loginCorrect(request, user.get())){
            throw new BadCredentialsException("Credenciais inválidas");
        }

        var authorities = mapRoleToAuthorities(user.get().getRole());

        var now = Instant.now();
        var expiresIn = 300L;

        var claims = JwtClaimsSet.builder()
                .issuer("Auth API")
                .subject(user.get().getId().toString())
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(jwtValue, expiresIn);
    }

    private List<String> mapRoleToAuthorities(String role){

        if (role.equalsIgnoreCase("ADMIN")) {
            return List.of("PRODUTOS_READ", "PRODUTOS_WRITE");
        }
        return List.of("PRODUTOS_READ");
    }
}
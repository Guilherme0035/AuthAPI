package com.gui.authAPI.Service;

import com.gui.authAPI.Controller.dto.LoginRequest;
import com.gui.authAPI.Entity.User;
import com.gui.authAPI.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(LoginRequest request){

         Optional<User> user = userRepository.findByUserName(request.username());

        if (user.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Usuário já existente");
        }

        User newUser = new User();
        newUser.setUserName(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));

        return userRepository.save(newUser);
    }

    public boolean loginCorrect(LoginRequest request, User user){
        return passwordEncoder.matches(request.password(), user.getPassword());
    }

}

package com.gui.authAPI.Controller;

import com.gui.authAPI.Controller.dto.CreateUserRequest;
import com.gui.authAPI.Entity.User;
import com.gui.authAPI.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    ResponseEntity<User> createUser(@RequestBody CreateUserRequest request){
        User user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }

}

package com.hussein221.controller;

import com.hussein221.model.User;
import com.hussein221.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class AuthController {


    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }



    record RegisterRequest(String firstName, String lastName,
                           String email, String password, String passwordConfirm){ }
    record RegisterResponse(Long id,String firstName,String lastName,String email){}

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        var user = userRepository.save(
                User.of(registerRequest.firstName(),
                        registerRequest.lastName(),
                        registerRequest.email(),
                        registerRequest.password())
        );

        return new RegisterResponse(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail());
    }
}

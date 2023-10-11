package com.hussein221.controller;

import com.hussein221.model.User;
import com.hussein221.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@RequestMapping(value = "/api")
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    record RegisterRequest(String firstName, String lastName, String email, String password, String passwordConfirm){}
    record RegisterResponse(Long id,String firstName,String lastName,String email){

    }



    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        var user = authService.register(
                registerRequest.firstName(),
                        registerRequest.lastName(),
                        registerRequest.email(),
                        registerRequest.password(),
                        registerRequest.passwordConfirm
                );

        return new RegisterResponse(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail());
    }
}

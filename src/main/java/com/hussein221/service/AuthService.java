package com.hussein221.service;

import com.hussein221.model.User;
import com.hussein221.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User register(String firstName, String lastName, String email, String password, String passwordConfirm) {


        if(!Objects.equals(password, passwordConfirm))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"password do not match");

        return userRepository.save(
                User.of(firstName,lastName,email,password)
        );
    }
}

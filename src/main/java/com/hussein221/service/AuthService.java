package com.hussein221.service;

import com.hussein221.exceptions.EmailAlreadyExistsError;
import com.hussein221.exceptions.InvalidCredentialsError;
import com.hussein221.exceptions.PasswordDontMatchError;
import com.hussein221.model.User;
import com.hussein221.repository.UserRepository;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    public User register(String firstName, String lastName, String email, String password, String passwordConfirm) {


        if(!Objects.equals(password, passwordConfirm))
            throw new PasswordDontMatchError();
        User user;
        try {
            user  = userRepository.save(
                    User.of(firstName,lastName,email, passwordEncoder.encode(password))
            );
        }catch (DbActionExecutionException exception){
            throw new EmailAlreadyExistsError();
        }
        return user;
    }

    public Token login(String email, String password) {
        // find user by email
        var user = userRepository.findByEmail(email).orElseThrow(
                InvalidCredentialsError::new
        );

        //see if password don't match
        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredentialsError();
        }
        return Token.of(user.getId(), 10L ,"very_long_and_secure_and_safe_access_key");
    }
}

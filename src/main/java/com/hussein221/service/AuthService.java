package com.hussein221.service;

import com.hussein221.exceptions.EmailAlreadyExistsError;
import com.hussein221.exceptions.InvalidCredentialsError;
import com.hussein221.exceptions.PasswordDontMatchError;
import com.hussein221.exceptions.UserNotFoundError;
import com.hussein221.model.User;
import com.hussein221.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final String accessTokenSecret;
    private final String refreshTokenSecret;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                       @Value("${application.security.access-token-secret}") String accessTokenSecret,
                       @Value("${application.security.refresh-token-secret}")String refreshTokenSecret) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.accessTokenSecret = accessTokenSecret;
        this.refreshTokenSecret = refreshTokenSecret;
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

    public Login login(String email, String password) {
        // find user by email
        var user = userRepository.findByEmail(email).orElseThrow(
                InvalidCredentialsError::new
        );

        //see if password don't match
        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredentialsError();
        }
        return Login.of(user.getId(), accessTokenSecret,refreshTokenSecret);
    }

    public User getUserFromToken(String token) {
        return userRepository.findById(Token.from(token, accessTokenSecret))
                .orElseThrow(UserNotFoundError:: new);
    }

    public Login refreshAccess(String refreshToken) {
        var userId = Token.from(refreshToken, refreshTokenSecret);

        return Login.of(userId, accessTokenSecret ,Token.of(refreshToken));
    }
}

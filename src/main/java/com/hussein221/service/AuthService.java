package com.hussein221.service;

import com.hussein221.exceptions.*;
import com.hussein221.model.Token;
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
        var login = Login.of(user.getId(), accessTokenSecret,refreshTokenSecret);
        var refreshJwt = login.getRefreshToken();

        user.addToken(new Token(refreshJwt.getToken(), refreshJwt.getIssuedAt() ,refreshJwt.getExpiration()));
        userRepository.save(user);

        return login;
    }

    public User getUserFromToken(String token) {
        return userRepository.findById(Jwt.from(token, accessTokenSecret).getUserId())
                .orElseThrow(UserNotFoundError:: new);
    }

    public Login refreshAccess(String refreshToken) {
        var refreshJwt = Jwt.from(refreshToken, refreshTokenSecret);

        var user = userRepository.findByIdAndTokensRefreshTokenAndTokensExpiratedAtGreaterThan(
                refreshJwt.getUserId(),refreshJwt.getToken(), refreshJwt.getExpiration())
                .orElseThrow(UnauthenticatedError::new);
        return Login.of(refreshJwt.getUserId(), accessTokenSecret , refreshJwt);
    }

    public Boolean logout(String refreshToken) {
        var refreshJwt = Jwt.from(refreshToken, refreshTokenSecret);

        var user = userRepository.findById(refreshJwt.getUserId())
                .orElseThrow(UnauthenticatedError::new);

        var tokenIsRemoved = user.removeTokenIf(token -> Objects.equals(token.refreshToken(),
                refreshToken));
        if (tokenIsRemoved) {
            userRepository.save(user);
        }

        return tokenIsRemoved;
    }
}

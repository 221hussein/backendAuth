package com.hussein221.controller;

import com.hussein221.model.User;
import com.hussein221.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.IconUIResource;
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

    record LoginRequest(String email, String password) {

    }
    record LoginResponse(String token){}
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        var login = authService.login(loginRequest.email() ,loginRequest.password());

        Cookie cookie = new Cookie("refresh_token",login.getRefreshToken().getToken());
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        cookie.setPath("/api");

        response.addCookie(cookie);

        return new LoginResponse(login.getAccessToken().getToken());
    }

    record UserResponse(Long id,String firstName,String lastName,String email){ }
    @GetMapping("/user")
    public UserResponse user(HttpServletRequest request) {
        var user = (User) request.getAttribute("user");
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    record RefreshResponse(String message) {}

    @PostMapping("/refresh")
    public RefreshResponse refresh (@CookieValue("refresh_token")String refreshToken ) {
        return new RefreshResponse(authService.refreshAccess(refreshToken).getAccessToken().getToken());
    }

    record LogoutResponse(String message) {}
    @PostMapping("/logout")
    public LogoutResponse logout(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token",null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return new LogoutResponse("success");
    }
}

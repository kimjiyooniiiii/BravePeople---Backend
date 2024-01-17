package com.example.brave_people_backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "Invalid Username or Password"; // 기본 예외 메시지

        if(exception instanceof BadCredentialsException) {
            errorMessage = "Invalid Username or Password";
        }else if(exception instanceof InsufficientAuthenticationException) {
            errorMessage = "Invalid Secret Key";
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}

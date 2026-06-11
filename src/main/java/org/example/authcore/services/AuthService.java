package org.example.authcore.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.authcore.dto.LoginRequest;
import org.example.authcore.dto.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse login(
            LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        System.out.println("AUTH CORE LOGIN CALLED");
        System.out.println("USERNAME = " + request.username());

        Authentication authRequest =
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                );

        try {
            Authentication authResult =
                    authenticationManager.authenticate(authRequest);

            System.out.println("AUTH SUCCESS = " + authResult.getName());
            System.out.println("AUTHORITIES = " + authResult.getAuthorities());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);

            securityContextRepository.saveContext(
                    context,
                    httpRequest,
                    httpResponse
            );

            return new LoginResponse(
                    "Login successful",
                    authResult.getName(),
                    authResult.getAuthorities()
            );

        } catch (Exception e) {
            System.out.println("AUTH FAILED");
            System.out.println("TYPE = " + e.getClass().getName());
            System.out.println("MESSAGE = " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
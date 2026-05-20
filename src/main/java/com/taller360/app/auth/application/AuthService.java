package com.taller360.app.auth.application;

import com.taller360.app.auth.application.dto.AuthenticatedUserResponse;
import com.taller360.app.auth.application.dto.LoginRequest;
import com.taller360.app.auth.application.dto.LoginResponse;
import com.taller360.app.security.JwtProperties;
import com.taller360.app.security.JwtService;
import com.taller360.app.users.application.UserService;
import com.taller360.app.users.domain.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserService userService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtProperties jwtProperties,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.userService = userService;
    }

    public LoginResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        User user = userService.getByEmail(request.email());

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtProperties.expirationSeconds(),
                new AuthenticatedUserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole()
                )
        );
    }
}

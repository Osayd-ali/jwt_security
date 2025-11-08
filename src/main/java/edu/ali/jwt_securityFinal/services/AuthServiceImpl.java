package edu.ali.jwt_securityFinal.services;

import edu.ali.jwt_securityFinal.dtos.JwtResponse;
import edu.ali.jwt_securityFinal.dtos.LoginRequest;
import edu.ali.jwt_securityFinal.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// Implementation of AuthService to handle authentication and role-based access
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public JwtResponse authenticateAndGenerateToken(LoginRequest request) {
        try {
            // Authenticating the user here.
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Setting the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Extracting user details and generating JWT token
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // Returning the JWT token in the response to the client
            return new JwtResponse(token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PreAuthorize("hasRole('USER')")
    @Override
    public String doUserThing(Authentication auth) {
        return "Hello User " + auth.getName() + " with roles " + auth.getAuthorities();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public String doAdminThing(Authentication auth) {
        return "Hello Admin " + auth.getName() + " with roles " + auth.getAuthorities();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public String doManagerThing(Authentication auth) {
        return "Hello Manager " + auth.getName() + " with roles " + auth.getAuthorities();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MANAGER')")
    @Override
    public String doCommonThing(Authentication auth) {
        return "Hello " + auth.getName() + " with roles " + auth.getAuthorities();
    }

    @Override
    public String doPublic(Authentication auth) {
        return "doPublic: " + auth.getName() + " with roles " + auth.getAuthorities();
    }
}


package edu.ali.jwt_securityFinal.controllers;

import edu.ali.jwt_securityFinal.dtos.ApiExceptionDto;
import edu.ali.jwt_securityFinal.dtos.JwtResponse;
import edu.ali.jwt_securityFinal.dtos.LoginRequest;
import edu.ali.jwt_securityFinal.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EndpointController {

    private static final Logger log = LoggerFactory.getLogger(EndpointController.class);

    private final AuthService authService;

    @Autowired
    public EndpointController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login") // With this endpoint, we must attach {"username": "...", "password": "..."} JSON body and get JWT token in response
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        JwtResponse response = authService.authenticateAndGenerateToken(request);
        return ResponseEntity.ok(response);
    }

    // The validation of JWT token and interception of requests is handled by JwtAuthenticationFilter configured in the security config.
    // The following endpoints demonstrate role-based access control using @PreAuthorize annotations.
    // Each endpoint requires a valid JWT token with appropriate roles to access.
    // After the request is successfully authenticated by the filter, the user's details are available in the SecurityContext.
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/useronly") // Only users with USER role can access this endpoint, JWT token required with the request
    public String userAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // The above line retrieves the current authenticated user's details from the Spring Security context.
        //SecurityContextHolder.getContext().getAuthentication() returns the Authentication object, which contains information about the user's identity, roles, and authentication status for the current request.
        //This object is set by Spring Security after successful authentication (e.g., after validating a JWT token).
        return authService.doUserThing(auth);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/manageronly")
    public String managerAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.doManagerThing(auth);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/adminonly")
    public String adminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.doAdminThing(auth);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MANAGER')")
    @GetMapping("/common/allok")
    public String anyAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.doCommonThing(auth);
    }

    @GetMapping("/public")
    public String publicEndpoint()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.doPublic(auth);
    }

    /////////////////////////////////////////////
    // Exception Handling
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleNotFoundException(
            BadCredentialsException ex, HttpServletRequest request) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(apiExceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleNotFoundException(
            Exception ex, HttpServletRequest request) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(apiExceptionDto, HttpStatus.BAD_REQUEST);
    }

}


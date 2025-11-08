package edu.ali.jwt_securityFinal.services;

import edu.ali.jwt_securityFinal.dtos.JwtResponse;
import edu.ali.jwt_securityFinal.dtos.LoginRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    JwtResponse authenticateAndGenerateToken(LoginRequest request);

    String doUserThing(Authentication auth);

    String doManagerThing(Authentication auth);

    String doAdminThing(Authentication auth);

    String doCommonThing(Authentication auth);

    String doPublic(Authentication auth);
}

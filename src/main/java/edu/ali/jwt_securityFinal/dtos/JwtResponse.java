package edu.ali.jwt_securityFinal.dtos;

//Returns JWT token after successful authentication
// JWT Token response will be in this format
public class JwtResponse {

    private String token;

    public JwtResponse() {
    }

    public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


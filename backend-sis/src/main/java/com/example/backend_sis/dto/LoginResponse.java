package com.example.backend_sis.dto;

public class LoginResponse {
    public String token;
    public String email;
    public String rol;

    public LoginResponse(String token, String email, String rol) {
        this.token = token;
        this.email = email;
        this.rol = rol;
    }
}
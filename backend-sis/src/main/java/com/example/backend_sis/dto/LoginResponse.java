package com.example.backend_sis.dto;

public class LoginResponse {
    public Long id;
    public String token;
    public String email;
    public String rol;

    public LoginResponse(Long id,String token, String email, String rol) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.rol = rol;
    }
}
package com.example.backend_sis.dto;

public class LoginResponse {
    public Long id;
    public String token;
    public String email;
    public String rol;
    public String nombre;
    public String apellido;

    public LoginResponse(Long id,String token, String email, String rol, String nombre, String apellido) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
    }
}
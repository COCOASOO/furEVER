package com.bemen.furever;

public class usuario {

    private String nombre;          // Variable para almacenar el nombre del usuario
    private String email;           // Variable para almacenar el email del usuario
    private String privilegios;     // Variable para almacenar los privilegios del usuario
    private String contraseña;      // Variable para almacenar la contraseña del usuario

    public usuario() {
        // Constructor vacío requerido para Firebase
    }

    public usuario(String nombre, String email, String privilegios, String contraseña) {
        this.nombre = nombre;               // Asignar el nombre del usuario
        this.email = email;                 // Asignar el email del usuario
        this.privilegios = privilegios;     // Asignar los privilegios del usuario
        this.contraseña = contraseña;       // Asignar la contraseña del usuario
    }

    public String getNombre() {
        return nombre;                      // Obtener el nombre del usuario
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;               // Establecer el nombre del usuario
    }

    public String getEmail() {
        return email;                       // Obtener el email del usuario
    }

    public void setEmail(String email) {
        this.email = email;                 // Establecer el email del usuario
    }

    public String getPrivilegios() {
        return privilegios;                 // Obtener los privilegios del usuario
    }

    public void setPrivilegios(String privilegios) {
        this.privilegios = privilegios;     // Establecer los privilegios del usuario
    }

    public String getContraseña() {
        return contraseña;                  // Obtener la contraseña del usuario
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;       // Establecer la contraseña del usuario
    }
}

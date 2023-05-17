package com.bemen.furever;

public class Perros {
    private String nombre; // Nombre del perro
    private String id_Dog; // ID del perro
    private String edad; // Edad del perro
    private String esterilización; // Estado de esterilización del perro
    private String fecha_nacimiento; // Fecha de nacimiento del perro
    private String peso; // Peso del perro
    private String sexo; // Sexo del perro
    private String raza; // Raza del perro
    private String urlImagen; // URL de la imagen del perro

    public Perros() {
        // Constructor sin argumentos requerido por Firestore
    }

    public Perros(String nombre, String id_Dog, String edad, String esterilización, String fecha_nacimiento, String peso, String raza, String sexo, String urlImagen) {
        this.nombre = nombre;
        this.id_Dog = id_Dog;
        this.edad = edad;
        this.esterilización = esterilización;
        this.fecha_nacimiento = fecha_nacimiento;
        this.peso = peso;
        this.raza = raza;
        this.sexo = sexo;
        this.urlImagen = urlImagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId_Dog() {
        return id_Dog;
    }

    public void setId_Dog(String id_Dog) {
        this.id_Dog = id_Dog;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getEsterilización() {
        return esterilización;
    }

    public void setEsterilización(String esterilización) {
        this.esterilización = esterilización;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String geturlImagen() {
        return urlImagen;
    }

    public void seturlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}

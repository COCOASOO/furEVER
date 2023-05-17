package com.bemen.furever;

import java.util.ArrayList;

public class Publicacion {

    private String titulo; // Título de la publicación
    private String categoria; // Categoría de la publicación
    private String contenido; // Contenido de la publicación
    private String usuario; // Usuario que realizó la publicación

    public Publicacion() {
        // Constructor sin argumentos requerido por Firebase
    }

    public Publicacion(String titulo, String categoria, String contenido, String usuario) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.contenido = contenido;
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}


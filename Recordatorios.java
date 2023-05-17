package com.bemen.furever;


public class Recordatorios {

    private int ID; // Identificador del recordatorio
    private String asunto; // Asunto del recordatorio
    private String fecha; // Fecha del recordatorio
    private String hora; // Hora del recordatorio
    private String tipo; // Tipo del recordatorio
    private String importancia; // Nivel de importancia del recordatorio

    private String nombre; // Nombre asociado al recordatorio

    public Recordatorios() {
        // Constructor sin argumentos requerido por Firestore
    }

    public Recordatorios(int ID, String asunto, String fecha, String hora, String tipo, String importancia, String nombre) {
        this.ID = ID;
        this.asunto = asunto;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.importancia = importancia;
        this.nombre = nombre;
    }

    public int getID() {
        return ID;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getImportancia() {
        return importancia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setImportancia(String importancia) {
        this.importancia = importancia;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}


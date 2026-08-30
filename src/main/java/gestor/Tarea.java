package gestor;

import java.time.LocalDate;

public class Tarea {
    private String descripcion;
    private boolean estado;
    private Prioridad prioridad;
    private LocalDate fechaLimite;
    
    public Tarea(String descripcion, boolean estado, Prioridad prioridad, LocalDate fechaLimite) {
        this.descripcion = descripcion;
        this.estado = estado;
        this.prioridad = prioridad;
        this.fechaLimite = fechaLimite;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public boolean isCompletada() {
        return estado;
    }
    public Prioridad getPrioridad() {
        return prioridad;
    }
    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }
    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        if (estado == true) {
            return """
                [X] %s
                """.formatted(descripcion);
        } else {
            return """
                    [ ] %s
                    """.formatted(descripcion);
        }
    }
}

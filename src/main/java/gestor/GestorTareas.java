package gestor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GestorTareas {
    private ArrayList<Tarea> tareas;

    public GestorTareas() {
        tareas = new ArrayList<>();
    }

    public ArrayList<Tarea> getTareas() {
        return tareas;
    }

    public void crearTarea(Tarea newTarea) {
        tareas.add(newTarea);
    }

    public void completarTarea(Tarea tareaSeleccionada) {
        tareaSeleccionada.setEstado(true);
        System.out.println("Tarea completada correctamente");
    }

    public void eliminarTarea(Tarea tareaSeleccionada) {
        tareas.remove(tareaSeleccionada);
        System.out.println("Tarea borrada correctamente");
    }

    public int getNumTareas() {
        return tareas.size();
    }
    public int howManyCompletada() {
        int cont = 0;
        for (Tarea tarea : tareas) {
            if (tarea.isCompletada()) {
                cont++;
            }
        }
        return cont;
    }
    public int howManyPendiente() {
        return getNumTareas() - howManyCompletada();
    }


    public void guardarTareas() {
        try {
            FileWriter escritor = new FileWriter("tareas.txt");
            for (Tarea tarea : tareas) {
                String fechaLimite = "";
                if (tarea.getFechaLimite() != null) {
                    fechaLimite = tarea.getFechaLimite().toString();
                }
                String linea = tarea.getDescripcion() + ";"
                                 + tarea.isCompletada() + ";"
                                 + tarea.getPrioridad() + ";"
                                 + fechaLimite;
                escritor.write(linea + "\n");
            }
            escritor.close();
            System.out.println("Tareas guardadas correctamente");
        } catch (IOException e) {
            // TODO: handle exception
            System.out.println("Error al guardar las tareas");
            e.printStackTrace();
        }
    }

    public void recuperarTareas() {
        try {
            BufferedReader lector = new BufferedReader(new FileReader("tareas.txt"));
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split(";", -1);

                String descripcion = datos[0];
                boolean estado = Boolean.parseBoolean(datos[1]);
                Prioridad prioridad = Prioridad.valueOf(datos[2]);
                LocalDate fechaLimite = null;
                if (!datos[3].isBlank()) {
                    fechaLimite = LocalDate.parse(datos[3]);
                }
                
                tareas.add(new Tarea(descripcion, estado, prioridad, fechaLimite));
            }
            lector.close();
            System.out.println("Tareas recuperadas correctamente");
        } catch (IOException e) {
            // TODO: handle exception
            System.out.println("Error al recuperar las tareas");
            e.printStackTrace();
        }
    }
}
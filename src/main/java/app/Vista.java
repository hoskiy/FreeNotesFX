package app;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import gestor.*;

public class Vista {

    ListView<Tarea> lista; 
    Button añadir;
    ComboBox<String> filtros;
    ContextMenu menuTarea;
    Menu cambiarPrioridad;
    MenuItem cambiarAlta, cambiarNormal, cambiarBaja;
    MenuItem editar, eliminar, completarTarea, cambiarFecha;
    BorderPane root;
    HBox opciones, estadisticas;
    GestorTareas gestor;
    

    public Vista() {
        opciones = new HBox();
        root = new BorderPane();
        añadir = new Button("Añadir tarea");
        filtros = new ComboBox<>();
        lista = new ListView<>();
        
        menuTarea = new ContextMenu();
        
        cambiarPrioridad = new Menu("Cambiar prioridad");
        cambiarAlta = new MenuItem("Alta");
        cambiarNormal = new MenuItem("Normal");
        cambiarBaja = new MenuItem("Baja");

        completarTarea = new MenuItem("Completar");
        editar = new MenuItem("Editar");
        cambiarFecha = new MenuItem("Cambiar fecha limite");
        eliminar = new MenuItem("Eliminar");
        
        estadisticas = new HBox();
        
        filtros.setPromptText("🔍 Filtros");
        filtros.getItems().addAll("Todas", "Pendientes", "Completadas");
        cambiarPrioridad.getItems().addAll(cambiarAlta, cambiarNormal, cambiarBaja);
        menuTarea.getItems().addAll(cambiarPrioridad, completarTarea, editar, cambiarFecha, eliminar);
        
        lista.setContextMenu(menuTarea);

        opciones.getChildren().addAll(añadir, filtros);
        root.setTop(opciones);
        root.setCenter(lista);
        root.setBottom(estadisticas);
    }
}

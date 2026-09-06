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
    ComboBox<Filtros> filtros;
    ComboBox<String> idiomas;
    ContextMenu menuTarea;
    Menu cambiarPrioridad;
    MenuItem cambiarAlta, cambiarNormal, cambiarBaja;
    MenuItem editar, eliminar, completarTarea, cambiarFecha;
    BorderPane root;
    HBox opciones, estadisticas;
    
    

    public Vista() {
        opciones = new HBox();
        root = new BorderPane();
        añadir = new Button();
        filtros = new ComboBox<>();
        idiomas = new ComboBox<>();

        lista = new ListView<>();
        
        menuTarea = new ContextMenu();
        
        cambiarPrioridad = new Menu();
        cambiarAlta = new MenuItem();
        cambiarNormal = new MenuItem();
        cambiarBaja = new MenuItem();

        completarTarea = new MenuItem();
        editar = new MenuItem();
        cambiarFecha = new MenuItem();
        eliminar = new MenuItem();
        
        estadisticas = new HBox();
        
        filtros.getItems().addAll(Filtros.values());
        idiomas.getItems().addAll("Español", "English");
        cambiarPrioridad.getItems().addAll(cambiarAlta, cambiarNormal, cambiarBaja);
        menuTarea.getItems().addAll(cambiarPrioridad, completarTarea, editar, cambiarFecha, eliminar);
        
        lista.setContextMenu(menuTarea);

        opciones.getChildren().addAll(añadir, filtros, idiomas);
        root.setTop(opciones);
        root.setCenter(lista);
        root.setBottom(estadisticas);
    }
}

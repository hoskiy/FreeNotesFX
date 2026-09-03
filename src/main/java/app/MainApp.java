package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import gestor.*;

public class MainApp extends Application {
    Vista vista;
    Scene scene;
    Image icono;

    Controlador controlador;
    GestorTareas gestor;

    @Override
    public void start(Stage stage) {
        vista = new Vista();
        gestor = new GestorTareas();
        controlador = new Controlador(vista, gestor);
        
        controlador.configurarEventos();
        
        vista.añadir.getStyleClass().add("boton");
        vista.menuTarea.getStyleClass().add("menu-desplegable");
        gestor.recuperarTareas();


        for (Tarea tarea : gestor.getTareas()) {
            vista.lista.getItems().add(tarea);
        }
        
        stage.setTitle("FreeNotesFX");
                
        icono = new Image(getClass().getResourceAsStream("/icono.png"));
        stage.getIcons().add(icono);
        
        vista.root.getStyleClass().add("ventana");
        
        
        scene = new Scene(vista.root, 500, 400);
        scene.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        
        stage.setOnCloseRequest(event -> {
            gestor.guardarTareas();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    
}
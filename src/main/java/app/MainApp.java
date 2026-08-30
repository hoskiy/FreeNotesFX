package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

import java.util.Optional;

import gestor.*;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        HBox opciones = new HBox();
        opciones.setSpacing(2);
        VBox root = new VBox();
        Button añadir = new Button("Añadir tarea");
        añadir.getStyleClass().add("boton");

        ListView<Tarea> lista = new ListView<>();
        lista.setId("listaTareas");
        lista.setCellFactory(param -> new ListCell<Tarea>() {

            @Override
            protected void updateItem(Tarea tarea, boolean empty) {

                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox contenedorTarea = new VBox();
                    HBox contenedor1 = new HBox();
                    HBox contenedor2 = new HBox();
                    Label descripcion = new Label(tarea.getDescripcion());
                    descripcion.getStyleClass().add("descripcion");
                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());
                    prioridad.getStyleClass().add("prioridad");
                    
                    Label estado;
                    if(tarea.isCompletada()) {
                        estado = new Label("✅");
                    } else {
                        estado = new Label("❌");
                    }
                    estado.getStyleClass().add("estado");
                    
                    contenedor1.getChildren().addAll(estado, descripcion);
                    contenedor1.setSpacing(8);
                    contenedor2.getChildren().add(prioridad);
                    if (tarea.getFechaLimite() != null) {
                        Label fechaLimite = new Label("Fecha limite: " + tarea.getFechaLimite());
                        fechaLimite.getStyleClass().add("fechaLimite");
                        contenedor2.getChildren().add(fechaLimite);
                    }
                    contenedor2.setSpacing(12);
                    contenedorTarea.getChildren().addAll(contenedor1, contenedor2);
                    contenedorTarea.getStyleClass().add("contenedor-tarea");

                    setGraphic(contenedorTarea);
                }
            }
        });

        ContextMenu menuTarea = new ContextMenu();
       
        Menu cambiarPrioridad = new Menu("Cambiar prioridad");
        MenuItem cambiarAlta = new MenuItem("Alta");
        MenuItem cambiarNormal = new MenuItem("Normal");
        MenuItem cambiarBaja = new MenuItem("Baja");
        cambiarPrioridad.getItems().addAll(cambiarAlta, cambiarNormal, cambiarBaja);

        MenuItem completarTarea = new MenuItem("Completar");
        MenuItem editar = new MenuItem("Editar");
        MenuItem cambiarFecha = new MenuItem("Cambiar fecha limite");
        MenuItem eliminar = new MenuItem("Eliminar");

        menuTarea.getItems().addAll(cambiarPrioridad, completarTarea, editar, cambiarFecha, eliminar);
        menuTarea.getStyleClass().add("menu-desplegable");
        lista.setContextMenu(menuTarea);

        GestorTareas gestor = new GestorTareas();
        gestor.recuperarTareas();

        HBox estadisticas = new HBox();
        actualizarEstadisticas(estadisticas, gestor);

        for (Tarea tarea : gestor.getTareas()) {
            lista.getItems().add(tarea);
        }

        stage.setTitle("Gestor de Tareas");

        opciones.getChildren().addAll(añadir);
        root.getChildren().addAll(opciones, lista, estadisticas);

        añadir.setOnAction(event -> {
            TextField campoTarea = new TextField();
            campoTarea.setPromptText("Introduce una tarea");
            root.getChildren().add(campoTarea);

            campoTarea.setOnAction(eventCampo -> {
                String descripcion = campoTarea.getText();
                if (descripcion.isEmpty() || descripcion.isBlank()) {
                    Alert aviso = new Alert(AlertType.ERROR);
                    aviso.setTitle("Error");
                    aviso.setHeaderText("Entrada no válida");
                    aviso.setContentText("La descripcion no puede estar vacia");
                    aviso.showAndWait();
                } else {
                    Tarea newTarea = new Tarea(descripcion, false, Prioridad.NORMAL, null);
                    gestor.crearTarea(newTarea);
                    lista.getItems().add(newTarea);
                }
                root.getChildren().remove(campoTarea);
                actualizarEstadisticas(estadisticas, gestor);
            });
        });

        // Cambios de prioridad en la tarea seleccionada
        cambiarAlta.setOnAction(event -> {
            lista.getItems().get(lista.getSelectionModel().getSelectedIndex()).setPrioridad(Prioridad.ALTA);
            lista.refresh();
        });
        cambiarNormal.setOnAction(event -> {
            lista.getItems().get(lista.getSelectionModel().getSelectedIndex()).setPrioridad(Prioridad.NORMAL);
            lista.refresh();
        });
        cambiarBaja.setOnAction(event -> {
            lista.getItems().get(lista.getSelectionModel().getSelectedIndex()).setPrioridad(Prioridad.BAJA);
            lista.refresh();
        });

        completarTarea.setOnAction(event -> {
            lista.getItems().get(lista.getSelectionModel().getSelectedIndex()).setEstado(true);
            lista.refresh();
            actualizarEstadisticas(estadisticas, gestor);
        });

        editar.setOnAction(event -> {
            Tarea tareaSeleccionada = lista.getSelectionModel().getSelectedItem();
            String preDescripcion = tareaSeleccionada.getDescripcion();
            TextField campoTarea = new TextField();
            campoTarea.setText(preDescripcion);
            root.getChildren().add(campoTarea);

            campoTarea.setOnAction(event2 -> {
                String newDescripcion = campoTarea.getText();
                if (newDescripcion.isEmpty() || newDescripcion.isBlank()) {
                    Alert aviso = new Alert(AlertType.ERROR);
                    aviso.setTitle("Error");
                    aviso.setHeaderText("Entrada no válida");
                    aviso.setContentText("La descripcion no puede estar vacia");
                    aviso.showAndWait();
                } else {
                    tareaSeleccionada.setDescripcion(newDescripcion);
                }
                lista.refresh();
                root.getChildren().remove(campoTarea);
            });
        });

        cambiarFecha.setOnAction(event -> {
            DatePicker selectorFecha = new DatePicker();
            Stage stageFecha = new Stage();
            Scene sceneFecha = new Scene(selectorFecha, 200, 100);
            
            stageFecha.setScene(sceneFecha);
            stageFecha.show();

            selectorFecha.setOnAction(eventSelector -> {
                Tarea tarea = lista.getSelectionModel().getSelectedItem();
                tarea.setFechaLimite(selectorFecha.getValue());
                stageFecha.close();
                lista.refresh();
            });
        });

        eliminar.setOnAction(event -> {
            Tarea tareaSeleccionada = lista.getSelectionModel().getSelectedItem();
            if (tareaSeleccionada != null) {
                Alert aviso = new Alert(AlertType.CONFIRMATION);
                aviso.setTitle("Confirmacion");
                aviso.setHeaderText("¿Deseas eliminar esta tarea?");
                aviso.setContentText("Esta acción no se puede revertir");

                Optional<ButtonType> resultado = aviso.showAndWait();
                if (resultado.get().equals(ButtonType.OK)) {
                    lista.getItems().remove(tareaSeleccionada);
                    gestor.eliminarTarea(tareaSeleccionada);
                }
            }
            actualizarEstadisticas(estadisticas, gestor);
        });


        Image icono = new Image(getClass().getResourceAsStream("/icono.png"));
        stage.getIcons().add(icono);

        root.getStyleClass().add("ventana");

        Scene scene = new Scene(root, 500, 400);
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

    private void actualizarEstadisticas(HBox estadisticas, GestorTareas gestor) {
        estadisticas.getChildren().clear();
        Label total = new Label("Total: " + gestor.getNumTareas());
        total.getStyleClass().add("estadisticas");
        Label completadas = new Label("Completadas: " + gestor.howManyCompletada());
        completadas.getStyleClass().add("estadisticas");
        Label pendientes = new Label("Pendientes: " + gestor.howManyPendiente());
        pendientes.getStyleClass().add("estadisticas");

        estadisticas.getChildren().addAll(total, completadas, pendientes);
        estadisticas.setSpacing(10);
        estadisticas.setId("panelEstadisticas");
    }
}
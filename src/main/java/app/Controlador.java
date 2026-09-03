package app;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.util.Optional;

import gestor.*;

public class Controlador {
    private final GestorTareas gestor;
    private final Vista vista;

    public Controlador(Vista vista, GestorTareas gestor) {
        this.gestor = gestor;
        this.vista = vista;
        actualizarEstadisticas();
    }

    public void configurarEventos() {
        configurarAñadir();
        configurarLista();
        configurarCompletar();
        configurarEditar();
        configurarPrioridad();
        configurarEliminar();
        configurarFecha();
    }

    private void configurarAñadir() {
        vista.añadir.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();

            dialog.setTitle("Nueva tarea");
            dialog.setHeaderText(null);
            dialog.setContentText("Descripción:");

            Optional<String> resultado = dialog.showAndWait();
            if(resultado.isPresent()) {
                String descripcion = resultado.get();
                if (descripcion.isEmpty() || descripcion.isBlank()) {
                    Alert aviso = new Alert(AlertType.ERROR);
                    aviso.setTitle("Error");
                    aviso.setHeaderText("Entrada no válida");
                    aviso.setContentText("La descripcion no puede estar vacia");
                    aviso.showAndWait();
                } else {
                    Tarea newTarea = new Tarea(descripcion, false, Prioridad.NORMAL, null);
                    gestor.crearTarea(newTarea);
                    vista.lista.getItems().add(newTarea);
                }
            }
            actualizarEstadisticas();
            
        });
    }

    private void configurarEditar() {
        vista.editar.setOnAction(event -> {
            Tarea tareaSeleccionada = vista.lista.getSelectionModel().getSelectedItem();
            String preDescripcion = tareaSeleccionada.getDescripcion();
            TextInputDialog dialog = new TextInputDialog();

            dialog.setTitle("Nueva tarea");
            dialog.setHeaderText(null);
            dialog.setContentText(preDescripcion);

            Optional<String> resultado = dialog.showAndWait();
            if(resultado.isPresent()) {
                String newDescripcion = resultado.get();
                if (newDescripcion.isEmpty() || newDescripcion.isBlank()) {
                    Alert aviso = new Alert(AlertType.ERROR);
                    aviso.setTitle("Error");
                    aviso.setHeaderText("Entrada no válida");
                    aviso.setContentText("La descripcion no puede estar vacia");
                    aviso.showAndWait();
                } else {
                    tareaSeleccionada.setDescripcion(newDescripcion);
                    vista.lista.refresh();
                }
            }
        });
    }

    private void configurarCompletar() {
        vista.completarTarea.setOnAction(event -> {
            vista.lista.getItems().get(vista.lista.getSelectionModel().getSelectedIndex()).setEstado(true);
            vista.lista.refresh();
            actualizarEstadisticas();
        });
    }

    private void configurarEliminar() {
        vista.eliminar.setOnAction(event -> {
            Tarea tareaSeleccionada = vista.lista.getSelectionModel().getSelectedItem();
            if (tareaSeleccionada != null) {
                Alert aviso = new Alert(AlertType.CONFIRMATION);
                aviso.setTitle("Confirmacion");
                aviso.setHeaderText("¿Deseas eliminar esta tarea?");
                aviso.setContentText("Esta acción no se puede revertir");
    
                Optional<ButtonType> resultado = aviso.showAndWait();
                if (resultado.get().equals(ButtonType.OK)) {
                    vista.lista.getItems().remove(tareaSeleccionada);
                    gestor.eliminarTarea(tareaSeleccionada);
                }
            }
            actualizarEstadisticas();
        });
    }

    private void configurarPrioridad() {
        vista.cambiarAlta.setOnAction(event -> {
            vista.lista.getItems().get(vista.lista.getSelectionModel().getSelectedIndex()).setPrioridad(Prioridad.ALTA);
            vista.lista.refresh();
        });
        vista.cambiarNormal.setOnAction(event -> {
            vista.lista.getItems().get(vista.lista.getSelectionModel().getSelectedIndex()).setPrioridad(Prioridad.NORMAL);
            vista.lista.refresh();
        });
        vista.cambiarBaja.setOnAction(event -> {
            vista.lista.getItems().get(vista.lista.getSelectionModel().getSelectedIndex()).setPrioridad(Prioridad.BAJA);
            vista.lista.refresh();
        });
    }

    private void configurarFecha() {
        vista.cambiarFecha.setOnAction(event -> {
            DatePicker selectorFecha = new DatePicker();
            Stage stageFecha = new Stage();
            Scene sceneFecha = new Scene(selectorFecha, 200, 100);
            
            stageFecha.setScene(sceneFecha);
            stageFecha.show();
    
            selectorFecha.setOnAction(eventSelector -> {
                Tarea tarea = vista.lista.getSelectionModel().getSelectedItem();
                tarea.setFechaLimite(selectorFecha.getValue());
                stageFecha.close();
                vista.lista.refresh();
            });
        });
    }

    private void configurarLista() {
        vista.lista.setId("listaTareas");
        vista.lista.setCellFactory(param -> new ListCell<Tarea>() {
    
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
    }

    private void actualizarEstadisticas() {
        vista.estadisticas.getChildren().clear();
        Label total = new Label("Total: " + gestor.getNumTareas());
        total.getStyleClass().add("estadisticas");
        Label completadas = new Label("Completadas: " + gestor.howManyCompletada());
        completadas.getStyleClass().add("estadisticas");
        Label pendientes = new Label("Pendientes: " + gestor.howManyPendiente());
        pendientes.getStyleClass().add("estadisticas");

        vista.estadisticas.getChildren().addAll(total, completadas, pendientes);
        vista.estadisticas.setSpacing(10);
        vista.estadisticas.setId("panelEstadisticas");
    }
}

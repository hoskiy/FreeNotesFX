package app;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import gestor.*;

public class Controlador {
    private final GestorTareas gestor;
    private final Vista vista;
    private ResourceBundle idioma;

    public Controlador(Vista vista, GestorTareas gestor, ResourceBundle idioma) {
        this.gestor = gestor;
        this.vista = vista;
        this.idioma = idioma;
        actualizarEstadisticas();
        cambiarIdioma("es");
    }

    public void configurarEventos() {
        configurarAñadir();
        configurarFiltros();
        configurarIdiomas();
        configurarLista();
        configurarCompletar();
        configurarEditar();
        configurarPrioridad();
        configurarEliminar();
        configurarFecha();
        actualizarEstadisticas();
    }

    private void configurarAñadir() {
        vista.añadir.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();

            dialog.setTitle("Nueva tarea");
            dialog.setHeaderText(null);
            dialog.setContentText("Descripción:");

            Optional<String> resultado = dialog.showAndWait();
            if (resultado.isPresent()) {
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

    private void configurarFiltros() {
        vista.filtros.setOnAction(event -> {
            Filtros filtro = vista.filtros.getValue();
            vista.lista.getItems().clear();

            switch (filtro) {
                case TODAS:
                    mostrarTodas();
                    break;
                case PENDIENTES:
                    mostrarPendientes();
                    break;
                case COMPLETADAS:
                    mostrarCompletadas();
                    break;
                default:
                    break;
            }
            vista.lista.refresh();
        });
    }

    private void configurarIdiomas() {
        vista.idiomas.setOnAction(event -> {
            String idioma = vista.idiomas.getValue();

            switch (idioma) {
                case "Español":
                    cambiarIdioma("es");
                    break;
                case "English":
                    cambiarIdioma("en");
                    break;
                default:
                    break;
            }
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
            if (resultado.isPresent()) {
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
            vista.lista.getItems().get(vista.lista.getSelectionModel().getSelectedIndex())
                    .setPrioridad(Prioridad.NORMAL);
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
                    HBox tarjetaTarea = new HBox();
                    Region barraColor = new Region();
                    barraColor.setPrefWidth(6);
                    VBox contenedorTarea = new VBox();
                    HBox contenedor1 = new HBox();
                    HBox contenedor2 = new HBox();
                    Label descripcion = new Label(tarea.getDescripcion());
                    descripcion.getStyleClass().add("descripcion");
                    Label prioridad = new Label();
                    prioridad.getStyleClass().add("secundario");

                    switch (tarea.getPrioridad()) {
                        case ALTA:
                            prioridad.setText(
                                    idioma.getString("tarea.prioridad") + idioma.getString("tarea.prioridad.alta"));
                            barraColor.getStyleClass().add("prioridad-alta");
                            break;
                        case NORMAL:
                            prioridad.setText(
                                    idioma.getString("tarea.prioridad") + idioma.getString("tarea.prioridad.normal"));
                            barraColor.getStyleClass().add("prioridad-normal");
                            break;
                        case BAJA:
                            prioridad.setText(
                                    idioma.getString("tarea.prioridad") + idioma.getString("tarea.prioridad.baja"));
                            barraColor.getStyleClass().add("prioridad-baja");
                            break;
                    }

                    Label estado;
                    if (tarea.isCompletada()) {
                        estado = new Label("✅");
                    } else {
                        estado = new Label("❌");
                    }
                    estado.getStyleClass().add("estado");

                    contenedor1.getChildren().addAll(estado, descripcion);
                    contenedor1.setSpacing(8);
                    contenedor2.getChildren().add(prioridad);
                    if (tarea.getFechaLimite() != null) {
                        Label fechaLimite = new Label(idioma.getString("tarea.fechaLimite") + tarea.getFechaLimite());
                        fechaLimite.getStyleClass().add("secundario");
                        contenedor2.getChildren().add(fechaLimite);
                    }
                    contenedor2.setSpacing(12);
                    contenedorTarea.getChildren().addAll(contenedor1, contenedor2);
                    contenedorTarea.getStyleClass().add("contenedor-tarea");

                    tarjetaTarea.getChildren().addAll(barraColor, contenedorTarea);

                    setGraphic(tarjetaTarea);
                }
            }
        });
    }

    private void actualizarEstadisticas() {
        vista.estadisticas.getChildren().clear();
        Label total = new Label(idioma.getString("estadisticas.total") + gestor.getNumTareas());
        total.getStyleClass().add("estadisticas");
        Label completadas = new Label(idioma.getString("estadisticas.completadas") + gestor.howManyCompletada());
        completadas.getStyleClass().add("estadisticas");
        Label pendientes = new Label(idioma.getString("estadisticas.pendientes") + gestor.howManyPendiente());
        pendientes.getStyleClass().add("estadisticas");

        vista.estadisticas.getChildren().addAll(total, completadas, pendientes);
        vista.estadisticas.setId("panelEstadisticas");
    }

    // Metodos para los filtros
    private void mostrarTodas() {
        for (Tarea tarea : gestor.getTareas()) {
            vista.lista.getItems().add(tarea);
        }
    }

    private void mostrarPendientes() {
        for (Tarea tarea : gestor.getTareas()) {
            if (!tarea.isCompletada()) {
                vista.lista.getItems().add(tarea);
            }
        }
    }

    private void mostrarCompletadas() {
        for (Tarea tarea : gestor.getTareas()) {
            if (tarea.isCompletada()) {
                vista.lista.getItems().add(tarea);
            }
        }
    }

    private void cambiarIdioma(String codigoIdioma) {
        idioma = ResourceBundle.getBundle("idiomas.mensajes", Locale.forLanguageTag(codigoIdioma));
        actualizarTextos();
    }

    private void actualizarTextos() {
        vista.idiomas.setPromptText(idioma.getString("idiomas.menu"));

        vista.añadir.setText(idioma.getString("boton.añadir"));
        vista.editar.setText(idioma.getString("boton.editar"));
        vista.eliminar.setText(idioma.getString("boton.eliminar"));
        vista.completarTarea.setText(idioma.getString("boton.completar"));
        vista.cambiarFecha.setText(idioma.getString("boton.fecha"));

        vista.filtros.setPromptText(idioma.getString("filtros.menu"));
        
        Filtros seleccionado = vista.filtros.getValue();
        actualizarFiltros();
        vista.filtros.getItems().setAll(Filtros.values());
        vista.filtros.setValue(seleccionado);

        vista.cambiarPrioridad.setText(idioma.getString("prioridad.menu"));
        vista.cambiarAlta.setText(idioma.getString("prioridad.alta"));
        vista.cambiarNormal.setText(idioma.getString("prioridad.normal"));
        vista.cambiarBaja.setText(idioma.getString("prioridad.baja"));

        vista.lista.refresh();
        actualizarEstadisticas();

    }

    private void actualizarFiltros() {

        vista.filtros.setConverter(
                new javafx.util.StringConverter<Filtros>() {

                    @Override
                    public String toString(Filtros filtro) {

                        if (filtro == null) {
                            return "";
                        }

                        return switch (filtro) {

                            case TODAS ->
                                idioma.getString("filtro.todas");

                            case PENDIENTES ->
                                idioma.getString("filtro.pendientes");

                            case COMPLETADAS ->
                                idioma.getString("filtro.completadas");
                        };
                    }

                    @Override
                    public Filtros fromString(String string) {
                        return null;
                    }
                });
    }
}

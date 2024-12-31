package Controlador;

import Modelo.Categoria;
import Modelo.CategoriaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.TableCell;

public class Gestion_CategoriasController implements Initializable {

    @FXML
    private TextField txt_nombre;
    @FXML
    private TableView<Categoria> tbl_categorias;
    @FXML
    private TableColumn<Categoria, Integer> col_id;
    @FXML
    private TableColumn<Categoria, String> col_nombre;
    @FXML
    private TableColumn<Categoria, String> col_estado;
    @FXML
    private TableColumn<Categoria, String> col_accion;

    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private ObservableList<Categoria> listaCategorias;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_estado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar la columna de acción con botones
        col_accion.setCellFactory(param -> new TableCell<>() {
            private final Button btnInhabilitar = new Button("Inhabilitar");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    btnInhabilitar.setOnAction(event -> {
                        Categoria categoria = getTableView().getItems().get(getIndex());
                        inhabilitarCategoria(categoria);
                    });
                    setGraphic(btnInhabilitar);
                    setText(null);
                }
            }
        });

        cargarCategorias();
    }

    private void cargarCategorias() {
        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        listaCategorias = FXCollections.observableArrayList(categorias);
        tbl_categorias.setItems(listaCategorias);
    }

    @FXML
    private void agregarCategoria(ActionEvent event) {
        String nombre = txt_nombre.getText();
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "El campo nombre no puede estar vacío.");
            return;
        }
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre(nombre);
        nuevaCategoria.setEstado('A');
        categoriaDAO.guardar(nuevaCategoria);
        cargarCategorias();
        txt_nombre.clear();
    }

    private void inhabilitarCategoria(Categoria categoria) {
        if (categoria != null) {
            categoria.setEstado('I');
            categoriaDAO.actualizar(categoria);
            cargarCategorias();
        } else {
            mostrarAlerta("Error", "Debe seleccionar una categoría.");
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
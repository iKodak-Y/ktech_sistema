package Controlador;

import Modelo.Categoria;
import Modelo.CategoriaDAO;
import Modelo.Producto;
import Modelo.ProductoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gestion_ProductosController implements Initializable {

    @FXML
    private TextField txt_codigo;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_precio;
    @FXML
    private TextField txt_pvp;
    @FXML
    private TextField txt_stock_inicial;
    @FXML
    private TextField txt_stock_actual; // Asegúrate de que este campo esté definido
    @FXML
    private TextField txt_iva;
    @FXML
    private TextField txt_estado;
    @FXML
    private ComboBox<Categoria> cmb_categoria;
    @FXML
    private Button btn_guardar;
    @FXML
    private Button btn_eliminar;
    @FXML
    private TableView<Producto> tbl_productos;
    @FXML
    private TableColumn<Producto, String> col_codigo;
    @FXML
    private TableColumn<Producto, String> col_nombre;
    @FXML
    private TableColumn<Producto, Double> col_precio;
    @FXML
    private TableColumn<Producto, Double> col_pvp;
    @FXML
    private TableColumn<Producto, Integer> col_stock_actual;
    @FXML
    private TableColumn<Producto, Double> col_iva;
    @FXML
    private TableColumn<Producto, String> col_estado;
    @FXML
    private TextField txt_buscar_producto;
    @FXML
    private Button btn_buscar;

    private ProductoDAO productoDAO = new ProductoDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private ObservableList<Producto> listaProductos;
    private ObservableList<Categoria> listaCategorias;
    private Producto productoSeleccionado; // Variable para almacenar el producto seleccionado
    @FXML
    private Button btn_limpiar;
    @FXML
    private Button btn_cerrar;
    @FXML
    private Button btn_categoria;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        col_codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        col_pvp.setCellValueFactory(new PropertyValueFactory<>("pvp"));
        col_stock_actual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        col_iva.setCellValueFactory(new PropertyValueFactory<>("iva"));
        col_estado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarProductos();
        cargarCategorias();
        tbl_productos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarProducto(newSelection);
                productoSeleccionado = newSelection; // Almacenar el producto seleccionado
            }
        });
    }

    private void cargarProductos() {
        List<Producto> productos = productoDAO.obtenerTodos();
        listaProductos = FXCollections.observableArrayList(productos);
        tbl_productos.setItems(listaProductos);
    }

    private void cargarCategorias() {
        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        listaCategorias = FXCollections.observableArrayList(categorias);
        cmb_categoria.setItems(listaCategorias);
    }

    private void mostrarProducto(Producto producto) {
        if (producto != null) {
            txt_codigo.setText(producto.getCodigo());
            txt_nombre.setText(producto.getNombre());
            txt_precio.setText(String.valueOf(producto.getPrecio()));
            txt_pvp.setText(String.valueOf(producto.getPvp()));
            txt_stock_inicial.setText(String.valueOf(producto.getStockInicial()));
            txt_stock_actual.setText(String.valueOf(producto.getStockActual())); // Asegúrate de que este campo esté definido
            txt_iva.setText(String.valueOf(producto.getIva()));
            txt_estado.setText(producto.getEstado());
            for (Categoria categoria : listaCategorias) {
                if (categoria.getId() == producto.getIdCategoria()) {
                    cmb_categoria.setValue(categoria);
                    break;
                }
            }
        }
    }

    private void limpiarCampos() {
        txt_codigo.clear();
        txt_nombre.clear();
        txt_precio.clear();
        txt_pvp.clear();
        txt_stock_inicial.clear();
        txt_stock_actual.clear(); // Asegúrate de que este campo esté definido
        txt_iva.clear();
        txt_estado.clear();
        cmb_categoria.setValue(null);
        productoSeleccionado = null; // Limpiar el producto seleccionado
    }

    @FXML
    private void acc_guardar(ActionEvent event) {
        String codigo = txt_codigo.getText();
        String nombre = txt_nombre.getText();
        double precio = Double.parseDouble(txt_precio.getText());
        double pvp = Double.parseDouble(txt_pvp.getText());
        int stockInicial = Integer.parseInt(txt_stock_inicial.getText());
        int stockActual = Integer.parseInt(txt_stock_actual.getText()); // Asegúrate de que este campo esté definido
        double iva = Double.parseDouble(txt_iva.getText());
        String estado = txt_estado.getText();
        Categoria categoriaSeleccionada = cmb_categoria.getValue();

        if (productoSeleccionado == null) {
            // Insertar nuevo producto
            Producto producto = new Producto();
            producto.setCodigo(codigo);
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setPvp(pvp);
            producto.setStockInicial(stockInicial);
            producto.setStockActual(stockActual); // Asegúrate de que este campo esté definido
            producto.setIva(iva);
            producto.setEstado(estado);
            producto.setIdCategoria(categoriaSeleccionada.getId());
            productoDAO.guardar(producto);
        } else {
            // Actualizar producto existente
            productoSeleccionado.setCodigo(codigo);
            productoSeleccionado.setNombre(nombre);
            productoSeleccionado.setPrecio(precio);
            productoSeleccionado.setPvp(pvp);
            productoSeleccionado.setStockInicial(stockInicial);
            productoSeleccionado.setStockActual(stockActual); // Asegúrate de que este campo esté definido
            productoSeleccionado.setIva(iva);
            productoSeleccionado.setEstado(estado);
            productoSeleccionado.setIdCategoria(categoriaSeleccionada.getId());
            productoDAO.actualizar(productoSeleccionado);
        }

        cargarProductos();
        limpiarCampos();
    }

    @FXML
    private void acc_eliminar(ActionEvent event) {
        Producto productoSeleccionado = tbl_productos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            productoDAO.eliminar(productoSeleccionado.getId());
            cargarProductos();
            limpiarCampos();
        }
    }

    @FXML
    private void acc_buscar(ActionEvent event) {
        String termino = txt_buscar_producto.getText();
        List<Producto> productos = productoDAO.buscarPorCodigoONombre(termino);
        if (productos != null && !productos.isEmpty()) {
            listaProductos = FXCollections.observableArrayList(productos);
            tbl_productos.setItems(listaProductos);
        } else {
            tbl_productos.getItems().clear();
        }
    }

    @FXML
    private void acc_limpiar(ActionEvent event) {
        limpiarCampos();
    }

    @FXML
    private void acc_cerrar(ActionEvent event) {
        try {
            // abrir formulario
            String formulario = "/Vista/Principal.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formulario));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.setResizable(false);
            stage.show();
            //cerrar formulario
            Stage myStage = (Stage) this.btn_cerrar.getScene().getWindow();
            myStage.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    @FXML
    private void acc_categoria(ActionEvent event) {
        try {
            String formulario = "/Vista/Gestion_Categorias.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formulario));
            Parent root = loader.load();

            Gestion_CategoriasController controlador = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            // evita que la paguina se ponga en modo ventana completa
            stage.setResizable(false);
            stage.show();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

package Controlador;

import Modelo.Venta;
import Modelo.VentaDAO;
import Modelo.DetalleVenta;
import Modelo.Producto;
import Modelo.ProductoDAO;
import Modelo.Cliente;
import Modelo.ClienteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TicketsVentasController implements Initializable {

    @FXML
    private TextField txt_buscar_producto;
    @FXML
    private TextField txt_cliente_id;
    @FXML
    private TextField txt_producto_id;
    @FXML
    private TextField txt_cantidad;
    @FXML
    private TextField txt_total;
    @FXML
    private ComboBox<String> cmb_metodo_pago;

    @FXML
    private TableView<DetalleVenta> tbl_detalle_venta;
    @FXML
    private TableColumn<DetalleVenta, String> colCodigo; // Cambiamos el nombre para que coincida con el FXML
    @FXML
    private TableColumn<DetalleVenta, String> col_producto;
    @FXML
    private TableColumn<DetalleVenta, Integer> col_cantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> col_precio;
    @FXML
    private TableColumn<DetalleVenta, Double> col_total;

    private ObservableList<DetalleVenta> listaDetalleVenta;
    private ProductoDAO productoDAO = new ProductoDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private VentaDAO ventaDAO = new VentaDAO();
    @FXML
    private RadioButton radioConsumidorFinal;
    @FXML
    private RadioButton radioConDatos;
    @FXML
    private VBox clienteBox;
    @FXML
    private TextField txtNombreCliente;
    @FXML
    private TextField txtApellidoCliente;
    @FXML
    private TextField txtTelefonoCliente;
    @FXML
    private TextField txtDireccionCliente;
    @FXML
    private TextField txtNombreProducto;
    @FXML
    private TextField txtPrecioUnitario; // txtPVP
    @FXML
    private Button btn_cerrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoProducto")); // Mostrar el código del producto
        col_producto.setCellValueFactory(new PropertyValueFactory<>("productoNombre"));
        col_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        col_precio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario")); // Mostrar el PVP
        col_total.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        listaDetalleVenta = FXCollections.observableArrayList();
        tbl_detalle_venta.setItems(listaDetalleVenta);

        cmb_metodo_pago.setItems(FXCollections.observableArrayList("Efectivo", "Tarjeta", "Transferencia"));

        // Listener para mostrar/ocultar el VBox de cliente según el RadioButton seleccionado
        radioConsumidorFinal.setOnAction(e -> clienteBox.setVisible(false));
        radioConDatos.setOnAction(e -> clienteBox.setVisible(true));
    }

    @FXML
    private void acc_buscar_producto(ActionEvent event) {
        String termino = txt_buscar_producto.getText();
        if (termino.isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar un nombre o código de producto para buscar.");
            return;
        }

        Producto producto = productoDAO.buscarUnoPorCodigoONombre(termino);
        if (producto != null) {
            txt_producto_id.setText(String.valueOf(producto.getId()));
            txtNombreProducto.setText(producto.getNombre());
            txtPrecioUnitario.setText(String.valueOf(producto.getPvp())); // Mostrar PVP
        } else {
            mostrarAlerta("No encontrado", "No se encontró ningún producto con el término ingresado.");
        }
    }

    @FXML
    private void acc_buscar_cliente(ActionEvent event) {
        String clienteId = txt_cliente_id.getText();
        if (clienteId.isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar un ID de cliente para buscar.");
            return;
        }

        Cliente cliente = clienteDAO.buscarPorCedulaONombre(clienteId);
        if (cliente != null) {
            txtNombreCliente.setText(cliente.getNombre());
            txtApellidoCliente.setText(cliente.getApellido());
            txtTelefonoCliente.setText(cliente.getTelefono());
            txtDireccionCliente.setText(cliente.getDireccion());
        } else {
            mostrarAlerta("No encontrado", "No se encontró ningún cliente con el ID ingresado.");
        }
    }

    @FXML
    private void acc_agregar_producto(ActionEvent event) {
        try {
            int productoId = Integer.parseInt(txt_producto_id.getText());
            int cantidad = Integer.parseInt(txt_cantidad.getText());

            Producto producto = productoDAO.buscarPorId(productoId);
            if (producto == null) {
                mostrarAlerta("Error", "El producto no existe.");
                return;
            }

            if (cantidad <= 0 || cantidad > producto.getStockActual()) {
                mostrarAlerta("Error", "La cantidad debe ser válida y no superar el stock disponible.");
                return;
            }

            double pvp = Double.parseDouble(txtPrecioUnitario.getText()); // Usar PVP para el cálculo
            double subtotal = cantidad * pvp;
            double iva = subtotal * producto.getIva(); // Usar el IVA del producto
            double total = subtotal + iva;

            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(productoId);
            detalle.setCodigoProducto(producto.getCodigo()); // Usar el código del producto
            detalle.setProductoNombre(producto.getNombre());
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(pvp); // Usar el PVP para el cálculo
            detalle.setSubtotal(subtotal);
            detalle.setIva(iva); 
            detalle.setTotal(total);

            listaDetalleVenta.add(detalle);
            calcularTotalVenta();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Debe ingresar valores válidos para el producto y la cantidad.");
        }
    }

    @FXML
    private void acc_registrar_venta(ActionEvent event) {
        try {
            // Validar y obtener datos del cliente
            Integer clienteId = null;
            if (!radioConsumidorFinal.isSelected()) {
                if (txt_cliente_id.getText().isEmpty()) {
                    mostrarAlerta("Error", "Debe ingresar un ID de cliente o seleccionar 'Consumidor Final'.");
                    return;
                }
                clienteId = Integer.parseInt(txt_cliente_id.getText());
            } else {
                clienteId = 1; // Usar 1 para "Consumidor Final"
            }

            String metodoPago = cmb_metodo_pago.getValue();
            double total = Double.parseDouble(txt_total.getText());

            if (metodoPago == null || listaDetalleVenta.isEmpty()) {
                mostrarAlerta("Error", "Debe seleccionar un método de pago y agregar productos.");
                return;
            }

            // Crear la venta y registrar
            Venta venta = new Venta();
            venta.setIdCliente(clienteId); // Asegurar que el clienteId es correcto
            venta.setFecha(LocalDateTime.now());
            venta.setTotal(total);
            venta.setEstado("Emitida"); // Usar 'Emitida' para representar "Emitida"
            venta.setDetalles(new ArrayList<>(listaDetalleVenta));

            boolean exito = ventaDAO.registrarVenta(venta);
            if (exito) {
                mostrarAlerta("Éxito", "La venta fue registrada correctamente.");
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "Ocurrió un error al registrar la venta.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Debe ingresar valores válidos para los campos de cliente y total.");
        }
    }

    @FXML
    private void acc_limpiar(ActionEvent event) {
        limpiarCampos();
    }

    private void limpiarCampos() {
        txt_buscar_producto.clear();
        txt_cliente_id.clear();
        txt_producto_id.clear();
        txt_cantidad.clear();
        txt_total.clear();
        cmb_metodo_pago.getSelectionModel().clearSelection();
        listaDetalleVenta.clear();
        txtNombreCliente.clear();
        txtApellidoCliente.clear();
        txtTelefonoCliente.clear();
        txtDireccionCliente.clear();
        txtNombreProducto.clear();
        txtPrecioUnitario.clear();
    }

    private void calcularTotalVenta() {
        double total = listaDetalleVenta.stream().mapToDouble(DetalleVenta::getTotal).sum();
        txt_total.setText(String.valueOf(total));
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
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
            e.printStackTrace();
        }
    }
}
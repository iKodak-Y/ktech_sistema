package Controlador;

import Modelo.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;

public class FacturacionController implements Initializable {

    @FXML
    private TextField txtCliente;
    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private TableView<DetalleFactura> tblDetalles;
    @FXML
    private TableColumn<DetalleFactura, String> colCodigo;
    @FXML
    private TableColumn<DetalleFactura, String> colDescripcion;
    @FXML
    private TableColumn<DetalleFactura, Integer> colCantidad;
    @FXML
    private TableColumn<DetalleFactura, Double> colPrecio;
    @FXML
    private TableColumn<DetalleFactura, Double> colSubtotal;
    @FXML
    private TableColumn<DetalleFactura, Double> colIVA;
    @FXML
    private TableColumn<DetalleFactura, Double> colTotal;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblIVA;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblInfoCliente;

    private FacturaDAO facturaDAO = new FacturaDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    private Cliente clienteSeleccionado;
    private ObservableList<DetalleFactura> detallesFactura;
    private ObservableList<String> productosObservableList;
    private ListView<String> listaProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarListaProductos();
        detallesFactura = FXCollections.observableArrayList();
        tblDetalles.setItems(detallesFactura);

        detallesFactura.addListener((javafx.collections.ListChangeListener.Change<? extends DetalleFactura> c) -> {
            calcularTotales();
        });
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("productoNombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colIVA.setCellValueFactory(new PropertyValueFactory<>("iva"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Agregar botón de eliminar en cada fila
        TableColumn<DetalleFactura, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setOnAction(event -> {
                    DetalleFactura detalle = getTableView().getItems().get(getIndex());
                    detallesFactura.remove(detalle);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });
        tblDetalles.getColumns().add(colAcciones);
    }

    private void configurarListaProductos() {
        // Crear y configurar el ListView
        listaProductos = new ListView<>();
        listaProductos.setPrefHeight(100);
        listaProductos.setVisible(false);

        // Agregar el ListView debajo del campo de búsqueda
        GridPane gridProductos = (GridPane) txtBuscarProducto.getParent();
        GridPane.setColumnIndex(listaProductos, 1);
        GridPane.setRowIndex(listaProductos, 2);
        GridPane.setColumnSpan(listaProductos, 2);
        gridProductos.getChildren().add(listaProductos);

        // Configurar el listener para actualizar la lista mientras se escribe
        txtBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                listaProductos.setVisible(false);
            } else {
                List<Producto> productos = productoDAO.buscarPorCodigoONombre(newValue);
                productosObservableList = FXCollections.observableArrayList();
                for (Producto producto : productos) {
                    productosObservableList.add(String.format("%s - %s", producto.getCodigo(), producto.getNombre()));
                }
                listaProductos.setItems(productosObservableList);
                listaProductos.setVisible(!productosObservableList.isEmpty());
            }
        });

        // Configurar la selección de producto
        listaProductos.setOnMouseClicked(event -> {
            String seleccion = listaProductos.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                String codigo = seleccion.split(" - ")[0];
                Producto producto = productoDAO.buscarPorCodigo(codigo);
                if (producto != null) {
                    mostrarDialogoCantidad(producto);
                    listaProductos.setVisible(false);
                    txtBuscarProducto.clear();
                }
            }
        });
    }

    @FXML
    private void buscarCliente() {
        String identificacion = txtCliente.getText().trim();
        if (!identificacion.isEmpty()) {
            Cliente cliente = clienteDAO.buscarPorCedulaONombre(identificacion);
            if (cliente != null) {
                clienteSeleccionado = cliente;
                // Mostrar información del cliente
                lblInfoCliente.setText(String.format("%s %s - %s",
                        cliente.getNombre(),
                        cliente.getApellido(),
                        cliente.getCedulaRUC()));
                lblInfoCliente.setVisible(true);
            } else {
                mostrarError("Error", "Cliente no encontrado");
                clienteSeleccionado = null;
                lblInfoCliente.setVisible(false);
            }
        }
    }

    private void mostrarDialogoCantidad(Producto producto) {
        Dialog<Pair<Integer, Double>> dialog = new Dialog<>();
        dialog.setTitle("Agregar Producto");
        dialog.setHeaderText("Producto: " + producto.getNombre());

        // Botones
        ButtonType buttonTypeOk = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);

        // Crear grid para campos
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField cantidadField = new TextField("1");
        TextField precioField = new TextField(String.format("%.2f", producto.getPvp()));

        grid.add(new Label("Cantidad:"), 0, 0);
        grid.add(cantidadField, 1, 0);
        grid.add(new Label("Precio Unitario:"), 0, 1);
        grid.add(precioField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convertir el resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                try {
                    int cantidad = Integer.parseInt(cantidadField.getText());
                    double precio = Double.parseDouble(precioField.getText());
                    if (cantidad > 0 && precio >= 0) {
                        return new Pair<>(cantidad, precio);
                    }
                } catch (NumberFormatException e) {
                    // Error de conversión
                }
            }
            return null;
        });

        Optional<Pair<Integer, Double>> result = dialog.showAndWait();
        result.ifPresent(cantidadPrecio -> {
            agregarProductoADetalle(producto, cantidadPrecio.getKey(), cantidadPrecio.getValue());
        });
    }

    private void agregarProductoADetalle(Producto producto, int cantidad, double precioUnitario) {
        DetalleFactura detalle = new DetalleFactura(
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                cantidad,
                precioUnitario
        );

        // Calcular subtotal
        double subtotal = cantidad * precioUnitario;
        detalle.setSubtotal(subtotal);

        // Calcular IVA (usando el valor de la base de datos, ej: 0.15 para 15%)
        double iva = subtotal * producto.getIva();
        detalle.setIva(iva);

        // Calcular total
        detalle.setTotal(subtotal + iva);

        detallesFactura.add(detalle);
        calcularTotales();
    }

    private void calcularTotales() {
        double subtotal = 0;
        double iva = 0;
        double total = 0;

        for (DetalleFactura detalle : detallesFactura) {
            subtotal += detalle.getSubtotal();
            iva += detalle.getIva();
            total += detalle.getTotal();
        }

        lblSubtotal.setText(String.format("%.2f", subtotal));
        lblIVA.setText(String.format("%.2f", iva));
        lblTotal.setText(String.format("%.2f", total));
    }

    @FXML
    private void emitirFactura() {
        if (validarFactura()) {
            try {
                Factura factura = prepararFactura();
                if (facturaDAO.guardar(factura)) {
                    mostrarMensaje("Éxito", "Factura emitida correctamente");
                    limpiarFormulario();
                } else {
                    mostrarError("Error", "No se pudo guardar la factura");
                }
            } catch (Exception e) {
                mostrarError("Error al emitir factura", e.getMessage());
            }
        }
    }

    private boolean validarFactura() {
        if (clienteSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un cliente");
            return false;
        }

        if (detallesFactura.isEmpty()) {
            mostrarError("Error", "Debe agregar al menos un producto");
            return false;
        }

        return true;
    }

    private Factura prepararFactura() {
        Factura factura = new Factura();
        factura.setIdCliente(clienteSeleccionado.getIdCliente());
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotal(Double.parseDouble(lblSubtotal.getText()));
        factura.setIva(Double.parseDouble(lblIVA.getText()));
        factura.setTotal(Double.parseDouble(lblTotal.getText()));
        factura.setEstado("EMITIDA");
        factura.setDetalles(new ArrayList<>(detallesFactura));

        // Configurar datos de facturación electrónica
        factura.setAmbiente("1"); // 1: Pruebas, 2: Producción
        factura.setTipoEmision("1"); // 1: Normal
        factura.setClaveAcceso(generarClaveAcceso());

        return factura;
    }

    private String generarClaveAcceso() {
        LocalDateTime now = LocalDateTime.now();
        String fecha = now.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String tipoComprobante = "01"; // 01 para facturas
        String rucEmisor = "1234567890001"; // Debe venir de la configuración
        String ambiente = "1"; // 1: Pruebas, 2: Producción
        String serie = "001001"; // Establecimiento + Punto Emisión
        String secuencial = String.format("%09d", obtenerSecuencial());
        String codigoNumerico = "12345678"; // 8 dígitos
        String tipoEmision = "1"; // 1: Normal

        String claveAcceso = fecha
                + tipoComprobante
                + rucEmisor
                + ambiente
                + serie
                + secuencial
                + codigoNumerico
                + tipoEmision;

        // Agregar dígito verificador
        int digitoVerificador = calcularDigitoVerificador(claveAcceso);
        return claveAcceso + digitoVerificador;
    }

    private int obtenerSecuencial() {
        // Implementar lógica para obtener el siguiente secuencial
        // Por ahora retornamos un valor de prueba
        return 1;
    }

    private int calcularDigitoVerificador(String claveAcceso) {
        int[] coeficientes = {2, 3, 4, 5, 6, 7};
        int suma = 0;
        int indiceCoeficiente = 0;

        for (int i = claveAcceso.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(claveAcceso.charAt(i));
            suma += digito * coeficientes[indiceCoeficiente % 6];
            indiceCoeficiente++;
        }

        int digitoVerificador = 11 - (suma % 11);
        if (digitoVerificador == 11) {
            digitoVerificador = 0;
        } else if (digitoVerificador == 10) {
            digitoVerificador = 1;
        }

        return digitoVerificador;
    }

    @FXML
    private void buscarProducto() {
        String termino = txtBuscarProducto.getText().trim();
        if (!termino.isEmpty()) {
            Producto producto = productoDAO.buscarUnoPorCodigoONombre(termino);
            if (producto != null) {
                mostrarDialogoCantidad(producto);
            } else {
                mostrarError("Error", "Producto no encontrado");
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtCliente.clear();
        txtBuscarProducto.clear();
        detallesFactura.clear();
        clienteSeleccionado = null;
        lblInfoCliente.setText("");
        lblSubtotal.setText("0.00");
        lblIVA.setText("0.00");
        lblTotal.setText("0.00");
    }

    @FXML
    private void cancelar() {
        if (!detallesFactura.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar cancelación");
            alert.setHeaderText(null);
            alert.setContentText("¿Está seguro que desea cancelar la factura?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                limpiarFormulario();
            }
        } else {
            limpiarFormulario();
        }
    }

    @FXML
    private void cerrar() {
        try {
            Stage stage = (Stage) txtCliente.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/Principal.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage nuevaVentana = new Stage();
            nuevaVentana.setScene(scene);
            nuevaVentana.setResizable(false);
            nuevaVentana.show();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

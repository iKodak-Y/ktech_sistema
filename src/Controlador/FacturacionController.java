package Controlador;

import Modelo.*;
import sri.FacturaElectronica;
import util.ValidacionesUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.stage.Stage;

public class FacturacionController {

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

    private FacturaElectronicaDAO facturaDAO = new FacturaElectronicaDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    private ObservableList<DetalleFactura> detallesFactura;
    private Cliente clienteSeleccionado;
    private double totalFactura = 0.0;
    private double subtotalFactura = 0.0;
    private double ivaFactura = 0.0;

    @FXML
    public void initialize() {
        configurarTabla();
        detallesFactura = FXCollections.observableArrayList();
        tblDetalles.setItems(detallesFactura);

        // Agregar listener para actualizar totales cuando cambia la lista
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
    }

    @FXML
    private void buscarCliente() {
        String identificacion = txtCliente.getText().trim();
        if (!identificacion.isEmpty()) {
            Cliente cliente = clienteDAO.buscarPorIdentificacion(identificacion);
            if (cliente != null) {
                clienteSeleccionado = cliente;
                mostrarMensaje("Cliente encontrado", "Se ha seleccionado al cliente: " + cliente.getNombre());
            } else {
                mostrarError("Error", "Cliente no encontrado");
            }
        }
    }

    @FXML
    private void buscarProducto() {
        String termino = txtBuscarProducto.getText().trim();
        if (!termino.isEmpty()) {
            Producto producto = productoDAO.buscarUnoPorCodigoONombre(termino);
            if (producto != null) {
                // Mostrar diálogo para cantidad
                TextInputDialog dialog = new TextInputDialog("1");
                dialog.setTitle("Cantidad");
                dialog.setHeaderText("Ingrese la cantidad");
                dialog.setContentText("Cantidad:");

                dialog.showAndWait().ifPresent(cantidadStr -> {
                    try {
                        int cantidad = Integer.parseInt(cantidadStr);
                        if (cantidad > 0 && cantidad <= producto.getStockActual()) {
                            agregarProductoADetalle(producto, cantidad);
                        } else {
                            mostrarError("Error", "Cantidad no válida o stock insuficiente");
                        }
                    } catch (NumberFormatException e) {
                        mostrarError("Error", "Por favor ingrese un número válido");
                    }
                });
            } else {
                mostrarError("Error", "Producto no encontrado");
            }
        }
    }

    private void agregarProductoADetalle(Producto producto, int cantidad) {
        DetalleFactura detalle = new DetalleFactura(
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                cantidad,
                producto.getPvp()
        );
        detallesFactura.add(detalle);
        calcularTotales();
    }

    private void calcularTotales() {
        subtotalFactura = 0;
        ivaFactura = 0;
        totalFactura = 0;

        for (DetalleFactura detalle : detallesFactura) {
            subtotalFactura += detalle.getSubtotal();
            ivaFactura += detalle.getIva();
            totalFactura += detalle.getTotal();
        }

        // Actualizar labels con formato de 2 decimales
        lblSubtotal.setText(String.format("%.2f", subtotalFactura));
        lblIVA.setText(String.format("%.2f", ivaFactura));
        lblTotal.setText(String.format("%.2f", totalFactura));
    }

    @FXML
    private void emitirFactura() {
        if (validarFactura()) {
            try {
                // 1. Crear la factura
                Factura factura = prepararFactura();

                // 2. Generar factura electrónica
                FacturaElectronica facturaE = new FacturaElectronica();
                facturaE.setEstablecimiento(obtenerEstablecimiento());
                facturaE.setPuntoEmision(obtenerPuntoEmision());
                facturaE.setSecuencial(generarSecuencial());
                facturaE.setAmbiente("1"); // 1: Pruebas, 2: Producción
                facturaE.setTipoEmision("1"); // 1: Normal

                // 3. Generar XML
                String xml = facturaE.generarXML();

                // 4. Firmar
                if (facturaE.firmar()) {
                    // 5. Enviar al SRI
                    if (facturaE.enviar()) {
                        // 6. Obtener autorización
                        if (facturaE.autorizar()) {
                            // 7. Guardar en base de datos
                            if (facturaDAO.guardar(factura)) {
                                mostrarMensaje("Éxito", "Factura emitida correctamente");
                                limpiarFormulario();
                            }
                        }
                    }
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
        factura.setIdCliente(clienteSeleccionado.getId());
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotal(subtotalFactura);
        factura.setIva(ivaFactura);
        factura.setTotal(totalFactura);
        factura.setEstado("EMITIDA");
        factura.setDetalles(new ArrayList<>(detallesFactura));

        // Generar clave de acceso
        String claveAcceso = ValidacionesUtil.generarClaveAcceso(
                factura.getFechaEmision().format(DateTimeFormatter.ofPattern("ddMMyyyy")),
                "01", // 01: Factura
                clienteSeleccionado.getRuc(),
                factura.getAmbiente(),
                obtenerEstablecimiento(),
                obtenerPuntoEmision(),
                generarSecuencial(),
                "1" // Tipo de emisión: Normal
        );
        factura.setClaveAcceso(claveAcceso);

        return factura;
    }

    private String obtenerEstablecimiento() {
        return "001"; // Debería venir de configuración
    }

    private String obtenerPuntoEmision() {
        return "001"; // Debería venir de configuración
    }

    private String generarSecuencial() {
        // Implementar lógica para generar secuencial
        // Por ahora retornamos un valor de prueba
        return String.format("%09d", 1);
    }

    @FXML
    private void cancelar() {
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        txtCliente.clear();
        txtBuscarProducto.clear();
        detallesFactura.clear();
        clienteSeleccionado = null;
        calcularTotales();
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

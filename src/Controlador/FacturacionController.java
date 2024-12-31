// src/Controlador/FacturacionController.java
package Controlador;

import Modelo.*;
import sri.FacturaElectronica;
import util.ValidacionesUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;

public class FacturacionController {
    @FXML private TextField txtCliente;
    @FXML private TableView<DetalleFactura> tblDetalles;
    @FXML private Label lblSubtotal;
    @FXML private Label lblIVA;
    @FXML private Label lblTotal;
    
    private FacturaElectronicaDAO facturaDAO = new FacturaElectronicaDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    
    @FXML
    private void emitirFactura() {
        try {
            // 1. Crear la factura
            Factura factura = prepararFactura();
            
            // 2. Generar factura electrónica
            FacturaElectronica facturaE = new FacturaElectronica();
            
            // 3. Generar XML
            String xml = facturaE.generarXML();
            
            // 4. Firmar
            if (facturaE.firmar()) {
                // 5. Enviar al SRI
                if (facturaE.enviar()) {
                    // 6. Obtener autorización
                    if (facturaE.autorizar()) {
                        // 7. Guardar en base de datos
                        facturaDAO.guardar(factura);
                        mostrarMensaje("Éxito", "Factura emitida correctamente");
                    }
                }
            }
            
        } catch (Exception e) {
            mostrarError("Error al emitir factura", e.getMessage());
        }
    }
    
    private Factura prepararFactura() {
        // Implementación para preparar la factura
        return new Factura();
    }
}
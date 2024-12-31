package Controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PrincipalController implements Initializable {

    @FXML
    private Button btn_clientes;
    @FXML
    private Button btn_productos;
    @FXML
    private Button btn_reportes;
    @FXML
    private Button btn_facturacion;
    @FXML
    private Button btn_cerrar;
    @FXML
    private Button btn_venta;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void acc_clientes(ActionEvent event) {
        try {
            String formulario = "/Vista/Clientes.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formulario));
            Parent root = loader.load();

            ClientesController controlador = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            // evita que la paguina se ponga en modo ventana completa
            stage.setResizable(false);
            stage.show();

            Stage myStage = (Stage) this.btn_cerrar.getScene().getWindow();
            myStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void acc_productos(ActionEvent event) {
        try {
            String formulario = "/Vista/Gestion_Productos.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formulario));
            Parent root = loader.load();

            Gestion_ProductosController controlador = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            // evita que la paguina se ponga en modo ventana completa
            stage.setResizable(false);
            stage.show();

            Stage myStage = (Stage) this.btn_cerrar.getScene().getWindow();
            myStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void acc_reportes(ActionEvent event) {
    }

    @FXML
    private void acc_facturacion(ActionEvent event) {
        try {
            String formulario = "/Vista/Facturacion.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(formulario));
            Parent root = loader.load();

            FacturacionController controlador = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.setResizable(false);
            stage.show();

            Stage myStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            if (myStage != null) {
                myStage.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    @FXML
    private void acc_cerrar(ActionEvent event) {
        try {
            System.exit(0);
        } catch (Exception e) {
        }
    }

    @FXML
    private void acc_venta(ActionEvent event) {
        try {
            // Ruta del archivo FXML
            String formulario = "/Vista/TicketsVentas.fxml";

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formulario));
            Parent root = loader.load();

            // Obtener el controlador
            TicketsVentasController controlador = loader.getController();

            // Configurar la escena y el stage
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            // Evitar que la ventana se ponga en modo ventana completa
            stage.setResizable(false);
            stage.show();

            // Cerrar la ventana actual solo si btn_cerrar no es null
            Stage myStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            if (myStage != null) {
                myStage.close();
            }
        } catch (Exception e) {
            // Imprimir el mensaje de excepción para ayudar en la depuración
            e.printStackTrace();
        }
    }

}

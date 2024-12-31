package Controlador;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import Controlador.SqlConection;
import java.sql.Connection;
import javafx.scene.control.Alert;

/**
 *
 * @author isaac
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            // Crear una instancia de SqlConection
            SqlConection conexion = new SqlConection();

            // Intentar conectar a la base de datos
            Connection con = conexion.getConexion();

            // Verificar si la conexión fue exitosa
            if (con != null) {
                System.out.println("Conexión establecida con éxito.");
            } else {
                System.out.println("Main: Fallo al establecer conexión con la base de datos.");
            }

            // todo esta correcto
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/Vista/Principal.fxml"));
            Pane ventana = (Pane) loader.load();
            // Presentar la pantalla
            Scene scene = new Scene(ventana);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            // Mostrar alerta al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error al iniciar la aplicación: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

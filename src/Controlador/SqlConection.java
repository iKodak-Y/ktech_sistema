package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConection {
    public Connection getConexion() {
        String connectionUrl =
                "jdbc:sqlserver://IKODAK;" +
                "database=BD_KTECH;" +
                "user=sa;" +
                "password=admin;" +
                "timeout=30;" +
                "encrypt=true;trustServerCertificate=true";
        try {
            Connection con = DriverManager.getConnection(connectionUrl);
            System.out.println("Conexión exitosa a la base de datos.");
            return con;
        } catch (SQLException ex) {
            System.out.println("Error al conectar a la base de datos: " + ex.getMessage());
            return null;
        }
    }
}

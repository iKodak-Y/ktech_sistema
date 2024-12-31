// src/Modelo/FacturaElectronicaDAO.java
package Modelo;

import Controlador.SqlConection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaElectronicaDAO {
    
    public boolean guardar(Factura factura) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean resultado = false;
        
        try {
            con = new SqlConection().getConexion();
            con.setAutoCommit(false);
            
            // Insertar factura
            String sqlFactura = "INSERT INTO Facturas (ClaveAcceso, NumeroFactura, FechaEmision, " +
                              "IDCliente, Total, Subtotal, IVA, Estado, IDPuntoEmision, Ambiente, " +
                              "TipoEmision, XMLFirmado) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            
            pstmt = con.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS);
            // Establecer parámetros
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int idFactura = rs.getInt(1);
                    // Insertar detalles
                    resultado = insertarDetalles(con, idFactura, factura.getDetalles());
                }
            }
            
            if (resultado) {
                con.commit();
            } else {
                con.rollback();
            }
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // Cerrar conexiones
        }
        
        return resultado;
    }
    
    private boolean insertarDetalles(Connection con, int idFactura, 
                                   List<DetalleFactura> detalles) throws SQLException {
        String sql = "INSERT INTO DetalleFactura (IDFactura, IDProducto, Cantidad, " +
                    "PrecioUnitario, Subtotal, IVA, Total) VALUES (?,?,?,?,?,?,?)";
        
        PreparedStatement pstmt = con.prepareStatement(sql);
        
        for (DetalleFactura detalle : detalles) {
            // Establecer parámetros para cada detalle
            pstmt.addBatch();
        }
        
        int[] results = pstmt.executeBatch();
        return results.length == detalles.size();
    }
}
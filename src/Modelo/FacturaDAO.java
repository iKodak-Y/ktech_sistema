package Modelo;

import Controlador.SqlConection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {
    public boolean guardar(Factura factura) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        
        try {
            con = new SqlConection().getConexion();
            con.setAutoCommit(false);
            
            // Insertar factura
            String sqlFactura = "INSERT INTO Facturas (ClaveAcceso, NumeroFactura, FechaEmision, " +
                              "IdCliente, Total, Subtotal, IVA, Estado, NumeroAutorizacion, " +
                              "IdPuntoEmision, Ambiente, TipoEmision, XmlFirmado, XmlAutorizado) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, factura.getClaveAcceso());
            pstmt.setString(2, factura.getNumeroFactura());
            pstmt.setTimestamp(3, Timestamp.valueOf(factura.getFechaEmision()));
            pstmt.setInt(4, factura.getIdCliente());
            pstmt.setDouble(5, factura.getTotal());
            pstmt.setDouble(6, factura.getSubtotal());
            pstmt.setDouble(7, factura.getIva());
            pstmt.setString(8, factura.getEstado());
            pstmt.setString(9, factura.getNumeroAutorizacion());
            pstmt.setInt(10, factura.getIdPuntoEmision());
            pstmt.setString(11, factura.getAmbiente());
            pstmt.setString(12, factura.getTipoEmision());
            pstmt.setString(13, factura.getXmlFirmado());
            pstmt.setString(14, factura.getXmlAutorizado());
            
            pstmt.executeUpdate();
            
            // Obtener el ID generado para la factura
            generatedKeys = pstmt.getGeneratedKeys();
            int facturaId = -1;
            if (generatedKeys.next()) {
                facturaId = generatedKeys.getInt(1);
            }
            
            // Insertar detalles
            String sqlDetalle = "INSERT INTO DetallesFactura (IdFactura, IdProducto, Cantidad, " +
                              "PrecioUnitario, Subtotal, IVA, Total) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(sqlDetalle);
            for (DetalleFactura detalle : factura.getDetalles()) {
                pstmt.setInt(1, facturaId);
                pstmt.setInt(2, detalle.getIdProducto());
                pstmt.setInt(3, detalle.getCantidad());
                pstmt.setDouble(4, detalle.getPrecioUnitario());
                pstmt.setDouble(5, detalle.getSubtotal());
                pstmt.setDouble(6, detalle.getIva());
                pstmt.setDouble(7, detalle.getTotal());
                pstmt.executeUpdate();
            }
            
            con.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
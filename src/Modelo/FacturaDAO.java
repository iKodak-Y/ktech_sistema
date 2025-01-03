package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import utils.Database;

public class FacturaDAO {

    public boolean guardar(Factura factura) {
        Connection conn = null;
        PreparedStatement pstmtFactura = null;
        PreparedStatement pstmtDetalle = null;

        String sqlFactura = "INSERT INTO Facturas (ClaveAcceso, NumeroFactura, FechaEmision, IDCliente, Total, Subtotal, IVA, Estado, Ambiente, TipoEmision) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO DetalleFactura (IDFactura, IDProducto, Cantidad, PrecioUnitario, Subtotal, IVA, Total) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // Guardar factura
            pstmtFactura = conn.prepareStatement(sqlFactura, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtFactura.setString(1, factura.getClaveAcceso());
            pstmtFactura.setString(2, factura.getNumeroFactura());
            pstmtFactura.setObject(3, factura.getFechaEmision());
            pstmtFactura.setInt(4, factura.getIdCliente());
            pstmtFactura.setDouble(5, factura.getTotal());
            pstmtFactura.setDouble(6, factura.getSubtotal());
            pstmtFactura.setDouble(7, factura.getIva());
            pstmtFactura.setString(8, factura.getEstado());
            pstmtFactura.setString(9, factura.getAmbiente());
            pstmtFactura.setString(10, factura.getTipoEmision());
            pstmtFactura.executeUpdate();

            // Obtener ID de la factura generada
            int idFactura = -1;
            try (var rs = pstmtFactura.getGeneratedKeys()) {
                if (rs.next()) {
                    idFactura = rs.getInt(1);
                }
            }

            if (idFactura == -1) {
                throw new SQLException("Error al obtener el ID de la factura generada");
            }

            // Guardar detalles de la factura
            pstmtDetalle = conn.prepareStatement(sqlDetalle);
            for (DetalleFactura detalle : factura.getDetalles()) {
                pstmtDetalle.setInt(1, idFactura);
                pstmtDetalle.setInt(2, detalle.getIdProducto());
                pstmtDetalle.setInt(3, detalle.getCantidad());
                pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                pstmtDetalle.setDouble(5, detalle.getSubtotal());
                pstmtDetalle.setDouble(6, detalle.getIva());
                pstmtDetalle.setDouble(7, detalle.getTotal());
                pstmtDetalle.addBatch();
            }
            pstmtDetalle.executeBatch();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (pstmtFactura != null) {
                try {
                    pstmtFactura.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmtDetalle != null) {
                try {
                    pstmtDetalle.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
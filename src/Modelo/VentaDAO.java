package Modelo;

import Controlador.SqlConection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public boolean registrarVenta(Venta venta) {
        // SQL para insertar en la tabla Ventas
        String sqlVenta = "INSERT INTO Ventas (IdCliente, Fecha, Total, Estado) VALUES (?, ?, ?, ?)";
        
        // SQL para insertar en la tabla DetalleVenta
        String sqlDetalle = "INSERT INTO DetalleVenta (IdVenta, IdProducto, Cantidad, PrecioUnitario, Subtotal, IVA, Total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        // SQL para actualizar el stock de los productos
        String sqlUpdateStock = "UPDATE dbo.Productos SET StockActual = StockActual - ? WHERE IdProducto = ?";

        try (Connection con = new SqlConection().getConexion()) {
            // Iniciar transacción
            con.setAutoCommit(false);

            // Insertar en la tabla Ventas
            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, venta.getIdCliente());
                psVenta.setTimestamp(2, Timestamp.valueOf(venta.getFecha()));
                psVenta.setDouble(3, venta.getTotal());
                psVenta.setString(4, venta.getEstado());

                int affectedRows = psVenta.executeUpdate();
                if (affectedRows == 0) {
                    con.rollback();
                    return false;
                }

                // Obtener el ID de la venta recién insertada
                try (ResultSet generatedKeys = psVenta.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        venta.setIdVenta(generatedKeys.getInt(1));
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            // Insertar en la tabla DetalleVenta
            try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    psDetalle.setInt(1, venta.getIdVenta());
                    psDetalle.setInt(2, detalle.getIdProducto());
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setDouble(4, detalle.getPrecioUnitario());
                    psDetalle.setDouble(5, detalle.getSubtotal());
                    psDetalle.setDouble(6, detalle.getIva());
                    psDetalle.setDouble(7, detalle.getTotal());
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();
            }

            // Actualizar el stock de los productos
            try (PreparedStatement psUpdateStock = con.prepareStatement(sqlUpdateStock)) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    psUpdateStock.setInt(1, detalle.getCantidad());
                    psUpdateStock.setInt(2, detalle.getIdProducto());
                    psUpdateStock.addBatch();
                }
                psUpdateStock.executeBatch();
            }

            // Confirmar transacción
            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Venta> obtenerVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM Ventas";

        try (Connection con = new SqlConection().getConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Venta venta = new Venta();
                venta.setIdVenta(rs.getInt("IdVenta"));
                venta.setIdCliente(rs.getInt("IdCliente"));
                venta.setFecha(rs.getTimestamp("Fecha").toLocalDateTime());
                venta.setTotal(rs.getDouble("Total"));
                venta.setEstado(rs.getString("Estado"));
                ventas.add(venta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ventas;
    }
}
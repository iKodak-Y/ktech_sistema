package Modelo;

import Controlador.SqlConection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Productos WHERE Estado = 'A'";

        try (Connection con = new SqlConection().getConexion(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("IDProducto"));
                producto.setCodigo(rs.getString("Codigo"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setPrecio(rs.getDouble("Precio"));
                producto.setPvp(rs.getDouble("PVP"));
                producto.setStockInicial(rs.getInt("StockInicial"));
                producto.setStockActual(rs.getInt("StockActual"));
                producto.setIva(rs.getDouble("IVA"));
                producto.setEstado(rs.getString("Estado"));
                producto.setIdCategoria(rs.getInt("IDCategoria"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public Producto buscarUnoPorCodigoONombre(String termino) {
        String sql = "SELECT TOP 1 * FROM Productos WHERE (Codigo = ? OR Nombre LIKE ?) AND Estado = 'A'";

        try (Connection con = new SqlConection().getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, termino);
            pstmt.setString(2, "%" + termino + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getInt("IDProducto"));
                    producto.setCodigo(rs.getString("Codigo"));
                    producto.setNombre(rs.getString("Nombre"));
                    producto.setPrecio(rs.getDouble("Precio"));
                    producto.setPvp(rs.getDouble("PVP"));
                    producto.setStockInicial(rs.getInt("StockInicial"));
                    producto.setStockActual(rs.getInt("StockActual"));
                    producto.setIva(rs.getDouble("IVA"));
                    producto.setEstado(rs.getString("Estado"));
                    producto.setIdCategoria(rs.getInt("IDCategoria"));
                    return producto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Producto buscarPorId(int idProducto) {
        String sql = "SELECT * FROM Productos WHERE IDProducto = ? AND Estado = 'A'";

        try (Connection con = new SqlConection().getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getInt("IDProducto"));
                    producto.setCodigo(rs.getString("Codigo"));
                    producto.setNombre(rs.getString("Nombre"));
                    producto.setPrecio(rs.getDouble("Precio"));
                    producto.setPvp(rs.getDouble("PVP"));
                    producto.setStockInicial(rs.getInt("StockInicial"));
                    producto.setStockActual(rs.getInt("StockActual"));
                    producto.setIva(rs.getDouble("IVA"));
                    producto.setEstado(rs.getString("Estado"));
                    producto.setIdCategoria(rs.getInt("IDCategoria"));
                    return producto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Producto buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM Productos WHERE Codigo = ? AND Estado = 'A'";

        try (Connection con = new SqlConection().getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, codigo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getInt("IDProducto"));
                    producto.setCodigo(rs.getString("Codigo"));
                    producto.setNombre(rs.getString("Nombre"));
                    producto.setPrecio(rs.getDouble("Precio"));
                    producto.setPvp(rs.getDouble("PVP"));
                    producto.setStockInicial(rs.getInt("StockInicial"));
                    producto.setStockActual(rs.getInt("StockActual"));
                    producto.setIva(rs.getDouble("IVA"));
                    producto.setEstado(rs.getString("Estado"));
                    producto.setIdCategoria(rs.getInt("IDCategoria"));
                    return producto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Producto> buscarPorCodigoONombre(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Productos WHERE (Codigo LIKE ? OR Nombre LIKE ?) AND Estado = 'A'";

        try (Connection con = new SqlConection().getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, "%" + termino + "%");
            pstmt.setString(2, "%" + termino + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getInt("IDProducto"));
                    producto.setCodigo(rs.getString("Codigo"));
                    producto.setNombre(rs.getString("Nombre"));
                    producto.setPrecio(rs.getDouble("Precio"));
                    producto.setPvp(rs.getDouble("PVP"));
                    producto.setStockInicial(rs.getInt("StockInicial"));
                    producto.setStockActual(rs.getInt("StockActual"));
                    producto.setIva(rs.getDouble("IVA"));
                    producto.setEstado(rs.getString("Estado"));
                    producto.setIdCategoria(rs.getInt("IDCategoria"));
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public boolean guardar(Producto producto) {
        String sql = "INSERT INTO Productos (Codigo, Nombre, Precio, PVP, StockInicial, StockActual, IVA, Estado, IDCategoria) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = new SqlConection().getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setDouble(4, producto.getPvp());
            pstmt.setInt(5, producto.getStockInicial());
            pstmt.setInt(6, producto.getStockActual());
            pstmt.setDouble(7, producto.getIva());
            pstmt.setString(8, producto.getEstado());
            pstmt.setInt(9, producto.getIdCategoria());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Productos SET Codigo = ?, Nombre = ?, Precio = ?, PVP = ?, StockInicial = ?, StockActual = ?, IVA = ?, Estado = ?, IDCategoria = ? WHERE IDProducto = ?";

        try (Connection con = new SqlConection().getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setDouble(4, producto.getPvp());
            pstmt.setInt(5, producto.getStockInicial());
            pstmt.setInt(6, producto.getStockActual());
            pstmt.setDouble(7, producto.getIva());
            pstmt.setString(8, producto.getEstado());
            pstmt.setInt(9, producto.getIdCategoria());
            pstmt.setInt(10, producto.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE Productos SET Estado = 'I' WHERE IDProducto = ?";

        try (Connection con = new SqlConection().getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
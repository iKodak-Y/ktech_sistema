package Modelo;

public class DetalleFactura {
    private int idDetalle;
    private int idFactura;
    private int idProducto;
    private String codigoProducto;
    private String productoNombre;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private double iva;
    private double total;
    
    public DetalleFactura() {
    }
    
    // Constructor con parámetros principales
    public DetalleFactura(int idProducto, String codigoProducto, String productoNombre, 
                         int cantidad, double precioUnitario) {
        this.idProducto = idProducto;
        this.codigoProducto = codigoProducto;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularTotales();
    }
    
    // Método para calcular subtotal, IVA y total
    private void calcularTotales() {
        this.subtotal = this.cantidad * this.precioUnitario;
        this.iva = this.subtotal * 0.12; // 12% IVA
        this.total = this.subtotal + this.iva;
    }
    
    // Getters y Setters
    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularTotales();
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularTotales();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
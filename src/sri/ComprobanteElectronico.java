package sri;

public abstract class ComprobanteElectronico {
    protected String claveAcceso;
    protected String numeroAutorizacion;
    protected String estado;
    protected String xml;
    
    // Métodos comunes para todos los comprobantes electrónicos
    public abstract String generarXML();
    public abstract boolean firmar();
    public abstract boolean enviar();
    public abstract boolean autorizar();
}
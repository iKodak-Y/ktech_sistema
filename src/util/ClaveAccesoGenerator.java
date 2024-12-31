package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClaveAccesoGenerator {

    public static String generarClaveAcceso() {
        LocalDateTime now = LocalDateTime.now();
        String fecha = now.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String tipoComprobante = "01"; // 01 para facturas
        String rucEmisor = "1234567890001"; // Debe venir de la configuración de la empresa
        String ambiente = "1"; // 1: Pruebas, 2: Producción
        String serie = "001001"; // Establecimiento + Punto Emisión
        String secuencial = String.format("%09d", obtenerSecuencial());
        String codigoNumerico = "12345678"; // 8 dígitos
        String tipoEmision = "1"; // 1: Normal

        String claveAcceso = fecha
                + tipoComprobante
                + rucEmisor
                + ambiente
                + serie
                + secuencial
                + codigoNumerico
                + tipoEmision;

        // Agregar dígito verificador
        int digitoVerificador = calcularDigitoVerificador(claveAcceso);
        return claveAcceso + digitoVerificador;
    }

    private static int obtenerSecuencial() {
        int secuencial = 1;
        String sql = "SELECT Secuencial FROM Secuenciales WHERE TipoDocumento = '01' AND IDPuntoEmision = (SELECT ID FROM PuntosEmision WHERE Codigo = '001')";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                secuencial = rs.getInt("Secuencial");
                incrementarSecuencial(secuencial + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return secuencial;
    }

    private static void incrementarSecuencial(int nuevoSecuencial) {
        String sql = "UPDATE Secuenciales SET Secuencial = ? WHERE TipoDocumento = '01' AND IDPuntoEmision = (SELECT ID FROM PuntosEmision WHERE Codigo = '001')";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevoSecuencial);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int calcularDigitoVerificador(String claveAcceso) {
        int[] coeficientes = {2, 3, 4, 5, 6, 7};
        int suma = 0;
        int indiceCoeficiente = 0;

        for (int i = claveAcceso.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(claveAcceso.charAt(i));
            suma += digito * coeficientes[indiceCoeficiente % 6];
            indiceCoeficiente++;
        }

        int digitoVerificador = 11 - (suma % 11);
        if (digitoVerificador == 11) {
            digitoVerificador = 0;
        } else if (digitoVerificador == 10) {
            digitoVerificador = 1;
        }

        return digitoVerificador;
    }
}
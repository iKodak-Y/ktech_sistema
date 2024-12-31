package Modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ClaveAccesoGenerator {

    public static String generarClaveAcceso(LocalDate fechaEmision, String tipoComprobante, String ruc, String tipoAmbiente, String serie, String numeroComprobante, String tipoEmision) {
        // Formato de la fecha de emisión
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String fechaEmisionStr = fechaEmision.format(dateFormatter);

        // Generar un código numérico aleatorio de 8 dígitos
        String codigoNumerico = String.format("%08d", new Random().nextInt(100000000));

        // Concatenar los elementos para formar la clave de acceso sin el dígito verificador
        String claveAccesoSinDV = fechaEmisionStr + tipoComprobante + ruc + tipoAmbiente + serie + numeroComprobante + codigoNumerico + tipoEmision;

        // Calcular el dígito verificador
        int digitoVerificador = calcularDigitoVerificador(claveAccesoSinDV);

        // Formar la clave de acceso completa añadiendo el dígito verificador
        String claveAccesoCompleta = claveAccesoSinDV + digitoVerificador;

        // Validar longitud
        if (claveAccesoCompleta.length() != 49) {
            throw new RuntimeException("La clave de acceso generada no tiene 49 caracteres. Clave: " + claveAccesoCompleta);
        }

        return claveAccesoCompleta;
    }

    private static int calcularDigitoVerificador(String claveAccesoSinDV) {
        int[] coeficientes = {2, 3, 4, 5, 6, 7};
        int suma = 0;
        int coefIndex = 0;

        // Recorrer la clave de acceso en reversa
        for (int i = claveAccesoSinDV.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(claveAccesoSinDV.charAt(i));
            suma += digito * coeficientes[coefIndex];
            coefIndex = (coefIndex + 1) % coeficientes.length;
        }

        int residuo = suma % 11;
        int digitoVerificador = 11 - residuo;

        if (digitoVerificador == 11) {
            digitoVerificador = 0;
        } else if (digitoVerificador == 10) {
            digitoVerificador = 1;
        }

        return digitoVerificador;
    }
}

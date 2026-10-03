package vnx.sisterfix.domain.util;

import java.security.SecureRandom;

public class GeneradorCodigoSeguimiento {

    private static final String CARACTERES = "23456789ABCDEFGHJKMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generar(int bloque1Longitud, int bloque2Longitud) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < bloque1Longitud; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }

        sb.append("-");

        for (int i = 0; i < bloque2Longitud; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }

        return sb.toString();

    }

    public static String generar(){
        return generar(4,4);
    }
}

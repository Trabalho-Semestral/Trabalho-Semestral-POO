package util;

import java.util.Random;

/**
 * Classe utilitária para geração de IDs alfanuméricos únicos.
 * Gera IDs de 4 caracteres compostos por letras maiúsculas e números.
 */
public class GeradorID {
    
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TAMANHO_ID = 4;
    private static final Random random = new Random();
    
    /**
     * Gera um ID alfanumérico único de 4 caracteres.
     * @return String contendo o ID gerado
     */
    public static String gerarID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TAMANHO_ID; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}


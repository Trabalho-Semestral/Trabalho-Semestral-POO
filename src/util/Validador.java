package util;

import java.util.regex.Pattern;

/**
 * Classe utilitária para validação de dados conforme as regras de negócio de Moçambique.
 */
public class Validador {

    // Padrão de e-mail RFC 5322 para uma validação robusta
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    );

    /**
     * Valida o número de BI (Bilhete de Identidade) de Moçambique.
     * Formato: 12 dígitos seguidos de 1 letra maiúscula.
     * @param bi O número de BI a ser validado
     * @return true se o BI for válido, false caso contrário
     */
    public static boolean validarBI(String bi) {
        if (bi == null || bi.trim().isEmpty()) {
            return false;
        }
        // Exatamente 13 caracteres: 12 dígitos + 1 letra maiúscula no final
        return bi.matches("^[0-9]{12}[A-Z]$");
    }

    /**
     * Valida o NUIT (Número Único de Identificação Tributária) de Moçambique.
     * Formato: 9 dígitos.
     * @param nuit O NUIT a ser validado
     * @return true se o NUIT for válido, false caso contrário
     */
    public static boolean validarNuit(String nuit) {
        if (nuit == null || nuit.trim().isEmpty()) {
            return false;
        }
        return nuit.matches("^[0-9]{9}$");
    }

    /**
     * Valida o número de telefone de Moçambique.
     * Formato: Inicia com +258, seguido por 82, 83, 84, 85, 86 ou 87 e mais 7 dígitos.
     * @param telefone O número de telefone a ser validado
     * @return true se o telefone for válido, false caso contrário
     */
    public static boolean validarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }
        // Deve iniciar com o código de Moçambique +258, seguido por um prefixo válido e 7 dígitos
        return telefone.matches("^\\+2588[2-7][0-9]{7}$");
    }

    /**
     * Valida um endereço de e-mail.
     * @param email O e-mail a ser validado
     * @return true se o e-mail for válido, false caso contrário
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida se uma string não é nula nem vazia.
     * @param valor A string a ser validada
     * @return true se a string for válida, false caso contrário
     */
    public static boolean validarCampoObrigatorio(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    /**
     * Valida se um valor numérico é positivo.
     * @param valor O valor a ser validado
     * @return true se o valor for positivo, false caso contrário
     */
    public static boolean validarValorPositivo(double valor) {
        return valor > 0;
    }
}


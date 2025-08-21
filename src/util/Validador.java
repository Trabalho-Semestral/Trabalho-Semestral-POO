package util;

/**
 * Classe utilitária para validação de dados conforme as regras de negócio de Moçambique.
 */
public class Validador {
    
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
        return bi.matches("[0-9]{12}[A-Z]");
    }
    
    /**
     * Valida o número de telefone de Moçambique.
     * Formato: +258 seguido de 9 dígitos.
     * @param telefone O número de telefone a ser validado
     * @return true se o telefone for válido, false caso contrário
     */
    public static boolean validarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }
        // Deve iniciar com o código de Moçambique +258 seguido de 9 dígitos
        return telefone.matches("\\+258[0-9]{9}");
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


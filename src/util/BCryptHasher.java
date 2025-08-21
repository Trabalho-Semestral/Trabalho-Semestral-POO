package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Classe utilitária para hash e verificação de senhas usando SHA-256 com salt.
 * Implementação simplificada para demonstração (em produção, use BCrypt real).
 */
public class BCryptHasher {
    
    private static final String ALGORITHM = "SHA-256";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Gera um hash da senha com salt.
     * @param password A senha em texto simples
     * @return A senha hasheada com salt
     */
    public static String hashPassword(String password) {
        try {
            // Gerar salt aleatório
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Criar hash da senha com salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combinar salt e hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }
    
    /**
     * Verifica se uma senha corresponde ao hash armazenado.
     * @param password A senha em texto simples
     * @param hashedPassword O hash armazenado
     * @return true se a senha corresponder, false caso contrário
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        try {
            // Decodificar o hash armazenado
            byte[] combined = Base64.getDecoder().decode(hashedPassword);
            
            // Extrair salt e hash
            byte[] salt = new byte[16];
            byte[] storedHash = new byte[combined.length - 16];
            System.arraycopy(combined, 0, salt, 0, 16);
            System.arraycopy(combined, 16, storedHash, 0, storedHash.length);
            
            // Gerar hash da senha fornecida com o mesmo salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] testHash = md.digest(password.getBytes());
            
            // Comparar os hashes
            return MessageDigest.isEqual(storedHash, testHash);
            
        } catch (Exception e) {
            return false;
        }
    }
}


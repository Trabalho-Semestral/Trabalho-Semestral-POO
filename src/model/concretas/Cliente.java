package model.concretas;

import util.Validador;
import model.abstractas.Pessoa;

/**
 * Classe que representa um cliente no sistema.
 */
public class Cliente extends Pessoa {
    
    private String endereco;
    private String email;
    
    /**
     * Construtor padrão.
     */
    public Cliente(String clienteNome, String clienteId, String s, String string) {
        super();
    }
    
    /**
     * Construtor com parâmetros.
     * @param nome Nome do cliente
     * @param nrBI Número do BI
     * @param nuit NUIT do cliente
     * @param telefone Telefone do cliente
     * @param endereco Endereço do cliente
     * @param email Email do cliente
     */
    public Cliente(String nome, String nrBI, String nuit, String telefone, 
                   String endereco, String email) {
        super(nome, nrBI, nuit, telefone);
        this.endereco = endereco;
        this.email = email;
    }
    
    /**
     * Valida os dados específicos do cliente.
     * @return true se todos os dados forem válidos, false caso contrário
     */
    @Override
    public boolean validarDados() {
        return super.validarDados() &&
               Validador.validarCampoObrigatorio(endereco) &&
               Validador.validarCampoObrigatorio(email);
    }
    
    // Getters e Setters
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", nrBI='" + nrBI + '\'' +
                ", nuit='" + nuit + '\'' +
                ", telefone='" + telefone + '\'' +
                ", endereco='" + endereco + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}


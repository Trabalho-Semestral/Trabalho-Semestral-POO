package model.concretas;

import util.GeradorID;
import util.Validador;

/**
 * Classe que representa um vendedor no sistema.
 */
public class Vendedor extends Funcionario {
    
    private String codigoFuncionario;
    
    /**
     * Construtor padrão.
     */
    public Vendedor() {
        super();
        this.codigoFuncionario = "VEN" + GeradorID.gerarID();
    }
    
    /**
     * Construtor com parâmetros.
     * @param nome Nome do vendedor
     * @param nrBI Número do BI
     * @param nuit NUIT do vendedor
     * @param telefone Telefone do vendedor
     * @param salario Salário do vendedor
     * @param senha Senha do vendedor
     */
    public Vendedor(String nome, String nrBI, String nuit, String telefone, 
                   double salario, String senha) {
        super(nome, nrBI, nuit, telefone, salario, senha);
        this.codigoFuncionario = "VEN" + GeradorID.gerarID();
    }
    
    /**
     * Valida os dados específicos do vendedor.
     * @return true se todos os dados forem válidos, false caso contrário
     */
    @Override
    public boolean validarDados() {
        return super.validarDados() &&
               Validador.validarCampoObrigatorio(codigoFuncionario);
    }
    
    // Getters e Setters
    public String getCodigoFuncionario() {
        return codigoFuncionario;
    }
    
    public void setCodigoFuncionario(String codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }
    
    @Override
    public String toString() {
        return "Vendedor{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", nrBI='" + nrBI + '\'' +
                ", nuit='" + nuit + '\'' +
                ", telefone='" + telefone + '\'' +
                ", salario=" + salario +
                ", dataContratacao=" + dataContratacao +
                ", codigoFuncionario='" + codigoFuncionario + '\'' +
                '}';
    }
}


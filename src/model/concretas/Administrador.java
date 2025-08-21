package model.concretas;

/**
 * Classe que representa um administrador no sistema.
 */
public class Administrador extends Funcionario {
    
    /**
     * Construtor padrão.
     */
    public Administrador() {
        super();
    }
    
    /**
     * Construtor com parâmetros.
     * @param nome Nome do administrador
     * @param nrBI Número do BI
     * @param nuit NUIT do administrador
     * @param telefone Telefone do administrador
     * @param salario Salário do administrador
     * @param senha Senha do administrador
     */
    public Administrador(String nome, String nrBI, String nuit, String telefone, 
                        double salario, String senha) {
        super(nome, nrBI, nuit, telefone, salario, senha);
    }
    
    @Override
    public String toString() {
        return "Administrador{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", nrBI='" + nrBI + '\'' +
                ", nuit='" + nuit + '\'' +
                ", telefone='" + telefone + '\'' +
                ", salario=" + salario +
                ", dataContratacao=" + dataContratacao +
                '}';
    }
}


package model.concretas;

import model.abstractas.Funcionario;
import util.GeradorID;
import util.Validador;

/**
 * Classe que representa um vendedor no sistema.
 */
public class Vendedor extends Funcionario {

    private String codigoFuncionario;
    private String fotoPath;

    /**
     * Construtor padrão.
     */
    public Vendedor() {
        super();
        setTipoUsuario(TipoUsuario.VENDEDOR);
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
    public Vendedor(String nome, String nrBI, String nuit, String telefone, double salario, String senha) {

        super(nome, nrBI, nuit, telefone, TipoUsuario.VENDEDOR, salario, senha);

        this.fotoPath = null;
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
    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
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
                ", tipoUsuario=" + tipoUsuario +
                ", dataContratacao=" + dataContratacao +
                ", codigoFuncionario='" + codigoFuncionario + '\'' +
                '}';
    }
}
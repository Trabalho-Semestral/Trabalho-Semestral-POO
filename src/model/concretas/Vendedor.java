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
    private boolean primeiroLogin = true;
    private boolean suspenso = false;
    private boolean online = false;


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

    // MÉTODOS NOVOS ADICIONADOS

    /**
     * Obtém o número do BI do vendedor
     * @return Número do BI
     */
    public String getBI() {
        return this.nrBI; // Usa o campo nrBI da classe pai
    }

    /**
     * Verifica se o vendedor está suspenso
     * @return true se está suspenso, false caso contrário
     */
    public boolean isSuspenso() {
        return this.suspenso;
    }

    /**
     * Define o status de suspensão do vendedor
     * @param suspenso true para suspender, false para ativar
     */
    public void setSuspenso(boolean suspenso) {
        this.suspenso = suspenso;
    }

    /**
     * Verifica se o vendedor está online
     * @return true se está online, false caso contrário
     */
    public boolean isOnline() {
        return this.online;
    }

    /**
     * Define o status online do vendedor
     * @param online true para online, false para offline
     */
    public void setOnline(boolean online) {
        this.online = online;
    }


    // Getters e Setters existentes
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

    public boolean isPrimeiroLogin() {
        return primeiroLogin;
    }

    public void setPrimeiroLogin(boolean primeiroLogin) {
        this.primeiroLogin = primeiroLogin;
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
                ", primeiroLogin=" + primeiroLogin +
                ", suspenso=" + suspenso +
                ", online=" + online +
                '}';
    }
}

package model.concretas;

import model.abstractas.Funcionario;

/**
 * Classe que representa um administrador no sistema.
 */
public class Administrador extends Funcionario {


    private String nivelAcesso;
    private boolean podeCadastrarAdmin = true;
    private boolean primeiroLogin = true;
    private boolean suspenso = false;
    private boolean online = false;

    /**
     * Construtor padrão.
     */
    public Administrador() {
        super();
        setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        this.nivelAcesso = "TOTAL";
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
        super(nome, nrBI, nuit, telefone, TipoUsuario.ADMINISTRADOR, salario, senha);
        this.nivelAcesso = "TOTAL";
    }

    /**
     * Administrador tem acesso total ao sistema
     * @return true - pode gerir operações
     */
    public boolean podeGerirOperacoes() {
        return true;
    }

    /**
     * Administrador pode configurar o sistema
     * @return true - pode configurar sistema
     */
    public boolean podeConfigurarSistema() {
        return true;
    }

    /**
     * Administrador pode gerir outros usuários
     * @return true - pode gerir usuários
     */
    public boolean podeGerirUsuarios() {
        return true;
    }

    // MÉTODOS NOVOS ADICIONADOS

    /**
     * Obtém o número do BI do administrador
     * @return Número do BI
     */
    public String getBI() {
        return this.nrBI; // Usa o campo nrBI da classe pai
    }

    /**
     * Verifica se o administrador está suspenso
     * @return true se está suspenso, false caso contrário
     */
    public boolean isSuspenso() {
        return this.suspenso;
    }

    /**
     * Define o status de suspensão do administrador
     * @param suspenso true para suspender, false para ativar
     */
    public void setSuspenso(boolean suspenso) {
        this.suspenso = suspenso;
    }

    /**
     * Verifica se o administrador está online
     * @return true se está online, false caso contrário
     */
    public boolean isOnline() {
        return this.online;
    }

    /**
     * Define o status online do administrador
     * @param online true para online, false para offline
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    // Getters e Setters existentes
    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public boolean podeCadastrarAdmin() {
        return podeCadastrarAdmin;
    }

    public void setPodeCadastrarAdmin(boolean pode) {
        this.podeCadastrarAdmin = pode;
    }

    public boolean isPrimeiroLogin() {
        return primeiroLogin;
    }

    public void setPrimeiroLogin(boolean primeiro) {
        this.primeiroLogin = primeiro;
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
                ", tipoUsuario=" + tipoUsuario +
                ", nivelAcesso='" + nivelAcesso + '\'' +
                ", dataContratacao=" + dataContratacao +
                ", suspenso=" + suspenso +
                ", online=" + online +
                '}';
    }
}
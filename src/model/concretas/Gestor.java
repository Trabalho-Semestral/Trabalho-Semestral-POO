package model.concretas;

import model.abstractas.Funcionario;

/**
 * Representa um gestor no sistema de venda de equipamentos.
 * Um gestor tem acesso às funcionalidades de gestão operacional,
 * mas não às configurações administrativas do sistema.
 */
public class Gestor extends Funcionario {

    private String departamento;
    private String fotoPath;
    private boolean primeiroLogin = true;
    private boolean suspenso = false; // Novo campo
    private boolean online = false;   // Novo campo

    /**
     * Construtor padrão.
     */
    public Gestor() {
        super();
        setTipoUsuario(TipoUsuario.GESTOR);
        this.departamento = "OPERACIONAL";
    }

    /**
     * Construtor com parâmetros.
     * @param nome Nome do gestor
     * @param nrBI Número do BI
     * @param nuit NUIT do gestor
     * @param telefone Telefone do gestor
     * @param salario Salário do gestor
     * @param senha Senha do gestor
     */
    public Gestor(String nome, String nrBI, String nuit, String telefone, double salario, String senha) {
        super(nome, nrBI, nuit, telefone, TipoUsuario.GESTOR, salario, senha);
        this.departamento = "OPERACIONAL";
    }

    /**
     * Gestor pode gerir vendas e operações mas não configurações do sistema
     * @return true se pode gerir operações
     */
    public boolean podeGerirOperacoes() {
        return true;
    }

    /**
     * Gestor não tem acesso a configurações administrativas
     * @return false - não pode configurar sistema
     */
    public boolean podeConfigurarSistema() {
        return false;
    }

    /**
     * Gestor pode gerir vendedores e operações relacionadas
     * @return true - pode gerir vendedores
     */
    public boolean podeGerirVendedores() {
        return true;
    }

    // MÉTODOS NOVOS ADICIONADOS

    /**
     * Obtém o número do BI do gestor
     * @return Número do BI
     */
    public String getBI() {
        return this.nrBI; // Usa o campo nrBI da classe pai
    }

    /**
     * Verifica se o gestor está suspenso
     * @return true se está suspenso, false caso contrário
     */
    public boolean isSuspenso() {
        return this.suspenso;
    }

    /**
     * Define o status de suspensão do gestor
     * @param suspenso true para suspender, false para ativar
     */
    public void setSuspenso(boolean suspenso) {
        this.suspenso = suspenso;
    }

    /**
     * Verifica se o gestor está online
     * @return true se está online, false caso contrário
     */
    public boolean isOnline() {
        return this.online;
    }

    /**
     * Define o status online do gestor
     * @param online true para online, false para offline
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    // Getters e Setters existentes
    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
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
        return "Gestor{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", nrBI='" + nrBI + '\'' +
                ", nuit='" + nuit + '\'' +
                ", telefone='" + telefone + '\'' +
                ", salario=" + salario +
                ", tipoUsuario=" + tipoUsuario +
                ", departamento='" + departamento + '\'' +
                ", dataContratacao=" + dataContratacao +
                ", primeiroLogin=" + primeiroLogin +
                ", suspenso=" + suspenso +
                ", online=" + online +
                '}';
    }
}
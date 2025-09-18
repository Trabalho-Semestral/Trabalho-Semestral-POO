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

    // Getters e Setters
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
                '}';
    }
}
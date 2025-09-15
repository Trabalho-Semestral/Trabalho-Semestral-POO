package model.abstractas;

import model.concretas.TipoUsuario;
import util.Validador;
import java.util.Date;

/**
 * Classe abstrata que representa um funcionário no sistema.
 * Serve como base para Vendedor, Administrador e Gestor.
 */
public abstract class Funcionario extends Pessoa {

    protected double salario;
    protected Date dataContratacao;
    protected String senha;
    protected TipoUsuario tipoUsuario;

    /**
     * Construtor padrão.
     */
    public Funcionario() {
        super();
        this.dataContratacao = new Date();
    }

    /**
     * Construtor com parâmetros.
     * @param nome Nome do funcionário
     * @param nrBI Número do BI
     * @param nuit NUIT do funcionário
     * @param telefone Telefone do funcionário
     * @param salario Salário do funcionário
     * @param senha Senha do funcionário
     */
    public Funcionario(String nome, String nrBI, String nuit, String telefone,
                       double salario, String senha) {
        super(nome, nrBI, nuit, telefone);
        this.salario = salario;
        this.senha = senha;
        this.dataContratacao = new Date();
    }

    /**
     * Construtor com parâmetros incluindo tipo de usuário.
     * @param nome Nome do funcionário
     * @param nrBI Número do BI
     * @param nuit NUIT do funcionário
     * @param telefone Telefone do funcionário
     * @param tipoUsuario Tipo de usuário
     * @param salario Salário do funcionário
     * @param senha Senha do funcionário
     */
    public Funcionario(String nome, String nrBI, String nuit, String telefone,
                       TipoUsuario tipoUsuario, double salario, String senha) {
        super(nome, nrBI, nuit, telefone);
        this.salario = salario;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
        this.dataContratacao = new Date();
    }

    /**
     * Valida os dados específicos do funcionário.
     * @return true se todos os dados forem válidos, false caso contrário
     */
    @Override
    public boolean validarDados() {
        return super.validarDados() &&
                Validador.validarValorPositivo(salario) &&
                Validador.validarCampoObrigatorio(senha);
    }

    // Getters e Setters
    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public Date getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(Date dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * ✅ Novo método para obter o tipo de usuário
     * @return Tipo de usuário
     */
    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    /**
     * ✅ Novo método para definir o tipo de usuário
     * @param tipoUsuario Tipo de usuário
     */
    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", nrBI='" + nrBI + '\'' +
                ", nuit='" + nuit + '\'' +
                ", telefone='" + telefone + '\'' +
                ", salario=" + salario +
                ", tipoUsuario=" + tipoUsuario +
                ", dataContratacao=" + dataContratacao +
                '}';
    }
}
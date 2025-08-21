package model.abstractas;

import util.GeradorID;
import util.Validador;

/**
 * Classe abstrata que representa uma pessoa no sistema.
 * Serve como base para Cliente, Vendedor e Administrador.
 */
public abstract class Pessoa {

    protected String id;
    protected String nome;
    protected String nrBI;
    protected String nuit;
    protected String telefone;

    /**
     * Construtor padrão que gera automaticamente um ID.
     */
    public Pessoa() {
        this.id = GeradorID.gerarID();
    }

    /**
     * Construtor com parâmetros.
     * @param nome Nome da pessoa
     * @param nrBI Número do BI
     * @param nuit NUIT da pessoa
     * @param telefone Telefone da pessoa
     */
    public Pessoa(String nome, String nrBI, String nuit, String telefone) {
        this();
        this.nome = nome;
        this.nrBI = nrBI;
        this.nuit = nuit;
        this.telefone = telefone;
    }

    /**
     * Valida os dados da pessoa.
     * @return true se todos os dados forem válidos, false caso contrário
     */
    public boolean validarDados() {
        return Validador.validarCampoObrigatorio(nome) &&
                Validador.validarBI(nrBI) &&
                Validador.validarCampoObrigatorio(nuit) &&
                Validador.validarTelefone(telefone);
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNrBI() {
        return nrBI;
    }

    public void setNrBI(String nrBI) {
        this.nrBI = nrBI;
    }

    public String getNuit() {
        return nuit;
    }

    public void setNuit(String nuit) {
        this.nuit = nuit;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", nrBI='" + nrBI + '\'' +
                ", nuit='" + nuit + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}


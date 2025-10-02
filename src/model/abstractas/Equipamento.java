package model.abstractas;

import util.GeradorID;
import util.Validador;

/**
 * Classe abstrata que representa um equipamento no sistema.
 * Serve como base para Computador e Periferico.
 */
public abstract class Equipamento implements Cloneable {

    /**
     * Enumeração para o estado do equipamento.
     */
    public enum EstadoEquipamento {
        NOVO, USADO
    }

    protected String id;
    protected String marca;
    protected double preco;
    protected int quantidadeEstoque;
    protected int reservado;
    protected int disponivel;
    protected EstadoEquipamento estado;
    protected String fotoPath;

    /**
     * Construtor padrão que gera automaticamente um ID.
     */
    public Equipamento() {
        this.id = GeradorID.gerarID();
    }

    /**
     * Construtor com parâmetros.
     * @param marca Marca do equipamento
     * @param preco Preço do equipamento
     * @param quantidadeEstoque Quantidade em estoque
     * @param estado Estado do equipamento (NOVO ou USADO)
     * @param fotoPath Caminho para a foto do equipamento
     */
    public Equipamento(String marca, double preco, int quantidadeEstoque,
                       EstadoEquipamento estado, String fotoPath) {
        this();
        this.marca = marca;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estado = estado;
        this.fotoPath = fotoPath;
    }

    /**
     * Valida os dados do equipamento.
     * @return true se todos os dados forem válidos, false caso contrário
     */
    public boolean validarDados() {
        return Validador.validarCampoObrigatorio(marca) &&
                Validador.validarValorPositivo(preco) &&
                quantidadeEstoque >= 0 &&
                estado != null &&
                Validador.validarCampoObrigatorio(fotoPath);
    }

    /**
     * Reduz a quantidade em estoque.
     * @param quantidade Quantidade a ser reduzida
     * @return true se a operação foi bem-sucedida, false se não há estoque suficiente
     */
    public boolean reduzirEstoque(int quantidade) {
        if (quantidadeEstoque >= quantidade) {
            quantidadeEstoque -= quantidade;
            return true;
        }
        return false;
    }

    /**
     * Aumenta a quantidade em estoque.
     * @param quantidade Quantidade a ser adicionada
     */
    public void aumentarEstoque(int quantidade) {
        if (quantidade > 0) {
            quantidadeEstoque += quantidade;
        }
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public EstadoEquipamento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipamento estado) {
        this.estado = estado;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }


    public int getReservado() { return reservado; }
    public void setReservado(int reservado) { this.reservado = reservado; }
    public int getDisponivel() {
        return Math.max(0, this.quantidadeEstoque - this.reservado);
    }
    public void setDisponivel(int disponivel){ }

    @Override
    public String toString() {
        return "Equipamento{" +
                "id='" + id + '\'' +
                ", marca='" + marca + '\'' +
                ", preco=" + preco +
                ", quantidadeEstoque=" + quantidadeEstoque +
                ", estado=" + estado +
                ", fotoPath='" + fotoPath + '\'' +
                '}';
    }


    @Override
    public Equipamento clone() {
        try {
            return (Equipamento) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Should not happen
        }
    }
}


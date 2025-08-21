package model.concretas;

import model.abstractas.Equipamento;
import util.Validador;
/**
 * Classe que representa um periférico no sistema.
 */
public class Periferico extends Equipamento {
    
    private String tipo;
    
    /**
     * Construtor padrão.
     */
    public Periferico() {
        super();
    }
    
    /**
     * Construtor com parâmetros.
     * @param marca Marca do periférico
     * @param preco Preço do periférico
     * @param quantidadeEstoque Quantidade em estoque
     * @param estado Estado do periférico (NOVO ou USADO)
     * @param fotoPath Caminho para a foto do periférico
     * @param tipo Tipo do periférico (ex: mouse, teclado, monitor)
     */
    public Periferico(String marca, double preco, int quantidadeEstoque, 
                     EstadoEquipamento estado, String fotoPath, String tipo) {
        super(marca, preco, quantidadeEstoque, estado, fotoPath);
        this.tipo = tipo;
    }
    
    /**
     * Valida os dados específicos do periférico.
     * @return true se todos os dados forem válidos, false caso contrário
     */
    @Override
    public boolean validarDados() {
        return super.validarDados() &&
               Validador.validarCampoObrigatorio(tipo);
    }
    
    // Getters e Setters
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    @Override
    public String toString() {
        return "Periferico{" +
                "id='" + id + '\'' +
                ", marca='" + marca + '\'' +
                ", preco=" + preco +
                ", quantidadeEstoque=" + quantidadeEstoque +
                ", estado=" + estado +
                ", fotoPath='" + fotoPath + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}


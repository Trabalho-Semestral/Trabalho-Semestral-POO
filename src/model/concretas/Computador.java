package model.concretas;

import model.abstractas.Equipamento;
import util.Validador;

/**
 * Classe que representa um computador no sistema.
 */
public class Computador extends Equipamento {
    
    private String processador;
    private String memoriaRAM;
    private String armazenamento;
    private String placaGrafica;
    
    /**
     * Construtor padrão.
     */
    public Computador() {
        super();
    }
    
    /**
     * Construtor com parâmetros.
     * @param marca Marca do computador
     * @param preco Preço do computador
     * @param quantidadeEstoque Quantidade em estoque
     * @param estado Estado do computador (NOVO ou USADO)
     * @param fotoPath Caminho para a foto do computador
     * @param processador Processador do computador
     * @param memoriaRAM Memória RAM do computador
     * @param armazenamento Armazenamento do computador
     * @param placaGrafica Placa gráfica do computador
     */
    public Computador(String marca, double preco, int quantidadeEstoque, 
                     EstadoEquipamento estado, String fotoPath,
                     String processador, String memoriaRAM, String armazenamento, 
                     String placaGrafica) {
        super(marca, preco, quantidadeEstoque, estado, fotoPath);
        this.processador = processador;
        this.memoriaRAM = memoriaRAM;
        this.armazenamento = armazenamento;
        this.placaGrafica = placaGrafica;
    }
    
    /**
     * Valida os dados específicos do computador.
     * @return true se todos os dados forem válidos, false caso contrário
     */
    @Override
    public boolean validarDados() {
        return super.validarDados() &&
               Validador.validarCampoObrigatorio(processador) &&
               Validador.validarCampoObrigatorio(memoriaRAM) &&
               Validador.validarCampoObrigatorio(armazenamento) &&
               Validador.validarCampoObrigatorio(placaGrafica);
    }
    
    // Getters e Setters
    public String getProcessador() {
        return processador;
    }
    
    public void setProcessador(String processador) {
        this.processador = processador;
    }
    
    public String getMemoriaRAM() {
        return memoriaRAM;
    }
    
    public void setMemoriaRAM(String memoriaRAM) {
        this.memoriaRAM = memoriaRAM;
    }
    
    public String getArmazenamento() {
        return armazenamento;
    }
    
    public void setArmazenamento(String armazenamento) {
        this.armazenamento = armazenamento;
    }
    
    public String getPlacaGrafica() {
        return placaGrafica;
    }
    
    public void setPlacaGrafica(String placaGrafica) {
        this.placaGrafica = placaGrafica;
    }
    
    @Override
    public String toString() {
        return "Computador{" +
                "id='" + id + '\'' +
                ", marca='" + marca + '\'' +
                ", preco=" + preco +
                ", quantidadeEstoque=" + quantidadeEstoque +
                ", estado=" + estado +
                ", fotoPath='" + fotoPath + '\'' +
                ", processador='" + processador + '\'' +
                ", memoriaRAM='" + memoriaRAM + '\'' +
                ", armazenamento='" + armazenamento + '\'' +
                ", placaGrafica='" + placaGrafica + '\'' +
                '}';
    }
}


package model.concretas;

import model.abstractas.Equipamento;
import util.GeradorID;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe que representa uma venda no sistema.
 */
public class Venda {
    
    private String idVenda;
    private Date data;
    private Vendedor vendedor;
    private Cliente cliente;
    private List<Equipamento> equipamentos;
    private double valorTotal;
    
    public Venda(Date data, Vendedor vendedor, Cliente cliente, List<Equipamento> equipamentos, double valorTotal) {
        this.idVenda = "VND" + GeradorID.gerarID();
        this.data = data;
        this.vendedor = vendedor;
        this.cliente = cliente;
        this.equipamentos = new ArrayList<>(equipamentos);
        this.valorTotal = valorTotal;
    }

    /**
     * Construtor padrão.
     */
    public Venda() {
        this.idVenda = "VND" + GeradorID.gerarID();
        this.data = new Date();
        this.equipamentos = new ArrayList<>();
        this.valorTotal = 0.0;
    }
    
    /**
     * Construtor com parâmetros.
     * @param vendedor Vendedor responsável pela venda
     * @param cliente Cliente que realizou a compra
     */
    public Venda(Vendedor vendedor, Cliente cliente) {
        this();
        this.vendedor = vendedor;
        this.cliente = cliente;
    }
    
    /**
     * Adiciona um equipamento à venda.
     * @param equipamento Equipamento a ser adicionado
     * @return true se o equipamento foi adicionado com sucesso, false caso contrário
     */
    public boolean adicionarEquipamento(Equipamento equipamento) {
        if (equipamento != null && equipamento.getQuantidadeEstoque() > 0) {
            equipamentos.add(equipamento);
            valorTotal += equipamento.getPreco();
            equipamento.reduzirEstoque(1);
            return true;
        }
        return false;
    }
    
    /**
     * Remove um equipamento da venda.
     * @param equipamento Equipamento a ser removido
     * @return true se o equipamento foi removido com sucesso, false caso contrário
     */
    public boolean removerEquipamento(Equipamento equipamento) {
        if (equipamentos.remove(equipamento)) {
            valorTotal -= equipamento.getPreco();
            equipamento.aumentarEstoque(1);
            return true;
        }
        return false;
    }
    
    /**
     * Calcula o valor total da venda.
     */
    public void calcularValorTotal() {
        valorTotal = equipamentos.stream()
                .mapToDouble(Equipamento::getPreco)
                .sum();
    }
    
    /**
     * Valida os dados da venda.
     * @return true se todos os dados forem válidos, false caso contrário
     */
    public boolean validarDados() {
        return vendedor != null &&
               cliente != null &&
               !equipamentos.isEmpty() &&
               valorTotal > 0;
    }
    
    // Getters e Setters
    public String getIdVenda() {
        return idVenda;
    }
    
    public void setIdVenda(String idVenda) {
        this.idVenda = idVenda;
    }
    
    public Date getData() {
        return data;
    }
    
    public void setData(Date data) {
        this.data = data;
    }
    
    public Vendedor getVendedor() {
        return vendedor;
    }
    
    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public List<Equipamento> getEquipamentos() {
        return equipamentos;
    }
    
    public void setEquipamentos(List<Equipamento> equipamentos) {
        this.equipamentos = equipamentos;
        calcularValorTotal();
    }
    
    public double getValorTotal() {
        return valorTotal;
    }
    
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
    
    @Override
    public String toString() {
        return "Venda{" +
                "idVenda='" + idVenda + '\'' +
                ", data=" + data +
                ", vendedor=" + vendedor.getNome() +
                ", cliente=" + cliente.getNome() +
                ", equipamentos=" + equipamentos.size() +
                ", valorTotal=" + valorTotal +
                '}';
    }
}


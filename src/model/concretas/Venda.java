package model.concretas;

import model.abstractas.Equipamento;
import java.math.BigDecimal;
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
    private List<ItemVenda> itens;
    private BigDecimal valorTotal;
    private BigDecimal desconto = BigDecimal.ZERO;
    private BigDecimal imposto = BigDecimal.ZERO;
    private BigDecimal descontoPercent = BigDecimal.ZERO;


    public Venda(Date data, Vendedor vendedor, Cliente cliente, List<ItemVenda> itens, BigDecimal valorTotal) {
        this.idVenda = "VND" + GeradorID.gerarID();
        this.data = data;
        this.vendedor = vendedor;
        this.cliente = cliente;
        this.itens = new ArrayList<>(itens);
        this.valorTotal = valorTotal != null ? valorTotal : BigDecimal.ZERO;
    }

    public Venda() {
        this.idVenda = "VND" + GeradorID.gerarID();
        this.data = new Date();
        this.itens = new ArrayList<>();
        this.valorTotal = BigDecimal.ZERO;
    }

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
    public boolean adicionarItem(Equipamento equipamento, int quantidade) {
        if (equipamento == null || quantidade <= 0) return false;
        if (equipamento.getQuantidadeEstoque() < quantidade) return false;
        ItemVenda item = new ItemVenda(equipamento, quantidade);
        itens.add(item);
        equipamento.reduzirEstoque(quantidade);
        calcularValorTotal();
        return true;
    }
    
    /**
     * Remove um equipamento da venda.
     * @param item Equipamento a ser removido
     * @return true se o equipamento foi removido com sucesso, false caso contrário
     */
    public boolean removerItem(ItemVenda item) {
        if (item == null) return false;
        boolean removed = itens.remove(item);
        if (removed) {
            item.getEquipamento().aumentarEstoque(item.getQuantidade());
            calcularValorTotal();
        }
        return removed;
    }


    /**
     * Calcula o valor total da venda.
     */
    public void calcularValorTotal() {
        this.valorTotal = itens.stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * Valida os dados da venda.
     * @return true se todos os dados forem válidos, false caso contrário
     */
    public boolean validarDados() {
        return vendedor != null &&
                cliente != null &&
                itens != null && !itens.isEmpty() &&
                valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }
    public void setDescontoPercent(BigDecimal p) { this.descontoPercent = p != null ? p : BigDecimal.ZERO; }

    // Getters e Setters
    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; calcularValorTotal(); }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto != null ? desconto : BigDecimal.ZERO; }

    public BigDecimal getImposto() { return imposto; }
    public void setImposto(BigDecimal imposto) { this.imposto = imposto != null ? imposto : BigDecimal.ZERO; }
    public String getIdVenda() { return idVenda; }
    public void setIdVenda(String idVenda) { this.idVenda = idVenda; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public Vendedor getVendedor() { return vendedor; }
    public void setVendedor(Vendedor vendedor) { this.vendedor = vendedor; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public BigDecimal getTotalComDescontosImpostos() {
        BigDecimal base = valorTotal != null ? valorTotal : BigDecimal.ZERO;
        BigDecimal liquido = base.subtract(desconto != null ? desconto : BigDecimal.ZERO);
        liquido = liquido.add(imposto != null ? imposto : BigDecimal.ZERO);
        return liquido.max(BigDecimal.ZERO);
    }
    public BigDecimal getTotalComDescontoPercent() {
        BigDecimal base = valorTotal != null ? valorTotal : BigDecimal.ZERO;
        if (descontoPercent == null) return base;
        BigDecimal fator = BigDecimal.ONE.subtract(descontoPercent.divide(BigDecimal.valueOf(100)));
        return base.multiply(fator).max(BigDecimal.ZERO);
    }
    @Override
    public String toString() {
        String vendedorNome = vendedor != null ? vendedor.getNome() : "-";
        String clienteNome = cliente != null ? cliente.getNome() : "-";
        int totalItens = itens != null ? itens.size() : 0;
        return "Venda{" +
                "idVenda='" + idVenda + '\'' +
                ", data=" + data +
                ", vendedor=" + vendedorNome +
                ", cliente=" + clienteNome +
                ", itens=" + totalItens +
                ", valorTotal=" + valorTotal +
                ", totalFinal=" + getTotalComDescontosImpostos() +
                '}';
    }
}


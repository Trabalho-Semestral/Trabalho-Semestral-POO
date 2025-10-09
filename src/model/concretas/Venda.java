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
        System.out.println("=== ADICIONANDO ITEM À VENDA ===");
        System.out.println("Equipamento: " + equipamento.getMarca());
        System.out.println("Quantidade: " + quantidade);
        System.out.println("Estoque disponível: " + (equipamento.getQuantidadeEstoque() - equipamento.getReservado()));

        // Verificar estoque
        if (quantidade > (equipamento.getQuantidadeEstoque() - equipamento.getReservado())) {
            System.out.println("❌ Estoque insuficiente");
            return false;
        }

        // Diminuir estoque
        equipamento.setQuantidadeEstoque(equipamento.getQuantidadeEstoque() - quantidade);
        System.out.println("✅ Estoque atualizado: " + equipamento.getQuantidadeEstoque());

        // Adicionar item
        if (itens == null) {
            itens = new ArrayList<>();
        }

        // Verificar se item já existe
        for (ItemVenda item : itens) {
            if (item.getEquipamento().getId().equals(equipamento.getId())) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                System.out.println("✅ Quantidade atualizada no item existente");
                return true;
            }
        }

        // Criar novo item
        itens.add(new ItemVenda(equipamento, quantidade));
        System.out.println("✅ Novo item adicionado à venda");
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
        System.out.println("=== VALIDANDO VENDA ===");
        System.out.println("ID Venda: " + idVenda);
        System.out.println("Cliente: " + (cliente != null ? cliente.getNome() : "NULO"));
        System.out.println("Vendedor: " + (vendedor != null ? vendedor.getNome() : "NULO"));
        System.out.println("Data: " + data);
        System.out.println("Itens: " + (itens != null ? itens.size() : 0));

        if (idVenda == null || idVenda.trim().isEmpty()) {
            System.out.println("❌ ID Venda inválido");
            return false;
        }
        if (cliente == null) {
            System.out.println("❌ Cliente nulo");
            return false;
        }
        if (vendedor == null) {
            System.out.println("❌ Vendedor nulo");
            return false;
        }
        if (data == null) {
            System.out.println("❌ Data nula");
            return false;
        }
        if (itens == null || itens.isEmpty()) {
            System.out.println("❌ Lista de itens vazia");
            return false;
        }

        // Validar cada item
        for (ItemVenda item : itens) {
            if (item == null) {
                System.out.println("❌ Item nulo na lista");
                return false;
            }
            if (item.getEquipamento() == null) {
                System.out.println("❌ Equipamento nulo no item");
                return false;
            }
            if (item.getQuantidade() <= 0) {
                System.out.println("❌ Quantidade inválida: " + item.getQuantidade());
                return false;
            }
        }

        System.out.println("✅ Venda válida!");
        return true;
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



package model.concretas;

import model.abstractas.Equipamento;
import java.math.BigDecimal;
import java.util.Objects;

public class ItemVenda {
    private Equipamento equipamento;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemVenda(Equipamento equipamento, int quantidade) {
        if (equipamento == null) throw new IllegalArgumentException("Equipamento obrigatório");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser > 0");
        this.equipamento = equipamento;
        this.quantidade = quantidade;
        this.precoUnitario = BigDecimal.valueOf(equipamento.getPreco());
    }

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Equipamento getEquipamento() { return equipamento; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }

    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser > 0");
        this.quantidade = quantidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemVenda that = (ItemVenda) o;
        return Objects.equals(equipamento, that.equipamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipamento);
    }
}
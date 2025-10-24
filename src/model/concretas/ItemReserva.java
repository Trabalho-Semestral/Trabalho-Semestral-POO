package model.concretas;

import model.abstractas.Equipamento;

public class ItemReserva {
    private Equipamento equipamento;
    private int quantidade;

    public ItemReserva(Equipamento equipamento, int quantidade) {
        this.equipamento = equipamento;
        this.quantidade = quantidade;
    }

    public Equipamento getEquipamento() { return equipamento; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}

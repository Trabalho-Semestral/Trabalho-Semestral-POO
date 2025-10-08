package model.concretas;

import model.abstractas.Equipamento;

public class ItemReserva {
    private Equipamento equipamento;
    private int quantidade;

    // Construtor
    public ItemReserva(Equipamento equipamento, int quantidade) {
        this.equipamento = equipamento;
        this.quantidade = quantidade;
    }

    // Getters
    public Equipamento getEquipamento() {
        return equipamento;
    }

    public int getQuantidade() {
        return quantidade;
    }

    // SETTER PARA EQUIPAMENTO - ADICIONE ESTE MÉTODO
    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    // Setter para quantidade (se necessário)
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    }

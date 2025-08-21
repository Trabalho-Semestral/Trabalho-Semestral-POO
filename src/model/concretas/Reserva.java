package model.concretas;

import model.abstractas.Equipamento;
import util.GeradorID;
import java.util.Date;

public class Reserva {
    
    private String idReserva;
    private Cliente cliente;
    private Equipamento equipamento;
    private int quantidade;
    private Date dataReserva;
    private StatusReserva status;
    
    public enum StatusReserva {
        ATIVA, CANCELADA, FINALIZADA
    }
    
    public Reserva(Cliente cliente, Equipamento equipamento, int quantidade) {
        this.idReserva = "RSV" + GeradorID.gerarID();
        this.cliente = cliente;
        this.equipamento = equipamento;
        this.quantidade = quantidade;
        this.dataReserva = new Date();
        this.status = StatusReserva.ATIVA;
    }
    
    public String getIdReserva() {
        return idReserva;
    }
    
    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public Equipamento getEquipamento() {
        return equipamento;
    }
    
    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }
    
    public int getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    
    public Date getDataReserva() {
        return dataReserva;
    }
    
    public void setDataReserva(Date dataReserva) {
        this.dataReserva = dataReserva;
    }
    
    public StatusReserva getStatus() {
        return status;
    }
    
    public void setStatus(StatusReserva status) {
        this.status = status;
    }
    
    public double getValorTotal() {
        return equipamento.getPreco() * quantidade;
    }
    
    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva='" + idReserva + '\'' +
                ", cliente=" + cliente.getNome() +
                ", equipamento=" + equipamento.getMarca() +
                ", quantidade=" + quantidade +
                ", dataReserva=" + dataReserva +
                ", status=" + status +
                '}';
    }
}


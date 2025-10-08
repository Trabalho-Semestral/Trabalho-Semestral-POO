package model.concretas;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import model.abstractas.Equipamento;
import util.GeradorID;

public class Reserva extends Vendedor {
    private String idReserva;
    private Cliente cliente;
    private Vendedor vendedor;
    private List<ItemReserva> itens;
    private Date dataReserva;
    private Date expiraEm;
    private BigDecimal taxaPaga = BigDecimal.ZERO;  // Novo campo

    public BigDecimal getTaxaPaga() { return taxaPaga; }
    public void setTaxaPaga(BigDecimal taxaPaga) { this.taxaPaga = taxaPaga; }

    // Atualize getValorTotal() se necessário para considerar taxa
    private StatusReserva status;

    public void adicionarItem(Equipamento eq, int quantidade) {
    }


    public enum StatusReserva {
        ATIVA, EXPIRADA, CONVERTIDA, CANCELADA
    }

    public Reserva() {

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

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public List<ItemReserva> getItens() {
        return itens;
    }

    public void setItens(List<ItemReserva> itens) {
        this.itens = itens;
    }


    public void setDataReserva(Date dataReserva) {
        this.dataReserva = dataReserva;
    }


    public Date getDataReserva() { return dataReserva; }
    public Date getExpiraEm() { return expiraEm; }

    public void setExpiraEm(Date expiraEm) {
        this.expiraEm = expiraEm;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public double getValorTotal() {
        return itens.stream().mapToDouble(it -> it.getEquipamento().getPreco() * it.getQuantidade()).sum();
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva='" + idReserva + '\'' +
                ", cliente=" + cliente.getNome() +
                ", vendedor=" + vendedor.getNome() +
                ", itens=" + itens +
                ", dataReserva=" + dataReserva +
                ", expiraEm=" + expiraEm +
                ", status=" + status +
                '}';
    }
}
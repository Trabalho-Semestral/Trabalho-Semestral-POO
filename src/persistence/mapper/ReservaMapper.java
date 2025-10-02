package persistence.mapper;

import model.abstractas.Equipamento;
import model.concretas.*;
import persistence.dto.ReservaDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ReservaMapper {

    public static ReservaDTO toDTO(Reserva r) {
        ReservaDTO dto = new ReservaDTO();
        dto.idReserva = r.getIdReserva();
        dto.status = r.getStatus().name();
        dto.dataMillis = r.getDataReserva().getTime();
        dto.expiraEmMillis = r.getExpiraEm().getTime();
        if (r.getCliente() != null) {
            dto.clienteId = r.getCliente().getId();
            dto.clienteNome = r.getCliente().getNome();
        }
        if (r.getVendedor() != null) {
            dto.vendedorId = r.getVendedor().getId();
            dto.vendedorNome = r.getVendedor().getNome();
        }
        dto.itens = new ArrayList<>();
        if (r.getItens() != null) {
            for (ItemReserva it : r.getItens()) {
                var i = new ReservaDTO.ItemReservaDTO();
                i.equipamentoId = it.getEquipamento().getId();
                // marca/nome pode ser nulo; guardamos marca se disponível
                try { i.equipamentoMarca = it.getEquipamento().getMarca(); } catch (Exception ex) { i.equipamentoMarca = null; }
                i.quantidade = it.getQuantidade();
                dto.itens.add(i);
            }
        }
        return dto;
    }
    public static Reserva fromDTO(ReservaDTO dto) {
        // cria cliente e vendedor "mínimos"
        Cliente cliente = null;
        Vendedor vendedor = null;
        if (dto.clienteId != null) cliente = new Cliente(dto.clienteNome, dto.clienteId, "", "");
        if (dto.vendedorId != null) vendedor = new Vendedor(dto.vendedorNome, dto.vendedorId, "", "", 0.0, ""); // senha vazia

        List<ItemReserva> itens = new ArrayList<>();
        if (dto.itens != null) {
            for (var itemDto : dto.itens) {
                Equipamento e = new Equipamento() { }; // instância genérica se não existe classe abstrata concreta
                e.setId(itemDto.equipamentoId);
                try { e.setMarca(itemDto.equipamentoMarca); } catch (Exception ignored) {}
                itens.add(new ItemReserva(e, itemDto.quantidade));
            }
        }

        Reserva r = new Reserva();
        r.setIdReserva(dto.idReserva != null ? dto.idReserva : r.getIdReserva());
        try { r.setStatus(Reserva.StatusReserva.valueOf(dto.status)); } catch (Exception ex) { r.setStatus(Reserva.StatusReserva.ATIVA); }
        r.setDataReserva(new Date(dto.dataMillis));
        return r;
    }

}

package persistence.mapper;

import model.concretas.*;
import model.abstractas.Equipamento;
import persistence.dto.ReservaDTO;
import persistence.dto.ItemReservaDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ReservaMapper {

    public static ReservaDTO toDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.idReserva = reserva.getIdReserva();

        if (reserva.getCliente() != null) {
            dto.clienteId = reserva.getCliente().getId();
            dto.clienteNome = reserva.getCliente().getNome();
        }

        if (reserva.getVendedor() != null) {
            dto.vendedorId = reserva.getVendedor().getId();
            dto.vendedorNome = reserva.getVendedor().getNome();
        }

        dto.status = reserva.getStatus().name();
        dto.dataMillis = reserva.getDataReserva() != null ? reserva.getDataReserva().getTime() : new Date().getTime();
        dto.expiraEmMillis = reserva.getExpiraEm() != null ? reserva.getExpiraEm().getTime() : new Date().getTime() + (7L * 24 * 60 * 60 * 1000);

        // Mapear itens
        if (reserva.getItens() != null) {
            dto.itens = new ArrayList<>();
            for (ItemReserva item : reserva.getItens()) {
                ItemReservaDTO itemDTO = new ItemReservaDTO();
                itemDTO.equipamentoId = item.getEquipamento().getId();
                itemDTO.equipamentoMarca = item.getEquipamento().getMarca();
                itemDTO.quantidade = item.getQuantidade();
                dto.itens.add(itemDTO);
            }
        }

        return dto;
    }

    public static Reserva fromDTO(ReservaDTO dto) {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(dto.idReserva);

        // Mapear cliente (criar objeto básico)
        if (dto.clienteId != null && dto.clienteNome != null) {
            Cliente cliente = new Cliente();
            cliente.setId(dto.clienteId);
            cliente.setNome(dto.clienteNome);
            reserva.setCliente(cliente);
        }

        // Mapear vendedor (criar objeto básico)
        if (dto.vendedorId != null && dto.vendedorNome != null) {
            Vendedor vendedor = new Vendedor();
            vendedor.setId(dto.vendedorId);
            vendedor.setNome(dto.vendedorNome);
            reserva.setVendedor(vendedor);
        }

        // Mapear status
        try {
            reserva.setStatus(Reserva.StatusReserva.valueOf(dto.status));
        } catch (Exception e) {
            reserva.setStatus(Reserva.StatusReserva.ATIVA);
        }

        // Mapear datas
        reserva.setDataReserva(new Date(dto.dataMillis));
        reserva.setExpiraEm(new Date(dto.expiraEmMillis));

        // Mapear itens
        if (dto.itens != null && !dto.itens.isEmpty()) {
            List<ItemReserva> itens = new ArrayList<>();
            for (ItemReservaDTO itemDTO : dto.itens) {
                Equipamento equipamento = new model.concretas.Computador();
                equipamento.setId(itemDTO.equipamentoId);
                equipamento.setMarca(itemDTO.equipamentoMarca);

                ItemReserva item = new ItemReserva(equipamento, itemDTO.quantidade);
                itens.add(item);}
            reserva.setItens(itens);
        } else {
            reserva.setItens(new ArrayList<>());
        }
        return reserva;
    }
}

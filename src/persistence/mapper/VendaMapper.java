package persistence.mapper;

import model.concretas.ItemVenda;
import model.concretas.Venda;
import persistence.dto.ItemVendaDTO;
import persistence.dto.VendaDTO;

import java.util.ArrayList;

public class VendaMapper {
    public static VendaDTO toDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();
        dto.idVenda = venda.getIdVenda();
        dto.dataMillis = venda.getData().getTime();
        dto.vendedorId = venda.getVendedor().getId();
        dto.clienteId = venda.getCliente().getId();
        dto.itens = new ArrayList<>();
        for (ItemVenda it : venda.getItens()) {
            ItemVendaDTO i = new ItemVendaDTO();
            i.equipamentoId = it.getEquipamento().getId();
            i.quantidade = it.getQuantidade();
            i.precoUnitario = it.getPrecoUnitario();
            dto.itens.add(i);
        }
        dto.desconto = venda.getDesconto();
        dto.imposto = venda.getImposto();
        dto.total = venda.getTotalComDescontosImpostos();
        dto.metodoPagamento = venda.getMetodoPagamento();
        dto.totalPago = venda.getTotalPago();
        dto.troco = venda.getTroco();
        return dto;
    }
}
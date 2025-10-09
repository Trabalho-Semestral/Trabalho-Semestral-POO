package service;

import model.abstractas.Equipamento;
import model.concretas.Reserva;
import persistence.EquipamentoRepository;
import persistence.ReservaRepository;

import java.util.Date;
import java.util.Optional;

public class ReservaService {

    private final ReservaRepository reservas;
    private final EquipamentoRepository equipamentos;

    public ReservaService(ReservaRepository reservas, EquipamentoRepository equipamentos) {
        this.reservas = reservas;
        this.equipamentos = equipamentos;
    }

    public Reserva criar(Reserva r) {

        // Gerar ID
        if (r.getIdReserva() == null || r.getIdReserva().isBlank()) {
            String novoId = "RES" + util.GeradorID.gerarID();
            r.setIdReserva(novoId);
        }
        for (var it : r.getItens()) {
            String equipamentoId = it.getEquipamento().getId();
            Optional<Equipamento> equipamentoOpt = equipamentos.buscarPorId(equipamentoId);

            if (equipamentoOpt.isEmpty()) {
               throw new IllegalArgumentException("Equipamento não encontrado: " + equipamentoId);
            }

            Equipamento e = equipamentoOpt.get();
            int disponivelReal = e.getQuantidadeEstoque() - e.getReservado();

            if (disponivelReal < it.getQuantidade()) {
                   throw new IllegalArgumentException("Estoque insuficiente para " + e.getMarca() +
                        ". Disponível: " + disponivelReal +
                        ", Solicitado: " + it.getQuantidade());
            }
        }
        for (var it : r.getItens()) {
            String equipamentoId = it.getEquipamento().getId();
            Equipamento e = equipamentos.buscarPorId(equipamentoId)
                    .orElseThrow(() -> {
                       return new IllegalStateException("Equipamento desapareceu durante a operação: " + equipamentoId);
                    });

            int reservadoAntes = e.getReservado();
            e.setReservado(e.getReservado() + it.getQuantidade());

            try {
                equipamentos.salvar(e);
          } catch (Exception ex) {
               throw ex;
            }
        }
        r.setStatus(Reserva.StatusReserva.ATIVA);

        try {
            reservas.salvar(r);
        } catch (Exception ex) {
            for (var it : r.getItens()) {
                try {
                    String equipamentoId = it.getEquipamento().getId();
                    Equipamento e = equipamentos.buscarPorId(equipamentoId).orElse(null);
                    if (e != null) {
                        e.setReservado(Math.max(0, e.getReservado() - it.getQuantidade()));
                        equipamentos.salvar(e);
                    }
                } catch (Exception revertEx) {}
            }
            throw ex;
        }
        return r;
    }
    public Reserva atualizar(Reserva nova) {
        var opt = reservas.buscarPorId(nova.getIdReserva());
        if (opt.isEmpty()) throw new IllegalArgumentException("Reserva não encontrada: " + nova.getIdReserva());
        Reserva antiga = opt.get();

        // Reverte o reservado da antiga
        for (var it : antiga.getItens()) {
            Equipamento e = equipamentos.buscarPorId(it.getEquipamento().getId()).orElseThrow();
            e.setReservado(Math.max(0, e.getReservado() - it.getQuantidade()));
            equipamentos.salvar(e);
        }

        // Valida novo estoque e aplica reservado novo
        for (var it : nova.getItens()) {
            Equipamento e = equipamentos.buscarPorId(it.getEquipamento().getId()).orElseThrow();
            if (e.getDisponivel() < it.getQuantidade())
                throw new IllegalArgumentException("Estoque insuficiente para " + e.getMarca());
        }

        for (var it : nova.getItens()) {
            Equipamento e = equipamentos.buscarPorId(it.getEquipamento().getId()).orElseThrow();
            e.setReservado(e.getReservado() + it.getQuantidade());
            equipamentos.salvar(e);
        }

        reservas.atualizar(nova);
        return nova;
    }

    public void cancelar(String reservaId) {
        Reserva r = reservas.buscarPorId(reservaId).orElseThrow();
        if (r.getStatus() != Reserva.StatusReserva.ATIVA) return;

        for (var it : r.getItens()) {
            Equipamento e = equipamentos.buscarPorId(it.getEquipamento().getId()).orElseThrow();
            e.setReservado(Math.max(0, e.getReservado() - it.getQuantidade()));
            equipamentos.salvar(e);
        }

        r.setStatus(Reserva.StatusReserva.CANCELADA);
        reservas.atualizar(r);
    }

    public void expirarSeNecessario(Reserva r) {
        if (r.getStatus() != Reserva.StatusReserva.ATIVA) return;

        if (r.getExpiraEm().before(new Date())) {
            for (var it : r.getItens()) {
                Equipamento e = equipamentos.buscarPorId(it.getEquipamento().getId()).orElseThrow();
                e.setReservado(Math.max(0, e.getReservado() - it.getQuantidade()));
                equipamentos.salvar(e);
            }
            r.setStatus(Reserva.StatusReserva.EXPIRADA);
            reservas.atualizar(r);
        }
    }
}

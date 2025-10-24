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

        // Gerar ID se não existir
        if (r.getIdReserva() == null || r.getIdReserva().isBlank()) {
            String novoId = "RES" + util.GeradorID.gerarID();
            r.setIdReserva(novoId);
            System.out.println("✅ NOVO ID GERADO: " + novoId);
        } else {
            System.out.println("✅ ID EXISTENTE: " + r.getIdReserva());
        }

        // Validar e encontrar equipamentos antes de qualquer operação
        System.out.println("=== VALIDANDO ESTOQUE ===");
        for (var it : r.getItens()) {
            String equipamentoId = it.getEquipamento().getId(); 
            System.out.println("Processando item - Equipamento ID: " + equipamentoId + ", Quantidade: " + it.getQuantidade());

            Optional<Equipamento> equipamentoOpt = equipamentos.buscarPorId(equipamentoId);

            if (equipamentoOpt.isEmpty()) {
                System.err.println("❌ EQUIPAMENTO NÃO ENCONTRADO: " + equipamentoId);
                throw new IllegalArgumentException("Equipamento não encontrado: " + equipamentoId);
            }

            Equipamento e = equipamentoOpt.get();
            int disponivelReal = e.getQuantidadeEstoque() - e.getReservado();

            System.out.println("Equipamento: " + e.getMarca() +
                    ", Estoque: " + e.getQuantidadeEstoque() +
                    ", Reservado: " + e.getReservado() +
                    ", Disponível: " + disponivelReal);

            if (disponivelReal < it.getQuantidade()) {
                System.err.println("❌ ESTOQUE INSUFICIENTE: " + e.getMarca() +
                        " (Disponível: " + disponivelReal +
                        ", Solicitado: " + it.getQuantidade() + ")");
                throw new IllegalArgumentException("Estoque insuficiente para " + e.getMarca() +
                        ". Disponível: " + disponivelReal +
                        ", Solicitado: " + it.getQuantidade());
            }
            System.out.println("✅ Estoque válido para: " + e.getMarca());
        }

        // Aplicar reservas aos equipamentos
        System.out.println("=== APLICANDO RESERVAS AOS EQUIPAMENTOS ===");
        for (var it : r.getItens()) {
            String equipamentoId = it.getEquipamento().getId();
            System.out.println("Reservando equipamento: " + equipamentoId + ", Quantidade: " + it.getQuantidade());

            Equipamento e = equipamentos.buscarPorId(equipamentoId)
                    .orElseThrow(() -> {
                        System.err.println("❌ EQUIPAMENTO DESAPARECEU: " + equipamentoId);
                        return new IllegalStateException("Equipamento desapareceu durante a operação: " + equipamentoId);
                    });

            int reservadoAntes = e.getReservado();
            e.setReservado(e.getReservado() + it.getQuantidade());

            System.out.println("Equipamento " + e.getMarca() +
                    " - Reservado: " + reservadoAntes + " → " + e.getReservado());

            try {
                equipamentos.salvar(e);
                System.out.println("✅ Equipamento salvo: " + e.getMarca());
            } catch (Exception ex) {
                System.err.println("❌ ERRO AO SALVAR EQUIPAMENTO: " + e.getMarca() + " - " + ex.getMessage());
                throw ex;
            }
        }


        // Definir status e salvar reserva
        System.out.println("=== SALVANDO RESERVA ===");
        r.setStatus(Reserva.StatusReserva.ATIVA);
        System.out.println("Status definido: " + r.getStatus());

        try {
            reservas.salvar(r);
            System.out.println("✅ RESERVA SALVA COM SUCESSO - ID: " + r.getIdReserva());
        } catch (Exception ex) {
            System.err.println("❌ ERRO AO SALVAR RESERVA: " + ex.getMessage());

            // Reverter reservas em caso de erro
            System.err.println("=== REVERTENDO RESERVAS DE EQUIPAMENTOS ===");
            for (var it : r.getItens()) {
                try {
                    String equipamentoId = it.getEquipamento().getId();
                    Equipamento e = equipamentos.buscarPorId(equipamentoId).orElse(null);
                    if (e != null) {
                        e.setReservado(Math.max(0, e.getReservado() - it.getQuantidade()));
                        equipamentos.salvar(e);
                        System.out.println("✅ Reserva revertida para: " + e.getMarca());
                    }
                } catch (Exception revertEx) {
                    System.err.println("❌ ERRO AO REVERTER: " + revertEx.getMessage());
                }
            }
            throw ex;
        }

        System.out.println("=== CRIAÇÃO DE RESERVA CONCLUÍDA ===");
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

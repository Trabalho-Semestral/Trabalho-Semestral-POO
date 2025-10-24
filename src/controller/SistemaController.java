package controller;

import model.abstractas.Equipamento;
import persistence.*;
import service.ReservaService;
import util.BCryptHasher;
import util.GeradorID;
import view.CardLayoutManager;
import model.concretas.*;
import persistence.ReservaFileRepository;
import service.RelatorioVendasService;
import persistence.dto.VendaDTO;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Controlador principal do sistema.
 * Gerencia a lógica de negócio e a navegação entre telas.
 */
public class SistemaController {

    private final EquipamentoFileRepository equipamentoRepo = new EquipamentoFileRepository("data\\equipamentos.json");
    private final ClienteRepository clienteRepo = new ClienteRepository("data\\clientes.json");
    private final VendedorRepository vendedorRepo = new VendedorRepository("data\\vendedores.json");
    private final VendaFileRepository vendaRepo = new VendaFileRepository("data");
    private final GestorRepository gestorRepo = new GestorRepository("data\\gestores.json");
    private final AdministradorRepository administradorRepo = new AdministradorRepository("data\\administradores.json");
    private final RelatorioVendasService relatorioService = new RelatorioVendasService(vendaRepo); private Object usuarioLogado;
    private String tipoUsuarioLogado;
    private CardLayoutManager cardLayoutManager;
    private final ReservaFileRepository reservaRepo;
    private final ReservaService reservaService;




    public SistemaController() {
        reservaRepo = new ReservaFileRepository("data");
        try { reservaRepo.init(); } catch (Exception e) { e.printStackTrace(); }
        reservaService = new ReservaService(reservaRepo, equipamentoRepo);
        inicializarDados();
    }

    /**
     * Inicializa os dados de demonstração.
     */
    private void inicializarDados() {
        try {
            equipamentoRepo.init();
            clienteRepo.init();
            vendedorRepo.init();
            vendaRepo.init();
            gestorRepo.init();
            administradorRepo.init();
            if (administradorRepo.findAll().isEmpty() || gestorRepo.findAll().isEmpty() || vendedorRepo.findAll().isEmpty() || clienteRepo.findAll().isEmpty()) {
                criarUsuariosDemonstracaoPersistindo();
            }

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Falha ao inicializar armazenamento: " + e.getMessage());
        }
    }

    /**
     * Cria usuários de demonstração.
     */
    private void criarUsuariosDemonstracaoPersistindo() throws Exception {
        // Admin demo
        Administrador admin = new Administrador(
                "Administrador Sistema", "123456789012A",
                "123456789", "+258123456789", 50000.0,
                BCryptHasher.hashPassword("admin123")
        );
        admin.setId("ADMIN");
        administradorRepo.add(admin);

    }


    /**
     * Autentica um usuário no sistema.
     * @param id ID do usuário
     * @param senha Senha do usuário
     * @return true se autenticado com sucesso, false caso contrário
     */
    public String autenticarUsuario(String id, String senha) {
        try {
            var admin = administradorRepo.findById(id);
            if (admin.isPresent() && util.BCryptHasher.checkPassword(senha, admin.get().getSenha())) {
                usuarioLogado = admin.get();
                tipoUsuarioLogado = admin.get().getTipoUsuario().getDescricao();
                return "Administrador";
            }
            var gestor = gestorRepo.findById(id);
            if (gestor.isPresent() && util.BCryptHasher.checkPassword(senha, gestor.get().getSenha())) {
                usuarioLogado = gestor.get();
                tipoUsuarioLogado = gestor.get().getTipoUsuario().getDescricao();
                return "Gestor";
            }
            var vend = vendedorRepo.findById(id);
            if (vend.isPresent() && util.BCryptHasher.checkPassword(senha, vend.get().getSenha())) {
                usuarioLogado = vend.get();
                tipoUsuarioLogado = vend.get().getTipoUsuario().getDescricao();
                return "Vendedor";
            }
        } catch (Exception ignored) {}
        return null;
    }

    public java.util.Optional<Equipamento> findEquipamentoById(String id) { return equipamentoRepo.findById(id); }
    public java.util.Optional<Cliente> findClienteById(String id) { return clienteRepo.findById(id); }
    public java.util.Optional<Vendedor> findVendedorById(String id) { return vendedorRepo.findById(id); }
    /**
     * Faz logout do usuário atual.
     */
    public void logout() {
        usuarioLogado = null;
        tipoUsuarioLogado = null;
    }


    public boolean adicionarAdministrador(Administrador admin) {
        if (admin != null && admin.validarDados()) {
            admin.setId(util.GeradorID.gerarID());
            admin.setSenha(util.BCryptHasher.hashPassword(admin.getSenha()));
            try { administradorRepo.add(admin); return true; } catch (Exception e) { e.printStackTrace(); return false; }
        }
        return false;
    }

    public boolean removerAdministrador(Administrador admin) {
        if (admin == null || admin.getId() == null) return false;
        try { administradorRepo.removeById(admin.getId()); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean atualizarAdministrador(Administrador antigo, Administrador novo) {
        if (antigo == null || novo == null || !novo.validarDados()) return false;
        novo.setId(antigo.getId());
        try { administradorRepo.upsert(novo); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }
    //  ========== MÉTODOS PARA GESTÃO DE GESTORES ==========

    public boolean adicionarGestor(Gestor gestor) {
        if (gestor != null && gestor.validarDados()) {
            gestor.setId(util.GeradorID.gerarID());
            gestor.setSenha(util.BCryptHasher.hashPassword(gestor.getSenha()));
            try { gestorRepo.add(gestor); return true; } catch (Exception e) { e.printStackTrace(); return false; }
        }
        return false;
    }

    public boolean removerGestor(Gestor gestor) {
        if (gestor == null || gestor.getId() == null) return false;
        try { gestorRepo.removeById(gestor.getId()); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean atualizarGestor(Gestor antigo, Gestor novo) {
        if (antigo == null || novo == null || !novo.validarDados()) return false;
        novo.setId(antigo.getId());
        try { gestorRepo.upsert(novo); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }


    /**
     * Verifica se o usuário logado tem permissão para gerir operações.
     * @return true se tem permissão, false caso contrário
     */
    public boolean podeGerirOperacoes() {
        if (usuarioLogado == null) return false;

        switch (tipoUsuarioLogado) {
            case "Administrador":
                return true;
            case "Gestor":
                return ((Gestor) usuarioLogado).podeGerirOperacoes();
            case "Vendedor":
                return false;
            default:
                return false;
        }
    }

    /**
     * Verifica se o usuário logado tem permissão para configurar o sistema.
     * @return true se tem permissão, false caso contrário
     */
    public boolean podeConfigurarSistema() {
        if (usuarioLogado == null) return false;

        switch (tipoUsuarioLogado) {
            case "Administrador":
                return true;
            case "Gestor":
                return ((Gestor) usuarioLogado).podeConfigurarSistema();
            case "Vendedor":
                return false;
            default:
                return false;
        }
    }

    /**
     * Verifica se o usuário logado pode acessar relatórios.
     * @return true se tem permissão, false caso contrário
     */
    public boolean podeAcessarRelatorios() {
        if (usuarioLogado == null) return false;

        switch (tipoUsuarioLogado) {
            case "Administrador":
            case "Gestor":
                return true;
            case "Vendedor":
                return true;
            default:
                return false;
        }
    }

    // ========== MÉTODOS EXISTENTES ==========
    public boolean adicionarEquipamento(Equipamento equipamento) {
        if (equipamento != null && equipamento.validarDados()) {
            equipamento.setId(GeradorID.gerarID());
            try { equipamentoRepo.add(equipamento); return true; } catch (Exception e) { e.printStackTrace(); return false; }
        }
        return false;
    }

    public boolean removerEquipamento(Equipamento equipamento) {
        if (equipamento == null || equipamento.getId() == null) return false;
        try { equipamentoRepo.removeById(equipamento.getId()); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean atualizarEquipamento(Equipamento antigo, Equipamento novo) {
        if (antigo == null || novo == null || !novo.validarDados()) return false;
        novo.setId(antigo.getId());
        try { equipamentoRepo.upsert(novo); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean adicionarVendedor(Vendedor vendedor) {
        if (vendedor != null && vendedor.validarDados()) {
            vendedor.setId(GeradorID.gerarID());
            vendedor.setSenha(BCryptHasher.hashPassword(vendedor.getSenha()));
            try { vendedorRepo.add(vendedor); return true; } catch (Exception e) { e.printStackTrace(); return false; }
        }
        return false;
    }

    public boolean removerVendedor(Vendedor vendedor) {
        if (vendedor == null || vendedor.getId() == null) return false;
        try { vendedorRepo.removeById(vendedor.getId()); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean atualizarVendedor(Vendedor antigo, Vendedor novo) {
        if (antigo == null || novo == null || !novo.validarDados()) return false;
        novo.setId(antigo.getId());
        try { vendedorRepo.upsert(novo); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean adicionarCliente(Cliente cliente) {
        if (cliente != null && cliente.validarDados()) {
            cliente.setId(GeradorID.gerarID());
            try { clienteRepo.add(cliente); return true; } catch (Exception e) { e.printStackTrace(); return false; }
        }
        return false;
    }

    public boolean removerCliente(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) return false;
        try { clienteRepo.removeById(cliente.getId()); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean atualizarCliente(Cliente antigo, Cliente novo) {
        if (antigo == null || novo == null || !novo.validarDados()) return false;
        novo.setId(antigo.getId());
        try { clienteRepo.upsert(novo); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }


    /**
     * Registra uma nova venda no sistema.
     * @param venda Venda a ser registrada
     * @return true se a venda foi registrada com sucesso, false caso contrário
     */
    public boolean registrarVenda(Venda venda) {
        if (venda == null || !venda.validarDados()) return false;
        try {
            if (venda.getIdVenda() == null || venda.getIdVenda().isBlank()) {
                venda.setIdVenda("VND" + GeradorID.gerarID());
            }
            vendaRepo.salvar(venda);
            equipamentoRepo.replaceAll(equipamentoRepo.findAll());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.Map<String, java.math.BigDecimal> totalPorDia(java.util.Date inicio, java.util.Date fim) throws java.io.IOException {
        return relatorioService.totalPorDia(inicio, fim);
    }

    public java.nio.file.Path exportarVendasCSV(java.util.Date inicio, java.util.Date fim, String nomeArquivo) throws java.io.IOException {
        return relatorioService.exportarCSVPeriodo(inicio, fim, nomeArquivo);
    }

    // ========== GETTERS ==========
    public Object getUsuarioLogado() {
        return usuarioLogado;
    }

    public String getTipoUsuarioLogado() {
        return tipoUsuarioLogado;
    }


    public CardLayoutManager getCardLayoutManager() {
        return cardLayoutManager;
    }
    public java.util.List<VendaDTO> getVendasDTO() throws java.io.IOException {
        return vendaRepo.listarTodas();
    }
    public List<Equipamento> getEquipamentos() { return equipamentoRepo.findAll(); }
    public java.util.List<model.concretas.Gestor> getGestores() { return gestorRepo.findAll(); }
    public java.util.List<model.concretas.Administrador> getAdministradores() { return administradorRepo.findAll(); }
    public List<Cliente> getClientes() { return clienteRepo.findAll(); }

    public List<Vendedor> getVendedores() { return vendedorRepo.findAll(); }


    public void setCardLayoutManager(CardLayoutManager cardLayoutManager) {
        this.cardLayoutManager = cardLayoutManager;
    }

    // ================== MÉTODOS DE RESERVAS ==================

    public Reserva criarReserva(Reserva r) {
        if (!podeGerirReservas()) throw new SecurityException("Sem permissão");
        try {
            return reservaService.criar(r);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar reserva: " + e.getMessage(), e);
        }
    }

    public void cancelarReserva(String idReserva) {
        if (!podeGerirReservas()) throw new SecurityException("Sem permissão");
        try {
            reservaService.cancelar(idReserva);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cancelar reserva: " + e.getMessage(), e);
        }
    }


    public Reserva atualizarReserva(Reserva r) {
        if (!podeGerirReservas()) throw new SecurityException("Sem permissão");
        return reservaService.atualizar(r);
    }


    public void salvarReserva(Reserva reserva) {
        if (reserva == null) return;

        try {
            if (reserva.getIdReserva() == null || reserva.getIdReserva().isBlank()) {
                criarReserva(reserva);
            } else {
                Reserva existente = buscarReservaPorId(reserva.getIdReserva());
                if (existente != null) {
                    atualizarReserva(reserva);
                } else {criarReserva(reserva);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao salvar reserva: " + e.getMessage(), e);
        }
    }
    public Vendedor getVendedorLogado() {
        if (usuarioLogado instanceof Vendedor) {
            return (Vendedor) usuarioLogado;
        }
        return null;
    }
    public boolean removerReserva(String idReserva) {
        if (!podeGerirReservas()) throw new SecurityException("Sem permissão");
        try {
            reservaRepo.remover(idReserva);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void corrigirDadosEquipamentos() {
        try {
            List<Equipamento> equipamentos = equipamentoRepo.findAll();
            for (Equipamento eq : equipamentos) {
                if (eq.getDisponivel() < 0 || eq.getDisponivel() > eq.getQuantidadeEstoque()) {
                    eq.setReservado(Math.max(0, eq.getQuantidadeEstoque() - eq.getDisponivel()));
                    equipamentoRepo.upsert(eq);
                }
            }
            JOptionPane.showMessageDialog(null, "Dados dos equipamentos corrigidos!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao corrigir dados: " + e.getMessage());
        }
    }
    public List<Reserva> getReservas() {
        try {
            List<Reserva> reservas = reservaRepo.listarTodas();

            for (Reserva r : reservas) {
                System.out.println("Reserva: ID=" + r.getIdReserva() +
                        ", Cliente=" + (r.getCliente() != null ? r.getCliente().getNome() : "NULL") +
                        ", Status=" + r.getStatus() +
                        ", Itens=" + (r.getItens() != null ? r.getItens().size() : 0));
            }

            return reservas;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean registrarReserva(Reserva r) {
        if (!podeGerirReservas()) return false;
        try {
            reservaService.criar(r);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Reserva buscarReservaPorId(String idReserva) {
        return getReservas().stream()
                .filter(r -> r.getIdReserva().equals(idReserva))
                .findFirst()
                .orElse(null);
    }

    private boolean podeGerirReservas() {
        if (usuarioLogado == null || tipoUsuarioLogado == null) return false;
        return tipoUsuarioLogado.equals("Administrador") ||
                tipoUsuarioLogado.equals("Gestor") ||
                tipoUsuarioLogado.equals("Vendedor");
    }
    public void verificarEquipamento(String id) {
        Optional<Equipamento> equipamento = findEquipamentoById(id);
        if (equipamento.isPresent()) {
            System.out.println("Equipamento encontrado: " + equipamento.get());
        } else {
            System.out.println("Equipamento " + id + " não encontrado. Equipamentos disponíveis:");
            getEquipamentos().forEach(e -> System.out.println(" - " + e.getId() + ": " + e.getMarca()));
        }
    }
    public Map<String, Integer> getTotalVendasPorVendedor() {
        Map<String, Integer> vendasPorVendedor = new HashMap<>();
        try {
            List<VendaDTO> vendas = vendaRepo.listarTodas();
            for (VendaDTO venda : vendas) {
                String vendedorId = venda.vendedorId;
                if (vendedorId != null && !vendedorId.trim().isEmpty()) {
                    vendasPorVendedor.put(vendedorId,
                            vendasPorVendedor.getOrDefault(vendedorId, 0) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vendasPorVendedor;
    }

    public Map<String, BigDecimal> getFaturamentoPorVendedor() {
        Map<String, BigDecimal> faturamentoPorVendedor = new HashMap<>();
        try {
            List<VendaDTO> vendas = vendaRepo.listarTodas();
            for (VendaDTO venda : vendas) {
                String vendedorId = venda.vendedorId;
                BigDecimal totalVenda = venda.total != null ? venda.total : BigDecimal.ZERO;
                if (vendedorId != null && !vendedorId.trim().isEmpty()) {
                    faturamentoPorVendedor.put(vendedorId,
                            faturamentoPorVendedor.getOrDefault(vendedorId, BigDecimal.ZERO).add(totalVenda));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return faturamentoPorVendedor;
    }

    public Map<String, List<VendaDTO>> getVendasPorVendedor() {
        Map<String, List<VendaDTO>> vendasPorVendedor = new HashMap<>();
        try {
            List<VendaDTO> vendas = vendaRepo.listarTodas();
            for (VendaDTO venda : vendas) {
                String vendedorId = venda.vendedorId;
                if (vendedorId != null && !vendedorId.trim().isEmpty()) {
                    vendasPorVendedor.computeIfAbsent(vendedorId, k -> new ArrayList<>()).add(venda);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vendasPorVendedor;
    }

    public String getNomeVendedorPorId(String vendedorId) {
        if (vendedorId == null) return "N/A";

        Optional<Vendedor> vendedor = findVendedorById(vendedorId);
        if (vendedor.isPresent()) {
            return vendedor.get().getNome();
        }
        Optional<Gestor> gestor = gestorRepo.findById(vendedorId);
        if (gestor.isPresent()) {
            return gestor.get().getNome() + " (Gestor)";
        }

        Optional<Administrador> admin = administradorRepo.findById(vendedorId);
        if (admin.isPresent()) {
            return admin.get().getNome() + " (Admin)";
        }

        return vendedorId;
    }


    public boolean converterReservaParaVenda(String idReserva) {
        try {
            // Buscar reserva
            Reserva reserva = buscarReservaPorId(idReserva);
            if (reserva == null || reserva.getStatus() != Reserva.StatusReserva.ATIVA) {
                JOptionPane.showMessageDialog(null,
                        "Reserva não encontrada ou não está ativa.",
                        "Erro na Conversão",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 1. Cancelar reserva (libera estoque reservado)
            reservaService.cancelar(idReserva);

            // 2. Criar venda
            Venda venda = new Venda();
            venda.setIdVenda("VND" + GeradorID.gerarID());
            venda.setData(new Date());
            venda.setVendedor(reserva.getVendedor());
            venda.setCliente(reserva.getCliente());
            venda.setItens(new ArrayList<>());

            // 3. Converter itens da reserva para itens de venda
            for (ItemReserva itemReserva : reserva.getItens()) {
                String equipamentoId = itemReserva.getEquipamento().getId();

                Optional<Equipamento> equipamentoOpt = findEquipamentoById(equipamentoId);
                if (equipamentoOpt.isPresent()) {
                    Equipamento equipamentoAtual = equipamentoOpt.get();
                    ItemVenda itemVenda = new ItemVenda(equipamentoAtual, itemReserva.getQuantidade());
                    venda.getItens().add(itemVenda);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Equipamento não encontrado: " + equipamentoId,
                            "Erro na Conversão",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // 4. Registrar venda
            boolean sucesso = registrarVenda(venda);

            if (sucesso) {
                // Atualizar status da reserva para CONVERTIDA
                reserva.setStatus(Reserva.StatusReserva.CONVERTIDA);
                try {
                    reservaRepo.atualizar(reserva);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                            "A venda foi criada, mas não foi possível atualizar o status da reserva.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }

                // Calcular o total da venda
                BigDecimal totalVenda = BigDecimal.ZERO;
                for (ItemVenda item : venda.getItens()) {
                    totalVenda = totalVenda.add(item.getSubtotal());
                }

                JOptionPane.showMessageDialog(null,
                        "Reserva convertida em venda com sucesso!\n" +
                                "Número da Venda: " + venda.getIdVenda() + "\n" +
                                "Cliente: " + venda.getCliente().getNome() + "\n" +
                                "Total: " + String.format("%.2f MT", totalVenda),
                        "Conversão Bem-sucedida",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Erro ao registrar a venda.\nA reserva foi cancelada mas a venda não pôde ser criada.",
                        "Erro na Conversão",
                        JOptionPane.ERROR_MESSAGE);
            }

            return sucesso;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erro durante a conversão: " + e.getMessage(),
                    "Erro na Conversão",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

}

package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import model.abstractas.Equipamento;
import persistence.*;
import service.RelatorioVendasService;
import service.ReservaService;
import util.BCryptHasher;
import util.GeradorID;
import view.CardLayoutManager;
import model.concretas.*;
import persistence.dto.VendaDTO;
import javax.swing.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

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
    private final RelatorioVendasService relatorioService = new RelatorioVendasService(vendaRepo);
    private Object usuarioLogado;
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

    private void inicializarDados() {
        try {
            System.out.println("Inicializando repositórios...");
            equipamentoRepo.init();
            clienteRepo.init();
            vendedorRepo.init();
            vendaRepo.init();
            gestorRepo.init();
            administradorRepo.init();
            System.out.println("Repositórios inicializados com sucesso.");
            if (administradorRepo.findAll().isEmpty() || gestorRepo.findAll().isEmpty() || vendedorRepo.findAll().isEmpty() || clienteRepo.findAll().isEmpty()) {
                System.out.println("Criando usuários de demonstração...");
                criarUsuariosDemonstracaoPersistindo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Falha ao inicializar armazenamento: " + e.getMessage());
        }
    }

    private void criarUsuariosDemonstracaoPersistindo() throws Exception {
        Administrador admin = new Administrador(
                "Administrador Sistema",
                "123456789012A",
                "ADMIN",
                "+258123456789",
                50000.0,
                BCryptHasher.hashPassword("admin123")
        );
        admin.setPodeCadastrarAdmin(true);
        admin.setPrimeiroLogin(true);
        administradorRepo.add(admin);
    }

    public String autenticarUsuario(String id, String senha) {
        try {
            var admin = administradorRepo.findById(id);
            if (admin.isPresent() && BCryptHasher.checkPassword(senha, admin.get().getSenha())) {
                if (admin.get().isSuspenso()) {
                    return null;
                }
                usuarioLogado = admin.get();
                tipoUsuarioLogado = admin.get().getTipoUsuario().getDescricao();
                if (admin.get().isPrimeiroLogin()) {
                    return "PRIMEIRO_LOGIN_ADMIN";
                }
                return "Administrador";
            }

            var gestor = gestorRepo.findById(id);
            if (gestor.isPresent() && BCryptHasher.checkPassword(senha, gestor.get().getSenha())) {
                if (gestor.get().isSuspenso()) {
                    return null;
                }
                usuarioLogado = gestor.get();
                tipoUsuarioLogado = gestor.get().getTipoUsuario().getDescricao();
                if (gestor.get().isPrimeiroLogin()) {
                    return "PRIMEIRO_LOGIN_GESTOR";
                }
                return "Gestor";
            }

            var vend = vendedorRepo.findById(id);
            if (vend.isPresent() && BCryptHasher.checkPassword(senha, vend.get().getSenha())) {
                if (vend.get().isSuspenso()) {
                    return null;
                }
                usuarioLogado = vend.get();
                tipoUsuarioLogado = vend.get().getTipoUsuario().getDescricao();
                if (vend.get().isPrimeiroLogin()) {
                    return "PRIMEIRO_LOGIN_VENDEDOR";
                }
                return "Vendedor";
            }
        } catch (Exception ignored) {}
        return null;
    }

    public java.util.Optional<Equipamento> findEquipamentoById(String id) { return equipamentoRepo.findById(id); }
    public java.util.Optional<Cliente> findClienteById(String id) { return clienteRepo.findById(id); }
    public java.util.Optional<Vendedor> findVendedorById(String id) { return vendedorRepo.findById(id); }

    public void logout() {
        usuarioLogado = null;
        tipoUsuarioLogado = null;
    }

    public boolean adicionarAdministrador(Administrador admin) {
        if (!(usuarioLogado instanceof Administrador admLogado && admLogado.podeCadastrarAdmin())) {
            JOptionPane.showMessageDialog(null,
                    "Você não tem permissão para cadastrar administradores!",
                    "Permissão Negada",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (idUsuarioExiste(admin.getId())) {
            JOptionPane.showMessageDialog(null,
                    "Já existe um usuário com o ID: " + admin.getId() + "!\nEscolha um ID diferente.",
                    "ID Duplicado",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (admin.getSenha() == null || admin.getSenha().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "A senha é obrigatória!",
                    "Senha Inválida",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            admin.setPrimeiroLogin(true);
            admin.setPodeCadastrarAdmin(true);
            admin.setSuspenso(false);
            admin.setOnline(false);
            administradorRepo.add(admin);
            JOptionPane.showMessageDialog(null,
                    "Administrador adicionado com sucesso!\nID: " + admin.getId(),
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erro ao adicionar administrador: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean adicionarGestor(Gestor gestor) {
        if (gestor != null && gestor.validarDados()) {
            if (idUsuarioExiste(gestor.getId())) {
                JOptionPane.showMessageDialog(null,
                        "Já existe um usuário com o ID: " + gestor.getId() + "!\nEscolha um ID diferente.",
                        "ID Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (gestor.getSenha() == null || gestor.getSenha().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "A senha é obrigatória!",
                        "Senha Inválida",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            gestor.setSenha(BCryptHasher.hashPassword(gestor.getSenha()));
            gestor.setPrimeiroLogin(true);
            gestor.setDepartamento("OPERACIONAL");

            try {
                gestorRepo.add(gestor);
                JOptionPane.showMessageDialog(null,
                        "Gestor adicionado com sucesso!\nID: " + gestor.getId(),
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erro ao adicionar gestor: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Dados do gestor inválidos ou incompletos!",
                    "Dados Inválidos",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean adicionarVendedor(Vendedor vendedor) {
        if (vendedor != null && vendedor.validarDados()) {
            if (idUsuarioExiste(vendedor.getId())) {
                JOptionPane.showMessageDialog(null,
                        "Já existe um usuário com o ID: " + vendedor.getId() + "!\nEscolha um ID diferente.",
                        "ID Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (vendedor.getSenha() == null || vendedor.getSenha().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "A senha é obrigatória!",
                        "Senha Inválida",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            vendedor.setSenha(BCryptHasher.hashPassword(vendedor.getSenha()));
            vendedor.setPrimeiroLogin(true);

            if (vendedor.getCodigoFuncionario() == null || vendedor.getCodigoFuncionario().trim().isEmpty()) {
                vendedor.setCodigoFuncionario("VEN" + GeradorID.gerarID());
            }

            try {
                vendedorRepo.add(vendedor);
                JOptionPane.showMessageDialog(null,
                        "Vendedor adicionado com sucesso!\nID: " + vendedor.getId() +
                                "\nCódigo: " + vendedor.getCodigoFuncionario(),
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erro ao adicionar vendedor: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Dados do vendedor inválidos ou incompletos!",
                    "Dados Inválidos",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean idUsuarioExiste(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return administradorRepo.findById(id).isPresent() ||
                gestorRepo.findById(id).isPresent() ||
                vendedorRepo.findById(id).isPresent();
    }

    public boolean atualizarAdministrador(Administrador admin) {
        if (admin == null || admin.getId() == null || !admin.validarDados()) {
            return false;
        }
        try {
            administradorRepo.upsert(admin);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removerAdministrador(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        try {
            administradorRepo.removeById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<Administrador> buscarAdministradorPorId(String id) {
        return administradorRepo.findById(id);
    }

    public List<Administrador> listarAdministradores() {
        return administradorRepo.findAll();
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

    public boolean removerGestor(Gestor gestor) {
        if (gestor == null || gestor.getId() == null) return false;
        try { gestorRepo.removeById(gestor.getId()); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean atualizarGestor(Gestor antigo, Gestor novo) {
        if (antigo == null || novo == null || !novo.validarDados()) return false;
        novo.setId(antigo.getId());
        try { gestorRepo.upsert(novo); return true; } catch (Exception e) { e.printStackTrace(); return false; }
    }

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

    public boolean registrarVenda(Venda venda) {
        System.out.println("=== REGISTRANDO VENDA (SOLUÇÃO DEFINITIVA) ===");
        if (venda == null || !venda.validarDados()) {
            System.out.println("❌ Venda inválida");
            return false;
        }

        try {
            if (venda.getIdVenda() == null || venda.getIdVenda().isBlank()) {
                venda.setIdVenda("VND" + GeradorID.gerarID());
            }

            BigDecimal totalCalculado = venda.getTotalComDescontosImpostos();
            System.out.println("💰 Total calculado: " + totalCalculado + " para venda " + venda.getIdVenda());

            if (venda.getItens() != null) {
                for (ItemVenda item : venda.getItens()) {
                    System.out.println("  Item: " + item.getEquipamento().getMarca() +
                            " - Preço: " + item.getPrecoUnitario() +
                            " - Qtd: " + item.getQuantidade() +
                            " - Subtotal: " + item.getSubtotal());
                }
            }

            vendaRepo.salvar(venda);
            equipamentoRepo.replaceAll(equipamentoRepo.findAll());

            System.out.println("✅ Venda registrada com sucesso - Total: " + totalCalculado);

           // gerarFaturaVenda(venda);
            gerarFaturaVendaPDF(venda); // Generate PDF invoice

            return true;
        } catch (Exception e) {
            System.out.println("❌ Erro ao registrar venda: " + e.getMessage());
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
                } else {
                    criarReserva(reserva);
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
                if (r.getItens() != null) {
                    for (ItemReserva item : r.getItens()) {
                        if (item.getEquipamento() != null) {
                            String equipamentoId = item.getEquipamento().getId();
                            Optional<Equipamento> equipamentoCompleto = findEquipamentoById(equipamentoId);
                            if (equipamentoCompleto.isPresent()) {
                                item.setEquipamento(equipamentoCompleto.get());
                            }
                        }
                    }
                }
                System.out.println("Reserva " + r.getIdReserva() + " - Itens: " +
                        (r.getItens() != null ? r.getItens().size() : 0));
                if (r.getItens() != null) {
                    for (ItemReserva item : r.getItens()) {
                        if (item.getEquipamento() != null) {
                            System.out.println("  Item: " + item.getEquipamento().getMarca() +
                                    " - Preço: " + item.getEquipamento().getPreco());
                        }
                    }
                }
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
            gerarFaturaReserva(r);
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
        System.out.println("=== CONVERSÃO RESERVA->VENDA (SOLUÇÃO DEFINITIVA) ===");
        try {
            Reserva reserva = buscarReservaPorId(idReserva);
            if (reserva == null || reserva.getStatus() != Reserva.StatusReserva.ATIVA) {
                JOptionPane.showMessageDialog(null, "Reserva não encontrada ou não está ativa.", "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            reservaService.cancelar(idReserva);
            equipamentoRepo.init();

            Venda venda = new Venda();
            venda.setIdVenda("VND" + GeradorID.gerarID());
            venda.setData(new Date());
            venda.setVendedor(reserva.getVendedor());
            venda.setCliente(reserva.getCliente());
            venda.setItens(new ArrayList<>());

            BigDecimal totalVenda = BigDecimal.ZERO;
            for (ItemReserva itemReserva : reserva.getItens()) {
                String equipamentoId = itemReserva.getEquipamento().getId();
                Optional<Equipamento> equipamentoOpt = findEquipamentoById(equipamentoId);
                if (equipamentoOpt.isPresent()) {
                    Equipamento equipamentoAtual = equipamentoOpt.get();
                    ItemVenda itemVenda = new ItemVenda(equipamentoAtual, itemReserva.getQuantidade());
                    venda.getItens().add(itemVenda);
                    totalVenda = totalVenda.add(itemVenda.getSubtotal());
                    System.out.println("  ✅ Item convertido: " + equipamentoAtual.getMarca() +
                            " - Preço: " + itemVenda.getPrecoUnitario() +
                            " - Subtotal: " + itemVenda.getSubtotal());
                }
            }

            System.out.println("💰 Total da venda convertida: " + totalVenda);

            boolean sucesso = registrarVenda(venda);

            if (sucesso) {
                reserva.setStatus(Reserva.StatusReserva.CONVERTIDA);
                reservaRepo.atualizar(reserva);
                System.out.println("🎉 Conversão concluída - Venda: " + venda.getIdVenda() + " - Total: " + totalVenda);
                JOptionPane.showMessageDialog(null,
                        "Reserva convertida em venda com sucesso!\nID: " + venda.getIdVenda() + "\nTotal: " + totalVenda + " MT",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                restaurarReservaCancelada(reserva);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Erro na conversão: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void restaurarReservaCancelada(Reserva reserva) {
        for (ItemReserva item : reserva.getItens()) {
            Optional<Equipamento> opt = findEquipamentoById(item.getEquipamento().getId());
            if (opt.isPresent()) {
                Equipamento e = opt.get();
                e.setReservado(e.getReservado() + item.getQuantidade());
                equipamentoRepo.salvar(e);
            }
        }
        reserva.setStatus(Reserva.StatusReserva.ATIVA);
        reservaRepo.atualizar(reserva);
    }

    public AdministradorRepository getAdministradorRepo() {
        return administradorRepo;
    }

    public GestorRepository getGestorRepo() {
        return gestorRepo;
    }

    public VendedorRepository getVendedorRepo() {
        return vendedorRepo;
    }

    public EquipamentoFileRepository getEquipamentoRepo() {
        return equipamentoRepo;
    }

    public ClienteRepository getClienteRepo() {
        return clienteRepo;
    }

    public VendaFileRepository getVendaRepo() {
        return vendaRepo;
    }


    private void gerarFaturaVendaPDF(Venda venda) {
        if (venda == null) return;

        File directory = new File("data/faturas");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = "data/faturas/fatura_venda_" + venda.getIdVenda() + ".pdf";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            document.add(new Paragraph("=== FATURA DE VENDA ===", titleFont));
            document.add(new Paragraph("ID da Venda: " + venda.getIdVenda(), normalFont));
            document.add(new Paragraph("Data: " + sdf.format(venda.getData()), normalFont));
            document.add(new Paragraph("Vendedor: " + (venda.getVendedor() != null ? venda.getVendedor().getNome() : "N/A"), normalFont));
            document.add(new Paragraph("Cliente: " + (venda.getCliente() != null ? venda.getCliente().getNome() : "N/A"), normalFont));
            document.add(new Paragraph("Método de Pagamento: " + venda.getMetodoPagamento(), normalFont));
            document.add(new Paragraph("Total Pago: " + venda.getTotalPago() + " MT", normalFont));
            document.add(new Paragraph("Troco: " + venda.getTroco() + " MT", normalFont));
            document.add(new Paragraph("\nItens:", boldFont));

            for (ItemVenda item : venda.getItens()) {
                document.add(new Paragraph(" - " + item.getEquipamento().getMarca() + " | Qtd: " + item.getQuantidade() +
                        " | Preço Unit: " + item.getPrecoUnitario() + " MT | Subtotal: " + item.getSubtotal() + " MT", normalFont));
            }

            document.add(new Paragraph("\nDesconto: " + venda.getDesconto() + " MT", normalFont));
            document.add(new Paragraph("Imposto: " + venda.getImposto() + " MT", normalFont));
            document.add(new Paragraph("Total Final: " + venda.getTotalComDescontosImpostos() + " MT", boldFont));
            document.add(new Paragraph("=== FIM DA FATURA ===", titleFont));

            document.close();
            System.out.println(" Fatura PDF gerada para venda " + venda.getIdVenda() + ": " + filename);
        } catch (DocumentException | IOException e) {
            System.err.println(" Erro ao gerar fatura PDF para venda: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void gerarFaturaReserva(Reserva reserva) {
        if (reserva == null) return;

        File directory = new File("data/faturas");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = "data/faturas/fatura_reserva_" + reserva.getIdReserva() + ".txt";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== FATURA DE RESERVA ===");
            writer.println("ID da Reserva: " + reserva.getIdReserva());
            writer.println("Data de Criação: " + sdf.format(reserva.getDataReserva()));
            writer.println("Expira em: " + sdf.format(reserva.getExpiraEm()));
            writer.println("Vendedor: " + (reserva.getVendedor() != null ? reserva.getVendedor().getNome() : "N/A"));
            writer.println("Cliente: " + (reserva.getCliente() != null ? reserva.getCliente().getNome() : "N/A"));
            writer.println("Status: " + reserva.getStatus());
            writer.println("Taxa Paga: " + reserva.getTaxaPaga() + " MT");
            writer.println("\nItens:");
            for (ItemReserva item : reserva.getItens()) {
                writer.println(" - " + item.getEquipamento().getMarca() + " | Qtd: " + item.getQuantidade() +
                        " | Preço Unit: " + item.getEquipamento().getPreco() + " MT | Subtotal: " + (item.getEquipamento().getPreco() * item.getQuantidade()) + " MT");
            }
            writer.println("\nTotal Líquido: " + calcularValorReserva(reserva) + " MT");
            writer.println("=== FIM DA FATURA ===");
            System.out.println("✅ Fatura gerada para reserva " + reserva.getIdReserva() + ": " + filename);
        } catch (IOException e) {
            System.err.println("❌ Erro ao gerar fatura para reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double calcularValorReserva(Reserva reserva) {
        if (reserva.getItens() == null) return 0.0;
        double valorItens = 0.0;
        for (ItemReserva item : reserva.getItens()) {
            if (item.getEquipamento() != null) {
                valorItens += item.getEquipamento().getPreco() * item.getQuantidade();
            }
        }
        BigDecimal taxaPaga = reserva.getTaxaPaga() != null ? reserva.getTaxaPaga() : BigDecimal.ZERO;
        return valorItens - taxaPaga.doubleValue();
    }
}
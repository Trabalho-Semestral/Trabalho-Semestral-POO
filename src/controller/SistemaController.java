package controller;

import model.abstractas.Equipamento;
import util.BCryptHasher;
import util.GeradorID;
import view.CardLayoutManager;
import model.concretas.*;

import persistence.EquipamentoRepository;
import persistence.ClienteRepository;
import persistence.VendedorRepository;
import persistence.VendaFileRepository;
import service.RelatorioVendasService;

import persistence.dto.VendaDTO;
import persistence.GestorRepository;
import persistence.AdministradorRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal do sistema.
 * Gerencia a lógica de negócio e a navegação entre telas.
 */
public class SistemaController {

     List<Reserva> reservas;
    private final EquipamentoRepository equipamentoRepo = new EquipamentoRepository("data\\equipamentos.json");
    private final ClienteRepository clienteRepo = new ClienteRepository("data\\clientes.json");
    private final VendedorRepository vendedorRepo = new VendedorRepository("data\\vendedores.json");
    private final VendaFileRepository vendaRepo = new VendaFileRepository("data");
    private final GestorRepository gestorRepo = new GestorRepository("data\\gestores.json");
    private final AdministradorRepository administradorRepo = new AdministradorRepository("data\\administradores.json");
    private final RelatorioVendasService relatorioService = new RelatorioVendasService(vendaRepo); private Object usuarioLogado;
    private String tipoUsuarioLogado;
    private CardLayoutManager cardLayoutManager;

    public SistemaController() {
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

        // Gestor demo
        Gestor gestor = new Gestor(
                "Carlos Gestor", "555666777888D",
                "555666777", "+258555666777", 35000.0,
                BCryptHasher.hashPassword("gest123")
        );
        gestor.setId("GEST1");
        gestorRepo.add(gestor);

        // Vendedor demo
        Vendedor vendedor = new Vendedor(
                "João Vendedor", "987654321098B",
                "987654321", "+258987654321", 25000.0,
                BCryptHasher.hashPassword("vend123")
        );
        vendedor.setId("VEN1");
        vendedorRepo.add(vendedor);

        // Cliente demo
        Cliente cliente = new Cliente(
                "Maria Cliente", "111222333444C",
                "111222333", "+258111222333",
                "Rua das Flores, 123", "maria@email.com"
        );
        cliente.setId("CLI1");
        clienteRepo.add(cliente);
    }

    /**
     * Cria equipamentos de demonstração.
     */

    private void criarEquipamentosDemonstracaoPersistindo() throws Exception {
        Computador comp1 = new Computador("Dell", 45000.0, 5,
                Equipamento.EstadoEquipamento.NOVO, "C:\\Users\\Nelson Wilson\\IdeaProjects\\Gestao Equipamentos\\Trabalho\\resources\\fotos\\equipamentos\\93b4613e-2f3b-4079-b903-7cc3241694ef.jpg",
                "Intel i7", "16GB", "512GB SSD", "NVIDIA GTX 1650");
        comp1.setId(GeradorID.gerarID());

        Periferico per1 = new Periferico("Logitech", 1500.0, 10,
                Equipamento.EstadoEquipamento.NOVO, "C:\\Users\\Nelson Wilson\\IdeaProjects\\Gestao Equipamentos\\Trabalho\\resources\\fotos\\equipamentos\\c6eefaec-7deb-46a2-a6ab-40d995176d2e.jpg", "Mouse");
        per1.setId(GeradorID.gerarID());

        equipamentoRepo.add(comp1);
        equipamentoRepo.add(per1);
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
  //ADMINISTRADOR

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

    // ✅ ========== MÉTODOS DE VERIFICAÇÃO DE PERMISSÕES ==========

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
            // 1) Garante um id (se o construtor não tiver gerado)
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

    public boolean adicionarReserva(Reserva reserva) {
        if (reserva != null && reserva.getEquipamento().getQuantidadeEstoque() >= reserva.getQuantidade()) {
            reservas.add(reserva);
            return true;
        }
        return false;
    }

    public boolean removerReserva(Reserva reserva) {
        return reservas.remove(reserva);
    }

    public boolean atualizarReserva(Reserva reservaAntiga, Reserva reservaNova) {
        int index = reservas.indexOf(reservaAntiga);
        if (index >= 0) {
            reservas.set(index, reservaNova);
            return true;
        }
        return false;
    }

    public List<Reserva> getReservasPorCliente(Cliente cliente) {
        return reservas.stream()
                .filter(r -> r.getCliente().getId().equals(cliente.getId()) &&
                        r.getStatus() == Reserva.StatusReserva.ATIVA)
                .collect(java.util.stream.Collectors.toList());
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

    public List<Reserva> getReservas() {
        return reservas;
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
}
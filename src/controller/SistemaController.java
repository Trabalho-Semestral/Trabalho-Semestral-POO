package controller;

import model.abstractas.Equipamento;
import util.BCryptHasher;
import util.GeradorID;
import view.CardLayoutManager;
import model.concretas.*;

import java.util.ArrayList;
import java.util.List;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador principal do sistema.
 * Gerencia a lógica de negócio e a navegação entre telas.
 */
public class SistemaController {

    // Listas para simular base de dados
    private List<Administrador> administradores;
    private List<Vendedor> vendedores;
    private List<Cliente> clientes;
    private List<Equipamento> equipamentos;
    private List<Venda> vendas;
    private List<Reserva> reservas;

    // Usuário atualmente logado
    private Object usuarioLogado;
    private String tipoUsuarioLogado;

    // Gerenciador de navegação
    private CardLayoutManager cardLayoutManager;

    public SistemaController() {
        inicializarDados();
    }

    /**
     * Inicializa os dados de demonstração.
     */
    private void inicializarDados() {
        administradores = new ArrayList<>();
        vendedores = new ArrayList<>();
        clientes = new ArrayList<>();
        equipamentos = new ArrayList<>();
        vendas = new ArrayList<>();
        reservas = new ArrayList<>();

        criarUsuariosDemonstracao();
        criarEquipamentosDemonstracao();
    }

    /**
     * Cria usuários de demonstração.
     */
    private void criarUsuariosDemonstracao() {
        // Administrador
        Administrador admin = new Administrador("Administrador Sistema", "123456789012A",
                "123456789", "+258123456789", 50000.0, BCryptHasher.hashPassword("admin123"));
        admin.setId("ADMIN");
        administradores.add(admin);

        // Vendedor
        Vendedor vendedor = new Vendedor("João Vendedor", "987654321098B",
                "987654321", "+258987654321", 25000.0, BCryptHasher.hashPassword("vend123"));
        vendedor.setId("VEN1");
        vendedores.add(vendedor);

        // Cliente
        Cliente cliente = new Cliente("Maria Cliente", "111222333444C",
                "111222333", "+258111222333",
                "Rua das Flores, 123", "maria@email.com");
        cliente.setId("CLI1");
        clientes.add(cliente);
    }

    /**
     * Cria equipamentos de demonstração.
     */
    private void criarEquipamentosDemonstracao() {
        // Computador
        Computador comp1 = new Computador("Dell", 45000.0, 5,
                Equipamento.EstadoEquipamento.NOVO, "/images/dell_laptop.jpg",
                "Intel i7", "16GB", "512GB SSD", "NVIDIA GTX 1650");
        comp1.setId(GeradorID.gerarID());
        equipamentos.add(comp1);

        // Periférico
        Periferico per1 = new Periferico("Logitech", 1500.0, 10,
                Equipamento.EstadoEquipamento.NOVO, "/images/logitech_mouse.jpg", "Mouse");
        per1.setId(GeradorID.gerarID());
        equipamentos.add(per1);
    }

    /**
     * Autentica um usuário no sistema.
     * @param id ID do usuário
     * @param senha Senha do usuário
     * @param tipoUsuario Tipo de usuário (Administrador, Vendedor, Cliente)
     * @return true se autenticado com sucesso, false caso contrário
     */
    public boolean autenticarUsuario(String id, String senha, String tipoUsuario) {
        switch (tipoUsuario) {
            case "Administrador":
                for (Administrador admin : administradores) {
                    if (admin.getId().equals(id) && BCryptHasher.checkPassword(senha, admin.getSenha())) {
                        usuarioLogado = admin;
                        tipoUsuarioLogado = tipoUsuario;
                        return true;
                    }
                }
                break;

            case "Vendedor":
                for (Vendedor vendedor : vendedores) {
                    if (vendedor.getId().equals(id) && BCryptHasher.checkPassword(senha, vendedor.getSenha())) {
                        usuarioLogado = vendedor;
                        tipoUsuarioLogado = tipoUsuario;
                        return true;
                    }
                }
                break;


        }

        return false;
    }

    /**
     * Faz logout do usuário atual.
     */
    public void logout() {
        usuarioLogado = null;
        tipoUsuarioLogado = null;
    }

    /**
     * Adiciona um equipamento à lista.
     * @param equipamento Equipamento a ser adicionado
     * @return true se adicionado com sucesso, false caso contrário
     */
    public boolean adicionarEquipamento(Equipamento equipamento) {
        if (equipamento != null && equipamento.validarDados()) {
            equipamento.setId(GeradorID.gerarID());
            equipamentos.add(equipamento);
            return true;
        }
        return false;
    }

    /**
     * Remove um equipamento da lista.
     * @param equipamento Equipamento a ser removido
     * @return true se removido com sucesso, false caso contrário
     */
    public boolean removerEquipamento(Equipamento equipamento) {
        return equipamentos.remove(equipamento);
    }

    /**
     * Atualiza um equipamento na lista.
     * @param equipamentoAntigo Equipamento a ser atualizado
     * @param equipamentoNovo Novos dados do equipamento
     * @return true se atualizado com sucesso, false caso contrário
     */
    public boolean atualizarEquipamento(Equipamento equipamentoAntigo, Equipamento equipamentoNovo) {
        int index = equipamentos.indexOf(equipamentoAntigo);
        if (index >= 0 && equipamentoNovo.validarDados()) {
            equipamentoNovo.setId(equipamentoAntigo.getId()); // Manter o ID original
            equipamentos.set(index, equipamentoNovo);
            return true;
        }
        return false;
    }

    /**
     * Adiciona um vendedor à lista.
     * @param vendedor Vendedor a ser adicionado
     * @return true se adicionado com sucesso, false caso contrário
     */
    public boolean adicionarVendedor(Vendedor vendedor) {
        if (vendedor != null && vendedor.validarDados()) {
            vendedor.setId(GeradorID.gerarID());
            vendedor.setSenha(BCryptHasher.hashPassword(vendedor.getSenha()));
            vendedores.add(vendedor);
            return true;
        }
        return false;
    }

    /**
     * Remove um vendedor da lista.
     * @param vendedor Vendedor a ser removido
     * @return true se removido com sucesso, false caso contrário
     */
    public boolean removerVendedor(Vendedor vendedor) {
        return vendedores.remove(vendedor);
    }

    /**
     * Atualiza um vendedor na lista.
     * @param vendedorAntigo Vendedor a ser atualizado
     * @param vendedorNovo Novos dados do vendedor
     * @return true se atualizado com sucesso, false caso contrário
     */
    public boolean atualizarVendedor(Vendedor vendedorAntigo, Vendedor vendedorNovo) {
        int index = vendedores.indexOf(vendedorAntigo);
        if (index >= 0 && vendedorNovo.validarDados()) {
            vendedorNovo.setId(vendedorAntigo.getId()); // Manter o ID original
            // A senha já deve vir hasheada do formulário ou ser tratada separadamente
            vendedores.set(index, vendedorNovo);
            return true;
        }
        return false;
    }

    /**
     * Adiciona um cliente à lista.
     * @param cliente Cliente a ser adicionado
     * @return true se adicionado com sucesso, false caso contrário
     */
    public boolean adicionarCliente(Cliente cliente) {
        if (cliente != null && cliente.validarDados()) {
            cliente.setId(GeradorID.gerarID());
            clientes.add(cliente);
            return true;
        }
        return false;
    }

    /**
     * Remove um cliente da lista.
     * @param cliente Cliente a ser removido
     * @return true se removido com sucesso, false caso contrário
     */
    public boolean removerCliente(Cliente cliente) {
        return clientes.remove(cliente);
    }

    /**
     * Atualiza um cliente na lista.
     * @param clienteAntigo Cliente a ser atualizado
     * @param clienteNovo Novos dados do cliente
     * @return true se atualizado com sucesso, false caso contrário
     */
    public boolean atualizarCliente(Cliente clienteAntigo, Cliente clienteNovo) {
        int index = clientes.indexOf(clienteAntigo);
        if (index >= 0 && clienteNovo.validarDados()) {
            clienteNovo.setId(clienteAntigo.getId()); // Manter o ID original
            clientes.set(index, clienteNovo);
            return true;
        }
        return false;
    }

    /**
     * Registra uma nova venda no sistema.
     * @param venda Venda a ser registrada
     * @return true se a venda foi registrada com sucesso, false caso contrário
     */
    public boolean registrarVenda(Venda venda) {
        if (venda != null && venda.validarDados()) {
            venda.setIdVenda(GeradorID.gerarID());
            vendas.add(venda);
            return true;
        }
        return false;
    }

    // Getters
    public Object getUsuarioLogado() {
        return usuarioLogado;
    }

    public String getTipoUsuarioLogado() {
        return tipoUsuarioLogado;
    }

    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public List<Vendedor> getVendedores() {
        return vendedores;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public List<Equipamento> getEquipamentos() {
        return equipamentos;
    }

    public List<Venda> getVendas() {
        return vendas;
    }

    public List<Reserva> getReservas() {
        return reservas;
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

    public CardLayoutManager getCardLayoutManager() {
        return cardLayoutManager;
    }

    public void setCardLayoutManager(CardLayoutManager cardLayoutManager) {
        this.cardLayoutManager = cardLayoutManager;
    }
}


package controller;

import model.abstractas.Equipamento;
import util.BCryptHasher;
import util.GeradorID;
import view.CardLayoutManager;
import model.concretas.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Controlador principal do sistema.
 * Gerencia a lógica de negócio e a navegação entre telas.
 */
public class SistemaController {

    // Listas para simular base de dados
    private List<Administrador> administradores;
    private List<Gestor> gestores;
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
        gestores = new ArrayList<>();
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

        // Gestor
        Gestor gestor = new Gestor("Carlos Gestor", "555666777888D",
                "555666777", "+258555666777", 35000.0, BCryptHasher.hashPassword("gest123"));
        gestor.setId("GEST1");
        gestores.add(gestor);

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
                Equipamento.EstadoEquipamento.NOVO, "C:\\Users\\Nelson Wilson\\IdeaProjects\\Gestao Equipamentos\\Trabalho\\resources\\fotos\\equipamentos\\93b4613e-2f3b-4079-b903-7cc3241694ef.jpg",
                "Intel i7", "16GB", "512GB SSD", "NVIDIA GTX 1650");
        comp1.setId(GeradorID.gerarID());
        equipamentos.add(comp1);

        // Periférico
        Periferico per1 = new Periferico("Logitech", 1500.0, 10,
                Equipamento.EstadoEquipamento.NOVO, "C:\\Users\\Nelson Wilson\\IdeaProjects\\Gestao Equipamentos\\Trabalho\\resources\\fotos\\equipamentos\\c6eefaec-7deb-46a2-a6ab-40d995176d2e.jpg", "Mouse");
        per1.setId(GeradorID.gerarID());
        equipamentos.add(per1);
    }

    /**
     * Autentica um usuário no sistema.
     * @param id ID do usuário
     * @param senha Senha do usuário
     * @return true se autenticado com sucesso, false caso contrário
     */

    public String autenticarUsuario(String id, String senha) {
        // Administrador
        for (Administrador admin : administradores) {
            if (admin.getId().equals(id) && BCryptHasher.checkPassword(senha, admin.getSenha())) {
                usuarioLogado = admin;
                tipoUsuarioLogado = admin.getTipoUsuario().getDescricao();
                return "Administrador";
            }
        }

        // Gestor
        for (Gestor gestor : gestores) {
            if (gestor.getId().equals(id) && BCryptHasher.checkPassword(senha, gestor.getSenha())) {
                usuarioLogado = gestor;
                tipoUsuarioLogado = gestor.getTipoUsuario().getDescricao();
                return "Gestor";
            }
        }

        // Vendedor
        for (Vendedor vendedor : vendedores) {
            if (vendedor.getId().equals(id) && BCryptHasher.checkPassword(senha, vendedor.getSenha())) {
                usuarioLogado = vendedor;
                tipoUsuarioLogado = vendedor.getTipoUsuario().getDescricao();
                return "Vendedor";
            }
        }

        // Nenhum encontrado
        return null;
    }


    /**
     * Faz logout do usuário atual.
     */
    public void logout() {
        usuarioLogado = null;
        tipoUsuarioLogado = null;
    }

    // ✅ ========== MÉTODOS PARA GESTÃO DE GESTORES ==========

    /**
     * Adiciona um gestor à lista.
     * @param gestor Gestor a ser adicionado
     * @return true se adicionado com sucesso, false caso contrário
     */
    public boolean adicionarGestor(Gestor gestor) {
        if (gestor != null && gestor.validarDados()) {
            gestor.setId(GeradorID.gerarID());
            gestor.setSenha(BCryptHasher.hashPassword(gestor.getSenha()));
            gestores.add(gestor);
            return true;
        }
        return false;
    }

    /**
     * Remove um gestor da lista.
     * @param gestor Gestor a ser removido
     * @return true se removido com sucesso, false caso contrário
     */
    public boolean removerGestor(Gestor gestor) {
        return gestores.remove(gestor);
    }

    /**
     * Atualiza um gestor na lista.
     * @param gestorAntigo Gestor a ser atualizado
     * @param gestorNovo Novos dados do gestor
     * @return true se atualizado com sucesso, false caso contrário
     */
    public boolean atualizarGestor(Gestor gestorAntigo, Gestor gestorNovo) {
        int index = gestores.indexOf(gestorAntigo);
        if (index >= 0 && gestorNovo.validarDados()) {
            gestorNovo.setId(gestorAntigo.getId()); // Manter o ID original
            // A senha já deve vir hasheada do formulário ou ser tratada separadamente
            gestores.set(index, gestorNovo);
            return true;
        }
        return false;
    }

    /**
     * Getter para a lista de gestores.
     * @return Lista de gestores
     */
    public List<Gestor> getGestores() {
        return gestores;
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

    // ========== GETTERS ==========
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

    public CardLayoutManager getCardLayoutManager() {
        return cardLayoutManager;
    }

    public void setCardLayoutManager(CardLayoutManager cardLayoutManager) {
        this.cardLayoutManager = cardLayoutManager;
    }
}
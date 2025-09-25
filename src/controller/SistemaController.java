package controller;

import model.abstractas.Equipamento;
import util.BCryptHasher;
import util.GeradorID;
import view.CardLayoutManager;
import model.concretas.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador principal do sistema.
 * Gerencia lógica de negócio e navegação entre telas.
 */
public class SistemaController {

    // Base de dados simulada
    private final List<Administrador> administradores = new ArrayList<>();
    private final List<Gestor> gestores = new ArrayList<>();
    private final List<Vendedor> vendedores = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Equipamento> equipamentos = new ArrayList<>();
    private final List<Venda> vendas = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();

    // Usuário logado
    private Object usuarioLogado;
    private String tipoUsuarioLogado;

    // Gerenciador de navegação
    private CardLayoutManager cardLayoutManager;

    public SistemaController() {
        inicializarDados();
    }

    /** ----------------- INICIALIZAÇÃO ----------------- */
    private void inicializarDados() {
        criarUsuariosDemonstracao();
        criarEquipamentosDemonstracao();
    }

    private void criarUsuariosDemonstracao() {
        administradores.add(new Administrador("Administrador Sistema","123456789012A","123456789","+258123456789",50000.0, BCryptHasher.hashPassword("admin123")) {{
            setId("ADMIN");
        }});
        gestores.add(new Gestor("Carlos Gestor","555666777888D","555666777","+258555666777",35000.0, BCryptHasher.hashPassword("gest123")) {{
            setId("GEST1");
        }});
        vendedores.add(new Vendedor("João Vendedor","987654321098B","987654321","+258987654321",25000.0, BCryptHasher.hashPassword("vend123")) {{
            setId("VEN1");
        }});
        clientes.add(new Cliente("Maria Cliente","111222333444C","111222333","+258111222333","Rua das Flores, 123","maria@email.com") {{
            setId("CLI1");
        }});
    }

    private void criarEquipamentosDemonstracao() {
        equipamentos.add(new Computador("Dell",45000.0,5,Equipamento.EstadoEquipamento.NOVO,
                "C:\\Users\\Nelson Wilson\\IdeaProjects\\Gestao Equipamentos\\Trabalho\\resources\\fotos\\equipamentos\\93b4613e-2f3b-4079-b903-7cc3241694ef.jpg",
                "Intel i7","16GB","512GB SSD","NVIDIA GTX 1650") {{
            setId(GeradorID.gerarID());
        }});
        equipamentos.add(new Periferico("Logitech",1500.0,10,Equipamento.EstadoEquipamento.NOVO,
                "C:\\Users\\Nelson Wilson\\IdeaProjects\\Gestao Equipamentos\\Trabalho\\resources\\fotos\\equipamentos\\c6eefaec-7deb-46a2-a6ab-40d995176d2e.jpg","Mouse") {{
            setId(GeradorID.gerarID());
        }});
    }

    /** ----------------- AUTENTICAÇÃO ----------------- */
    public String autenticarUsuario(String id, String senha) {
        for (var u : administradores) if (u.getId().equals(id) && BCryptHasher.checkPassword(senha,u.getSenha())) return setUsuarioLogado(u);
        for (var u : gestores) if (u.getId().equals(id) && BCryptHasher.checkPassword(senha,u.getSenha())) return setUsuarioLogado(u);
        for (var u : vendedores) if (u.getId().equals(id) && BCryptHasher.checkPassword(senha,u.getSenha())) return setUsuarioLogado(u);
        return null;
    }

    private String setUsuarioLogado(Object usuario) {
        usuarioLogado = usuario;
        tipoUsuarioLogado = (usuario instanceof Administrador) ? "Administrador" :
                            (usuario instanceof Gestor) ? "Gestor" : "Vendedor";
        return tipoUsuarioLogado;
    }

    public void logout() {
        usuarioLogado = null;
        tipoUsuarioLogado = null;
    }

    /** ----------------- GESTÃO GENÉRICA ----------------- */
    private <T> boolean adicionarItem(List<T> lista, T item, boolean hasherSenha) {
        if (item == null) return false;
        if (item instanceof Gestor g && hasherSenha) g.setSenha(BCryptHasher.hashPassword(g.getSenha()));
        if (item instanceof Vendedor v && hasherSenha) v.setSenha(BCryptHasher.hashPassword(v.getSenha()));
        if (item instanceof Equipamento e) e.setId(GeradorID.gerarID());
        if (item instanceof Gestor || item instanceof Vendedor || item instanceof Cliente) {
            try {
                var metodoValidar = item.getClass().getMethod("validarDados");
                if (!(boolean) metodoValidar.invoke(item)) return false;
            } catch (Exception ignored) {}
        }
        lista.add(item);
        return true;
    }

    private <T> boolean removerItem(List<T> lista, T item) { return lista.remove(item); }

    private <T> boolean atualizarItem(List<T> lista, T antigo, T novo, boolean manterId) {
        int idx = lista.indexOf(antigo);
        if (idx < 0) return false;
        if (manterId) {
            try { var metodo = novo.getClass().getMethod("setId", String.class);
                  var id = antigo.getClass().getMethod("getId").invoke(antigo);
                  metodo.invoke(novo, id);
            } catch (Exception ignored) {}
        }
        lista.set(idx, novo);
        return true;
    }

    /** ----------------- MÉTODOS ESPECÍFICOS ----------------- */
    public boolean adicionarGestor(Gestor g) { return adicionarItem(gestores,g,true); }
    public boolean removerGestor(Gestor g) { return removerItem(gestores,g); }
    public boolean atualizarGestor(Gestor antigo, Gestor novo) { return atualizarItem(gestores,antigo,novo,true); }

    public boolean adicionarVendedor(Vendedor v) { return adicionarItem(vendedores,v,true); }
    public boolean removerVendedor(Vendedor v) { return removerItem(vendedores,v); }
    public boolean atualizarVendedor(Vendedor antigo,Vendedor novo) { return atualizarItem(vendedores,antigo,novo,true); }

    public boolean adicionarCliente(Cliente c) { return adicionarItem(clientes,c,false); }
    public boolean removerCliente(Cliente c) { return removerItem(clientes,c); }
    public boolean atualizarCliente(Cliente antigo, Cliente novo) { return atualizarItem(clientes,antigo,novo,true); }

    public boolean adicionarEquipamento(Equipamento e) { return adicionarItem(equipamentos,e,false); }
    public boolean removerEquipamento(Equipamento e) { return removerItem(equipamentos,e); }
    public boolean atualizarEquipamento(Equipamento antigo, Equipamento novo) { return atualizarItem(equipamentos,antigo,novo,true); }

    public boolean registrarVenda(Venda venda) {
        if (venda == null || !venda.validarDados()) return false;
        venda.setIdVenda(GeradorID.gerarID());
        vendas.add(venda);
        return true;
    }

    public boolean adicionarReserva(Reserva r) {
        if (r == null || r.getEquipamento().getQuantidadeEstoque() < r.getQuantidade()) return false;
        reservas.add(r);
        return true;
    }
    public boolean removerReserva(Reserva r) { return reservas.remove(r); }
    public boolean atualizarReserva(Reserva antiga, Reserva nova) { return atualizarItem(reservas, antiga, nova,false); }

    public List<Reserva> getReservasPorCliente(Cliente c) {
        return reservas.stream()
                .filter(r -> r.getCliente().getId().equals(c.getId()) &&
                        r.getStatus() == Reserva.StatusReserva.ATIVA)
                .collect(Collectors.toList());
    }

    /** ----------------- PERMISSÕES ----------------- */
    public boolean podeGerirOperacoes() {
        if (usuarioLogado == null) return false;
        return switch (tipoUsuarioLogado) {
            case "Administrador" -> true;
            case "Gestor" -> ((Gestor) usuarioLogado).podeGerirOperacoes();
            default -> false;
        };
    }

    public boolean podeConfigurarSistema() {
        if (usuarioLogado == null) return false;
        return switch (tipoUsuarioLogado) {
            case "Administrador" -> true;
            case "Gestor" -> ((Gestor) usuarioLogado).podeConfigurarSistema();
            default -> false;
        };
    }

    public boolean podeAcessarRelatorios() {
        if (usuarioLogado == null) return false;
        return tipoUsuarioLogado.equals("Administrador") || tipoUsuarioLogado.equals("Gestor") || tipoUsuarioLogado.equals("Vendedor");
    }

    /** ----------------- GETTERS / SETTERS ----------------- */
    public Object getUsuarioLogado() { return usuarioLogado; }
    public String getTipoUsuarioLogado() { return tipoUsuarioLogado; }
    public List<Administrador> getAdministradores() { return administradores; }
    public List<Gestor> getGestores() { return gestores; }
    public List<Vendedor> getVendedores() { return vendedores; }
    public List<Cliente> getClientes() { return clientes; }
    public List<Equipamento> getEquipamentos() { return equipamentos; }
    public List<Venda> getVendas() { return vendas; }
    public List<Reserva> getReservas() { return reservas; }

    public CardLayoutManager getCardLayoutManager() { return cardLayoutManager; }
    public void setCardLayoutManager(CardLayoutManager c) { this.cardLayoutManager = c; }
}

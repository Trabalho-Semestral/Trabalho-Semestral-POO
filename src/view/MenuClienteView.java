package view;

import controller.SistemaController;
import model.abstractas.Equipamento;
import model.concretas.Cliente;
import model.concretas.Computador;
import model.concretas.Reserva;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class MenuClienteView extends JPanel {

    private final SistemaController controller;
    private final Cliente clienteLogado;

    private final JTable tabelaEquipamentos;
    private final DefaultTableModel modeloTabela;
    private final JButton btnAtualizar, btnLogout, btnReservar, btnVerReservas;
    private final JTextField txtFiltro;
    private final JComboBox<String> cmbFiltroTipo;
    private final JSpinner spnQuantidade;
    private final JLabel lblFotoPreview;

    private final Map<String, Integer> reservas = new HashMap<>();

    public MenuClienteView(SistemaController controller, Cliente clienteLogado) {
        this.controller = controller;
        this.clienteLogado = clienteLogado;
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        txtFiltro = UITheme.createStyledTextField();
        txtFiltro.setPreferredSize(new Dimension(200, 35));

        cmbFiltroTipo = UITheme.createStyledComboBox(new String[]{"Todos", "Computador", "Periférico"});
        cmbFiltroTipo.setPreferredSize(new Dimension(150, 35));

        spnQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        spnQuantidade.setFont(UITheme.FONT_BODY);

        btnAtualizar = UITheme.createPrimaryButton("🔄 Atualizar");
        btnReservar = UITheme.createPrimaryButton("🛒 Reservar");
        btnVerReservas = UITheme.createSecondaryButton("📋 Ver Reservas");
        btnLogout = UITheme.createSecondaryButton("🚪 Sair");

        String[] colunas = {"ID", "Tipo", "Marca", "Preço", "Disponível", "Estado", "Especificações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaEquipamentos = new JTable(modeloTabela);
        tabelaEquipamentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaEquipamentos.setFont(UITheme.FONT_BODY);
        tabelaEquipamentos.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaEquipamentos.setRowHeight(30);
        tabelaEquipamentos.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        tabelaEquipamentos.setSelectionForeground(UITheme.TEXT_PRIMARY);

        lblFotoPreview = new JLabel("Selecione um equipamento", SwingConstants.CENTER);
        lblFotoPreview.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 2));
        lblFotoPreview.setOpaque(true);
        lblFotoPreview.setBackground(UITheme.CARD_BACKGROUND);
        lblFotoPreview.setPreferredSize(new Dimension(200, 150));

        setupLayout();
        setupEvents();
        carregarEquipamentos();
    }

    /** ------------------------- LAYOUT ------------------------- */
    private void setupLayout() {
        add(criarTopPanel(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(800, 0));
        leftPanel.add(criarPainelFiltros(), BorderLayout.NORTH);
        leftPanel.add(criarPainelTabela(), BorderLayout.CENTER);
        leftPanel.add(criarPainelAcoes(), BorderLayout.SOUTH);

        JPanel rightPanel = criarPainelFoto();
        rightPanel.setPreferredSize(new Dimension(250, 0));

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel criarTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.TOPBAR_BACKGROUND);
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        top.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        JLabel lblTitulo = UITheme.createHeadingLabel("🛒 Catálogo de Equipamentos");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        top.add(lblTitulo, BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        JLabel lblUsuario = UITheme.createBodyLabel("👤 " + clienteLogado.getNome());
        lblUsuario.setForeground(UITheme.TEXT_WHITE);
        userPanel.add(lblUsuario);
        userPanel.add(btnLogout);
        top.add(userPanel, BorderLayout.EAST);

        return top;
    }

    private JPanel criarPainelFiltros() {
        JPanel panel = UITheme.createCardPanel(new BorderLayout(), new Insets(20, 20, 20, 20));
        panel.add(UITheme.createSubtitleLabel("Filtros de Pesquisa"), BorderLayout.NORTH);

        JPanel filterContent = new JPanel(new GridBagLayout());
        filterContent.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        addFiltro(filterContent, gbc, 0, "Buscar:", txtFiltro);
        addFiltro(filterContent, gbc, 2, "Tipo:", cmbFiltroTipo);
        gbc.gridx = 4; gbc.gridy = 0; filterContent.add(btnAtualizar, gbc);

        panel.add(filterContent, BorderLayout.CENTER);
        return panel;
    }

    private void addFiltro(JPanel panel, GridBagConstraints gbc, int x, String label, JComponent comp) {
        gbc.gridx = x; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(UITheme.createBodyLabel(label), gbc);
        gbc.gridx = x + 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }

    private JPanel criarPainelTabela() {
        JPanel panel = UITheme.createCardPanel(new BorderLayout(), new Insets(20, 20, 20, 20));
        panel.add(UITheme.createSubtitleLabel("Equipamentos Disponíveis"), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabelaEquipamentos);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scroll.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelFoto() {
        JPanel panel = UITheme.createCardPanel(new BorderLayout(), new Insets(20, 20, 20, 20));
        panel.add(UITheme.createSubtitleLabel("Foto do Equipamento"), BorderLayout.NORTH);
        panel.add(lblFotoPreview, BorderLayout.CENTER);

        JLabel lblInfo = UITheme.createBodyLabel("<html><center>💡 Clique em um equipamento<br>para ver sua foto</center></html>");
        lblInfo.setForeground(UITheme.TEXT_SECONDARY);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblInfo, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelAcoes() {
        JPanel panel = UITheme.createCardPanel(new BorderLayout(), new Insets(20, 20, 20, 20));
        panel.add(UITheme.createSubtitleLabel("Ações do Cliente"), BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridBagLayout());
        actions.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx=0; actions.add(UITheme.createBodyLabel("Quantidade:"), gbc);
        gbc.gridx=1; actions.add(spnQuantidade, gbc);
        gbc.gridx=2; actions.add(btnReservar, gbc);
        gbc.gridx=3; actions.add(btnVerReservas, gbc);

        panel.add(actions, BorderLayout.CENTER);
        JLabel lblInfo = UITheme.createBodyLabel("💡 Selecione um equipamento e escolha a quantidade para reservar");
        lblInfo.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(lblInfo, BorderLayout.SOUTH);

        return panel;
    }

    /** ------------------------- EVENTOS ------------------------- */
    private void setupEvents() {
        btnAtualizar.addActionListener(e -> carregarEquipamentos());
        btnLogout.addActionListener(e -> logout());
        btnReservar.addActionListener(e -> reservarEquipamento());
        btnVerReservas.addActionListener(e -> verReservas());
        txtFiltro.addActionListener(e -> carregarEquipamentos());
        cmbFiltroTipo.addActionListener(e -> carregarEquipamentos());
        tabelaEquipamentos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarFotoEquipamentoSelecionado();
        });
    }

    /** ------------------------- LÓGICA ------------------------- */
    private void carregarFotoEquipamentoSelecionado() {
        int row = tabelaEquipamentos.getSelectedRow();
        if (row < 0) { lblFotoPreview.setText("Selecione um equipamento"); return; }

        String id = (String) modeloTabela.getValueAt(row, 0);
        controller.getEquipamentos().stream()
                .filter(eq -> eq.getId().equals(id))
                .findFirst()
                .ifPresent(eq -> lblFotoPreview.setText(
                        "<html><center><b>" + eq.getMarca() + "</b><br>" +
                        eq.getId() + "<br><br>Foto não disponível<br>💻</center></html>"));
    }

    private void carregarEquipamentos() {
        modeloTabela.setRowCount(0);
        String filtroTexto = txtFiltro.getText().trim().toLowerCase();
        String filtroTipo = Objects.requireNonNull(cmbFiltroTipo.getSelectedItem()).toString();

        controller.getEquipamentos().stream()
            .filter(eq -> filtroTexto.isEmpty() || eq.getMarca().toLowerCase().contains(filtroTexto))
            .filter(eq -> "Todos".equals(filtroTipo) ||
                    (eq instanceof Computador && "Computador".equals(filtroTipo)) ||
                    (!(eq instanceof Computador) && "Periférico".equals(filtroTipo)))
            .forEach(eq -> {
                String tipo = eq instanceof Computador ? "Computador" : "Periférico";
                int disponivel = eq.getQuantidadeEstoque() - reservas.getOrDefault(eq.getId(), 0);
                modeloTabela.addRow(new Object[]{
                        eq.getId(), tipo, eq.getMarca(),
                        String.format("%.2f MT", eq.getPreco()),
                        disponivel > 0 ? "Sim (" + disponivel + ")" : "Não",
                        eq.getEstado(), obterEspecificacoes(eq)
                });
            });
    }

    private String obterEspecificacoes(Equipamento eq) {
        return (eq instanceof Computador comp) ?
                String.format("CPU: %s, RAM: %s", comp.getProcessador(), comp.getMemoriaRAM()) :
                "Periférico de informática";
    }

    private void reservarEquipamento() {
        int row = tabelaEquipamentos.getSelectedRow();
        if (row < 0) { alert("Selecione um equipamento para reservar.", JOptionPane.WARNING_MESSAGE); return; }

        String id = (String) modeloTabela.getValueAt(row, 0);
        int qtd = (Integer) spnQuantidade.getValue();

        controller.getEquipamentos().stream()
            .filter(eq -> eq.getId().equals(id))
            .findFirst()
            .ifPresentOrElse(eq -> {
                int reservaAtual = reservas.getOrDefault(id, 0);
                if (reservaAtual + qtd <= eq.getQuantidadeEstoque()) {
                    Reserva r = new Reserva(clienteLogado, eq, qtd);
                    if (controller.adicionarReserva(r)) {
                        reservas.put(id, reservaAtual + qtd);
                        alert("Reserva realizada com sucesso!\nEquipamento: " + eq.getMarca() +
                                "\nQuantidade: " + qtd, JOptionPane.INFORMATION_MESSAGE);
                        carregarEquipamentos();
                    } else alert("Erro ao realizar reserva.", JOptionPane.ERROR_MESSAGE);
                } else {
                    alert("Quantidade indisponível. Disponível: " +
                            (eq.getQuantidadeEstoque() - reservaAtual), JOptionPane.WARNING_MESSAGE);
                }
            }, () -> alert("Equipamento não encontrado.", JOptionPane.ERROR_MESSAGE));
    }

    private void verReservas() {
        List<Reserva> minhas = controller.getReservasPorCliente(clienteLogado);
        if (minhas.isEmpty()) { alert("Você não possui reservas ativas.", JOptionPane.INFORMATION_MESSAGE); return; }

        String[] colunas = {"Equipamento", "Marca", "Quantidade", "Valor Unit.", "Valor Total", "Data"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);

        double total = 0;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Reserva r : minhas) {
            modelo.addRow(new Object[]{
                    r.getEquipamento().getId(), r.getEquipamento().getMarca(),
                    r.getQuantidade(), String.format("%.2f MT", r.getEquipamento().getPreco()),
                    String.format("%.2f MT", r.getValorTotal()), df.format(r.getDataReserva())
            });
            total += r.getValorTotal();
        }

        JTable tabela = new JTable(modelo);
        tabela.setFont(UITheme.FONT_BODY);
        tabela.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabela.setRowHeight(25);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        panel.add(UITheme.createSubtitleLabel("Valor Total: %.2f MT".formatted(total)), BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "Minhas Reservas", JOptionPane.PLAIN_MESSAGE);
    }

    private void logout() {
        if (!reservas.isEmpty() &&
            JOptionPane.showConfirmDialog(this, "Há reservas pendentes. Deseja sair?",
                    "Confirmar Logout", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        controller.logout();
        controller.getCardLayoutManager().showPanel("Login");
    }

    /** Utilitário simples para alertas */
    private void alert(String msg, int type) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", type);
    }
}


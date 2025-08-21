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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuClienteView extends JPanel {
    
    private SistemaController controller;
    private Cliente clienteLogado;
    
    private JTable tabelaEquipamentos;
    private DefaultTableModel modeloTabela;
    private JButton btnAtualizar;
    private JButton btnLogout;
    private JButton btnReservar;
    private JButton btnVerReservas;
    private JTextField txtFiltro;
    private JComboBox<String> cmbFiltroTipo;
    private JSpinner spnQuantidade;
    private JLabel lblFotoPreview;
    private JPanel panelFoto;
    
    private Map<String, Integer> reservas = new HashMap<>();
    
    public MenuClienteView(SistemaController controller, Cliente clienteLogado) {
        this.controller = controller;
        this.clienteLogado = clienteLogado;
        initComponents();
        setupLayout();
        setupEvents();
        carregarEquipamentos();
    }
    
    private void initComponents() {
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
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEquipamentos = new JTable(modeloTabela);
        tabelaEquipamentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaEquipamentos.setFont(UITheme.FONT_BODY);
        tabelaEquipamentos.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaEquipamentos.setRowHeight(30);
        tabelaEquipamentos.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        tabelaEquipamentos.setSelectionForeground(UITheme.TEXT_PRIMARY);
        
        lblFotoPreview = new JLabel("Selecione um equipamento");
        lblFotoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblFotoPreview.setVerticalAlignment(SwingConstants.CENTER);
        lblFotoPreview.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 2));
        lblFotoPreview.setBackground(UITheme.CARD_BACKGROUND);
        lblFotoPreview.setOpaque(true);
        lblFotoPreview.setPreferredSize(new Dimension(200, 150));
    }
    
    private void setupLayout() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        
        JLabel lblTitulo = UITheme.createHeadingLabel("🛒 Catálogo de Equipamentos");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        topPanel.add(lblTitulo, BorderLayout.CENTER);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        JLabel lblUsuario = UITheme.createBodyLabel("👤 " + clienteLogado.getNome());
        lblUsuario.setForeground(UITheme.TEXT_WHITE);
        userPanel.add(lblUsuario);
        userPanel.add(btnLogout);
        topPanel.add(userPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(800, 0));
        
        JPanel filterPanel = criarPainelFiltros();
        leftPanel.add(filterPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = criarPainelTabela();
        leftPanel.add(tablePanel, BorderLayout.CENTER);
        
        JPanel actionPanel = criarPainelAcoes();
        leftPanel.add(actionPanel, BorderLayout.SOUTH);
        
        JPanel rightPanel = criarPainelFoto();
        rightPanel.setPreferredSize(new Dimension(250, 0));
        
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel criarPainelFiltros() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = UITheme.createSubtitleLabel("Filtros de Pesquisa");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel filterContent = new JPanel(new GridBagLayout());
        filterContent.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        filterContent.add(UITheme.createBodyLabel("Buscar:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        filterContent.add(txtFiltro, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        filterContent.add(UITheme.createBodyLabel("Tipo:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        filterContent.add(cmbFiltroTipo, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        filterContent.add(btnAtualizar, gbc);
        
        panel.add(filterContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel criarPainelTabela() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTabelaTitulo = UITheme.createSubtitleLabel("Equipamentos Disponíveis");
        lblTabelaTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTabelaTitulo, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(tabelaEquipamentos);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel criarPainelFoto() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = UITheme.createSubtitleLabel("Foto do Equipamento");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        panel.add(lblFotoPreview, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(UITheme.CARD_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel lblInfo = UITheme.createBodyLabel("<html><center>💡 Clique em um equipamento<br>para ver sua foto</center></html>");
        lblInfo.setForeground(UITheme.TEXT_SECONDARY);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(lblInfo, BorderLayout.CENTER);
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel criarPainelAcoes() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = UITheme.createSubtitleLabel("Ações do Cliente");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel actionContent = new JPanel(new GridBagLayout());
        actionContent.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        actionContent.add(UITheme.createBodyLabel("Quantidade:"), gbc);
        gbc.gridx = 1;
        actionContent.add(spnQuantidade, gbc);
        
        gbc.gridx = 2;
        actionContent.add(btnReservar, gbc);
        
        gbc.gridx = 3;
        actionContent.add(btnVerReservas, gbc);
        
        panel.add(actionContent, BorderLayout.CENTER);
        
        JLabel lblInfo = UITheme.createBodyLabel("💡 Selecione um equipamento e escolha a quantidade para reservar");
        lblInfo.setForeground(UITheme.TEXT_SECONDARY);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(lblInfo, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEvents() {
        btnAtualizar.addActionListener(e -> carregarEquipamentos());
        btnLogout.addActionListener(e -> logout());
        btnReservar.addActionListener(e -> reservarEquipamento());
        btnVerReservas.addActionListener(e -> verReservas());
        txtFiltro.addActionListener(e -> carregarEquipamentos());
        cmbFiltroTipo.addActionListener(e -> carregarEquipamentos());
        
        tabelaEquipamentos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarFotoEquipamentoSelecionado();
            }
        });
    }
    
    private void carregarFotoEquipamentoSelecionado() {
        int selectedRow = tabelaEquipamentos.getSelectedRow();
        if (selectedRow >= 0) {
            String equipamentoId = (String) modeloTabela.getValueAt(selectedRow, 0);
            Equipamento equipamento = controller.getEquipamentos().stream()
                .filter(eq -> eq.getId().equals(equipamentoId))
                .findFirst()
                .orElse(null);
                
            if (equipamento != null) {
                // Simular carregamento de foto
                lblFotoPreview.setText("<html><center><b>" + equipamento.getMarca() + "</b><br>" +
                    equipamento.getId() + "<br><br>" +
                    "Foto não disponível<br>" +
                    "💻</center></html>");
                lblFotoPreview.setIcon(null);
            }
        } else {
            lblFotoPreview.setText("Selecione um equipamento");
            lblFotoPreview.setIcon(null);
        }
    }
    
    private void carregarEquipamentos() {
        modeloTabela.setRowCount(0);
        List<Equipamento> equipamentos = controller.getEquipamentos();
        
        String filtroTexto = txtFiltro.getText().trim().toLowerCase();
        String filtroTipo = (String) cmbFiltroTipo.getSelectedItem();
        
        for (Equipamento eq : equipamentos) {
            boolean passaFiltroTexto = filtroTexto.isEmpty() || 
                eq.getMarca().toLowerCase().contains(filtroTexto);
            
            boolean passaFiltroTipo = "Todos".equals(filtroTipo) ||
                (eq instanceof Computador && "Computador".equals(filtroTipo)) ||
                (!(eq instanceof Computador) && "Periférico".equals(filtroTipo));
            
            if (passaFiltroTexto && passaFiltroTipo) {
                String tipo = eq instanceof Computador ? "Computador" : "Periférico";
                int disponivel = eq.getQuantidadeEstoque() - reservas.getOrDefault(eq.getId(), 0);
                String disponivelStr = disponivel > 0 ? "Sim (" + disponivel + ")" : "Não";
                String especificacoes = obterEspecificacoes(eq);
                
                Object[] row = {
                    eq.getId(),
                    tipo,
                    eq.getMarca(),
                    String.format("%.2f MT", eq.getPreco()),
                    disponivelStr,
                    eq.getEstado(),
                    especificacoes
                };
                modeloTabela.addRow(row);
            }
        }
    }
    
    private String obterEspecificacoes(Equipamento eq) {
        if (eq instanceof Computador) {
            Computador comp = (Computador) eq;
            return String.format("CPU: %s, RAM: %s", comp.getProcessador(), comp.getMemoriaRAM());
        } else {
            return "Periférico de informática";
        }
    }
    
    private void reservarEquipamento() {
        int selectedRow = tabelaEquipamentos.getSelectedRow();
        if (selectedRow >= 0) {
            String equipamentoId = (String) modeloTabela.getValueAt(selectedRow, 0);
            int quantidade = (Integer) spnQuantidade.getValue();
            
            Equipamento equipamento = controller.getEquipamentos().stream()
                .filter(eq -> eq.getId().equals(equipamentoId))
                .findFirst()
                .orElse(null);
                
            if (equipamento != null) {
                int reservaAtual = reservas.getOrDefault(equipamentoId, 0);
                int totalReservado = reservaAtual + quantidade;
                
                if (totalReservado <= equipamento.getQuantidadeEstoque()) {
                    Reserva reserva = new Reserva(clienteLogado, equipamento, quantidade);
                    if (controller.adicionarReserva(reserva)) {
                        reservas.put(equipamentoId, totalReservado);
                        JOptionPane.showMessageDialog(this, 
                            String.format("Reserva realizada com sucesso!\nEquipamento: %s\nQuantidade: %d", 
                                equipamento.getMarca(), quantidade), 
                            "Reserva Confirmada", JOptionPane.INFORMATION_MESSAGE);
                        carregarEquipamentos();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao realizar reserva.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Quantidade indisponível. Disponível: %d", 
                            equipamento.getQuantidadeEstoque() - reservaAtual), 
                        "Quantidade Indisponível", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para reservar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void verReservas() {
        List<Reserva> minhasReservas = controller.getReservasPorCliente(clienteLogado);
        
        if (minhasReservas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Você não possui reservas ativas.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] colunas = {"Equipamento", "Marca", "Quantidade", "Valor Unit.", "Valor Total", "Data"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        
        double valorTotal = 0.0;
        for (Reserva reserva : minhasReservas) {
            Object[] row = {
                reserva.getEquipamento().getId(),
                reserva.getEquipamento().getMarca(),
                reserva.getQuantidade(),
                String.format("%.2f MT", reserva.getEquipamento().getPreco()),
                String.format("%.2f MT", reserva.getValorTotal()),
                new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(reserva.getDataReserva())
            };
            modelo.addRow(row);
            valorTotal += reserva.getValorTotal();
        }
        
        JTable tabela = new JTable(modelo);
        tabela.setFont(UITheme.FONT_BODY);
        tabela.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabela.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JLabel lblTotal = UITheme.createSubtitleLabel(String.format("Valor Total das Reservas: %.2f MT", valorTotal));
        lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(lblTotal, BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(this, panel, "Minhas Reservas", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void logout() {
        if (!reservas.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Há reservas pendentes. Deseja realmente sair?",
                "Confirmar Logout",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        controller.logout();
        controller.getCardLayoutManager().showPanel("Login");
    }
}


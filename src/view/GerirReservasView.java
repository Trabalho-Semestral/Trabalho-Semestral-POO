package view;

import controller.SistemaController;
import model.concretas.Reserva;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class GerirReservasView extends JPanel {
    
    private SistemaController controller;
    
    private JTable tabelaReservas;
    private DefaultTableModel modeloTabela;
    private JButton btnAtualizar;
    private JButton btnCancelarReserva;
    private JButton btnVoltar;
    private JLabel lblTotalReservas;
    private JLabel lblValorTotal;
    
    public GerirReservasView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
        carregarReservas();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);
        
        btnAtualizar = UITheme.createPrimaryButton("🔄 Atualizar");
        btnCancelarReserva = UITheme.createDangerButton("❌ Cancelar Reserva");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        
        lblTotalReservas = UITheme.createBodyLabel("Total de Reservas: 0");
        lblValorTotal = UITheme.createBodyLabel("Valor Total: 0.00 MT");
        
        String[] colunas = {"ID Reserva", "Cliente", "Equipamento", "Marca", "Quantidade", "Valor Unit.", "Valor Total", "Data", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaReservas = new JTable(modeloTabela);
        tabelaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaReservas.setFont(UITheme.FONT_BODY);
        tabelaReservas.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaReservas.setRowHeight(30);
        tabelaReservas.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        tabelaReservas.setSelectionForeground(UITheme.TEXT_PRIMARY);
    }
    
    private void setupLayout() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        
        JLabel lblTitulo = UITheme.createHeadingLabel("📋 Gestão de Reservas");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        topPanel.add(lblTitulo, BorderLayout.CENTER);
        
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        voltarPanel.add(btnVoltar);
        topPanel.add(voltarPanel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UITheme.BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel statsPanel = criarPainelEstatisticas();
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = criarPainelTabela();
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = criarPainelBotoes();
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel criarPainelEstatisticas() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new GridLayout(1, 2, 20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 80));
        
        JPanel leftStats = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftStats.setBackground(UITheme.CARD_BACKGROUND);
        leftStats.add(lblTotalReservas);
        
        JPanel rightStats = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightStats.setBackground(UITheme.CARD_BACKGROUND);
        rightStats.add(lblValorTotal);
        
        panel.add(leftStats);
        panel.add(rightStats);
        
        return panel;
    }
    
    private JPanel criarPainelTabela() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTabelaTitulo = UITheme.createSubtitleLabel("Reservas Ativas");
        lblTabelaTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTabelaTitulo, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(tabelaReservas);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(UITheme.CARD_BACKGROUND);
        JLabel lblInfo = UITheme.createBodyLabel("💡 Selecione uma reserva na tabela para cancelar");
        lblInfo.setForeground(UITheme.TEXT_SECONDARY);
        infoPanel.add(lblInfo);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel criarPainelBotoes() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        btnAtualizar.setPreferredSize(new Dimension(140, 40));
        btnCancelarReserva.setPreferredSize(new Dimension(160, 40));
        
        panel.add(btnAtualizar);
        panel.add(btnCancelarReserva);
        
        return panel;
    }
    
    private void setupEvents() {
        btnAtualizar.addActionListener(e -> carregarReservas());
        btnCancelarReserva.addActionListener(e -> cancelarReserva());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
    }
    
    private void carregarReservas() {
        modeloTabela.setRowCount(0);
        List<Reserva> reservas = controller.getReservas();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        double valorTotalGeral = 0.0;
        int totalReservas = 0;
        
        for (Reserva reserva : reservas) {
            if (reserva.getStatus() == Reserva.StatusReserva.ATIVA) {
                Object[] row = {
                    reserva.getIdReserva(),
                    reserva.getCliente().getNome(),
                    reserva.getEquipamento().getId(),
                    reserva.getEquipamento().getMarca(),
                    reserva.getQuantidade(),
                    String.format("%.2f MT", reserva.getEquipamento().getPreco()),
                    String.format("%.2f MT", reserva.getValorTotal()),
                    sdf.format(reserva.getDataReserva()),
                    reserva.getStatus()
                };
                modeloTabela.addRow(row);
                valorTotalGeral += reserva.getValorTotal();
                totalReservas++;
            }
        }
        
        lblTotalReservas.setText("Total de Reservas: " + totalReservas);
        lblValorTotal.setText(String.format("Valor Total: %.2f MT", valorTotalGeral));
    }
    
    private void cancelarReserva() {
        int selectedRow = tabelaReservas.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente cancelar esta reserva?",
                "Confirmar Cancelamento",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String idReserva = (String) modeloTabela.getValueAt(selectedRow, 0);
                    Reserva reserva = controller.getReservas().stream()
                        .filter(r -> r.getIdReserva().equals(idReserva))
                        .findFirst()
                        .orElse(null);
                        
                    if (reserva != null) {
                        reserva.setStatus(Reserva.StatusReserva.CANCELADA);
                        JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!");
                        carregarReservas();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao encontrar a reserva.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.");
        }
    }

    private void voltarMenuPrincipal() {
        String tipoUsuario = controller.getTipoUsuarioLogado();
        if (tipoUsuario == null) {
            controller.getCardLayoutManager().showPanel("Login");
            return;
        }

        switch (tipoUsuario) {
            case "Gestor":
                controller.getCardLayoutManager().showPanel("MenuGestor");
                break;
            case "Vendedor":
                controller.getCardLayoutManager().showPanel("MenuVendedor");
                break;
            case "Administrador":
            default:
                controller.getCardLayoutManager().showPanel("MenuAdministrador");
                break;
        }
    }
}


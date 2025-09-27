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

    private final SistemaController controller;
    private final JTable tabelaReservas;
    private final DefaultTableModel modeloTabela;
    private final JButton btnAtualizar, btnCancelarReserva, btnVoltar;
    private final JLabel lblTotalReservas, lblValorTotal;

    public GerirReservasView(SistemaController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        // Botões
        btnAtualizar = UITheme.createPrimaryButton("🔄 Atualizar");
        btnCancelarReserva = UITheme.createDangerButton("❌ Cancelar Reserva");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        for (JButton b : new JButton[]{btnAtualizar, btnCancelarReserva, btnVoltar})
            b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        // Labels
        lblTotalReservas = UITheme.createBodyLabel("Total de Reservas: 0");
        lblValorTotal = UITheme.createBodyLabel("Valor Total: 0.00 MT");

        // Tabela
        String[] colunas = {"ID Reserva", "Cliente", "Equipamento", "Marca", "Quantidade",
                "Valor Unit.", "Valor Total", "Data", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaReservas = new JTable(modeloTabela);
        tabelaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaReservas.setFont(UITheme.FONT_BODY);
        tabelaReservas.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaReservas.setRowHeight(30);
        tabelaReservas.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        tabelaReservas.setSelectionForeground(UITheme.TEXT_PRIMARY);

        // Layout
        add(criarTopPanel(), BorderLayout.NORTH);
        add(criarCenterPanel(), BorderLayout.CENTER);

        // Eventos
        btnAtualizar.addActionListener(e -> carregarReservas());
        btnCancelarReserva.addActionListener(e -> cancelarReserva());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());

        carregarReservas();
    }

    private JPanel criarTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.TOPBAR_BACKGROUND);
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        top.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        JLabel lblTitulo = UITheme.createHeadingLabel("📋 Gestão de Reservas");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        voltarPanel.add(btnVoltar);

        top.add(lblTitulo, BorderLayout.CENTER);
        top.add(voltarPanel, BorderLayout.WEST);
        return top;
    }

    private JPanel criarCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.BACKGROUND_COLOR);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        center.add(criarPainelEstatisticas(), BorderLayout.NORTH);
        center.add(criarPainelTabela(), BorderLayout.CENTER);
        center.add(criarPainelBotoes(), BorderLayout.SOUTH);
        return center;
    }

    private JPanel criarPainelEstatisticas() {
        JPanel panel = UITheme.createCardPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 80));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT)), right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JPanel p : new JPanel[]{left, right}) p.setBackground(UITheme.CARD_BACKGROUND);
        left.add(lblTotalReservas); right.add(lblValorTotal);

        panel.add(left); panel.add(right);
        return panel;
    }

    private JPanel criarPainelTabela() {
        JPanel panel = UITheme.createCardPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = UITheme.createSubtitleLabel("Reservas Ativas");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabelaReservas);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scroll.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scroll, BorderLayout.CENTER);

        JLabel lblInfo = UITheme.createBodyLabel("💡 Selecione uma reserva na tabela para cancelar");
        lblInfo.setForeground(UITheme.TEXT_SECONDARY);
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(UITheme.CARD_BACKGROUND);
        infoPanel.add(lblInfo);

        panel.add(infoPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel criarPainelBotoes() {
        JPanel panel = UITheme.createCardPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        btnAtualizar.setPreferredSize(new Dimension(140, 40));
        btnCancelarReserva.setPreferredSize(new Dimension(160, 40));
        panel.add(btnAtualizar); panel.add(btnCancelarReserva);
        return panel;
    }

    private void carregarReservas() {
        modeloTabela.setRowCount(0);
        List<Reserva> reservas = controller.getReservas();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        double valorTotal = 0;
        long total = reservas.stream().filter(r -> r.getStatus() == Reserva.StatusReserva.ATIVA).peek(r -> {
            modeloTabela.addRow(new Object[]{
                    r.getIdReserva(), r.getCliente().getNome(), r.getEquipamento().getId(),
                    r.getEquipamento().getMarca(), r.getQuantidade(),
                    String.format("%.2f MT", r.getEquipamento().getPreco()),
                    String.format("%.2f MT", r.getValorTotal()),
                    sdf.format(r.getDataReserva()), r.getStatus()
            });
        }).mapToDouble(Reserva::getValorTotal).sum();

        lblTotalReservas.setText("Total de Reservas: " + (int) reservas.stream().filter(r -> r.getStatus() == Reserva.StatusReserva.ATIVA).count());
        lblValorTotal.setText(String.format("Valor Total: %.2f MT", valorTotal));
    }

    private void cancelarReserva() {
        int row = tabelaReservas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Deseja realmente cancelar esta reserva?",
                "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                String id = (String) modeloTabela.getValueAt(row, 0);
                Reserva reserva = controller.getReservas().stream()
                        .filter(r -> r.getIdReserva().equals(id)).findFirst().orElse(null);

                if (reserva != null) {
                    reserva.setStatus(Reserva.StatusReserva.CANCELADA);
                    JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!");
                    carregarReservas();
                } else JOptionPane.showMessageDialog(this, "Erro ao encontrar a reserva.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    private void voltarMenuPrincipal() {
        String tipo = controller.getTipoUsuarioLogado();
        if (tipo == null) controller.getCardLayoutManager().showPanel("Login");
        else controller.getCardLayoutManager().showPanel(
                switch (tipo) {
                    case "Gestor" -> "MenuGestor";
                    case "Vendedor" -> "MenuVendedor";
                    default -> "MenuAdministrador";
                });
    }
}



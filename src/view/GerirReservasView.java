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
    private JLabel lblTotalReservas, lblValorTotal;
    private JButton btnAtualizar, btnCancelarReserva, btnConverterVenda, btnNovaReserva, btnEditarReserva, btnVoltar;

    private static GerirReservasView instance;

    public GerirReservasView(SistemaController controller) {
        this.controller = controller;
        instance = this;
        initComponents();
        setupLayout();
        setupEvents();
        carregarReservas();
    }

    public static void atualizarTabelaReservas() {
        if (instance != null) {
            instance.carregarReservas();
        }
    }

    public static GerirReservasView getInstance() {
        return instance;
    }

    private void initComponents() {
        setBackground(UITheme.BACKGROUND_COLOR);

        btnAtualizar = UITheme.createPrimaryButton("🔄 Atualizar");
        btnCancelarReserva = UITheme.createDangerButton("❌ Cancelar");
        btnConverterVenda = UITheme.createSuccessButton("💰 Converter em Venda");
        btnNovaReserva = UITheme.createPrimaryButton("➕ Nova Reserva");
        btnEditarReserva = UITheme.createPrimaryButton("✏️ Editar Reserva");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");

        lblTotalReservas = UITheme.createTitleLabel("Total de Reservas: 0");
        lblValorTotal = UITheme.createTitleLabel("Valor Total: 0,00 MT");

        String[] colunas = {"ID", "Cliente", "Data Criação", "Expira em", "Status", "Itens", "Valor"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaReservas = new JTable(modeloTabela);
        tabelaReservas.setRowHeight(28);
        tabelaReservas.setFont(UITheme.FONT_BODY);
        tabelaReservas.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = UITheme.createTopbar("📋 Gestão de Reservas", btnVoltar);
        add(topPanel, BorderLayout.NORTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setResizeWeight(0.75);
        mainSplit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelTabela = UITheme.createCardPanel();
        painelTabela.setLayout(new BorderLayout(10, 10));
        painelTabela.setBorder(BorderFactory.createTitledBorder("📑 Reservas Registradas"));
        painelTabela.add(new JScrollPane(tabelaReservas), BorderLayout.CENTER);

        JPanel estatisticasPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        estatisticasPanel.setOpaque(false);
        estatisticasPanel.add(lblTotalReservas);
        estatisticasPanel.add(lblValorTotal);
        painelTabela.add(estatisticasPanel, BorderLayout.SOUTH);

        JPanel painelAcoes = UITheme.createCardPanel();
        painelAcoes.setLayout(new GridLayout(0, 1, 10, 10));
        painelAcoes.setBorder(BorderFactory.createTitledBorder("⚙️ Ações"));
        painelAcoes.add(btnNovaReserva);
        painelAcoes.add(btnEditarReserva);
        painelAcoes.add(btnCancelarReserva);
        painelAcoes.add(btnConverterVenda);
        painelAcoes.add(btnAtualizar);

        mainSplit.setLeftComponent(painelTabela);
        mainSplit.setRightComponent(painelAcoes);

        add(mainSplit, BorderLayout.CENTER);
    }

    private void setupEvents() {
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
        btnAtualizar.addActionListener(e -> carregarReservas());
        btnNovaReserva.addActionListener(e -> abrirRegistrarReserva());
        btnEditarReserva.addActionListener(e -> editarReservaSelecionada());
        btnCancelarReserva.addActionListener(e -> cancelarReservaSelecionada());
        btnConverterVenda.addActionListener(e -> converterReservaEmVenda());
    }

    public void carregarReservas() {
        modeloTabela.setRowCount(0);
        try {
            List<Reserva> reservas = controller.getReservas();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            double total = 0;
            for (Reserva r : reservas) {
                if (r != null && r.getCliente() != null) {
                    modeloTabela.addRow(new Object[]{
                            r.getIdReserva(),
                            r.getCliente().getNome(),
                            r.getDataReserva() != null ? sdf.format(r.getDataReserva()) : "N/A",
                            r.getExpiraEm() != null ? sdf.format(r.getExpiraEm()) : "N/A",
                            r.getStatus(),
                            r.getItens() != null ? r.getItens().size() : 0,
                            String.format("%.2f MT", r.getValorTotal())
                    });
                    total += r.getValorTotal();
                }
            }

            lblTotalReservas.setText("Total de Reservas: " + reservas.size());
            lblValorTotal.setText("Valor Total: " + String.format("%.2f MT", total));

            // Forçar atualização da UI
            modeloTabela.fireTableDataChanged();
            revalidate();
            repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar reservas: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Reserva getReservaSelecionada() {
        int row = tabelaReservas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String reservaId = (String) modeloTabela.getValueAt(row, 0);
        return controller.buscarReservaPorId(reservaId);
    }

    private void abrirRegistrarReserva() {
        try {
            RegistrarVendaView registrar = RegistrarVendaView.criarParaReserva(controller);
            CardLayoutManager clm = controller.getCardLayoutManager();
            clm.addPanel(registrar, "RegistrarReserva");
            clm.showPanel("RegistrarReserva");
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarReservaSelecionada() {
        Reserva reserva = getReservaSelecionada();
        if (reserva != null) {
            try {
                RegistrarVendaView registrar = RegistrarVendaView.criarParaReserva(controller);
                registrar.carregarReserva(reserva);
                CardLayoutManager clm = controller.getCardLayoutManager();
                clm.addPanel(registrar, "EditarReserva");
                clm.showPanel("EditarReserva");
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void converterReservaEmVenda() {
        Reserva reserva = getReservaSelecionada();
        if (reserva != null) {
            try {
                RegistrarVendaView registrar = RegistrarVendaView.criarParaVenda(controller);
                registrar.carregarReserva(reserva);
                CardLayoutManager clm = controller.getCardLayoutManager();
                clm.addPanel(registrar, "ConverterVenda");
                clm.showPanel("ConverterVenda");
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelarReservaSelecionada() {
        Reserva reserva = getReservaSelecionada();
        if (reserva != null) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Tens certeza que deseja cancelar a reserva?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                controller.cancelarReserva(reserva.getIdReserva());
                carregarReservas();
            }
        }
    }

    private void voltarMenuPrincipal() {
        String tipoUsuario = controller.getTipoUsuarioLogado();
        if (tipoUsuario == null) {
            controller.getCardLayoutManager().showPanel("Login");
            return;
        }
        switch (tipoUsuario) {
            case "Gestor" -> controller.getCardLayoutManager().showPanel("MenuGestor");
            case "Vendedor" -> controller.getCardLayoutManager().showPanel("MenuVendedor");
            case "Administrador" -> controller.getCardLayoutManager().showPanel("MenuAdministrador");
        }
    }
}
package view;

import controller.SistemaController;
import model.concretas.ItemReserva;
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

        /// Visiblidade de imagens
        btnAtualizar.setFont(new Font("Sengoe UI Emoji", Font.BOLD, 18));
        btnCancelarReserva.setFont(new Font("Sengoe UI Emoji", Font.BOLD, 18));
        btnConverterVenda.setFont(new Font("Sengoe UI Emoji", Font.BOLD, 18));
        btnNovaReserva.setFont(new Font("Sengoe UI Emoji", Font.BOLD, 18));
        btnEditarReserva.setFont(new Font("Sengoe UI Emoji", Font.BOLD, 18));
        btnVoltar.setFont(new Font("Sengoe UI Emoji", Font.BOLD, 18));

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

        // === TOPBAR ===
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        JLabel lblTitulo = UITheme.createHeadingLabel(" Gestão de Reservas");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Sengoe UI Emoji", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar);
        topBar.add(voltarPanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // === CONTEÚDO PRINCIPAL ===
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setResizeWeight(0.75);
        mainSplit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel da tabela
        JPanel painelTabela = UITheme.createCardPanel();
        painelTabela.setLayout(new BorderLayout(10, 10));
        painelTabela.setBorder(BorderFactory.createTitledBorder("📑 Reservas Registradas"));
        painelTabela.add(new JScrollPane(tabelaReservas), BorderLayout.CENTER);

        JPanel estatisticasPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        estatisticasPanel.setOpaque(false);
        estatisticasPanel.add(lblTotalReservas);
        estatisticasPanel.add(lblValorTotal);
        painelTabela.add(estatisticasPanel, BorderLayout.SOUTH);

        // Painel de ações
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

        // === RODAPÉ ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UITheme.PRIMARY_COLOR));
        bottomPanel.setPreferredSize(new Dimension(0, 40));

        JLabel lblCopyright = new JLabel("© 2025 Sistema de Venda de Equipamentos Informáticos");
        lblCopyright.setFont(UITheme.FONT_SMALL);
        lblCopyright.setForeground(UITheme.TEXT_WHITE);
        bottomPanel.add(lblCopyright);

        add(bottomPanel, BorderLayout.SOUTH);
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
        controller.debugEquipamentos();

        modeloTabela.setRowCount(0);
        try {
            List<Reserva> reservas = controller.getReservas();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            double total = 0;
            int count = 0;

            System.out.println("=== DEBUG GERIR RESERVAS ===");
            System.out.println("Total de reservas encontradas: " + reservas.size());

            for (Reserva r : reservas) {
                if (r != null && r.getCliente() != null) {
                    // DEBUG DETALHADO
                    System.out.println("--- Reserva ID: " + r.getIdReserva() + " ---");
                    System.out.println("Cliente: " + r.getCliente().getNome());
                    System.out.println("Status: " + r.getStatus());
                    System.out.println("Número de itens: " + (r.getItens() != null ? r.getItens().size() : 0));

                    // Calcular valor manualmente para debug
                    double valorCalculado = 0.0;
                    if (r.getItens() != null) {
                        for (ItemReserva item : r.getItens()) {
                            if (item != null && item.getEquipamento() != null) {
                                double precoItem = item.getEquipamento().getPreco();
                                int quantidade = item.getQuantidade();
                                double subtotal = precoItem * quantidade;
                                valorCalculado += subtotal;
                                System.out.println("  Item: " + item.getEquipamento().getMarca() +
                                        " - Preço: " + precoItem +
                                        " - Qtd: " + quantidade +
                                        " - Subtotal: " + subtotal);
                            } else {
                                System.out.println("  ⚠️ Item ou equipamento nulo!");
                            }
                        }
                    }

                    System.out.println("Valor calculado: " + valorCalculado);
                    System.out.println("Valor getValorTotal(): " + r.getValorTotal());

                    // Usar o maior valor entre calculado e getValorTotal()
                    double valorFinal = Math.max(valorCalculado, r.getValorTotal());

                    modeloTabela.addRow(new Object[]{
                            r.getIdReserva(),
                            r.getCliente().getNome(),
                            r.getDataReserva() != null ? sdf.format(r.getDataReserva()) : "N/A",
                            r.getExpiraEm() != null ? sdf.format(r.getExpiraEm()) : "N/A",
                            r.getStatus(),
                            r.getItens() != null ? r.getItens().size() : 0,
                            String.format("%.2f MT", valorFinal)
                    });
                    total += valorFinal;
                    count++;
                    System.out.println("Valor final usado: " + valorFinal);
                } else {
                    System.out.println("⚠️ Reserva ou cliente nulo: " + r);
                }
            }

            System.out.println("=== TOTAL GERAL: " + total + " ===");
            lblTotalReservas.setText("Total de Reservas: " + count);
            lblValorTotal.setText("Valor Total: " + String.format("%.2f MT", total));

            modeloTabela.fireTableDataChanged();
            tabelaReservas.revalidate();
            tabelaReservas.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar reservas: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Método auxiliar para calcular valor da reserva
    private double calcularValorReserva(Reserva reserva) {
        if (reserva.getItens() == null) return 0.0;

        double total = 0.0;
        for (ItemReserva item : reserva.getItens()) {
            if (item.getEquipamento() != null) {
                total += item.getEquipamento().getPreco() * item.getQuantidade();
            }
        }
        return total;
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
            if (reserva.getStatus() != Reserva.StatusReserva.ATIVA) {
                JOptionPane.showMessageDialog(this,
                        "Apenas reservas ATIVAS podem ser convertidas em vendas.\n" +
                                "Status atual: " + reserva.getStatus(),
                        "Reserva Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Converter reserva " + reserva.getIdReserva() + " em venda?\n" +
                            "Cliente: " + reserva.getCliente().getNome() + "\n" +
                            "Itens: " + reserva.getItens().size() + "\n" +
                            "Valor: " + String.format("%.2f MT", reserva.getValorTotal()) + "\n\n" +
                            "Esta ação irá CANCELAR a reserva e criar uma VENDA.",
                    "Converter Reserva em Venda", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean sucesso = controller.converterReservaParaVenda(reserva.getIdReserva());

                    if (sucesso) {
                        JOptionPane.showMessageDialog(this,
                                "Reserva convertida em venda com sucesso!",
                                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        carregarReservas();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Erro ao converter reserva em venda.\n" +
                                        "Verifique se há estoque suficiente.",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Erro ao converter reserva: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    public Reserva getReservaSelecionada() {
        int row = tabelaReservas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String reservaId = (String) modeloTabela.getValueAt(row, 0);
        return controller.buscarReservaPorId(reservaId);
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

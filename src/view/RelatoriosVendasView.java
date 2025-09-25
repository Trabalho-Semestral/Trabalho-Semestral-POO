package view;

import controller.SistemaController;
import model.concretas.Venda;
import model.concretas.Cliente;
import model.concretas.Vendedor;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tela para visualização de relatórios de vendas com interface otimizada.
 */
public class RelatoriosVendasView extends JPanel {

    private final SistemaController controller;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private List<Venda> vendasFiltradas = new ArrayList<>();

    // Componentes
    private JComboBox<String> cmbTipoRelatorio;
    private JTextField txtDataInicio, txtDataFim;
    private JComboBox<Vendedor> cmbVendedor;
    private JComboBox<Cliente> cmbCliente;
    private JButton btnGerarRelatorio, btnExportarPDF, btnLimparFiltros, btnVoltar;
    private JLabel lblTotalVendas, lblTotalFaturamento, lblTicketMedio, lblMelhorVendedor, lblMelhorCliente;
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabelaVendas;
    private JPanel painelGraficos;

    public RelatoriosVendasView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
        carregarDadosIniciais();
        gerarRelatorio();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        // ComboBoxes
        cmbTipoRelatorio = UITheme.createStyledComboBox(new String[]{"Todas as Vendas", "Por Período", "Por Vendedor", "Por Cliente"});
        txtDataInicio = UITheme.createStyledTextField();
        txtDataFim = UITheme.createStyledTextField();
        txtDataInicio.setText(txtDataFim.getText() = dateFormat.format(new Date()));
        cmbVendedor = UITheme.createStyledComboBox(new Vendedor[0]);
        cmbCliente = UITheme.createStyledComboBox(new Cliente[0]);

        // Botões
        btnGerarRelatorio = criarBotaoPrincipal("📊 Gerar Relatório");
        btnExportarPDF = criarBotaoSecundario("📄 Exportar PDF");
        btnLimparFiltros = criarBotaoSecundario("🗑️ Limpar Filtros");
        btnVoltar = criarBotaoSecundario("⬅️ Voltar");

        // Labels estatísticas
        lblTotalVendas = UITheme.createHeadingLabel("0");
        lblTotalFaturamento = UITheme.createHeadingLabel("0,00 MT");
        lblTicketMedio = UITheme.createHeadingLabel("0,00 MT");
        lblMelhorVendedor = UITheme.createBodyLabel("N/A");
        lblMelhorCliente = UITheme.createBodyLabel("N/A");
        lblTotalVendas.setForeground(UITheme.PRIMARY_COLOR);
        lblTotalFaturamento.setForeground(UITheme.SUCCESS_COLOR);
        lblTicketMedio.setForeground(UITheme.INFO_COLOR);

        // Tabela
        String[] colunas = {"ID", "Data", "Vendedor", "Cliente", "Qtd Itens", "Total"};
        modeloTabelaVendas = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaVendas = new JTable(modeloTabelaVendas);
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setFont(UITheme.FONT_BODY);
        tabelaVendas.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaVendas.setRowHeight(30);
        tabelaVendas.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        tabelaVendas.setSelectionForeground(UITheme.TEXT_PRIMARY);

        // Painel gráficos
        painelGraficos = new JPanel(new GridLayout(2, 2, 10, 10));
        painelGraficos.setBackground(UITheme.CARD_BACKGROUND);
        painelGraficos.setBorder(UITheme.BORDER_CARD);
    }

    private JButton criarBotaoPrincipal(String texto) {
        JButton btn = UITheme.createPrimaryButton(texto);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        return btn;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = UITheme.createSecondaryButton(texto);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        return btn;
    }

    private void setupLayout() {
        add(criarTopPanel(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_SUBHEADING);
        tabbedPane.addTab("📋 Relatórios", criarAbaRelatorios());
        tabbedPane.addTab("📈 Estatísticas", criarAbaEstatisticas());
        tabbedPane.addTab("📊 Gráficos", criarAbaGraficos());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel criarTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createMatteBorder(0,0,2,0,UITheme.PRIMARY_COLOR));
        topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        JLabel lblTitulo = UITheme.createHeadingLabel("📊 Relatórios de Vendas");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        topPanel.add(lblTitulo, BorderLayout.CENTER);

        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        voltarPanel.add(btnVoltar);
        topPanel.add(voltarPanel, BorderLayout.WEST);

        return topPanel;
    }

    private JPanel criarAbaRelatorios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_COLOR);
        panel.add(criarPainelFiltros(), BorderLayout.NORTH);
        panel.add(criarPainelTabela(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarAbaEstatisticas() {
        JPanel panel = new JPanel(new GridLayout(2,3,15,15));
        panel.setBackground(UITheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel.add(criarCard("📊 Total de Vendas", lblTotalVendas));
        panel.add(criarCard("💰 Faturamento Total", lblTotalFaturamento));
        panel.add(criarCard("🎯 Ticket Médio", lblTicketMedio));
        panel.add(criarCard("🏆 Melhor Vendedor", lblMelhorVendedor));
        panel.add(criarCard("👑 Melhor Cliente", lblMelhorCliente));
        panel.add(criarCard("📅 Período", new JLabel("Filtros ativos")));
        return panel;
    }

    private JPanel criarCard(String titulo, JLabel valor) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout());

        JLabel lblTitulo = UITheme.createSubtitleLabel(titulo);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        card.add(lblTitulo, BorderLayout.NORTH);

        valor.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarAbaGraficos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_COLOR);

        JLabel lblTitulo = UITheme.createSubtitleLabel("Análise Gráfica das Vendas");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20,20,10,20));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(painelGraficos);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarPainelFiltros() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());

        JLabel lblTitulo = UITheme.createSubtitleLabel("Filtros de Relatório");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        addLabelAndComponent(formPanel, gbc, 0,0, "Tipo de Relatório:", cmbTipoRelatorio);
        addLabelAndComponent(formPanel, gbc, 2,0, "Data Início:", txtDataInicio);
        addLabelAndComponent(formPanel, gbc, 4,0, "Data Fim:", txtDataFim);
        addLabelAndComponent(formPanel, gbc, 0,1, "Vendedor:", cmbVendedor);
        addLabelAndComponent(formPanel, gbc, 2,1, "Cliente:", cmbCliente);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setBackground(UITheme.CARD_BACKGROUND);
        buttonPanel.add(btnGerarRelatorio);
        buttonPanel.add(btnLimparFiltros);
        buttonPanel.add(btnExportarPDF);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addLabelAndComponent(JPanel panel, GridBagConstraints gbc, int x, int y, String labelText, JComponent comp) {
        gbc.gridx = x; gbc.gridy = y;
        panel.add(UITheme.createBodyLabel(labelText), gbc);
        gbc.gridx = x+1;
        panel.add(comp, gbc);
    }

    private JPanel criarPainelTabela() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());

        JLabel lblTitulo = UITheme.createSubtitleLabel("Vendas Encontradas");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT,1));
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupEvents() {
        btnGerarRelatorio.addActionListener(e -> gerarRelatorio());
        btnLimparFiltros.addActionListener(e -> limparFiltros());
        btnExportarPDF.addActionListener(e -> exportarPDF());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
        cmbTipoRelatorio.addActionListener(e -> atualizarCamposFiltro());
    }

    private void carregarDadosIniciais() {
        carregarCombo(cmbVendedor, controller.getVendedores());
        carregarCombo(cmbCliente, controller.getClientes());
        atualizarCamposFiltro();
    }

    private <T> void carregarCombo(JComboBox<T> combo, List<T> itens) {
        combo.removeAllItems();
        combo.addItem(null); // Todos
        itens.forEach(combo::addItem);
    }

    private void atualizarCamposFiltro() {
        String tipo = (String) cmbTipoRelatorio.getSelectedItem();
        txtDataInicio.setEnabled("Por Período".equals(tipo));
        txtDataFim.setEnabled("Por Período".equals(tipo));
        cmbVendedor.setEnabled("Por Vendedor".equals(tipo));
        cmbCliente.setEnabled("Por Cliente".equals(tipo));
    }

    private void gerarRelatorio() {
        vendasFiltradas = new ArrayList<>(controller.getVendas());
        String tipo = (String) cmbTipoRelatorio.getSelectedItem();

        if ("Por Vendedor".equals(tipo) && cmbVendedor.getSelectedItem() != null) {
            String idV = ((Vendedor)cmbVendedor.getSelectedItem()).getId();
            vendasFiltradas = vendasFiltradas.stream()
                    .filter(v -> v.getVendedor() != null && v.getVendedor().getId().equals(idV))
                    .collect(Collectors.toList());
        } else if ("Por Cliente".equals(tipo) && cmbCliente.getSelectedItem() != null) {
            String idC = ((Cliente)cmbCliente.getSelectedItem()).getId();
            vendasFiltradas = vendasFiltradas.stream()
                    .filter(v -> v.getCliente().getId().equals(idC))
                    .collect(Collectors.toList());
        }

        atualizarTabelaVendas();
        calcularEstatisticas();
        gerarGraficos();
    }

    private void limparFiltros() {
        cmbTipoRelatorio.setSelectedIndex(0);
        txtDataInicio.setText(dateFormat.format(new Date()));
        txtDataFim.setText(dateFormat.format(new Date()));
        cmbVendedor.setSelectedIndex(0);
        cmbCliente.setSelectedIndex(0);
        gerarRelatorio();
    }

    private void exportarPDF() {
        JOptionPane.showMessageDialog(this, "Exportação para PDF será implementada em versão futura.", "Em Desenvolvimento", JOptionPane.INFORMATION_MESSAGE);
    }

    private void voltarMenuPrincipal() {
        String tipo = controller.getTipoUsuarioLogado();
        if (tipo == null) controller.getCardLayoutManager().showPanel("Login");
        else {
            controller.getCardLayoutManager().showPanel(
                switch(tipo) {
                    case "Gestor" -> "MenuGestor";
                    case "Vendedor" -> "MenuVendedor";
                    default -> "MenuAdministrador";
                });
        }
    }

    private void atualizarTabelaVendas() {
        modeloTabelaVendas.setRowCount(0);
        for (Venda v : vendasFiltradas) {
            modeloTabelaVendas.addRow(new Object[]{
                v.getIdVenda(),
                dateFormat.format(v.getData()),
                v.getVendedor() != null ? v.getVendedor().getNome() : "N/A",
                v.getCliente().getNome(),
                v.getEquipamentos().size(),
                String.format("%.2f MT", v.getValorTotal())
            });
        }
    }

    private void calcularEstatisticas() {
        if (vendasFiltradas.isEmpty()) {
            lblTotalVendas.setText("0"); lblTotalFaturamento.setText("0,00 MT"); lblTicketMedio.setText("0,00 MT");
            lblMelhorVendedor.setText("N/A"); lblMelhorCliente.setText("N/A");
            return;
        }

        int totalVendas = vendasFiltradas.size();
        double faturamentoTotal = vendasFiltradas.stream().mapToDouble(Venda::getValorTotal).sum();
        lblTotalVendas.setText(String.valueOf(totalVendas));
        lblTotalFaturamento.setText(String.format("%.2f MT", faturamentoTotal));
        lblTicketMedio.setText(String.format("%.2f MT", faturamentoTotal/totalVendas));

        lblMelhorVendedor.setText(obterMelhor(vendasFiltradas, true));
        lblMelhorCliente.setText(obterMelhor(vendasFiltradas, false));
    }

    private String obterMelhor(List<Venda> vendas, boolean porVendedor) {
        Map<String, Double> map = new HashMap<>();
        for (Venda v : vendas) {
            String key = porVendedor ? Optional.ofNullable(v.getVendedor()).map(Vendedor::getNome).orElse("N/A") : v.getCliente().getNome();
            map.put(key,

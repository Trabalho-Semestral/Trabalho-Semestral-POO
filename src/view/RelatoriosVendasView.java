package view;

import controller.SistemaController;
import model.concretas.Cliente;
import model.concretas.Vendedor;
import org.jfree.chart.plot.CategoryPlot;
import util.UITheme;
import persistence.dto.VendaDTO;
import persistence.dto.ItemVendaDTO;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// Outros utilitários
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.io.FileOutputStream;
/**
 * Tela para visualização de relatórios de vendas com interface melhorada.
 */
public class RelatoriosVendasView extends JPanel {

    private SistemaController controller;

    // Componentes da interface
    private JComboBox<String> cmbTipoRelatorio;
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JComboBox<Vendedor> cmbVendedor;
    private JComboBox<Cliente> cmbCliente;
    private JButton btnGerarRelatorio;
    private JButton btnExportarPDF;
    private JButton btnLimparFiltros;
    private JButton btnVoltar;

    // Labels de estatísticas
    private JLabel lblTotalVendas;
    private JLabel lblTotalFaturamento;
    private JLabel lblTicketMedio;
    private JLabel lblMelhorVendedor;
    private JLabel lblMelhorCliente;

    // Tabela de vendas
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabelaVendas;

    // Painel de gráficos
    private JPanel painelGraficos;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private List<VendaDTO> vendasFiltradas;

    public RelatoriosVendasView(SistemaController controller) {
        this.controller = controller;
        this.vendasFiltradas = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEvents();
        carregarDadosIniciais();
        gerarRelatorio(); // Carregar dados iniciais
    }

    /**
     * Inicializa os componentes da interface.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        // ComboBoxes e campos com tema personalizado
        cmbTipoRelatorio = UITheme.createStyledComboBox(new String[]{
                "Todas as Vendas", "Por Período", "Por Vendedor", "Por Cliente"
        });

        txtDataInicio = UITheme.createStyledTextField();
        txtDataInicio.setText(dateFormat.format(new Date()));
        txtDataFim = UITheme.createStyledTextField();
        txtDataFim.setText(dateFormat.format(new Date()));

        cmbVendedor = UITheme.createStyledComboBox(new Vendedor[0]);
        cmbCliente = UITheme.createStyledComboBox(new Cliente[0]);

        // Botões com tema personalizado
        btnGerarRelatorio = UITheme.createPrimaryButton("Relatório");
        btnExportarPDF = UITheme.createSecondaryButton("Exportar PDF");
        btnLimparFiltros = UITheme.createSecondaryButton(" Limpar");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");


        // Labels de estatísticas
        lblTotalVendas = UITheme.createHeadingLabel("0");
        lblTotalFaturamento = UITheme.createHeadingLabel("0,00 MT");
        lblTicketMedio = UITheme.createHeadingLabel("0,00 MT");
        lblMelhorVendedor = UITheme.createBodyLabel("N/A");
        lblMelhorCliente = UITheme.createBodyLabel("N/A");

        // Configurar cores das estatísticas
        lblTotalVendas.setForeground(UITheme.PRIMARY_COLOR);
        lblTotalFaturamento.setForeground(UITheme.SUCCESS_COLOR);
        lblTicketMedio.setForeground(UITheme.INFO_COLOR);

        // Tabela de vendas
        String[] colunasVendas = {"ID", "Data", "Vendedor", "Cliente", "Qtd Itens", "Total"};
        modeloTabelaVendas = new DefaultTableModel(colunasVendas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaVendas = new JTable(modeloTabelaVendas);
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setFont(UITheme.FONT_BODY);
        tabelaVendas.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaVendas.setRowHeight(30);
        tabelaVendas.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        tabelaVendas.setSelectionForeground(UITheme.TEXT_PRIMARY);

        // Painel de gráficos
        painelGraficos = new JPanel();
        painelGraficos.setBackground(UITheme.CARD_BACKGROUND);
        painelGraficos.setBorder(UITheme.BORDER_CARD);
    }

    /**
     * Configura o layout da interface.
     */
    private void setupLayout() {
        // Painel superior com título

        setLayout(new BorderLayout());
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        JLabel lblTitulo = UITheme.createHeadingLabel("📊 Relatórios de Vendas");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new java.awt.Font("Sengoe UI Emoji", java.awt.Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar);
        topBar.add(voltarPanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // Painel principal com abas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_SUBHEADING);

        // Aba de filtros e tabela
        JPanel abaRelatorios = criarAbaRelatorios();
        tabbedPane.addTab("📋 Relatórios", abaRelatorios);

        // Aba de estatísticas
        JPanel abaEstatisticas = criarAbaEstatisticas();
        tabbedPane.addTab("📈 Estatísticas", abaEstatisticas);

        // Aba de gráficos
        JPanel abaGraficos = criarAbaGraficos();
        tabbedPane.addTab("📊 Gráficos", abaGraficos);

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Cria a aba de relatórios com filtros e tabela.
     */
    private JPanel criarAbaRelatorios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_COLOR);

        // Painel de filtros
        JPanel filtrosPanel = criarPainelFiltros();
        panel.add(filtrosPanel, BorderLayout.NORTH);

        // Painel da tabela
        JPanel tabelaPanel = criarPainelTabela();
        panel.add(tabelaPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Cria a aba de estatísticas.
     */
    private JPanel criarAbaEstatisticas() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(UITheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cards de estatísticas
        panel.add(criarCardEstatistica("📊 Total de Vendas", lblTotalVendas, "Número total de vendas realizadas"));
        panel.add(criarCardEstatistica("💰 Faturamento Total", lblTotalFaturamento, "Valor total faturado"));
        panel.add(criarCardEstatistica("🎯 Ticket Médio", lblTicketMedio, "Valor médio por venda"));
        panel.add(criarCardEstatistica("🏆 Melhor Vendedor", lblMelhorVendedor, "Vendedor com maior faturamento"));
        panel.add(criarCardEstatistica("👑 Melhor Cliente", lblMelhorCliente, "Cliente com maior volume de compras"));
        panel.add(criarCardEstatistica("📅 Período", new JLabel("Filtros ativos"), "Período dos dados exibidos"));

        return panel;
    }

    /**
     * Cria a aba de gráficos.
     */
    private JPanel criarAbaGraficos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_COLOR);

        // Título
        JLabel lblTitulo = UITheme.createSubtitleLabel("Análise Gráfica das Vendas");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Painel de gráficos
        painelGraficos.setLayout(new GridLayout(2, 2, 10, 10));
        painelGraficos.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(painelGraficos);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Cria um card de estatística.
     */
    private JPanel criarCardEstatistica(String titulo, JLabel valorLabel, String descricao) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout());

        // Título
        JLabel lblTitulo = UITheme.createSubtitleLabel(titulo);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(lblTitulo, BorderLayout.NORTH);

        // Valor
        valorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valorLabel, BorderLayout.CENTER);

        // Descrição
        JLabel lblDescricao = UITheme.createBodyLabel(descricao);
        lblDescricao.setForeground(UITheme.TEXT_SECONDARY);
        lblDescricao.setHorizontalAlignment(SwingConstants.CENTER);
        lblDescricao.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(lblDescricao, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Cria o painel de filtros.
     */
    private JPanel criarPainelFiltros() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());

        // Título
        JLabel lblTitulo = UITheme.createSubtitleLabel("Filtros de Relatório");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Formulário de filtros
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Tipo de relatório
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UITheme.createBodyLabel("Tipo de Relatório:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbTipoRelatorio, gbc);

        // Data início
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(UITheme.createBodyLabel("Data Início:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtDataInicio, gbc);

        // Data fim
        gbc.gridx = 4; gbc.gridy = 0;
        formPanel.add(UITheme.createBodyLabel("Data Fim:"), gbc);
        gbc.gridx = 5;
        formPanel.add(txtDataFim, gbc);

        // Vendedor
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UITheme.createBodyLabel("Vendedor:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbVendedor, gbc);

        // Cliente
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(UITheme.createBodyLabel("Cliente:"), gbc);
        gbc.gridx = 3;
        formPanel.add(cmbCliente, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setBackground(UITheme.CARD_BACKGROUND);
        buttonPanel.add(btnGerarRelatorio);
        buttonPanel.add(btnLimparFiltros);
        buttonPanel.add(btnExportarPDF);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Cria o painel da tabela.
     */
    private JPanel criarPainelTabela() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());

        // Título
        JLabel lblTitulo = UITheme.createSubtitleLabel("Vendas Encontradas");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Configura os eventos da interface.
     */
    private void setupEvents() {
        // Botão gerar relatório
        btnGerarRelatorio.addActionListener(e -> gerarRelatorio());

        // Botão limpar filtros
        btnLimparFiltros.addActionListener(e -> limparFiltros());

        // Botão exportar PDF
        btnExportarPDF.addActionListener(e -> exportarPDF());

        // Botão voltar
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());

        // Mudança no tipo de relatório
        cmbTipoRelatorio.addActionListener(e -> atualizarCamposFiltro());
    }

    /**
     * Carrega os dados iniciais.
     */
    private void carregarDadosIniciais() {
        // Carregar vendedores
        List<Vendedor> vendedores = controller.getVendedores();
        cmbVendedor.removeAllItems();
        cmbVendedor.addItem(null); // Opção "Todos"
        for (Vendedor vendedor : vendedores) {
            cmbVendedor.addItem(vendedor);
        }

        // Carregar clientes
        List<Cliente> clientes = controller.getClientes();
        cmbCliente.removeAllItems();
        cmbCliente.addItem(null); // Opção "Todos"
        for (Cliente cliente : clientes) {
            cmbCliente.addItem(cliente);
        }

        atualizarCamposFiltro();
    }

    /**
     * Atualiza a visibilidade dos campos de filtro baseado no tipo selecionado.
     */
    private void atualizarCamposFiltro() {
        String tipoSelecionado = (String) cmbTipoRelatorio.getSelectedItem();

        // Habilitar/desabilitar campos baseado no tipo
        boolean usarPeriodo = "Por Período".equals(tipoSelecionado);
        boolean usarVendedor = "Por Vendedor".equals(tipoSelecionado);
        boolean usarCliente = "Por Cliente".equals(tipoSelecionado);

        txtDataInicio.setEnabled(usarPeriodo);
        txtDataFim.setEnabled(usarPeriodo);
        cmbVendedor.setEnabled(usarVendedor);
        cmbCliente.setEnabled(usarCliente);
    }
    private String formatCurrency(BigDecimal v) {
        if (v == null) return "0,00";
        // Ajuste o Locale conforme desejar (US usa ponto, PT usa vírgula)
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private String resolveVendedorNome(String vendedorId) {
        if (vendedorId == null || vendedorId.isBlank()) return "N/A";
        return controller.findVendedorById(vendedorId)
                .map(model.concretas.Vendedor::getNome)
                .orElse(vendedorId);
    }

    private String resolveClienteNome(String clienteId) {
        if (clienteId == null || clienteId.isBlank()) return "N/A";
        return controller.findClienteById(clienteId)
                .map(model.concretas.Cliente::getNome)
                .orElse(clienteId);
    }

    private int somaQuantidadeItens(List<ItemVendaDTO> itens) {
        if (itens == null) return 0;
        int total = 0;
        for (ItemVendaDTO it : itens) total += it.quantidade;
        return total;
    }

    private Date parseDateOrNull(String ddMMyyyy) {
        try {
            if (ddMMyyyy == null || ddMMyyyy.isBlank()) return null;
            return dateFormat.parse(ddMMyyyy.trim());
        } catch (Exception e) {
            return null;
        }
    }
    private DefaultCategoryDataset buildDatasetTotalPorDia() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (vendasFiltradas == null) return dataset;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Map<String, java.math.BigDecimal> mapa = new java.util.TreeMap<>();
        for (VendaDTO v : vendasFiltradas) {
            String dia = sdf.format(new Date(v.dataMillis));
            java.math.BigDecimal total = v.total != null ? v.total : java.math.BigDecimal.ZERO;
            mapa.merge(dia, total, java.math.BigDecimal::add);
        }
        for (var e : mapa.entrySet()) {
            dataset.addValue(e.getValue().doubleValue(), "Total", e.getKey());
        }
        return dataset;
    }

    private DefaultCategoryDataset buildDatasetFaturamentoPorVendedor() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (vendasFiltradas == null) return dataset;
        java.util.Map<String, java.math.BigDecimal> mapa = new java.util.HashMap<>();
        for (VendaDTO v : vendasFiltradas) {
            String vendedor = resolveVendedorNome(v.vendedorId);
            java.math.BigDecimal total = v.total != null ? v.total : java.math.BigDecimal.ZERO;
            mapa.merge(vendedor, total, java.math.BigDecimal::add);
        }
        for (var e : mapa.entrySet()) {
            dataset.addValue(e.getValue().doubleValue(), "Vendedor", e.getKey());
        }
        return dataset;
    }

    private DefaultPieDataset buildDatasetTopClientes(int topN) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (vendasFiltradas == null) return dataset;
        java.util.Map<String, java.math.BigDecimal> mapa = new java.util.HashMap<>();
        for (VendaDTO v : vendasFiltradas) {
            String cliente = resolveClienteNome(v.clienteId);
            java.math.BigDecimal total = v.total != null ? v.total : java.math.BigDecimal.ZERO;
            mapa.merge(cliente, total, java.math.BigDecimal::add);
        }
        // Ordenar por valor desc e pegar topN
        java.util.List<java.util.Map.Entry<String, java.math.BigDecimal>> list = new java.util.ArrayList<>(mapa.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        int count = 0;
        for (var e : list) {
            dataset.setValue(e.getKey(), e.getValue().doubleValue());
            if (++count >= topN) break;
        }
        return dataset;
    }

    private DefaultCategoryDataset buildDatasetQtdVendasPorDia() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (vendasFiltradas == null) return dataset;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Map<String, Integer> mapa = new java.util.TreeMap<>();
        for (VendaDTO v : vendasFiltradas) {
            String dia = sdf.format(new Date(v.dataMillis));
            mapa.merge(dia, 1, Integer::sum);
        }
        for (var e : mapa.entrySet()) {
            dataset.addValue(e.getValue(), "Vendas", e.getKey());
        }
        return dataset;
    }

    private ChartPanel smallChartPanel(JFreeChart chart) {

        int w = 360, h = 220;

        try {
            int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
            if (dpi >= 144) {
                w = (int)(w * 0.9);
                h = (int)(h * 0.9);
            }
        } catch (Exception ignored) {}


        ChartPanel cp = new ChartPanel(chart, false);
        cp.setPreferredSize(new Dimension(w, h));
        cp.setMaximumDrawWidth(w);
        cp.setMaximumDrawHeight(h);
        cp.setMinimumDrawWidth(Math.max(300, (int)(w * 0.85)));
        cp.setMinimumDrawHeight(Math.max(180, (int)(h * 0.85)));
        cp.setMouseZoomable(false);
        cp.setPopupMenu(null);
        return cp;
    }
    private JFreeChart buildChartBar(String title, String categoryAxis, String valueAxis, DefaultCategoryDataset dataset) {
        return ChartFactory.createBarChart(title, categoryAxis, valueAxis, dataset, PlotOrientation.VERTICAL, false, true, false);
    }

    private JFreeChart buildChartLine(String title, String categoryAxis, String valueAxis, DefaultCategoryDataset dataset) {
        return ChartFactory.createLineChart(title, categoryAxis, valueAxis, dataset, PlotOrientation.VERTICAL, false, true, false);
    }

    private JFreeChart buildChartPie(String title, DefaultPieDataset dataset) {
        return ChartFactory.createPieChart(title, dataset, true, true, false);
    }

    private Image chartToITextImage(JFreeChart chart, int width, int height) throws Exception {
        BufferedImage bi = chart.createBufferedImage(width, height);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bi, "png", baos);
            baos.flush();
            return Image.getInstance(baos.toByteArray());
        }

    }
    /**
     * Gera o relatório baseado nos filtros selecionados.
     */
    private void gerarRelatorio() {
        try {
            // Carregar TODAS as vendas do arquivo (NDJSON)
            List<VendaDTO> todasVendasDTO = controller.getVendasDTO();
            vendasFiltradas = new ArrayList<>(todasVendasDTO);

            String tipoRelatorio = (String) cmbTipoRelatorio.getSelectedItem();

            switch (tipoRelatorio) {
                case "Por Período": {
                    Date ini = parseDateOrNull(txtDataInicio.getText());
                    Date fim = parseDateOrNull(txtDataFim.getText());
                    if (ini == null || fim == null) {
                        JOptionPane.showMessageDialog(this, "Datas inválidas. Use dd/MM/yyyy.", "Período inválido", JOptionPane.WARNING_MESSAGE);
                        modeloTabelaVendas.setRowCount(0);
                        calcularEstatisticas();
                        gerarGraficos();
                        return;
                    } else {
                        long a = ini.getTime();
                        long b = fim.getTime();
                        if (a > b) { long tmp = a; a = b; b = tmp; }
                        final long fa = a, fb = b;
                        vendasFiltradas = vendasFiltradas.stream()
                                .filter(v -> v.dataMillis >= fa && v.dataMillis <= fb)
                                .collect(java.util.stream.Collectors.toList());
                    }
                    break;
                }
                case "Por Vendedor": {
                    Vendedor vendedorSelecionado = (Vendedor) cmbVendedor.getSelectedItem();
                    if (vendedorSelecionado != null) {
                        String vendedorId = vendedorSelecionado.getId();
                        vendasFiltradas = vendasFiltradas.stream()
                                .filter(v -> vendedorId.equals(v.vendedorId))
                                .collect(java.util.stream.Collectors.toList());
                    }
                    break;
                }
                case "Por Cliente": {
                    Cliente clienteSelecionado = (Cliente) cmbCliente.getSelectedItem();
                    if (clienteSelecionado != null) {
                        String clienteId = clienteSelecionado.getId();
                        vendasFiltradas = vendasFiltradas.stream()
                                .filter(v -> clienteId.equals(v.clienteId))
                                .collect(java.util.stream.Collectors.toList());
                    }
                    break;
                }
                default:
                    break;
            }

            atualizarTabelaVendas();
            calcularEstatisticas();
            gerarGraficos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(this,
                "Encontradas " + vendasFiltradas.size() + " venda(s) para os filtros.",
                "Relatório", JOptionPane.INFORMATION_MESSAGE);
        cmbVendedor.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Vendedor v ? v.getNome() : (value == null ? "Todos" : String.valueOf(value)));
                return this;
            }
        });

        cmbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Cliente c ? c.getNome() : (value == null ? "Todos" : String.valueOf(value)));
                return this;
            }
        });
    }

    /**
     * Limpa todos os filtros.
     */
    private void limparFiltros() {
        cmbTipoRelatorio.setSelectedIndex(0);
        txtDataInicio.setText(dateFormat.format(new Date()));
        txtDataFim.setText(dateFormat.format(new Date()));
        cmbVendedor.setSelectedIndex(0);
        cmbCliente.setSelectedIndex(0);
        gerarRelatorio();
    }

    /**
     * Exporta o relatório para PDF.
     */
    private void exportarPDF() {
        try {
            if (vendasFiltradas == null || vendasFiltradas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma venda para exportar.", "Relatórios", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Exportar Relatório em PDF");
            chooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));

            String nomeSugerido = "relatorio_vendas_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf";
            chooser.setSelectedFile(new java.io.File(nomeSugerido));
            int res = chooser.showSaveDialog(this);
            if (res != JFileChooser.APPROVE_OPTION) return;

            java.io.File arq = chooser.getSelectedFile();
            if (!arq.getName().toLowerCase().endsWith(".pdf")) {
                arq = new java.io.File(arq.getParentFile(), arq.getName() + ".pdf");
            }

            // Criar documento iText
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36); // paisagem
            PdfWriter.getInstance(doc, new FileOutputStream(arq));
            doc.open();

            // Título e período
            Font fTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fSub = FontFactory.getFont(FontFactory.HELVETICA, 12);
            doc.add(new Paragraph("Relatório de Vendas", fTitle));
            String tipoSel = (String) cmbTipoRelatorio.getSelectedItem();
            String periodo = "Tipo: " + tipoSel +
                    ("Por Período".equals(tipoSel) ?
                            ("  |  Início: " + txtDataInicio.getText() + "  Fim: " + txtDataFim.getText()) : "");
            doc.add(new Paragraph(periodo, fSub));
            doc.add(new Paragraph("Gerado em: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fSub));
            doc.add(new Paragraph(" \n"));

            // Estatísticas
            PdfPTable tblStats = new PdfPTable(5);
            tblStats.setWidthPercentage(100);
            tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Total de Vendas\n" + lblTotalVendas.getText())));
            tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Faturamento Total\n" + lblTotalFaturamento.getText())));
            tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Ticket Médio\n" + lblTicketMedio.getText())));
            tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Melhor Vendedor\n" + lblMelhorVendedor.getText())));
            tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Melhor Cliente\n" + lblMelhorCliente.getText())));
            doc.add(tblStats);
            doc.add(new Paragraph(" \n"));

            // Tabela de vendas (mesmo conteúdo da JTable)
            PdfPTable tbl = new PdfPTable(modeloTabelaVendas.getColumnCount());
            tbl.setWidthPercentage(100);
            // Cabeçalho
            for (int c = 0; c < modeloTabelaVendas.getColumnCount(); c++) {
                PdfPCell th = new PdfPCell(new com.itextpdf.text.Phrase(modeloTabelaVendas.getColumnName(c)));
                th.setBackgroundColor(new com.itextpdf.text.BaseColor(230, 230, 230));
                tbl.addCell(th);
            }
            // Linhas
            for (int r = 0; r < modeloTabelaVendas.getRowCount(); r++) {
                for (int c = 0; c < modeloTabelaVendas.getColumnCount(); c++) {
                    Object val = modeloTabelaVendas.getValueAt(r, c);
                    tbl.addCell(val != null ? val.toString() : "");
                }
            }
            doc.add(tbl);
            doc.add(new Paragraph(" \n"));

            try {
                JFreeChart chartDia = buildChartBar("Total por Dia", "Dia", "Total (MT)", buildDatasetTotalPorDia());
                JFreeChart chartVend = buildChartBar("Faturamento por Vendedor", "Vendedor", "Total (MT)", buildDatasetFaturamentoPorVendedor());
                JFreeChart chartClientes = buildChartPie("Top Clientes (por faturamento)", buildDatasetTopClientes(8));
                JFreeChart chartEvolucao = buildChartLine("Evolução de Vendas (qtd)", "Dia", "Vendas", buildDatasetQtdVendasPorDia());

                Image img1 = chartToITextImage(chartDia, 900, 400);
                Image img2 = chartToITextImage(chartVend, 900, 400);
                Image img3 = chartToITextImage(chartClientes, 900, 400);
                Image img4 = chartToITextImage(chartEvolucao, 900, 400);

                doc.add(new Paragraph("Gráficos:\n", fSub));
                doc.add(img1);
                doc.add(img2);
                doc.add(img3);
                doc.add(img4);
            } catch (Exception ig) {
            }

            doc.close();
            JOptionPane.showMessageDialog(this, "PDF exportado em: " + arq.getAbsolutePath(), "Exportar PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (DocumentException de) {
            de.printStackTrace();
            JOptionPane.showMessageDialog(this, "Falha ao gerar PDF: " + de.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
    /**
     * Atualiza a tabela de vendas com cálculo de fallback
     */
    private void atualizarTabelaVendas() {
        modeloTabelaVendas.setRowCount(0);

        for (VendaDTO dto : vendasFiltradas) {
            Date data = new Date(dto.dataMillis);
            String vendedorNome = resolveVendedorNome(dto.vendedorId);
            String clienteNome = resolveClienteNome(dto.clienteId);
            int qtdItens = somaQuantidadeItens(dto.itens);

            // SOLUÇÃO DEFINITIVA: Se total for zero, recalcular baseado nos itens
            BigDecimal total = dto.total != null ? dto.total : BigDecimal.ZERO;

            if (total.compareTo(BigDecimal.ZERO) == 0 && dto.itens != null && !dto.itens.isEmpty()) {
                // Recalcular total baseado nos itens
                total = BigDecimal.ZERO;
                for (ItemVendaDTO item : dto.itens) {
                    if (item.precoUnitario != null) {
                        total = total.add(item.precoUnitario.multiply(BigDecimal.valueOf(item.quantidade)));
                    }
                }
                System.out.println("🔄 Total recalculado para venda " + dto.idVenda + ": " + total);
            }

            modeloTabelaVendas.addRow(new Object[]{
                    dto.idVenda,
                    dateFormat.format(data),
                    vendedorNome,
                    clienteNome,
                    qtdItens,
                    formatCurrency(total) + " MT"
            });
        }
        modeloTabelaVendas.fireTableDataChanged();
        tabelaVendas.revalidate();
        tabelaVendas.repaint();
    }
    /**
     * Calcula as estatísticas das vendas.
     */
    private void calcularEstatisticas() {
        if (vendasFiltradas == null || vendasFiltradas.isEmpty()) {
            lblTotalVendas.setText("0");
            lblTotalFaturamento.setText("0,00 MT");
            lblTicketMedio.setText("0,00 MT");
            lblMelhorVendedor.setText("N/A");
            lblMelhorCliente.setText("N/A");
            return;
        }

        int totalVendas = vendasFiltradas.size();
        lblTotalVendas.setText(String.valueOf(totalVendas));

        java.math.BigDecimal faturamentoTotal = java.math.BigDecimal.ZERO;
        for (VendaDTO v : vendasFiltradas) {
            faturamentoTotal = faturamentoTotal.add(v.total != null ? v.total : java.math.BigDecimal.ZERO);
        }
        lblTotalFaturamento.setText(formatCurrency(faturamentoTotal) + " MT");

        java.math.BigDecimal ticketMedio = totalVendas > 0
                ? faturamentoTotal.divide(new java.math.BigDecimal(totalVendas), 2, java.math.RoundingMode.HALF_UP)
                : java.math.BigDecimal.ZERO;
        lblTicketMedio.setText(formatCurrency(ticketMedio) + " MT");

        java.util.Map<String, java.math.BigDecimal> vendedorFaturamento = new java.util.HashMap<>();
        for (VendaDTO v : vendasFiltradas) {
            String nomeVend = resolveVendedorNome(v.vendedorId);
            vendedorFaturamento.merge(nomeVend, v.total != null ? v.total : java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        }
        String melhorVendedor = vendedorFaturamento.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("N/A");
        lblMelhorVendedor.setText(melhorVendedor);

        java.util.Map<String, java.math.BigDecimal> clienteFaturamento = new java.util.HashMap<>();
        for (VendaDTO v : vendasFiltradas) {
            String nomeCli = resolveClienteNome(v.clienteId);
            clienteFaturamento.merge(nomeCli, v.total != null ? v.total : java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        }
        String melhorCliente = clienteFaturamento.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("N/A");
        lblMelhorCliente.setText(melhorCliente);
    }
    /**
     * Gera os gráficos das vendas.
     */
    private void gerarGraficos() {
        painelGraficos.removeAll();

        if (vendasFiltradas == null || vendasFiltradas.isEmpty()) {
            JLabel lblSemDados = UITheme.createBodyLabel("Nenhuma venda encontrada para gerar gráficos");
            lblSemDados.setHorizontalAlignment(SwingConstants.CENTER);
            painelGraficos.setLayout(new BorderLayout());
            painelGraficos.add(lblSemDados, BorderLayout.CENTER);
        } else {
            // Grid 2x2 com espaçamento
            painelGraficos.setLayout(new GridLayout(2, 2, 8, 8));
            painelGraficos.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            // 1) Total por Dia (Barras)
            DefaultCategoryDataset dsDia = buildDatasetTotalPorDia();
            JFreeChart chartDia = buildChartBar("Total por Dia", "Dia", "Total (MT)", dsDia);
            painelGraficos.add(smallChartPanel(chartDia));

            // 2) Faturamento por Vendedor (Barras)
            DefaultCategoryDataset dsVend = buildDatasetFaturamentoPorVendedor();
            JFreeChart chartVend = buildChartBar("Faturamento por Vendedor", "Vendedor", "Total (MT)", dsVend);
            painelGraficos.add(smallChartPanel(chartVend));

            // 3) Top Clientes (Pizza)
            DefaultPieDataset dsClientes = buildDatasetTopClientes(8);
            JFreeChart chartClientes = buildChartPie("Top Clientes (por faturamento)", dsClientes);
            painelGraficos.add(smallChartPanel(chartClientes));

            // 4) Evolução das Vendas (Linha)
            DefaultCategoryDataset dsEvolucao = buildDatasetQtdVendasPorDia();
            JFreeChart chartEvolucao = buildChartLine("Evolução de Vendas (qtd)", "Dia", "Vendas", dsEvolucao);
            painelGraficos.add(smallChartPanel(chartEvolucao));
        }

        painelGraficos.revalidate();
        painelGraficos.repaint();
    }
    private void tuneChartFonts(JFreeChart chart) {
        var plot = chart.getPlot();
        chart.getTitle().setFont(new java.awt.Font("SansSerif", Font.BOLD, 12));
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(new java.awt.Font("SansSerif", Font.ITALIC, 10));
        }
        // Exemplo para CategoryPlot
        if (plot instanceof CategoryPlot p) {
            p.getDomainAxis().setTickLabelFont(new java.awt.Font("SansSerif", Font.ITALIC, 10));
            p.getDomainAxis().setLabelFont(new java.awt.Font("SansSerif", Font.ITALIC, 11));
            p.getRangeAxis().setTickLabelFont(new java.awt.Font("SansSerif", Font.ITALIC, 10));
            p.getRangeAxis().setLabelFont(new java.awt.Font("SansSerif", Font.ITALIC, 11));
        }
    }



}


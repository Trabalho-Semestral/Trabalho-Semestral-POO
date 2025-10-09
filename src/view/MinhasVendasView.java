package view;

import controller.SistemaController;
import model.concretas.Vendedor;
import persistence.dto.VendaDTO;
import util.UITheme;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
        import javax.swing.table.DefaultTableModel;
import java.awt.*;
        import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
        import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

public class MinhasVendasView extends JPanel {

    private SistemaController controller;
    private Vendedor vendedorLogado;

    // Componentes da interface
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JButton btnFiltrar;
    private JButton btnLimparFiltros;
    private JButton btnVoltar;
    private JButton btnExportarPDF;

    // Labels de estatísticas pessoais
    private JLabel lblTotalMinhasVendas;
    private JLabel lblMeuFaturamento;
    private JLabel lblMeuTicketMedio;
    private JLabel lblMinhaPerformance;
    private JLabel lblMeuRanking;

    // Tabela de vendas do vendedor
    private JTable tabelaMinhasVendas;
    private DefaultTableModel modeloTabelaVendas;

    // Painel de gráficos pessoais
    private JPanel painelGraficos;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private List<VendaDTO> minhasVendasFiltradas;

    public MinhasVendasView(SistemaController controller, Vendedor vendedorLogado) {
        this.controller = controller;
        this.vendedorLogado = vendedorLogado;
        this.minhasVendasFiltradas = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEvents();
        carregarMinhasVendas();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        // Campos de data
        txtDataInicio = UITheme.createStyledTextField();
        txtDataInicio.setText(dateFormat.format(new Date()));
        txtDataFim = UITheme.createStyledTextField();
        txtDataFim.setText(dateFormat.format(new Date()));

        // Botões
        btnFiltrar = UITheme.createPrimaryButton("🔍 Filtrar");
        btnLimparFiltros = UITheme.createSecondaryButton("🔄 Limpar");
        btnExportarPDF = UITheme.createSecondaryButton("📄 Exportar PDF");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");

        // Labels de estatísticas pessoais
        lblTotalMinhasVendas = UITheme.createHeadingLabel("0");
        lblMeuFaturamento = UITheme.createHeadingLabel("0,00 MT");
        lblMeuTicketMedio = UITheme.createHeadingLabel("0,00 MT");
        lblMinhaPerformance = UITheme.createHeadingLabel("0%");
        lblMeuRanking = UITheme.createBodyLabel("-");

        // Configurar cores das estatísticas
        lblTotalMinhasVendas.setForeground(UITheme.PRIMARY_COLOR);
        lblMeuFaturamento.setForeground(UITheme.SUCCESS_COLOR);
        lblMeuTicketMedio.setForeground(UITheme.INFO_COLOR);
        lblMinhaPerformance.setForeground(UITheme.ACCENT_COLOR);

        // Tabela de vendas pessoais
        String[] colunasVendas = {"ID Venda", "Data", "Cliente", "Qtd Itens", "Total", "Status"};
        modeloTabelaVendas = new DefaultTableModel(colunasVendas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaMinhasVendas = new JTable(modeloTabelaVendas);
        tabelaMinhasVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaMinhasVendas.setFont(UITheme.FONT_BODY);
        tabelaMinhasVendas.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaMinhasVendas.setRowHeight(30);

        // Painel de gráficos pessoais
        painelGraficos = new JPanel();
        painelGraficos.setBackground(UITheme.CARD_BACKGROUND);
        painelGraficos.setBorder(UITheme.BORDER_CARD);
    }

    private void setupLayout() {
        // TopBar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        JLabel lblTitulo = UITheme.createHeadingLabel("📊 Minhas Vendas - " + vendedorLogado.getNome());
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));

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

        // Aba de vendas e estatísticas
        JPanel abaVendas = criarAbaMinhasVendas();
        tabbedPane.addTab("📋 Minhas Vendas", abaVendas);

        // Aba de estatísticas pessoais
        JPanel abaEstatisticas = criarAbaMinhasEstatisticas();
        tabbedPane.addTab("📈 Minhas Estatísticas", abaEstatisticas);

        // Aba de gráficos pessoais
        JPanel abaGraficos = criarAbaMeusGraficos();
        tabbedPane.addTab("📊 Meus Gráficos", abaGraficos);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel criarAbaMinhasVendas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_COLOR);

        // Painel de filtros
        JPanel filtrosPanel = criarPainelFiltros();
        panel.add(filtrosPanel, BorderLayout.NORTH);

        // Painel da tabela
        JPanel tabelaPanel = criarPainelTabelaVendas();
        panel.add(tabelaPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarAbaMinhasEstatisticas() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(UITheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cards de estatísticas pessoais
        panel.add(criarCardEstatisticaPessoal("📊 Total de Vendas", lblTotalMinhasVendas,
                "Número total de vendas realizadas por você"));
        panel.add(criarCardEstatisticaPessoal("💰 Meu Faturamento", lblMeuFaturamento,
                "Valor total faturado por você"));
        panel.add(criarCardEstatisticaPessoal("🎯 Meu Ticket Médio", lblMeuTicketMedio,
                "Valor médio das suas vendas"));
        panel.add(criarCardEstatisticaPessoal("🚀 Minha Performance", lblMinhaPerformance,
                "Sua performance em relação à meta"));
        panel.add(criarCardEstatisticaPessoal("🏆 Meu Ranking", lblMeuRanking,
                "Sua posição entre os vendedores"));
        panel.add(criarCardEstatisticaPessoal("📅 Período",
                new JLabel("Filtros ativos"), "Período dos dados exibidos"));

        return panel;
    }

    private JPanel criarAbaMeusGraficos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_COLOR);

        // Título
        JLabel lblTitulo = UITheme.createSubtitleLabel("📊 Meu Desempenho de Vendas");
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

    private JPanel criarPainelFiltros() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());

        // Título
        JLabel lblTitulo = UITheme.createSubtitleLabel("🔍 Filtrar Minhas Vendas");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Formulário de filtros
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formPanel.setBackground(UITheme.CARD_BACKGROUND);

        formPanel.add(UITheme.createBodyLabel("Data Início:"));
        formPanel.add(txtDataInicio);
        formPanel.add(UITheme.createBodyLabel("Data Fim:"));
        formPanel.add(txtDataFim);
        formPanel.add(btnFiltrar);
        formPanel.add(btnLimparFiltros);
        formPanel.add(btnExportarPDF);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarPainelTabelaVendas() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());

        // Título
        JLabel lblTitulo = UITheme.createSubtitleLabel("🛒 Minhas Vendas Realizadas");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tabelaMinhasVendas);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarCardEstatisticaPessoal(String titulo, JLabel valorLabel, String descricao) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout());

        // Título
        JLabel lblTitulo = UITheme.createSubtitleLabel(titulo);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(lblTitulo, BorderLayout.NORTH);

        // Valor
        valorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valorLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));

        card.add(valorLabel, BorderLayout.CENTER);

        // Descrição
        JLabel lblDescricao = UITheme.createBodyLabel(descricao);
        lblDescricao.setForeground(UITheme.TEXT_SECONDARY);
        lblDescricao.setHorizontalAlignment(SwingConstants.CENTER);
        lblDescricao.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(lblDescricao, BorderLayout.SOUTH);

        return card;
    }

    private void setupEvents() {
        btnFiltrar.addActionListener(e -> filtrarMinhasVendas());
        btnLimparFiltros.addActionListener(e -> limparFiltros());
        btnExportarPDF.addActionListener(e -> exportarMeuPDF());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
    }

    private void carregarMinhasVendas() {
        filtrarMinhasVendas();
    }

    private void filtrarMinhasVendas() {
        try {
            // Carregar todas as vendas
            List<VendaDTO> todasVendasDTO = controller.getVendasDTO();
            minhasVendasFiltradas = new ArrayList<>();

            // Filtrar apenas as vendas deste vendedor
            for (VendaDTO venda : todasVendasDTO) {
                if (vendedorLogado.getId().equals(venda.vendedorId)) {
                    minhasVendasFiltradas.add(venda);
                }
            }

            // Aplicar filtro de data se especificado
            Date dataInicio = parseDateOrNull(txtDataInicio.getText());
            Date dataFim = parseDateOrNull(txtDataFim.getText());

            if (dataInicio != null && dataFim != null) {
                long inicioMillis = dataInicio.getTime();
                long fimMillis = dataFim.getTime() + 86400000; // +1 dia para incluir o dia final

                List<VendaDTO> vendasFiltradasPorData = new ArrayList<>();
                for (VendaDTO venda : minhasVendasFiltradas) {
                    if (venda.dataMillis >= inicioMillis && venda.dataMillis <= fimMillis) {
                        vendasFiltradasPorData.add(venda);
                    }
                }
                minhasVendasFiltradas = vendasFiltradasPorData;
            }

            atualizarTabelaMinhasVendas();
            calcularMinhasEstatisticas();
            gerarMeusGraficos();

            JOptionPane.showMessageDialog(this,
                    "Encontradas " + minhasVendasFiltradas.size() + " venda(s) no período.",
                    "Minhas Vendas", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar vendas: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFiltros() {
        txtDataInicio.setText(dateFormat.format(new Date()));
        txtDataFim.setText(dateFormat.format(new Date()));
        filtrarMinhasVendas();
    }

    private Date parseDateOrNull(String text) {
        try {
            if (text != null && !text.trim().isEmpty()) {
                return dateFormat.parse(text.trim());
            }
        } catch (Exception e) {
            // Data inválida, retornar null
        }
        return null;
    }

    private void atualizarTabelaMinhasVendas() {
        modeloTabelaVendas.setRowCount(0);

        for (VendaDTO venda : minhasVendasFiltradas) {
            Date data = new Date(venda.dataMillis);
            String clienteNome = resolveClienteNome(venda.clienteId);
            int qtdItens = somaQuantidadeItens(venda.itens);
            BigDecimal total = venda.total != null ? venda.total : BigDecimal.ZERO;
            String status = "Concluída";

            modeloTabelaVendas.addRow(new Object[]{
                    venda.idVenda,
                    dateFormat.format(data),
                    clienteNome,
                    qtdItens,
                    String.format("%,.2f MT", total),
                    status
            });
        }
    }

    private String resolveClienteNome(String clienteId) {
        if (clienteId == null || clienteId.isBlank()) return "Cliente de Balcão";
        return controller.findClienteById(clienteId)
                .map(cliente -> cliente.getNome())
                .orElse(clienteId);
    }

    private int somaQuantidadeItens(List<persistence.dto.ItemVendaDTO> itens) {
        if (itens == null) return 0;
        int total = 0;
        for (persistence.dto.ItemVendaDTO item : itens) {
            total += item.quantidade;
        }
        return total;
    }

    private void calcularMinhasEstatisticas() {
        if (minhasVendasFiltradas == null || minhasVendasFiltradas.isEmpty()) {
            lblTotalMinhasVendas.setText("0");
            lblMeuFaturamento.setText("0,00 MT");
            lblMeuTicketMedio.setText("0,00 MT");
            lblMinhaPerformance.setText("0%");
            lblMeuRanking.setText("-");
            return;
        }

        // Total de vendas
        int totalVendas = minhasVendasFiltradas.size();
        lblTotalMinhasVendas.setText(String.valueOf(totalVendas));

        // Faturamento total
        BigDecimal faturamentoTotal = BigDecimal.ZERO;
        for (VendaDTO venda : minhasVendasFiltradas) {
            faturamentoTotal = faturamentoTotal.add(venda.total != null ? venda.total : BigDecimal.ZERO);
        }
        lblMeuFaturamento.setText(String.format("%,.2f MT", faturamentoTotal));

        // Ticket médio
        BigDecimal ticketMedio = totalVendas > 0 ?
                faturamentoTotal.divide(new BigDecimal(totalVendas), 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;
        lblMeuTicketMedio.setText(String.format("%,.2f MT", ticketMedio));

        // Performance (simulada - poderia ser baseada em metas)
        int performance = Math.min(100, (totalVendas * 10) + new Random().nextInt(30));
        lblMinhaPerformance.setText(performance + "%");

        // Ranking (simulado)
        Map<String, BigDecimal> faturamentoPorVendedor = controller.getFaturamentoPorVendedor();
        List<Map.Entry<String, BigDecimal>> ranking = new ArrayList<>(faturamentoPorVendedor.entrySet());
        ranking.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int minhaPosicao = -1;
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getKey().equals(vendedorLogado.getId())) {
                minhaPosicao = i + 1;
                break;
            }
        }

        if (minhaPosicao != -1) {
            String sufixo;
            switch (minhaPosicao) {
                case 1: sufixo = "🥇"; break;
                case 2: sufixo = "🥈"; break;
                case 3: sufixo = "🥉"; break;
                default: sufixo = "º";
            }
            lblMeuRanking.setText(minhaPosicao + sufixo + " de " + ranking.size());
        } else {
            lblMeuRanking.setText("-");
        }
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

    private void gerarMeusGraficos() {
        painelGraficos.removeAll();

        if (minhasVendasFiltradas == null || minhasVendasFiltradas.isEmpty()) {
            JLabel lblSemDados = UITheme.createBodyLabel("Nenhuma venda encontrada para gerar gráficos");
            lblSemDados.setHorizontalAlignment(SwingConstants.CENTER);
            painelGraficos.setLayout(new BorderLayout());
            painelGraficos.add(lblSemDados, BorderLayout.CENTER);
        } else {
            painelGraficos.setLayout(new GridLayout(2, 2, 8, 8));
            painelGraficos.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            // 1) Vendas por dia
            DefaultCategoryDataset dsVendasDia = buildDatasetVendasPorDia();
            JFreeChart chartVendasDia = ChartFactory.createBarChart(
                    "📈 Vendas por Dia", "Dia", "Quantidade", dsVendasDia
            );
            painelGraficos.add(smallChartPanel(chartVendasDia));

            // 2) Faturamento por dia
            DefaultCategoryDataset dsFaturamentoDia = buildDatasetFaturamentoPorDia();
            JFreeChart chartFaturamentoDia = ChartFactory.createLineChart(
                    "💰 Faturamento por Dia", "Dia", "Valor (MT)", dsFaturamentoDia
            );
            painelGraficos.add(smallChartPanel(chartFaturamentoDia));

            // 3) Distribuição por cliente
            DefaultPieDataset dsClientes = buildDatasetTopClientes();
            JFreeChart chartClientes = ChartFactory.createPieChart(
                    "👥 Top Clientes", dsClientes, true, true, false
            );
            painelGraficos.add(smallChartPanel(chartClientes));

            // 4) Comparação mensal
            DefaultCategoryDataset dsComparacao = buildDatasetComparacaoMensal();
            JFreeChart chartComparacao = ChartFactory.createBarChart(
                    "📅 Comparação Mensal", "Mês", "Faturamento (MT)", dsComparacao
            );
            painelGraficos.add(smallChartPanel(chartComparacao));
        }

        painelGraficos.revalidate();
        painelGraficos.repaint();
    }

    private DefaultCategoryDataset buildDatasetVendasPorDia() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

        Map<String, Integer> vendasPorDia = new TreeMap<>();
        for (VendaDTO venda : minhasVendasFiltradas) {
            String dia = sdf.format(new Date(venda.dataMillis));
            vendasPorDia.put(dia, vendasPorDia.getOrDefault(dia, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : vendasPorDia.entrySet()) {
            dataset.addValue(entry.getValue(), "Vendas", entry.getKey());
        }

        return dataset;
    }

    private DefaultCategoryDataset buildDatasetFaturamentoPorDia() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

        Map<String, BigDecimal> faturamentoPorDia = new TreeMap<>();
        for (VendaDTO venda : minhasVendasFiltradas) {
            String dia = sdf.format(new Date(venda.dataMillis));
            BigDecimal total = venda.total != null ? venda.total : BigDecimal.ZERO;
            faturamentoPorDia.put(dia, faturamentoPorDia.getOrDefault(dia, BigDecimal.ZERO).add(total));
        }

        for (Map.Entry<String, BigDecimal> entry : faturamentoPorDia.entrySet()) {
            dataset.addValue(entry.getValue().doubleValue(), "Faturamento", entry.getKey());
        }

        return dataset;
    }

    private DefaultPieDataset buildDatasetTopClientes() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, BigDecimal> faturamentoPorCliente = new HashMap<>();

        for (VendaDTO venda : minhasVendasFiltradas) {
            String clienteNome = resolveClienteNome(venda.clienteId);
            BigDecimal total = venda.total != null ? venda.total : BigDecimal.ZERO;
            faturamentoPorCliente.put(clienteNome,
                    faturamentoPorCliente.getOrDefault(clienteNome, BigDecimal.ZERO).add(total));
        }

        // Ordenar e pegar top 5
        List<Map.Entry<String, BigDecimal>> topClientes = new ArrayList<>(faturamentoPorCliente.entrySet());
        topClientes.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int count = 0;
        for (Map.Entry<String, BigDecimal> entry : topClientes) {
            if (count++ >= 5) break;
            dataset.setValue(entry.getKey(), entry.getValue().doubleValue());
        }

        return dataset;
    }

    private DefaultCategoryDataset buildDatasetComparacaoMensal() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

        Map<String, BigDecimal> faturamentoPorMes = new TreeMap<>();
        for (VendaDTO venda : minhasVendasFiltradas) {
            String mes = sdf.format(new Date(venda.dataMillis));
            BigDecimal total = venda.total != null ? venda.total : BigDecimal.ZERO;
            faturamentoPorMes.put(mes, faturamentoPorMes.getOrDefault(mes, BigDecimal.ZERO).add(total));
        }

        for (Map.Entry<String, BigDecimal> entry : faturamentoPorMes.entrySet()) {
            dataset.addValue(entry.getValue().doubleValue(), "Faturamento", entry.getKey());
        }

        return dataset;
    }

    private Image chartToITextImage(JFreeChart chart, int width, int height) throws Exception {
        BufferedImage bi = chart.createBufferedImage(width, height);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bi, "png", baos);
            baos.flush();
            return Image.getInstance(baos.toByteArray());
        }
    }

        private void exportarMeuPDF() {
            try {
                if (minhasVendasFiltradas == null || minhasVendasFiltradas.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nenhuma venda para exportar.", "Minhas Vendas", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Exportar Minhas Vendas em PDF");
                chooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));

                String nomeSugerido = "minhas_vendas_" + vendedorLogado.getNome().replace(" ", "_") +
                        "_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf";
                chooser.setSelectedFile(new java.io.File(nomeSugerido));
                int res = chooser.showSaveDialog(this);
                if (res != JFileChooser.APPROVE_OPTION) return;

                java.io.File arq = chooser.getSelectedFile();
                if (!arq.getName().toLowerCase().endsWith(".pdf")) {
                    arq = new java.io.File(arq.getParentFile(), arq.getName() + ".pdf");
                }

                // Criar documento iText
                Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
                PdfWriter.getInstance(doc, new FileOutputStream(arq));
                doc.open();

                // Título e informações do vendedor
                Font fTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Font fSub = FontFactory.getFont(FontFactory.HELVETICA, 12);

                doc.add(new Paragraph("RELATÓRIO DE MINHAS VENDAS", fTitle));
                doc.add(new Paragraph("Vendedor: " + vendedorLogado.getNome(), fSub));
                doc.add(new Paragraph("Período: " + txtDataInicio.getText() + " a " + txtDataFim.getText(), fSub));
                doc.add(new Paragraph("Gerado em: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fSub));
                doc.add(new Paragraph(" \n"));

                // Estatísticas pessoais
                PdfPTable tblStats = new PdfPTable(5);
                tblStats.setWidthPercentage(100);
                tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Total de Vendas\n" + lblTotalMinhasVendas.getText())));
                tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Meu Faturamento\n" + lblMeuFaturamento.getText())));
                tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Meu Ticket Médio\n" + lblMeuTicketMedio.getText())));
                tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Minha Performance\n" + lblMinhaPerformance.getText())));
                tblStats.addCell(new PdfPCell(new com.itextpdf.text.Phrase("Meu Ranking\n" + lblMeuRanking.getText())));
                doc.add(tblStats);
                doc.add(new Paragraph(" \n"));

                // Tabela de vendas
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

                // Gráficos
                try {
                    DefaultCategoryDataset dsVendasDia = buildDatasetVendasPorDia();
                    DefaultCategoryDataset dsFaturamentoDia = buildDatasetFaturamentoPorDia();
                    DefaultPieDataset dsClientes = buildDatasetTopClientes();
                    DefaultCategoryDataset dsComparacao = buildDatasetComparacaoMensal();

                    JFreeChart chart1 = ChartFactory.createBarChart("Vendas por Dia", "Dia", "Quantidade", dsVendasDia);
                    JFreeChart chart2 = ChartFactory.createLineChart("Faturamento por Dia", "Dia", "Valor (MT)", dsFaturamentoDia);
                    JFreeChart chart3 = ChartFactory.createPieChart("Top Clientes", dsClientes, true, true, false);
                    JFreeChart chart4 = ChartFactory.createBarChart("Comparação Mensal", "Mês", "Faturamento (MT)", dsComparacao);

                    Image img1 = chartToITextImage(chart1, 900, 400);
                    Image img2 = chartToITextImage(chart2, 900, 400);
                    Image img3 = chartToITextImage(chart3, 900, 400);
                    Image img4 = chartToITextImage(chart4, 900, 400);

                    doc.add(new Paragraph("Gráficos do Meu Desempenho:\n", fSub));
                    doc.add(img1);
                    doc.add(img2);
                    doc.add(img3);
                    doc.add(img4);
                } catch (Exception ig) {
                    doc.add(new Paragraph("(Gráficos não puderam ser incluídos)", fSub));
                }

                doc.close();
                JOptionPane.showMessageDialog(this,
                        "PDF exportado com sucesso!\nArquivo: " + arq.getAbsolutePath(),
                        "Exportar PDF", JOptionPane.INFORMATION_MESSAGE);

            } catch (DocumentException de) {
                de.printStackTrace();
                JOptionPane.showMessageDialog(this, "Falha ao gerar PDF: " + de.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }

    private void voltarMenuPrincipal() {
        controller.getCardLayoutManager().showPanel("MenuVendedor");
    }
}
package view;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import controller.SistemaController;
import model.concretas.Cliente;
import model.concretas.Reserva;
import model.concretas.Vendedor;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import util.UITheme;
import persistence.dto.VendaDTO;
import persistence.dto.ItemVendaDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Tela para visualização de relatórios de vendas e reservas com interface melhorada.
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
    private JLabel lblTotalReservas;
    private JLabel lblReservasAtivas;
    private JLabel lblReservasCanceladas;

    // Tabela de vendas
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabelaVendas;

    // Painel de gráficos
    private JPanel painelGraficos;

    // Painel para múltiplas tabelas
    private JPanel painelTabelas;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private List<VendaDTO> vendasFiltradas;
    private List<Reserva> reservasFiltradas;

    // Tipos de relatório
    private final String[] TIPOS_RELATORIO = {
            "Todas as Vendas",
            "Por Período",
            "Por Vendedor",
            "Relatório Geral de Vendas",
            "Reservas Ativas",
            "Reservas Canceladas",
            "Reservas Convertidas",
            "Relatório Geral de Reservas",
            "Relatório Geral Completo"
    };

    public RelatoriosVendasView(SistemaController controller) {
        this.controller = controller;
        this.vendasFiltradas = new ArrayList<>();
        this.reservasFiltradas = new ArrayList<>();
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
        cmbTipoRelatorio = UITheme.createStyledComboBox(TIPOS_RELATORIO);

        txtDataInicio = UITheme.createStyledTextField();
        txtDataInicio.setText(dateFormat.format(new Date()));

        txtDataFim = UITheme.createStyledTextField();
        txtDataFim.setText(dateFormat.format(new Date()));

        cmbVendedor = UITheme.createStyledComboBox(new Vendedor[0]);

        cmbCliente = UITheme.createStyledComboBox(new Cliente[0]);

        // Botões com tema personalizado
        btnGerarRelatorio = UITheme.createPrimaryButton("Gerar");
        btnExportarPDF = UITheme.createSecondaryButton("Exportar");
        btnLimparFiltros = UITheme.createSecondaryButton("Limpar Filtros");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnVoltar.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 18));

        // Labels de estatísticas
        lblTotalVendas = UITheme.createHeadingLabel("0");
        lblTotalFaturamento = UITheme.createHeadingLabel("0,00 MT");
        lblTicketMedio = UITheme.createHeadingLabel("0,00 MT");
        lblMelhorVendedor = UITheme.createBodyLabel("N/A");
        lblMelhorCliente = UITheme.createBodyLabel("N/A");
        lblTotalReservas = UITheme.createHeadingLabel("0");
        lblReservasAtivas = UITheme.createBodyLabel("0");
        lblReservasCanceladas = UITheme.createBodyLabel("0");

        // Configurar cores das estatísticas
        lblTotalVendas.setForeground(UITheme.PRIMARY_COLOR);
        lblTotalFaturamento.setForeground(UITheme.SUCCESS_COLOR);
        lblTicketMedio.setForeground(UITheme.INFO_COLOR);
        lblTotalReservas.setForeground(UITheme.WARNING_COLOR);

        // Tabela de vendas (para uso único)
        String[] colunasVendas = {"ID", "Data", "Vendedor", "Cliente", "Qtd Itens", "Total", "Tipo"};
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

        // Painel para múltiplas tabelas
        painelTabelas = new JPanel();
        painelTabelas.setLayout(new BoxLayout(painelTabelas, BoxLayout.Y_AXIS));
        painelTabelas.setBackground(UITheme.CARD_BACKGROUND);
        painelTabelas.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Painel de gráficos
        painelGraficos = new JPanel();
        painelGraficos.setBackground(UITheme.CARD_BACKGROUND);
        painelGraficos.setBorder(UITheme.BORDER_CARD);
    }

    /**
     * Configura o layout da interface.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel superior com título
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        JLabel lblTitulo = UITheme.createHeadingLabel("📊 Relatórios de Vendas e Reservas");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);

        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar);
        topBar.add(voltarPanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // Painel principal com abas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 14));

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
        JPanel panel = new JPanel(new GridLayout(3, 3, 15, 15));
        panel.setBackground(UITheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cards de estatísticas
        panel.add(criarCardEstatistica("📊 Total de Vendas", lblTotalVendas, "Número total de vendas realizadas"));
        panel.add(criarCardEstatistica("💰 Faturamento Total", lblTotalFaturamento, "Valor total faturado"));
        panel.add(criarCardEstatistica("🎯 Ticket Médio", lblTicketMedio, "Valor médio por venda"));
        panel.add(criarCardEstatistica("🏆 Melhor Vendedor", lblMelhorVendedor, "Vendedor com maior faturamento"));
        panel.add(criarCardEstatistica("👑 Melhor Cliente", lblMelhorCliente, "Cliente com maior volume de compras"));
        panel.add(criarCardEstatistica("📋 Total Reservas", lblTotalReservas, "Número total de reservas"));
        panel.add(criarCardEstatistica("✅ Reservas Ativas", lblReservasAtivas, "Reservas ativas no momento"));
        panel.add(criarCardEstatistica("❌ Reservas Canceladas", lblReservasCanceladas, "Reservas canceladas"));
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
        JLabel lblTitulo = UITheme.createSubtitleLabel("Análise Gráfica das Vendas e Reservas");
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
        lblTitulo.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 16));
        card.add(lblTitulo, BorderLayout.NORTH);

        // Valor
        valorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valorLabel.setFont(UITheme.FONT_HEADING);
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
     * Cria o painel de filtros com layout GridBagLayout.
     */
    private JPanel criarPainelFiltros() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());

        // Título
        JLabel lblTitulo = UITheme.createSubtitleLabel("Filtros de Relatório");
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Formulário de filtros - usando GridBagLayout para melhor organização
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

        // Data Início
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(UITheme.createBodyLabel("Data Início:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtDataInicio, gbc);

        // Data Fim
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
        JLabel lblTitulo = UITheme.createSubtitleLabel("Dados Encontrados");
        panel.add(lblTitulo, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(painelTabelas);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        scrollPane.setPreferredSize(new Dimension(0, 400)); // Altura limitada para múltiplas tabelas
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

        installAltForVoltar();
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
        cmbVendedor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Vendedor v ? v.getNome() : (value == null ? "Todos" : String.valueOf(value)));
                return this;
            }
        });

        // Carregar clientes
        List<Cliente> clientes = controller.getClientes();
        cmbCliente.removeAllItems();
        cmbCliente.addItem(null); // Opção "Todos"
        for (Cliente cliente : clientes) {
            cmbCliente.addItem(cliente);
        }
        cmbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Cliente c ? c.getNome() : (value == null ? "Todos" : String.valueOf(value)));
                return this;
            }
        });

        atualizarCamposFiltro();
    }

    /**
     * Atualiza a visibilidade dos campos de filtro baseado no tipo selecionado.
     */
    private void atualizarCamposFiltro() {
        String tipoSelecionado = (String) cmbTipoRelatorio.getSelectedItem();

        boolean usarPeriodo = tipoSelecionado.contains("Período") ||
                tipoSelecionado.equals("Todas as Vendas") ||
                tipoSelecionado.equals("Relatório Geral de Vendas") ||
                tipoSelecionado.equals("Relatório Geral de Reservas") ||
                tipoSelecionado.equals("Relatório Geral Completo");
        boolean usarVendedor = tipoSelecionado.contains("Vendedor") ||
                tipoSelecionado.equals("Todas as Vendas") ||
                tipoSelecionado.equals("Relatório Geral de Vendas") ||
                tipoSelecionado.equals("Relatório Geral Completo");
        boolean usarCliente = tipoSelecionado.contains("Cliente") ||
                tipoSelecionado.equals("Todas as Vendas") ||
                tipoSelecionado.equals("Relatório Geral de Vendas") ||
                tipoSelecionado.equals("Relatório Geral Completo");

        txtDataInicio.setEnabled(usarPeriodo);
        txtDataFim.setEnabled(usarPeriodo);
        cmbVendedor.setEnabled(usarVendedor);
        cmbCliente.setEnabled(usarCliente);
    }

    /**
     * Gera o relatório baseado nos filtros selecionados.
     */
    private void gerarRelatorio() {
        try {
            String tipoRelatorio = (String) cmbTipoRelatorio.getSelectedItem();

            // Limpar dados anteriores
            vendasFiltradas.clear();
            reservasFiltradas.clear();

            switch (tipoRelatorio) {
                case "Todas as Vendas":
                case "Por Período":
                case "Por Vendedor":
                case "Por Cliente":
                case "Relatório Geral de Vendas":
                    processarRelatorioVendas(tipoRelatorio);
                    break;

                case "Reservas Ativas":
                case "Reservas Canceladas":
                case "Reservas Convertidas":
                case "Relatório Geral de Reservas":
                    processarRelatorioReservas(tipoRelatorio);
                    break;

                case "Relatório Geral Completo":
                    processarRelatorioCompleto();
                    break;

                default:
                    JOptionPane.showMessageDialog(this, "Tipo de relatório não implementado: " + tipoRelatorio,
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            atualizarTabela();
            calcularEstatisticas();
            gerarGraficos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Processa relatórios de vendas
     */
    private void processarRelatorioVendas(String tipoRelatorio) {
        try {
            List<VendaDTO> todasVendas = controller.getVendasDTO();
            vendasFiltradas = new ArrayList<>(todasVendas);

            switch (tipoRelatorio) {
                case "Por Período":
                    Date ini = parseDateOrNull(txtDataInicio.getText());
                    Date fim = parseDateOrNull(txtDataFim.getText());
                    if (ini == null || fim == null) {
                        JOptionPane.showMessageDialog(this, "Datas inválidas. Use dd/MM/yyyy.",
                                "Período inválido", JOptionPane.WARNING_MESSAGE);
                        vendasFiltradas.clear();
                        return;
                    }
                    filtrarPorPeriodo(ini, fim);
                    break;

                case "Por Vendedor":
                    Vendedor vendedorSelecionado = (Vendedor) cmbVendedor.getSelectedItem();
                    if (vendedorSelecionado != null) {
                        String vendedorId = vendedorSelecionado.getId();
                        vendasFiltradas.removeIf(v -> !vendedorId.equals(v.vendedorId));
                    }
                    break;

                case "Por Cliente":
                    Cliente clienteSelecionado = (Cliente) cmbCliente.getSelectedItem();
                    if (clienteSelecionado != null) {
                        String clienteId = clienteSelecionado.getId();
                        vendasFiltradas.removeIf(v -> !clienteId.equals(v.clienteId));
                    }
                    break;

                case "Relatório Geral de Vendas":
                    // Inclui todas as vendas, pode aplicar filtros adicionais
                    Date inicioGeral = parseDateOrNull(txtDataInicio.getText());
                    Date fimGeral = parseDateOrNull(txtDataFim.getText());
                    if (inicioGeral != null && fimGeral != null) {
                        filtrarPorPeriodo(inicioGeral, fimGeral);
                    }
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar relatório de vendas: " + e.getMessage(), e);
        }
    }

    /**
     * Processa relatórios de reservas
     */
    private void processarRelatorioReservas(String tipoRelatorio) {
        try {
            List<Reserva> todasReservas = controller.getReservas();
            reservasFiltradas = new ArrayList<>(todasReservas);

            switch (tipoRelatorio) {
                case "Reservas Ativas":
                    reservasFiltradas.removeIf(r -> r.getStatus() != Reserva.StatusReserva.ATIVA);
                    break;

                case "Reservas Canceladas":
                    reservasFiltradas.removeIf(r -> r.getStatus() != Reserva.StatusReserva.CANCELADA);
                    break;

                case "Reservas Convertidas":
                    reservasFiltradas.removeIf(r -> r.getStatus() != Reserva.StatusReserva.CONVERTIDA);
                    break;

                case "Relatório Geral de Reservas":
                    // Inclui todas as reservas, pode aplicar filtros adicionais
                    Date inicioReservas = parseDateOrNull(txtDataInicio.getText());
                    Date fimReservas = parseDateOrNull(txtDataFim.getText());
                    if (inicioReservas != null && fimReservas != null) {
                        reservasFiltradas.removeIf(r ->
                                r.getDataReserva() == null ||
                                        r.getDataReserva().before(inicioReservas) ||
                                        r.getDataReserva().after(fimReservas));
                    }
                    break;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar relatório de reservas: " + e.getMessage(), e);
        }
    }

    /**
     * Filtra vendas por período
     */
    private void filtrarPorPeriodo(Date inicio, Date fim) {
        Calendar calInicio = Calendar.getInstance();
        calInicio.setTime(inicio);
        calInicio.set(Calendar.HOUR_OF_DAY, 0);
        calInicio.set(Calendar.MINUTE, 0);
        calInicio.set(Calendar.SECOND, 0);
        calInicio.set(Calendar.MILLISECOND, 0);

        Calendar calFim = Calendar.getInstance();
        calFim.setTime(fim);
        calFim.set(Calendar.HOUR_OF_DAY, 23);
        calFim.set(Calendar.MINUTE, 59);
        calFim.set(Calendar.SECOND, 59);
        calFim.set(Calendar.MILLISECOND, 999);

        long inicioMillis = calInicio.getTimeInMillis();
        long fimMillis = calFim.getTimeInMillis();

        if (inicioMillis > fimMillis) {
            long temp = inicioMillis;
            inicioMillis = fimMillis;
            fimMillis = temp;
        }

        final long inicioFinal = inicioMillis;
        final long fimFinal = fimMillis;

        List<VendaDTO> vendasTemp = new ArrayList<>();
        for (VendaDTO venda : vendasFiltradas) {
            if (venda.dataMillis >= inicioFinal && venda.dataMillis <= fimFinal) {
                vendasTemp.add(venda);
            }
        }
        vendasFiltradas = vendasTemp;
    }

    /**
     * Atualiza a tabela com dados de vendas e reservas
     */
    private void atualizarTabela() {
        String tipoRelatorio = (String) cmbTipoRelatorio.getSelectedItem();

        if (tipoRelatorio.equals("Relatório Geral Completo")) {
            atualizarTabelasMultiplas();
        } else {
            atualizarTabelaUnica();
        }
    }

    private void atualizarTabelaUnica() {
        // Limpar painelTabelas
        painelTabelas.removeAll();

        modeloTabelaVendas.setRowCount(0);

        // Adicionar vendas
        for (VendaDTO dto : vendasFiltradas) {
            Date data = new Date(dto.dataMillis);
            String vendedorNome = resolveVendedorNome(dto.vendedorId);
            String clienteNome = resolveClienteNome(dto.clienteId);
            int qtdItens = somaQuantidadeItens(dto.itens);
            BigDecimal total = calcularTotalVenda(dto);

            modeloTabelaVendas.addRow(new Object[]{
                    dto.idVenda,
                    dateFormat.format(data),
                    vendedorNome,
                    clienteNome,
                    qtdItens,
                    formatCurrency(total) + " MT",
                    "Venda"
            });
        }

        // Adicionar reservas
        for (Reserva reserva : reservasFiltradas) {
            String clienteNome = reserva.getCliente() != null ? reserva.getCliente().getNome() : "N/A";
            String vendedorNome = reserva.getVendedor() != null ? reserva.getVendedor().getNome() : "N/A";
            int qtdItens = reserva.getItens() != null ? reserva.getItens().size() : 0;
            double valorTotal = reserva.getValorTotal();

            modeloTabelaVendas.addRow(new Object[]{
                    reserva.getIdReserva(),
                    reserva.getDataReserva() != null ? dateFormat.format(reserva.getDataReserva()) : "N/A",
                    vendedorNome,
                    clienteNome,
                    qtdItens,
                    String.format("%.2f MT", valorTotal),
                    "Reserva (" + reserva.getStatus() + ")"
            });
        }

        modeloTabelaVendas.fireTableDataChanged();
        tabelaVendas.revalidate();
        tabelaVendas.repaint();

        // Adicionar a tabela única ao painel
        JScrollPane scrollUnica = new JScrollPane(tabelaVendas);
        scrollUnica.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scrollUnica.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        painelTabelas.add(scrollUnica);
    }

    private void atualizarTabelasMultiplas() {
        // Limpar painel anterior
        painelTabelas.removeAll();

        // Preparar filtros globais (reutilizar lógica do PDF)
        Date iniPeriodo = parseDateOrNull(txtDataInicio.getText());
        Date fimPeriodo = parseDateOrNull(txtDataFim.getText());
        long inicioMillis = 0;
        long fimMillis = Long.MAX_VALUE;
        if (iniPeriodo != null && fimPeriodo != null) {
            Calendar calInicio = Calendar.getInstance();
            calInicio.setTime(iniPeriodo);
            calInicio.set(Calendar.HOUR_OF_DAY, 0);
            calInicio.set(Calendar.MINUTE, 0);
            calInicio.set(Calendar.SECOND, 0);
            calInicio.set(Calendar.MILLISECOND, 0);
            inicioMillis = calInicio.getTimeInMillis();

            Calendar calFim = Calendar.getInstance();
            calFim.setTime(fimPeriodo);
            calFim.set(Calendar.HOUR_OF_DAY, 23);
            calFim.set(Calendar.MINUTE, 59);
            calFim.set(Calendar.SECOND, 59);
            calFim.set(Calendar.MILLISECOND, 999);
            fimMillis = calFim.getTimeInMillis();

            if (inicioMillis > fimMillis) {
                long temp = inicioMillis;
                inicioMillis = fimMillis;
                fimMillis = temp;
            }
        }

        Vendedor vendedorSel = (Vendedor) cmbVendedor.getSelectedItem();
        String vendedorId = (vendedorSel != null) ? vendedorSel.getId() : null;
        Cliente clienteSel = (Cliente) cmbCliente.getSelectedItem();
        String clienteId = (clienteSel != null) ? clienteSel.getId() : null;

        // Carregar dados completos
        List<VendaDTO> allVendas = null;
        try {
            allVendas = controller.getVendasDTO();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Reserva> allReservas = controller.getReservas();

        // Colunas
        String[] colunas = {"ID", "Data", "Vendedor", "Cliente", "Qtd Itens", "Total", "Tipo"};

        for (String relTipo : TIPOS_RELATORIO) {
            List<VendaDTO> vendasParaTipo = new ArrayList<>();
            List<Reserva> reservasParaTipo = new ArrayList<>();

            if (relTipo.equals("Todas as Vendas") || relTipo.equals("Relatório Geral de Vendas")) {
                vendasParaTipo = filtrarVendasBase(allVendas, inicioMillis, fimMillis, vendedorId, clienteId);
            } else if (relTipo.equals("Por Período")) {
                List<VendaDTO> temp = new ArrayList<>(allVendas);
                if (iniPeriodo != null && fimPeriodo != null) {
                    final long startMillis = inicioMillis;
                    final long endMillis = fimMillis;
                    temp.removeIf(v -> v.dataMillis < startMillis || v.dataMillis > endMillis);
                }
                vendasParaTipo = filtrarVendasBase(temp, inicioMillis, fimMillis, vendedorId, clienteId);
            } else if (relTipo.equals("Por Vendedor")) {
                List<VendaDTO> temp = new ArrayList<>(allVendas);
                if (vendedorId != null) {
                    final String vid = vendedorId;
                    temp.removeIf(v -> !vid.equals(v.vendedorId));
                }
                vendasParaTipo = filtrarVendasBase(temp, inicioMillis, fimMillis, vendedorId, clienteId);
            } else if (relTipo.equals("Por Cliente")) {
                List<VendaDTO> temp = new ArrayList<>(allVendas);
                if (clienteId != null) {
                    final String cid = clienteId;
                    temp.removeIf(v -> !cid.equals(v.clienteId));
                }
                vendasParaTipo = filtrarVendasBase(temp, inicioMillis, fimMillis, vendedorId, clienteId);
            } else if (relTipo.equals("Reservas Ativas")) {
                reservasParaTipo = filtrarReservasBase(allReservas, inicioMillis, fimMillis, vendedorId, clienteId);
                reservasParaTipo.removeIf(r -> r.getStatus() != Reserva.StatusReserva.ATIVA);
            } else if (relTipo.equals("Reservas Canceladas")) {
                reservasParaTipo = filtrarReservasBase(allReservas, inicioMillis, fimMillis, vendedorId, clienteId);
                reservasParaTipo.removeIf(r -> r.getStatus() != Reserva.StatusReserva.CANCELADA);
            } else if (relTipo.equals("Reservas Convertidas")) {
                reservasParaTipo = filtrarReservasBase(allReservas, inicioMillis, fimMillis, vendedorId, clienteId);
                reservasParaTipo.removeIf(r -> r.getStatus() != Reserva.StatusReserva.CONVERTIDA);
            } else if (relTipo.equals("Relatório Geral de Reservas")) {
                reservasParaTipo = filtrarReservasBase(allReservas, inicioMillis, fimMillis, vendedorId, clienteId);
            }

            int totalRegistros = vendasParaTipo.size() + reservasParaTipo.size();

            // Criar subpainel para este tipo
            JPanel subPanel = new JPanel(new BorderLayout());
            subPanel.setBackground(UITheme.CARD_BACKGROUND);
            subPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

            // Título
            JLabel lblSubTitulo = UITheme.createSubtitleLabel(relTipo + " (" + totalRegistros + " registros)");
            subPanel.add(lblSubTitulo, BorderLayout.NORTH);

            // Criar modelo e tabela para este tipo
            DefaultTableModel modeloSub = new DefaultTableModel(colunas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JTable tabelaSub = new JTable(modeloSub);
            tabelaSub.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tabelaSub.setFont(UITheme.FONT_BODY);
            tabelaSub.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
            tabelaSub.setRowHeight(30);
            tabelaSub.setSelectionBackground(UITheme.PRIMARY_LIGHT);
            tabelaSub.setSelectionForeground(UITheme.TEXT_PRIMARY);
            tabelaSub.setPreferredScrollableViewportSize(new Dimension(500, 70 * Math.min(10, totalRegistros))); // Altura dinâmica limitada

            // Adicionar linhas de vendas
            for (VendaDTO dto : vendasParaTipo) {
                Date data = new Date(dto.dataMillis);
                String vendedorNome = resolveVendedorNome(dto.vendedorId);
                String clienteNome = resolveClienteNome(dto.clienteId);
                int qtdItens = somaQuantidadeItens(dto.itens);
                BigDecimal total = calcularTotalVenda(dto);

                modeloSub.addRow(new Object[]{
                        dto.idVenda,
                        dateFormat.format(data),
                        vendedorNome,
                        clienteNome,
                        qtdItens,
                        formatCurrency(total) + " MT",
                        "Venda"
                });
            }

            // Adicionar linhas de reservas
            for (Reserva reserva : reservasParaTipo) {
                String clienteNome = reserva.getCliente() != null ? reserva.getCliente().getNome() : "N/A";
                String vendedorNome = reserva.getVendedor() != null ? reserva.getVendedor().getNome() : "N/A";
                int qtdItens = reserva.getItens() != null ? reserva.getItens().size() : 0;
                double valorTotal = reserva.getValorTotal();
                String dataStr = reserva.getDataReserva() != null ? dateFormat.format(reserva.getDataReserva()) : "N/A";
                String tipoStr = "Reserva (" + reserva.getStatus() + ")";

                modeloSub.addRow(new Object[]{
                        reserva.getIdReserva(),
                        dataStr,
                        vendedorNome,
                        clienteNome,
                        qtdItens,
                        String.format("%.2f MT", valorTotal),
                        tipoStr
                });
            }

            if (totalRegistros == 0) {
                modeloSub.addRow(new Object[]{"Nenhum registro encontrado", "", "", "", "", "", ""});
            }

            modeloSub.fireTableDataChanged();

            // Adicionar scrollpane da sub-tabela ao subpanel
            JScrollPane scrollSub = new JScrollPane(tabelaSub);
            scrollSub.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
            subPanel.add(scrollSub, BorderLayout.CENTER);

            // Adicionar subpanel ao painel principal
            painelTabelas.add(subPanel);
        }

        painelTabelas.revalidate();
        painelTabelas.repaint();
    }

    /**
     * Calcula estatísticas completas
     */
    private void calcularEstatisticas() {
        // Estatísticas de vendas
        int totalVendas = vendasFiltradas.size();
        lblTotalVendas.setText(String.valueOf(totalVendas));

        BigDecimal faturamentoTotal = BigDecimal.ZERO;
        for (VendaDTO v : vendasFiltradas) {
            faturamentoTotal = faturamentoTotal.add(calcularTotalVenda(v));
        }
        lblTotalFaturamento.setText(formatCurrency(faturamentoTotal) + " MT");

        BigDecimal ticketMedio = totalVendas > 0
                ? faturamentoTotal.divide(new BigDecimal(totalVendas), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        lblTicketMedio.setText(formatCurrency(ticketMedio) + " MT");

        calcularMelhorVendedorECliente();

        // Estatísticas de reservas
        int totalReservas = reservasFiltradas.size();
        lblTotalReservas.setText(String.valueOf(totalReservas));

        long reservasAtivas = reservasFiltradas.stream()
                .filter(r -> r.getStatus() == Reserva.StatusReserva.ATIVA)
                .count();
        lblReservasAtivas.setText(String.valueOf(reservasAtivas));

        long reservasCanceladas = reservasFiltradas.stream()
                .filter(r -> r.getStatus() == Reserva.StatusReserva.CANCELADA)
                .count();
        lblReservasCanceladas.setText(String.valueOf(reservasCanceladas));
    }

    /**
     * Calcula melhor vendedor e cliente
     */
    private void calcularMelhorVendedorECliente() {
        Map<String, BigDecimal> vendedorFaturamento = new HashMap<>();
        Map<String, BigDecimal> clienteFaturamento = new HashMap<>();

        for (VendaDTO v : vendasFiltradas) {
            String vendedorNome = resolveVendedorNome(v.vendedorId);
            String clienteNome = resolveClienteNome(v.clienteId);
            BigDecimal total = calcularTotalVenda(v);

            vendedorFaturamento.merge(vendedorNome, total, BigDecimal::add);
            clienteFaturamento.merge(clienteNome, total, BigDecimal::add);
        }

        String melhorVendedor = vendedorFaturamento.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        lblMelhorVendedor.setText(melhorVendedor);

        String melhorCliente = clienteFaturamento.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        lblMelhorCliente.setText(melhorCliente);
    }

    /**
     * Processa o relatório geral completo com vendas e reservas
     */
    private void processarRelatorioCompleto() {
        try {
            System.out.println("Processando relatório completo...");

            // Carregar todas as vendas
            List<VendaDTO> todasVendas = controller.getVendasDTO();
            vendasFiltradas = new ArrayList<>(todasVendas);

            // Carregar todas as reservas
            List<Reserva> todasReservas = controller.getReservas();
            reservasFiltradas = new ArrayList<>(todasReservas);

            System.out.println("Vendas carregadas: " + todasVendas.size());
            System.out.println("Reservas carregadas: " + todasReservas.size());

            // Aplicar filtros de período se especificados
            Date inicio = parseDateOrNull(txtDataInicio.getText());
            Date fim = parseDateOrNull(txtDataFim.getText());

            if (inicio != null && fim != null) {
                System.out.println("Filtrando por período: " + inicio + " até " + fim);

                // Filtrar vendas por período
                filtrarPorPeriodo(inicio, fim);

                // Filtrar reservas por período
                List<Reserva> reservasTemp = new ArrayList<>();
                for (Reserva reserva : reservasFiltradas) {
                    if (reserva.getDataReserva() != null &&
                            !reserva.getDataReserva().before(inicio) &&
                            !reserva.getDataReserva().after(fim)) {
                        reservasTemp.add(reserva);
                    }
                }
                reservasFiltradas = reservasTemp;
            }

            // Aplicar filtro de vendedor se especificado
            Vendedor vendedorSelecionado = (Vendedor) cmbVendedor.getSelectedItem();
            if (vendedorSelecionado != null) {
                String vendedorId = vendedorSelecionado.getId();
                System.out.println("Filtrando por vendedor: " + vendedorId);

                // Filtrar vendas
                List<VendaDTO> vendasTemp = new ArrayList<>();
                for (VendaDTO venda : vendasFiltradas) {
                    if (vendedorId.equals(venda.vendedorId)) {
                        vendasTemp.add(venda);
                    }
                }
                vendasFiltradas = vendasTemp;

                // Filtrar reservas
                List<Reserva> reservasTemp = new ArrayList<>();
                for (Reserva reserva : reservasFiltradas) {
                    if (reserva.getVendedor() != null && vendedorId.equals(reserva.getVendedor().getId())) {
                        reservasTemp.add(reserva);
                    }
                }
                reservasFiltradas = reservasTemp;
            }

            // Aplicar filtro de cliente se especificado
            Cliente clienteSelecionado = (Cliente) cmbCliente.getSelectedItem();
            if (clienteSelecionado != null) {
                String clienteId = clienteSelecionado.getId();
                System.out.println("Filtrando por cliente: " + clienteId);

                // Filtrar vendas
                List<VendaDTO> vendasTemp = new ArrayList<>();
                for (VendaDTO venda : vendasFiltradas) {
                    if (clienteId.equals(venda.clienteId)) {
                        vendasTemp.add(venda);
                    }
                }
                vendasFiltradas = vendasTemp;

                // Filtrar reservas
                List<Reserva> reservasTemp = new ArrayList<>();
                for (Reserva reserva : reservasFiltradas) {
                    if (reserva.getCliente() != null && clienteId.equals(reserva.getCliente().getId())) {
                        reservasTemp.add(reserva);
                    }
                }
                reservasFiltradas = reservasTemp;
            }

            System.out.println("Vendas filtradas: " + vendasFiltradas.size());
            System.out.println("Reservas filtradas: " + reservasFiltradas.size());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar relatório completo: " + e.getMessage(), e);
        }
    }

    /**
     * Gera gráficos para vendas e reservas
     */
    private void gerarGraficos() {
        painelGraficos.removeAll();

        if (vendasFiltradas.isEmpty() && reservasFiltradas.isEmpty()) {
            JLabel lblSemDados = UITheme.createBodyLabel("Nenhum dado encontrado para gerar gráficos");
            lblSemDados.setHorizontalAlignment(SwingConstants.CENTER);
            painelGraficos.setLayout(new BorderLayout());
            painelGraficos.add(lblSemDados, BorderLayout.CENTER);
        } else {
            // Grid 2x2 com espaçamento
            painelGraficos.setLayout(new GridLayout(2, 2, 8, 8));
            painelGraficos.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            String tipoRelatorio = (String) cmbTipoRelatorio.getSelectedItem();

            if (tipoRelatorio.equals("Relatório Geral Completo")) {
                // Gráficos para relatório completo - misto de vendas e reservas
                DefaultCategoryDataset dsDia = buildDatasetTotalPorDia();
                JFreeChart chartDia = buildChartBar("📊 Total por Dia", "Dia", "Total (MT)", dsDia);
                tuneChartFonts(chartDia);
                painelGraficos.add(smallChartPanel(chartDia));

                DefaultCategoryDataset dsVend = buildDatasetFaturamentoPorVendedor();
                JFreeChart chartVend = buildChartBar("💰 Faturamento por Vendedor", "Vendedor", "Total (MT)", dsVend);
                tuneChartFonts(chartVend);
                painelGraficos.add(smallChartPanel(chartVend));

                DefaultPieDataset dsStatus = buildDatasetStatusReservas();
                JFreeChart chartStatus = buildChartPie("📋 Status das Reservas", dsStatus);
                tuneChartFonts(chartStatus);
                painelGraficos.add(smallChartPanel(chartStatus));

                DefaultCategoryDataset dsComparativo = buildDatasetComparativoVendasReservas();
                JFreeChart chartComparativo = buildChartBar("📈 Comparativo Vendas vs Reservas", "Data", "Quantidade", dsComparativo);
                tuneChartFonts(chartComparativo);
                painelGraficos.add(smallChartPanel(chartComparativo));
            } else if (tipoRelatorio.contains("Reserva")) {
                // Gráficos para reservas
                DefaultPieDataset dsStatus = buildDatasetStatusReservas();
                JFreeChart chartStatus = buildChartPie("📋 Status das Reservas", dsStatus);
                tuneChartFonts(chartStatus);
                painelGraficos.add(smallChartPanel(chartStatus));

                DefaultCategoryDataset dsVendRes = buildDatasetReservasPorVendedor();
                JFreeChart chartVendRes = buildChartBar("👥 Reservas por Vendedor", "Vendedor", "Número de Reservas", dsVendRes);
                tuneChartFonts(chartVendRes);
                painelGraficos.add(smallChartPanel(chartVendRes));

                DefaultCategoryDataset dsCliRes = buildDatasetTopReservasPorCliente(5);
                JFreeChart chartCliRes = buildChartBar("👑 Top 5 Clientes (Reservas)", "Cliente", "Número de Reservas", dsCliRes);
                tuneChartFonts(chartCliRes);
                painelGraficos.add(smallChartPanel(chartCliRes));

                DefaultCategoryDataset dsEvolRes = buildDatasetQtdReservasPorDia();
                JFreeChart chartEvolRes = buildChartLine("📈 Evolução de Reservas", "Dia", "Reservas", dsEvolRes);
                tuneChartFonts(chartEvolRes);
                painelGraficos.add(smallChartPanel(chartEvolRes));
            } else {
                // Gráficos para vendas
                DefaultCategoryDataset dsDia = buildDatasetTotalPorDia();
                JFreeChart chartDia = buildChartBar("📊 Total por Dia", "Dia", "Total (MT)", dsDia);
                tuneChartFonts(chartDia);
                painelGraficos.add(smallChartPanel(chartDia));

                DefaultCategoryDataset dsVend = buildDatasetFaturamentoPorVendedor();
                JFreeChart chartVend = buildChartBar("💰 Faturamento por Vendedor", "Vendedor", "Total (MT)", dsVend);
                tuneChartFonts(chartVend);
                painelGraficos.add(smallChartPanel(chartVend));

                DefaultPieDataset dsClientes = buildDatasetTopClientes(5);
                JFreeChart chartClientes = buildChartPie("👑 Top 5 Clientes (por faturamento)", dsClientes);
                tuneChartFonts(chartClientes);
                painelGraficos.add(smallChartPanel(chartClientes));

                DefaultCategoryDataset dsEvolucao = buildDatasetQtdVendasPorDia();
                JFreeChart chartEvolucao = buildChartLine("📈 Evolução de Vendas (qtd)", "Dia", "Vendas", dsEvolucao);
                tuneChartFonts(chartEvolucao);
                painelGraficos.add(smallChartPanel(chartEvolucao));
            }
        }

        painelGraficos.revalidate();
        painelGraficos.repaint();
    }

    private DefaultCategoryDataset buildDatasetTotalPorDia() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (vendasFiltradas == null) return dataset;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Map<String, java.math.BigDecimal> mapa = new java.util.TreeMap<>();
        for (VendaDTO v : vendasFiltradas) {
            String dia = sdf.format(new Date(v.dataMillis));
            java.math.BigDecimal total = calcularTotalVenda(v);
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
            java.math.BigDecimal total = calcularTotalVenda(v);
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
            java.math.BigDecimal total = calcularTotalVenda(v);
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

    private DefaultPieDataset buildDatasetStatusReservas() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (reservasFiltradas == null) return dataset;
        java.util.Map<Reserva.StatusReserva, Integer> mapa = new java.util.HashMap<>();
        for (Reserva r : reservasFiltradas) {
            mapa.merge(r.getStatus(), 1, Integer::sum);
        }
        for (var e : mapa.entrySet()) {
            dataset.setValue(e.getKey().toString(), e.getValue());
        }
        return dataset;
    }

    private DefaultCategoryDataset buildDatasetReservasPorVendedor() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (reservasFiltradas == null) return dataset;
        java.util.Map<String, Integer> mapa = new java.util.HashMap<>();
        for (Reserva r : reservasFiltradas) {
            String vendedor = r.getVendedor() != null ? r.getVendedor().getNome() : "N/A";
            mapa.merge(vendedor, 1, Integer::sum);
        }
        for (var e : mapa.entrySet()) {
            dataset.addValue(e.getValue(), "Reservas", e.getKey());
        }
        return dataset;
    }

    private DefaultCategoryDataset buildDatasetTopReservasPorCliente(int topN) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (reservasFiltradas == null) return dataset;
        java.util.Map<String, Integer> mapa = new java.util.HashMap<>();
        for (Reserva r : reservasFiltradas) {
            String cliente = r.getCliente() != null ? r.getCliente().getNome() : "N/A";
            mapa.merge(cliente, 1, Integer::sum);
        }
        java.util.List<java.util.Map.Entry<String, Integer>> list = new java.util.ArrayList<>(mapa.entrySet());
        list.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        int count = 0;
        for (var e : list) {
            dataset.addValue(e.getValue(), "Reservas", e.getKey());
            if (++count >= topN) break;
        }
        return dataset;
    }

    private DefaultCategoryDataset buildDatasetQtdReservasPorDia() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (reservasFiltradas == null) return dataset;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Map<String, Integer> mapa = new java.util.TreeMap<>();
        for (Reserva r : reservasFiltradas) {
            if (r.getDataReserva() != null) {
                String dia = sdf.format(r.getDataReserva());
                mapa.merge(dia, 1, Integer::sum);
            }
        }
        for (var e : mapa.entrySet()) {
            dataset.addValue(e.getValue(), "Reservas", e.getKey());
        }
        return dataset;
    }

    private DefaultCategoryDataset buildDatasetComparativoVendasReservas() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (vendasFiltradas == null || reservasFiltradas == null) return dataset;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Set<String> dias = new java.util.TreeSet<>();
        java.util.Map<String, Integer> vendasPorDia = new java.util.HashMap<>();
        java.util.Map<String, Integer> reservasPorDia = new java.util.HashMap<>();
        for (VendaDTO v : vendasFiltradas) {
            String dia = sdf.format(new Date(v.dataMillis));
            vendasPorDia.merge(dia, 1, Integer::sum);
            dias.add(dia);
        }
        for (Reserva r : reservasFiltradas) {
            if (r.getDataReserva() != null) {
                String dia = sdf.format(r.getDataReserva());
                reservasPorDia.merge(dia, 1, Integer::sum);
                dias.add(dia);
            }
        }
        for (String dia : dias) {
            dataset.addValue(vendasPorDia.getOrDefault(dia, 0), "Vendas", dia);
            dataset.addValue(reservasPorDia.getOrDefault(dia, 0), "Reservas", dia);
        }
        return dataset;
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

    private void tuneChartFonts(JFreeChart chart) {
        var plot = chart.getPlot();
        chart.getTitle().setFont(new java.awt.Font("Segoe UI Emoji", Font.BOLD, 12));
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(new java.awt.Font("Segoe UI Emoji", Font.ITALIC, 10));
            chart.getLegend().setPosition(RectangleEdge.BOTTOM);
            chart.getLegend().setHorizontalAlignment(HorizontalAlignment.CENTER);
        }
        // Exemplo para CategoryPlot
        if (plot instanceof CategoryPlot p) {
            p.getDomainAxis().setTickLabelFont(new java.awt.Font("Segoe UI Emoji", Font.ITALIC, 10));
            p.getDomainAxis().setLabelFont(new java.awt.Font("Segoe UI Emoji", Font.ITALIC, 11));
            p.getRangeAxis().setTickLabelFont(new java.awt.Font("Segoe UI Emoji", Font.ITALIC, 10));
            p.getRangeAxis().setLabelFont(new java.awt.Font("Segoe UI Emoji", Font.ITALIC, 11));
        }
        if (plot instanceof PiePlot pie) {
            pie.setLabelFont(new java.awt.Font("Segoe UI Emoji", Font.ITALIC, 10));
        }
    }

    // Métodos auxiliares

    private String formatCurrency(BigDecimal v) {
        if (v == null) return "0,00";
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private String resolveVendedorNome(String vendedorId) {
        if (vendedorId == null || vendedorId.isBlank()) return "N/A";
        return controller.findVendedorById(vendedorId)
                .map(Vendedor::getNome)
                .orElse(vendedorId);
    }

    private String resolveClienteNome(String clienteId) {
        if (clienteId == null || clienteId.isBlank()) return "N/A";
        return controller.findClienteById(clienteId)
                .map(Cliente::getNome)
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

    private BigDecimal calcularTotalVenda(VendaDTO venda) {
        if (venda.total != null && venda.total.compareTo(BigDecimal.ZERO) > 0) {
            return venda.total;
        }

        // Recalcular se necessário
        BigDecimal total = BigDecimal.ZERO;
        if (venda.itens != null) {
            for (ItemVendaDTO item : venda.itens) {
                if (item.precoUnitario != null) {
                    total = total.add(item.precoUnitario.multiply(BigDecimal.valueOf(item.quantidade)));
                }
            }
        }
        return total;
    }

    /**
     * Limpa os filtros aplicados.
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
     * Exporta o relatório atual para PDF.
     */
    private void exportarPDF() {
        try {
            if ((vendasFiltradas == null || vendasFiltradas.isEmpty()) && (reservasFiltradas == null || reservasFiltradas.isEmpty())) {
                JOptionPane.showMessageDialog(this, "Nenhum registro para exportar.", "Relatórios", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar Relatório PDF");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
            fileChooser.setSelectedFile(new java.io.File("relatorio_vendas_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File arquivo = fileChooser.getSelectedFile();
                if (!arquivo.getName().toLowerCase().endsWith(".pdf")) {
                    arquivo = new java.io.File(arquivo.getAbsolutePath() + ".pdf");
                }

                gerarPDF(arquivo);
                JOptionPane.showMessageDialog(this,
                        "Relatório exportado com sucesso para:\n" + arquivo.getAbsolutePath(),
                        "Exportação Concluída", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Gera o arquivo PDF com o relatório completo incluindo gráficos
     */
    private void gerarPDF(java.io.File arquivo) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(arquivo));
        document.open();

        // Configurações de fonte
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);

        // Título
        document.add(new Paragraph("RELATÓRIO DE VENDAS E RESERVAS", fontTitulo));
        document.add(new Paragraph("Data de geração: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontNormal));
        document.add(new Paragraph("Tipo de relatório: " + cmbTipoRelatorio.getSelectedItem(), fontNormal));
        document.add(new Paragraph(" "));

        // Estatísticas gerais
        document.add(new Paragraph("ESTATÍSTICAS GERAIS", fontSubtitulo));
        document.add(new Paragraph("Total de Vendas: " + lblTotalVendas.getText(), fontNormal));
        document.add(new Paragraph("Faturamento Total: " + lblTotalFaturamento.getText(), fontNormal));
        document.add(new Paragraph("Ticket Médio: " + lblTicketMedio.getText(), fontNormal));
        document.add(new Paragraph("Melhor Vendedor: " + lblMelhorVendedor.getText(), fontNormal));
        document.add(new Paragraph("Melhor Cliente: " + lblMelhorCliente.getText(), fontNormal));
        document.add(new Paragraph("Total de Reservas: " + lblTotalReservas.getText(), fontNormal));
        document.add(new Paragraph("Reservas Ativas: " + lblReservasAtivas.getText(), fontNormal));
        document.add(new Paragraph("Reservas Canceladas: " + lblReservasCanceladas.getText(), fontNormal));
        document.add(new Paragraph(" "));

        if (cmbTipoRelatorio.getSelectedItem().equals("Relatório Geral Completo")) {
            document.add(new Paragraph("RESUMO POR TIPO DE RELATÓRIO", fontSubtitulo));
            document.add(new Paragraph("Contagem de registros e tabelas completas para cada tipo, considerando filtros globais (período, vendedor e cliente).", fontNormal));
            document.add(new Paragraph(" "));

            // Carregar dados completos
            List<VendaDTO> allVendas = controller.getVendasDTO();
            List<Reserva> allReservas = controller.getReservas();

            // Preparar filtros globais
            Date iniPeriodo = parseDateOrNull(txtDataInicio.getText());
            Date fimPeriodo = parseDateOrNull(txtDataFim.getText());
            long inicioMillis = 0;
            long fimMillis = Long.MAX_VALUE;
            if (iniPeriodo != null && fimPeriodo != null) {
                Calendar calInicio = Calendar.getInstance();
                calInicio.setTime(iniPeriodo);
                calInicio.set(Calendar.HOUR_OF_DAY, 0);
                calInicio.set(Calendar.MINUTE, 0);
                calInicio.set(Calendar.SECOND, 0);
                calInicio.set(Calendar.MILLISECOND, 0);
                inicioMillis = calInicio.getTimeInMillis();

                Calendar calFim = Calendar.getInstance();
                calFim.setTime(fimPeriodo);
                calFim.set(Calendar.HOUR_OF_DAY, 23);
                calFim.set(Calendar.MINUTE, 59);
                calFim.set(Calendar.SECOND, 59);
                calFim.set(Calendar.MILLISECOND, 999);
                fimMillis = calFim.getTimeInMillis();

                if (inicioMillis > fimMillis) {
                    long temp = inicioMillis;
                    inicioMillis = fimMillis;
                    fimMillis = temp;
                }
            }

            Vendedor vendedorSel = (Vendedor) cmbVendedor.getSelectedItem();
            String vendedorId = (vendedorSel != null) ? vendedorSel.getId() : null;
            Cliente clienteSel = (Cliente) cmbCliente.getSelectedItem();
            String clienteId = (clienteSel != null) ? clienteSel.getId() : null;

            // Colunas da tabela
            String[] colunas = {"ID", "Data", "Vendedor", "Cliente", "Qtd Itens", "Total", "Tipo"};

            // Loop por cada tipo de relatório
            for (String relTipo : TIPOS_RELATORIO) {
                if (relTipo.equals("Relatório Geral Completo")) continue;

                // Obter dados para este tipo (listas filtradas)
                List<VendaDTO> vendasParaTipo = new ArrayList<>();
                List<Reserva> reservasParaTipo = new ArrayList<>();

                if (relTipo.equals("Todas as Vendas") || relTipo.equals("Relatório Geral de Vendas")) {
                    vendasParaTipo = filtrarVendasBase(allVendas, inicioMillis, fimMillis, vendedorId, clienteId);
                } else if (relTipo.equals("Por Período")) {
                    List<VendaDTO> temp = new ArrayList<>(allVendas);
                    if (iniPeriodo != null && fimPeriodo != null) {
                        final long startMillis = inicioMillis;
                        final long endMillis = fimMillis;
                        temp.removeIf(v -> v.dataMillis < startMillis || v.dataMillis > endMillis);
                    }
                    vendasParaTipo = filtrarVendasBase(temp, inicioMillis, fimMillis, vendedorId, clienteId);
                } else if (relTipo.equals("Por Vendedor")) {
                    List<VendaDTO> temp = new ArrayList<>(allVendas);
                    if (vendedorId != null) {
                        final String vid = vendedorId;
                        temp.removeIf(v -> !vid.equals(v.vendedorId));
                    }
                    vendasParaTipo = filtrarVendasBase(temp, inicioMillis, fimMillis, vendedorId, clienteId);
                } else if (relTipo.equals("Por Cliente")) {
                    List<VendaDTO> temp = new ArrayList<>(allVendas);
                    if (clienteId != null) {
                        final String cid = clienteId;
                        temp.removeIf(v -> !cid.equals(v.clienteId));
                    }
                    vendasParaTipo = filtrarVendasBase(temp, inicioMillis, fimMillis, vendedorId, clienteId);
                } else if (relTipo.equals("Reservas Ativas")) {
                    reservasParaTipo = filtrarReservasBase(allReservas, inicioMillis, fimMillis, vendedorId, clienteId);
                    reservasParaTipo.removeIf(r -> r.getStatus() != Reserva.StatusReserva.ATIVA);
                } else if (relTipo.equals("Reservas Canceladas")) {
                    reservasParaTipo = filtrarReservasBase(allReservas, inicioMillis, fimMillis, vendedorId, clienteId);
                    reservasParaTipo.removeIf(r -> r.getStatus() != Reserva.StatusReserva.CANCELADA);
                } else if (relTipo.equals("Reservas Convertidas")) {
                    reservasParaTipo = filtrarReservasBase(allReservas, inicioMillis, fimMillis, vendedorId, clienteId);
                    reservasParaTipo.removeIf(r -> r.getStatus() != Reserva.StatusReserva.CONVERTIDA);
                } else if (relTipo.equals("Relatório Geral de Reservas")) {
                    reservasParaTipo = filtrarReservasBase(allReservas, inicioMillis, fimMillis, vendedorId, clienteId);
                }

                int totalRegistros = vendasParaTipo.size() + reservasParaTipo.size();

                // Título da seção
                document.add(new Paragraph(relTipo + " (" + totalRegistros + " registros)", fontSubtitulo));

                // Criar tabela
                PdfPTable table = new PdfPTable(colunas.length);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(20f);

                // Cabeçalho
                for (String coluna : colunas) {
                    PdfPCell cell = new PdfPCell(new Paragraph(coluna, fontSubtitulo));
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(240, 240, 240));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                // Adicionar linhas de vendas
                for (VendaDTO dto : vendasParaTipo) {
                    Date data = new Date(dto.dataMillis);
                    String vendedorNome = resolveVendedorNome(dto.vendedorId);
                    String clienteNome = resolveClienteNome(dto.clienteId);
                    int qtdItens = somaQuantidadeItens(dto.itens);
                    BigDecimal total = calcularTotalVenda(dto);
                    String totalStr = formatCurrency(total) + " MT";

                    adicionarLinhaTabela(table, fontNormal, dto.idVenda, dateFormat.format(data), vendedorNome, clienteNome, qtdItens, totalStr, "Venda");
                }

                // Adicionar linhas de reservas
                for (Reserva reserva : reservasParaTipo) {
                    String clienteNome = reserva.getCliente() != null ? reserva.getCliente().getNome() : "N/A";
                    String vendedorNome = reserva.getVendedor() != null ? reserva.getVendedor().getNome() : "N/A";
                    int qtdItens = reserva.getItens() != null ? reserva.getItens().size() : 0;
                    double valorTotal = reserva.getValorTotal();
                    String totalStr = String.format("%.2f MT", valorTotal);
                    String dataStr = reserva.getDataReserva() != null ? dateFormat.format(reserva.getDataReserva()) : "N/A";
                    String tipoStr = "Reserva (" + reserva.getStatus() + ")";

                    adicionarLinhaTabela(table, fontNormal, reserva.getIdReserva(), dataStr, vendedorNome, clienteNome, qtdItens, totalStr, tipoStr);
                }

                // Se não há dados, adicionar linha vazia
                if (totalRegistros == 0) {
                    PdfPCell emptyCell = new PdfPCell(new Paragraph("Nenhum registro encontrado.", fontNormal));
                    emptyCell.setColspan(colunas.length);
                    emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(emptyCell);
                }

                document.add(table);
                document.add(new Paragraph(" "));
            }
            document.newPage();
        }

        // Gráficos
        if (cmbTipoRelatorio.getSelectedItem().equals("Relatório Geral Completo")) {
            document.add(new Paragraph("GRÁFICOS ANALÍTICOS", fontSubtitulo));
            document.add(new Paragraph(" "));
            adicionarGraficosAoPDF(document, writer);
            document.newPage();
        }

        // Tabela de dados final
        document.add(new Paragraph("DETALHES DOS REGISTROS FILTRADOS (RELATÓRIO COMPLETO)", fontSubtitulo));
        PdfPTable tableFinal = new PdfPTable(modeloTabelaVendas.getColumnCount());
        tableFinal.setWidthPercentage(100);

        // Cabeçalho
        for (int i = 0; i < modeloTabelaVendas.getColumnCount(); i++) {
            PdfPCell cell = new PdfPCell(new Paragraph(modeloTabelaVendas.getColumnName(i), fontSubtitulo));
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(240, 240, 240));
            tableFinal.addCell(cell);
        }

        // Dados
        for (int row = 0; row < modeloTabelaVendas.getRowCount(); row++) {
            for (int col = 0; col < modeloTabelaVendas.getColumnCount(); col++) {
                Object value = modeloTabelaVendas.getValueAt(row, col);
                tableFinal.addCell(new PdfPCell(new Paragraph(value != null ? value.toString() : "", fontNormal)));
            }
        }

        document.add(tableFinal);
        document.close();
    }

    /**
     * Método auxiliar para filtrar vendas base (aplicar filtros globais de vendedor e cliente)
     */
    private List<VendaDTO> filtrarVendasBase(List<VendaDTO> vendas, long inicioMillis, long fimMillis, String vendedorId, String clienteId) {
        List<VendaDTO> result = new ArrayList<>(vendas);

        // Filtro de período global (sempre aplicar se definido)
        if (inicioMillis != 0 && fimMillis != Long.MAX_VALUE) {
            final long startMillis = inicioMillis;
            final long endMillis = fimMillis;
            result.removeIf(v -> v.dataMillis < startMillis || v.dataMillis > endMillis);
        }

        // Filtro de vendedor global
        if (vendedorId != null) {
            final String vid = vendedorId;
            result.removeIf(v -> !vid.equals(v.vendedorId));
        }

        // Filtro de cliente global
        if (clienteId != null) {
            final String cid = clienteId;
            result.removeIf(v -> !cid.equals(v.clienteId));
        }

        return result;
    }

    /**
     * Metodo auxiliar para filtrar reservas base (aplicar filtros globais de período, vendedor e cliente)
     */
    private List<Reserva> filtrarReservasBase(List<Reserva> reservas, long inicioMillis, long fimMillis, String vendedorId, String clienteId) {
        List<Reserva> result = new ArrayList<>(reservas);

        // Filtro de período global
        if (inicioMillis != 0 && fimMillis != Long.MAX_VALUE) {
            final long startMillis = inicioMillis;
            final long endMillis = fimMillis;
            result.removeIf(r -> r.getDataReserva() == null ||
                    r.getDataReserva().getTime() < startMillis ||
                    r.getDataReserva().getTime() > endMillis);
        }

        // Filtro de vendedor global
        if (vendedorId != null) {
            final String vid = vendedorId;
            result.removeIf(r -> r.getVendedor() == null || !vid.equals(r.getVendedor().getId()));
        }

        // Filtro de cliente global
        if (clienteId != null) {
            final String cid = clienteId;
            result.removeIf(r -> r.getCliente() == null || !cid.equals(r.getCliente().getId()));
        }

        return result;
    }

    /**
     * Metodo auxiliar para adicionar uma linha à tabela PDF
     */
    private void adicionarLinhaTabela(PdfPTable table, Font font, Object id, String data, String vendedor, String cliente, int qtd, String total, String tipo) {
        table.addCell(new PdfPCell(new Paragraph(id != null ? id.toString() : "", font)));
        table.addCell(new PdfPCell(new Paragraph(data, font)));
        table.addCell(new PdfPCell(new Paragraph(vendedor, font)));
        table.addCell(new PdfPCell(new Paragraph(cliente, font)));
        table.addCell(new PdfPCell(new Paragraph(String.valueOf(qtd), font)));
        table.addCell(new PdfPCell(new Paragraph(total, font)));
        table.addCell(new PdfPCell(new Paragraph(tipo, font)));
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
     * Adiciona gráficos ao documento PDF
     */
    private void adicionarGraficosAoPDF(Document document, PdfWriter writer) throws Exception {
        // Criar os gráficos que serão exportados
        DefaultCategoryDataset dsDia = buildDatasetTotalPorDia();
        JFreeChart chartDia = buildChartBar("📊 Total por Dia", "Dia", "Total (MT)", dsDia);

        DefaultCategoryDataset dsVend = buildDatasetFaturamentoPorVendedor();
        JFreeChart chartVend = buildChartBar("💰 Faturamento por Vendedor", "Vendedor", "Total (MT)", dsVend);

        DefaultPieDataset dsStatus = buildDatasetStatusReservas();
        JFreeChart chartStatus = buildChartPie("📋 Status das Reservas", dsStatus);

        DefaultCategoryDataset dsComparativo = buildDatasetComparativoVendasReservas();
        JFreeChart chartComparativo = buildChartBar("📈 Comparativo Vendas vs Reservas", "Data", "Quantidade", dsComparativo);

        JFreeChart[] graficos = {chartDia, chartVend, chartStatus, chartComparativo};

        String[] titulos = {
                "Total de Vendas por Dia",
                "Faturamento por Vendedor",
                "Status das Reservas",
                "Comparativo: Vendas vs Reservas"
        };

        for (int i = 0; i < graficos.length; i++) {
            // Adicionar título do gráfico
            document.add(new Paragraph(titulos[i], FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));

            // Converter gráfico para imagem e adicionar ao PDF
            Image img = chartToITextImage(graficos[i], 900, 400);
            img.setAlignment(Image.ALIGN_CENTER);
            document.add(img);

            document.add(new Paragraph(" "));
            if (i == 1) {
                document.newPage();
            }
        }
    }

    /**
     * Volta para o menu principal.
     */
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
     * Instala bindings para que a tecla ALT dê o efeito visual no btnVoltar.
     */
    private void installAltForVoltar() {
        JComponent root = getRootPane();
        if (root == null) {
            root = this;
        }

        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        KeyStroke altPress = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, false);  // press
        KeyStroke altRelease = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true); // release

        im.put(altPress, "altPressed_voltar");
        im.put(altRelease, "altReleased_voltar");

        am.put("altPressed_voltar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnVoltar != null) {
                    ButtonModel m = btnVoltar.getModel();
                    m.setArmed(true);
                    m.setPressed(true);
                    btnVoltar.requestFocusInWindow();
                }
            }
        });

        am.put("altReleased_voltar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnVoltar != null) {
                    btnVoltar.doClick();

                    ButtonModel m = btnVoltar.getModel();
                    m.setPressed(false);
                    m.setArmed(false);
                }
            }
        });
    }
}
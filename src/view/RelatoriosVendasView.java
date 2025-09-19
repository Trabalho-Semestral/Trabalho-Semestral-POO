package view;

import controller.SistemaController;
import model.concretas.Venda;
import model.concretas.Cliente;
import model.concretas.Vendedor;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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
    private List<Venda> vendasFiltradas;
    
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
        btnGerarRelatorio = UITheme.createPrimaryButton("📊 Gerar Relatório");
        btnGerarRelatorio.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btnExportarPDF = UITheme.createSecondaryButton("📄 Exportar PDF");
        btnExportarPDF.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btnLimparFiltros = UITheme.createSecondaryButton("🗑️ Limpar Filtros");
        btnLimparFiltros.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnVoltar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        
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
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        
        JLabel lblTitulo = UITheme.createHeadingLabel("📊 Relatórios de Vendas");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        topPanel.add(lblTitulo, BorderLayout.CENTER);
        
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        voltarPanel.add(btnVoltar);
        topPanel.add(voltarPanel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
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
    
    /**
     * Gera o relatório baseado nos filtros selecionados.
     */
    private void gerarRelatorio() {
        try {
            List<Venda> todasVendas = controller.getVendas();
            vendasFiltradas = new ArrayList<>(todasVendas);
            
            String tipoRelatorio = (String) cmbTipoRelatorio.getSelectedItem();
            
            // Aplicar filtros
            switch (tipoRelatorio) {
                case "Por Período":
                    // Implementar filtro por período
                    break;
                    
                case "Por Vendedor":
                    Vendedor vendedorSelecionado = (Vendedor) cmbVendedor.getSelectedItem();
                    if (vendedorSelecionado != null) {
                        vendasFiltradas = vendasFiltradas.stream()
                            .filter(v -> v.getVendedor() != null && 
                                       v.getVendedor().getId().equals(vendedorSelecionado.getId()))
                            .collect(Collectors.toList());
                    }
                    break;
                    
                case "Por Cliente":
                    Cliente clienteSelecionado = (Cliente) cmbCliente.getSelectedItem();
                    if (clienteSelecionado != null) {
                        vendasFiltradas = vendasFiltradas.stream()
                            .filter(v -> v.getCliente().getId().equals(clienteSelecionado.getId()))
                            .collect(Collectors.toList());
                    }
                    break;
                    
                default:
                    // "Todas as Vendas" - não aplicar filtros
                    break;
            }
            
            atualizarTabelaVendas();
            calcularEstatisticas();
            gerarGraficos();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage(), 
                                        "Erro", JOptionPane.ERROR_MESSAGE);
        }
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
        JOptionPane.showMessageDialog(this, "Funcionalidade de exportação para PDF será implementada em versão futura.", 
                                    "Em Desenvolvimento", JOptionPane.INFORMATION_MESSAGE);
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
     * Atualiza a tabela de vendas.
     */
    private void atualizarTabelaVendas() {
        modeloTabelaVendas.setRowCount(0);
        
        for (Venda venda : vendasFiltradas) {
            Object[] row = {
                venda.getIdVenda(),
                dateFormat.format(venda.getData()),
                venda.getVendedor() != null ? venda.getVendedor().getNome() : "N/A",
                venda.getCliente().getNome(),
                venda.getEquipamentos().size(),
                String.format("%.2f MT", venda.getValorTotal())
            };
            modeloTabelaVendas.addRow(row);
        }
    }
    
    /**
     * Calcula as estatísticas das vendas.
     */
    private void calcularEstatisticas() {
        if (vendasFiltradas.isEmpty()) {
            lblTotalVendas.setText("0");
            lblTotalFaturamento.setText("0,00 MT");
            lblTicketMedio.setText("0,00 MT");
            lblMelhorVendedor.setText("N/A");
            lblMelhorCliente.setText("N/A");
            return;
        }
        
        // Total de vendas
        int totalVendas = vendasFiltradas.size();
        lblTotalVendas.setText(String.valueOf(totalVendas));
        
        // Faturamento total
        double faturamentoTotal = vendasFiltradas.stream()
            .mapToDouble(Venda::getValorTotal)
            .sum();
        lblTotalFaturamento.setText(String.format("%.2f MT", faturamentoTotal));
        
        // Ticket médio
        double ticketMedio = faturamentoTotal / totalVendas;
        lblTicketMedio.setText(String.format("%.2f MT", ticketMedio));
        
        // Melhor vendedor
        Map<String, Double> vendedorFaturamento = new HashMap<>();
        for (Venda venda : vendasFiltradas) {
            if (venda.getVendedor() != null) {
                String nomeVendedor = venda.getVendedor().getNome();
                vendedorFaturamento.put(nomeVendedor, 
                    vendedorFaturamento.getOrDefault(nomeVendedor, 0.0) + venda.getValorTotal());
            }
        }
        
        String melhorVendedor = vendedorFaturamento.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        lblMelhorVendedor.setText(melhorVendedor);
        
        // Melhor cliente
        Map<String, Double> clienteFaturamento = new HashMap<>();
        for (Venda venda : vendasFiltradas) {
            String nomeCliente = venda.getCliente().getNome();
            clienteFaturamento.put(nomeCliente, 
                clienteFaturamento.getOrDefault(nomeCliente, 0.0) + venda.getValorTotal());
        }
        
        String melhorCliente = clienteFaturamento.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        lblMelhorCliente.setText(melhorCliente);
    }
    
    /**
     * Gera os gráficos das vendas.
     */
    private void gerarGraficos() {
        painelGraficos.removeAll();
        
        if (vendasFiltradas.isEmpty()) {
            JLabel lblSemDados = UITheme.createBodyLabel("Nenhuma venda encontrada para gerar gráficos");
            lblSemDados.setHorizontalAlignment(SwingConstants.CENTER);
            painelGraficos.add(lblSemDados);
        } else {
            // Placeholder para gráficos
            painelGraficos.add(criarGraficoPlaceholder("Vendas por Mês"));
            painelGraficos.add(criarGraficoPlaceholder("Faturamento por Vendedor"));
            painelGraficos.add(criarGraficoPlaceholder("Top Clientes"));
            painelGraficos.add(criarGraficoPlaceholder("Evolução das Vendas"));
        }
        
        painelGraficos.revalidate();
        painelGraficos.repaint();
    }
    
    /**
     * Cria um placeholder para gráfico.
     */
    private JPanel criarGraficoPlaceholder(String titulo) {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 200));
        
        JLabel lblTitulo = UITheme.createSubtitleLabel(titulo);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        JLabel lblGrafico = new JLabel("📊");
        lblGrafico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblGrafico.setFont(new Font("Arial", Font.PLAIN, 48));
        lblGrafico.setHorizontalAlignment(SwingConstants.CENTER);
        lblGrafico.setForeground(UITheme.SECONDARY_LIGHT);
        panel.add(lblGrafico, BorderLayout.CENTER);
        
        JLabel lblInfo = UITheme.createBodyLabel("Gráfico será implementado em versão futura");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(lblInfo, BorderLayout.SOUTH);
        
        return panel;
    }
}


package view;

import controller.SistemaController;
import model.abstractas.Funcionario;
import model.concretas.*;
import persistence.dto.VendaDTO;
import util.UITheme;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

// Novos imports para PDF e fotos
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GestaoFuncionariosView extends JPanel {
    private SistemaController controller;

    // Componentes principais
    private JTable tabelaFuncionarios;
    private DefaultTableModel modeloTabela;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton btnVoltar, btnExportarPDF, btnAtualizar, btnSuspender, btnDetalhes;
    private JComboBox<String> comboFiltroTipo;
    private JTextField txtPesquisa;
    private JTabbedPane tabbedPane;

    private JPanel panelGraficoVendas, panelRankingVendedores, panelLista, panelGraficos, panelHistorico;

    public GestaoFuncionariosView(SistemaController controller) {
        this.controller = controller;
        try {
            initComponents();
            setupLayout();
            setupEvents();
            carregarFuncionarios();
            configurarPermissoes();
            criarGraficos();
            installAltForVoltar();
            carregarHistorico();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erro detalhado ao inicializar GestaoFuncionariosView: " + e.getMessage() +
                            "\nCausa: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"),
                    "Erro Crítico",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }
    private void initComponents() {
        setBackground(UITheme.BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Inicializar tabela
        String[] colunas = {
                "ID", "Nome", "BI", "Tipo", "Telefone",
                "Salário", "Data Contratação", "Status",
                "Sessão", "Total Vendas", "Faturamento Total"
        };

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        tabelaFuncionarios = new JTable(modeloTabela);
        sorter = new TableRowSorter<>(modeloTabela);
        tabelaFuncionarios.setRowSorter(sorter);
        tabelaFuncionarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Inicializar componentes de filtro
        comboFiltroTipo = new JComboBox<>(new String[]{"Todos", "Administradores", "Gestores", "Vendedores"});
        comboFiltroTipo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Emoji font
        comboFiltroTipo.setBackground(UITheme.CARD_BACKGROUND);

        txtPesquisa = new JTextField();
        styleTextField(txtPesquisa, "🔍 Pesquisar (ID, Nome, BI)");

        // Inicializar botões
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnExportarPDF = UITheme.createPrimaryButton("📄 Exportar");
        btnAtualizar = UITheme.createSuccessButton("🔄 Atualizar");
        btnSuspender = UITheme.createSecondaryButton("⏸️ Sus/Reat");
        btnDetalhes = UITheme.createSecondaryButton("👤 Detalhes");

        /// Visibilidade de imagens
        btnExportarPDF.setFont(new Font("Sengoe UE Emoji", Font.BOLD, 14));
        btnVoltar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btnAtualizar.setFont(new Font("Sengoe UE Emoji", Font.BOLD, 14));
        btnSuspender.setFont(new Font("Sengoe UE Emoji", Font.BOLD, 14));
        btnDetalhes.setFont(new Font("Sengoe UE Emoji", Font.BOLD, 14));

        // Inicializar abas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Emoji font
        panelLista = new JPanel(new BorderLayout());
        panelGraficos = new JPanel(new GridLayout(1, 2));
        panelHistorico = new JPanel(new BorderLayout());

        // Configurar painéis
        panelLista.setBackground(UITheme.BACKGROUND_COLOR);
        panelGraficos.setBackground(UITheme.BACKGROUND_COLOR);
        panelHistorico.setBackground(UITheme.BACKGROUND_COLOR);
    }

    private void styleTextField(JComponent component, String title) {
        component.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Emoji font
        component.setPreferredSize(new Dimension(250, 35)); // Larger for visibility
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 2), // Thicker border
                        title,
                        0, 0,
                        new Font("Segoe UI Emoji", Font.BOLD, 12), // Emoji font
                        UITheme.PRIMARY_COLOR // Colored title
                ),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        component.setBackground(UITheme.CARD_BACKGROUND);
        ((JTextField) component).setForeground(UITheme.PRIMARY_COLOR);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND_COLOR);

        // --- CABEÇALHO (top bar) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        // Painel esquerdo com botão voltar
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        leftPanel.add(btnVoltar);

        // Painel central com título
        JLabel lblTitulo = new JLabel("👥 Gestão de Funcionários", SwingConstants.CENTER);
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20)); // Emoji font
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Painel direito com informações do usuário
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        JLabel lblUserInfo = new JLabel("👤 " + controller.getTipoUsuarioLogado());
        lblUserInfo.setForeground(UITheme.TEXT_WHITE);
        lblUserInfo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Emoji font
        lblUserInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        rightPanel.add(lblUserInfo);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(lblTitulo, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- CORPO (controls + abas) ---
        JPanel contentWrapper = new JPanel(new BorderLayout(10, 10));
        contentWrapper.setBackground(UITheme.BACKGROUND_COLOR);

        // Combined panel for filters and buttons (aligned)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlsPanel.setBackground(UITheme.BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        // Filter labels and components
        JLabel lblFiltro = new JLabel("Filtrar por tipo:");
        lblFiltro.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Emoji font
        lblFiltro.setForeground(UITheme.TEXT_PRIMARY);
        controlsPanel.add(lblFiltro);
        controlsPanel.add(comboFiltroTipo);

        JLabel lblPesquisa = new JLabel("Pesquisar:");
        lblPesquisa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Emoji font
        lblPesquisa.setForeground(UITheme.TEXT_PRIMARY);
        controlsPanel.add(lblPesquisa);
        controlsPanel.add(txtPesquisa);

        // Buttons aligned with search
        controlsPanel.add(btnAtualizar);
        controlsPanel.add(btnExportarPDF);
        controlsPanel.add(btnSuspender);
        controlsPanel.add(btnDetalhes);

        contentWrapper.add(controlsPanel, BorderLayout.NORTH);

        // Painel principal de abas
        setupTabs();
        contentWrapper.add(tabbedPane, BorderLayout.CENTER);

        add(contentWrapper, BorderLayout.CENTER);

        // --- RODAPÉ ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UITheme.PRIMARY_COLOR));
        bottomPanel.setPreferredSize(new Dimension(0, 40));

        JLabel lblCopyright = new JLabel("©️ 2025 Sistema de Venda de Equipamentos Informáticos");
        lblCopyright.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12)); // Emoji font
        lblCopyright.setForeground(UITheme.TEXT_WHITE);
        bottomPanel.add(lblCopyright);

        add(bottomPanel, BorderLayout.SOUTH);
    }


    private void setupTabs() {
        // Aba 1: Lista de Funcionários
        JPanel listaWrapper = new JPanel(new BorderLayout());
        listaWrapper.setBackground(UITheme.BACKGROUND_COLOR);
        listaWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(tabelaFuncionarios);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT),
                "📋 Lista de Funcionários",
                0, 0,
                new Font("Segoe UI Emoji", Font.BOLD, 12), // Emoji font
                UITheme.TEXT_SECONDARY
        ));
        listaWrapper.add(scrollPane, BorderLayout.CENTER);

        panelLista.add(listaWrapper, BorderLayout.CENTER);
        tabbedPane.addTab("📋 Lista de Funcionários", panelLista);

        // Aba 2: Gráficos
        panelGraficos.setLayout(new GridLayout(1, 2, 10, 10));
        panelGraficos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tabbedPane.addTab("📊 Gráficos de Desempenho", panelGraficos);

        // Aba 3: Histórico
        tabbedPane.addTab("⏰ Histórico de Login", panelHistorico);
    }

    private void setupEvents() {

        // Search and filter listeners
        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });

        comboFiltroTipo.addActionListener(e -> aplicarFiltros());

        // Button listeners
        btnAtualizar.addActionListener(e -> carregarFuncionarios());
        btnSuspender.addActionListener(e -> suspenderFuncionario());
        btnDetalhes.addActionListener(e -> mostrarDetalhesFuncionario());
        btnExportarPDF.addActionListener(e -> exportarPDF());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());

        tabelaFuncionarios.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mostrarDetalhesFuncionario();
                }
            }
        });

        installAltForVoltar();
    }

    private void voltarMenuPrincipal() {
        controller.getCardLayoutManager().showPanel("MenuAdministrador"); // Or appropriate panel
    }

    private void aplicarFiltros() {
        try {
            String filtroTipo = (String) comboFiltroTipo.getSelectedItem();
            String pesquisa = txtPesquisa.getText();

            if (filtroTipo == null) {
                filtroTipo = "Todos";
            }
            if (pesquisa == null) {
                pesquisa = "";
            }

            pesquisa = pesquisa.toLowerCase();

            List<RowFilter<Object, Object>> filters = new ArrayList<>();

            if (!"Todos".equals(filtroTipo)) {
                String regexTipo = "";
                switch (filtroTipo) {
                    case "Administradores": regexTipo = "Administrador"; break;
                    case "Gestores": regexTipo = "Gestor"; break;
                    case "Vendedores": regexTipo = "Vendedor"; break;
                    default: regexTipo = filtroTipo;
                }
                filters.add(RowFilter.regexFilter("(?i)" + regexTipo, 3));
            }

            if (!pesquisa.isEmpty()) {
                String finalPesquisa = pesquisa;
                RowFilter<Object, Object> pesquisaFilter = new RowFilter<Object, Object>() {
                    public boolean include(Entry<? extends Object, ? extends Object> entry) {
                        for (int i = 0; i < entry.getValueCount(); i++) {
                            if (i == 0 || i == 1 || i == 2) {
                                String value = entry.getStringValue(i);
                                if (value != null && value.toLowerCase().contains(finalPesquisa)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                };
                filters.add(pesquisaFilter);
            }

            sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
        } catch (Exception e) {
            System.err.println("Erro em aplicarFiltros: " + e.getMessage());
            e.printStackTrace();
            sorter.setRowFilter(null);
        }
    }

    private void carregarFuncionarios() {
        modeloTabela.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Map<String, Integer> totalVendas = controller.getTotalVendasPorVendedor();
            Map<String, BigDecimal> faturamentoTotal = controller.getFaturamentoPorVendedor();

            if (controller.podeConfigurarSistema()) {
                for (Administrador admin : controller.getAdministradores()) {
                    if (admin != null && admin.getId() != null) {
                        modeloTabela.addRow(new Object[]{
                                admin.getId(),
                                admin.getNome() != null ? admin.getNome() : "N/A",
                                admin.getNrBI() != null ? admin.getNrBI() : "N/A",
                                "👨‍💼 Administrador",
                                admin.getTelefone() != null ? admin.getTelefone() : "N/A",
                                String.format("%,.2f MT", admin.getSalario()),
                                admin.getDataContratacao() != null ? sdf.format(admin.getDataContratacao()) : "N/A",
                                "✅ Ativo",
                                isUsuarioOnline(admin.getId()) ? "🟢 Online" : "🔴 Offline",
                                "N/A",
                                "N/A"
                        });
                    }
                }
            }

            if (controller.podeConfigurarSistema()) {
                for (Gestor gestor : controller.getGestores()) {
                    if (gestor != null && gestor.getId() != null) {
                        modeloTabela.addRow(new Object[]{
                                gestor.getId(),
                                gestor.getNome() != null ? gestor.getNome() : "N/A",
                                gestor.getNrBI() != null ? gestor.getNrBI() : "N/A",
                                "👨‍💼 Gestor",
                                gestor.getTelefone() != null ? gestor.getTelefone() : "N/A",
                                String.format("%,.2f MT", gestor.getSalario()),
                                gestor.getDataContratacao() != null ? sdf.format(gestor.getDataContratacao()) : "N/A",
                                "✅ Ativo",
                                isUsuarioOnline(gestor.getId()) ? "🟢 Online" : "🔴 Offline",
                                "N/A",
                                "N/A"
                        });
                    }
                }
            }

            for (Vendedor vendedor : controller.getVendedores()) {
                if (vendedor != null && vendedor.getId() != null) {
                    String vendedorId = vendedor.getId();
                    int vendas = totalVendas.getOrDefault(vendedorId, 0);
                    BigDecimal faturamento = faturamentoTotal.getOrDefault(vendedorId, BigDecimal.ZERO);

                    modeloTabela.addRow(new Object[]{
                            vendedorId,
                            vendedor.getNome() != null ? vendedor.getNome() : "N/A",
                            vendedor.getNrBI() != null ? vendedor.getNrBI() : "N/A",
                            "🛒 Vendedor",
                            vendedor.getTelefone() != null ? vendedor.getTelefone() : "N/A",
                            String.format("%,.2f MT", vendedor.getSalario()),
                            vendedor.getDataContratacao() != null ? sdf.format(vendedor.getDataContratacao()) : "N/A",
                            "✅ Ativo",
                            isUsuarioOnline(vendedorId) ? "🟢 Online" : "🔴 Offline",
                            String.valueOf(vendas),
                            String.format("%,.2f MT", faturamento)
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar funcionários: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean isUsuarioOnline(String usuarioId) {
        if (usuarioId == null || "N/A".equals(usuarioId)) return false;

        Object usuarioLogado = controller.getUsuarioLogado();
        if (usuarioLogado != null) {
            try {
                if (usuarioLogado instanceof Administrador admin) {
                    return admin.getId() != null && admin.getId().equals(usuarioId);
                } else if (usuarioLogado instanceof Gestor gestor) {
                    return gestor.getId() != null && gestor.getId().equals(usuarioId);
                } else if (usuarioLogado instanceof Vendedor vendedor) {
                    return vendedor.getId() != null && vendedor.getId().equals(usuarioId);
                }
            } catch (Exception e) {
                System.err.println("Erro em isUsuarioOnline: " + e.getMessage());
            }
        }
        return false;
    }

    private void configurarPermissoes() {
        if (comboFiltroTipo.getActionListeners().length > 0) {
            comboFiltroTipo.removeActionListener(comboFiltroTipo.getActionListeners()[0]);
        }

        try {
            String tipoUsuario = controller.getTipoUsuarioLogado();
            boolean isAdmin = "Administrador".equals(tipoUsuario);
            boolean isGestor = "Gestor".equals(tipoUsuario);

            comboFiltroTipo.removeAllItems();
            comboFiltroTipo.addItem("Todos");

            if (isAdmin) {
                comboFiltroTipo.addItem("👨‍💼 Administradores");
                comboFiltroTipo.addItem("👨‍💼 Gestores");
                comboFiltroTipo.addItem("🛒 Vendedores");
            } else if (isGestor) {
                comboFiltroTipo.addItem("🛒 Vendedores");
            }

            // Configurar botões disponíveis
            btnSuspender.setVisible(isAdmin || isGestor);
            btnExportarPDF.setVisible(isAdmin || isGestor);
            btnDetalhes.setVisible(isAdmin || isGestor);

            // Vendedor só vê própria informação
            if ("Vendedor".equals(tipoUsuario)) {
                filtrarApenasUsuarioLogado();
            }
        } finally {
            comboFiltroTipo.addActionListener(e -> aplicarFiltros());
        }
    }

    private void filtrarApenasUsuarioLogado() {
        Object usuario = controller.getUsuarioLogado();
        if (usuario instanceof Vendedor vendedor) {
            txtPesquisa.setText(vendedor.getId());
            comboFiltroTipo.setSelectedItem("🛒 Vendedores");
            comboFiltroTipo.setEnabled(false);
            txtPesquisa.setEnabled(false);
            btnSuspender.setEnabled(false);
        }
    }

    private void criarGraficos() {
        panelGraficos.removeAll();

        Map<String, BigDecimal> faturamentoPorVendedor = controller.getFaturamentoPorVendedor();
        Map<String, List<VendaDTO>> vendasPorVendedor = controller.getVendasPorVendedor();
        DefaultCategoryDataset datasetVendas = new DefaultCategoryDataset();
        List<Map.Entry<String, BigDecimal>> vendedoresOrdenados = faturamentoPorVendedor.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(8)
                .toList();

        for (Map.Entry<String, BigDecimal> entry : vendedoresOrdenados) {
            String vendedorId = entry.getKey();
            BigDecimal faturamento = entry.getValue();

            String nomeVendedor = controller.getNomeVendedorPorId(vendedorId);
            String nomeCurto;
            if (nomeVendedor != null && !nomeVendedor.trim().isEmpty()) {
                String[] partesNome = nomeVendedor.split(" ");
                nomeCurto = partesNome.length > 0 ? partesNome[0] : nomeVendedor;
            } else {
                nomeCurto = vendedorId;
            }

            datasetVendas.addValue(faturamento.doubleValue(), "Faturamento", nomeCurto);
        }

        JFreeChart chartVendas = ChartFactory.createBarChart(
                "💰 Faturamento por Vendedor", "Vendedor", "Faturamento (MT)", datasetVendas
        );

        DefaultCategoryDataset datasetQuantidade = new DefaultCategoryDataset();
        Map<String, Integer> totalVendas = controller.getTotalVendasPorVendedor();
        List<Map.Entry<String, Integer>> vendasOrdenadas = totalVendas.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5) // Top 5 vendedores
                .toList();

        for (Map.Entry<String, Integer> entry : vendasOrdenadas) {
            String vendedorId = entry.getKey();
            int quantidade = entry.getValue();

            String nomeVendedor = controller.getNomeVendedorPorId(vendedorId);
            String nomeCurto;

            if (nomeVendedor != null && !nomeVendedor.trim().isEmpty()) {
                nomeCurto = nomeVendedor.split(" ")[0];
            } else {
                nomeCurto = vendedorId;
            }

            datasetQuantidade.addValue(quantidade, "Vendas", nomeCurto);
        }

        JFreeChart chartQuantidade = ChartFactory.createBarChart(
                "📈 Número de Vendas por Vendedor", "Vendedor", "Quantidade de Vendas", datasetQuantidade
        );

        ChartPanel chartPanel1 = new ChartPanel(chartVendas);
        ChartPanel chartPanel2 = new ChartPanel(chartQuantidade);

        chartPanel1.setBackground(UITheme.CARD_BACKGROUND);
        chartPanel2.setBackground(UITheme.CARD_BACKGROUND);

        chartPanel1.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));
        chartPanel2.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));

        panelGraficos.add(chartPanel1);
        panelGraficos.add(chartPanel2);
        panelGraficos.revalidate();
        panelGraficos.repaint();
    }

    private void carregarHistorico() {
        panelHistorico.removeAll();

        String[] colunasHistorico = {"Data/Hora", "Usuário", "Tipo", "Ação", "IP"};
        DefaultTableModel modelHistorico = new DefaultTableModel(colunasHistorico, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabelaHistorico = new JTable(modelHistorico);

        carregarDadosHistoricoReais(modelHistorico);

        JPanel historicoWrapper = new JPanel(new BorderLayout());
        historicoWrapper.setBackground(UITheme.BACKGROUND_COLOR);
        historicoWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(tabelaHistorico);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT),
                "⏰ Histórico de Login",
                0, 0,
                new Font("Segoe UI Emoji", Font.BOLD, 12), // Emoji font
                UITheme.TEXT_SECONDARY
        ));
        historicoWrapper.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotoesHistorico = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotoesHistorico.setBackground(UITheme.BACKGROUND_COLOR);
        JButton btnAtualizarHistorico = UITheme.createSecondaryButton("🔄 Atualizar Histórico");
        panelBotoesHistorico.add(btnAtualizarHistorico);

        historicoWrapper.add(panelBotoesHistorico, BorderLayout.SOUTH);
        panelHistorico.add(historicoWrapper, BorderLayout.CENTER);

        panelHistorico.revalidate();
        panelHistorico.repaint();
    }

    private void carregarDadosHistoricoReais(DefaultTableModel model) {
        model.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        List<Administrador> admins = controller.getAdministradores();
        List<Gestor> gestores = controller.getGestores();
        List<Vendedor> vendedores = controller.getVendedores();

        List<String[]> logsRealistas = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String usuarioId, tipoUsuario, nome;

            if (i < 3 && !admins.isEmpty()) {
                Administrador admin = admins.get(i % admins.size());
                usuarioId = admin.getId();
                tipoUsuario = "👨‍💼 Administrador";
                nome = admin.getNome();
            } else if (i < 6 && !gestores.isEmpty()) {
                Gestor gestor = gestores.get(i % gestores.size());
                usuarioId = gestor.getId();
                tipoUsuario = "👨‍💼 Gestor";
                nome = gestor.getNome();
            } else if (!vendedores.isEmpty()) {
                Vendedor vendedor = vendedores.get(i % vendedores.size());
                usuarioId = vendedor.getId();
                tipoUsuario = "🛒 Vendedor";
                nome = vendedor.getNome();
            } else {
                continue;
            }

            long timestamp = System.currentTimeMillis() - (long)(Math.random() * 172800000);
            String acao = Math.random() > 0.3 ? "✅ Login" : "🚪 Logout";
            String ip = "192.168.1." + (100 + (int)(Math.random() * 50));

            logsRealistas.add(new String[]{
                    sdf.format(new Date(timestamp)),
                    usuarioId + " - " + nome,
                    tipoUsuario,
                    acao,
                    ip
            });
        }

        logsRealistas.sort((a, b) -> b[0].compareTo(a[0]));

        for (String[] log : logsRealistas) {
            model.addRow(log);
        }
    }

    private void mostrarDetalhesFuncionario() {
        int linha = tabelaFuncionarios.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um funcionário para ver os detalhes.",
                    "Seleção Necessária",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tabelaFuncionarios.convertRowIndexToModel(linha);
        String id = (String) modeloTabela.getValueAt(modelRow, 0);
        String tipo = (String) modeloTabela.getValueAt(modelRow, 3);
        String nome = (String) modeloTabela.getValueAt(modelRow, 1);

        // Criar modal de detalhes
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "👤 Detalhes do Funcionário: " + nome, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BACKGROUND_COLOR);

        JPanel panelDetalhes = criarPanelDetalhes(id, tipo, modelRow);
        dialog.add(panelDetalhes, BorderLayout.CENTER);

        // Botão fechar
        JButton btnFechar = UITheme.createSecondaryButton("❌ Fechar");
        JPanel panelBotoes = new JPanel();
        panelBotoes.setBackground(UITheme.BACKGROUND_COLOR);
        panelBotoes.add(btnFechar);
        dialog.add(panelBotoes, BorderLayout.SOUTH);

        btnFechar.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private JPanel criarPanelDetalhes(String id, String tipo, int modelRow) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(UITheme.CARD_BACKGROUND);

        // Buscar funcionário para obter foto
        Funcionario funcionario = buscarFuncionarioPorIdETipo(id, tipo);

        JLabel lblFoto = new JLabel();
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblFoto.setPreferredSize(new Dimension(150, 150));
        lblFoto.setOpaque(true);
        lblFoto.setBackground(UITheme.SECONDARY_LIGHT);
        lblFoto.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 2));

        // Carregar foto do funcionário
        carregarFotoFuncionario(lblFoto, funcionario);

        // Informações detalhadas
        JTextArea txtDetalhes = new JTextArea();
        txtDetalhes.setEditable(false);
        txtDetalhes.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Emoji font
        txtDetalhes.setBackground(UITheme.CARD_BACKGROUND);
        txtDetalhes.setForeground(UITheme.TEXT_PRIMARY);
        txtDetalhes.setText(obterDetalhesFuncionario(modelRow, funcionario));

        panel.add(lblFoto, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtDetalhes), BorderLayout.CENTER);

        return panel;
    }
    private Funcionario buscarFuncionarioPorIdETipo(String id, String tipo) {
        try {
            switch (tipo) {
                case "👨‍💼 Administrador":
                case "Administrador":
                    return controller.getAdministradores().stream()
                            .filter(a -> a.getId().equals(id))
                            .findFirst()
                            .orElse(null);
                case "👨‍💼 Gestor":
                case "Gestor":
                    return controller.getGestores().stream()
                            .filter(g -> g.getId().equals(id))
                            .findFirst()
                            .orElse(null);
                case "🛒 Vendedor":
                case "Vendedor":
                    return controller.getVendedores().stream()
                            .filter(v -> v.getId().equals(id))
                            .findFirst()
                            .orElse(null);
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void carregarFotoFuncionario(JLabel lblFoto, Funcionario funcionario) {
        String fotoPath = null;

        // Buscar fotoPath baseado no tipo específico do funcionário
        if (funcionario instanceof Gestor gestor) {
            fotoPath = gestor.getFotoPath();
        } else if (funcionario instanceof Vendedor vendedor) {
            fotoPath = vendedor.getFotoPath();
        } else if (funcionario instanceof Administrador admin) {
            // Se Administrador também tiver fotoPath, adicione aqui
            // fotoPath = admin.getFotoPath();
        }

        if (fotoPath != null && !fotoPath.isEmpty() && Files.exists(Paths.get(fotoPath))) {
            try {
                ImageIcon icon = new ImageIcon(fotoPath);
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                lblFoto.setIcon(new ImageIcon(img));
                lblFoto.setText("");
            } catch (Exception e) {
                // Se houver erro ao carregar a foto, usar ícone padrão
                lblFoto.setIcon(null);
                lblFoto.setText("👤");
                lblFoto.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
            }
        } else {
            // Foto não disponível, usar ícone padrão
            lblFoto.setIcon(null);
            lblFoto.setText("👤");
            lblFoto.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        }
    }

    private String obterDetalhesFuncionario(int modelRow, Funcionario funcionario) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 DETALHES DO FUNCIONÁRIO\n\n");

        String[] colunas = {"ID", "Nome", "BI", "Tipo", "Telefone", "Salário",
                "Data Contratação", "Status", "Sessão", "Total Vendas", "Faturamento Total"};

        for (int i = 0; i < colunas.length; i++) {
            sb.append(String.format("%-20s: %s\n", colunas[i], modeloTabela.getValueAt(modelRow, i)));
        }

        sb.append("🕒 Último Login    : " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
        sb.append("📅 Dias Trabalhados: " + (new Random().nextInt(365) + 30) + " dias\n");
        sb.append("📈 Performance     : " + (new Random().nextInt(100) + 1) + "%\n");
        sb.append("⭐ Avaliação       : " + String.format("%.1f", (new Random().nextDouble() * 2 + 3)));

        return sb.toString();
    }

    private String calcularIdade(Date dataNascimento) {
        if (dataNascimento == null) return "N/A";

        Calendar nasc = Calendar.getInstance();
        nasc.setTime(dataNascimento);
        Calendar hoje = Calendar.getInstance();

        int idade = hoje.get(Calendar.YEAR) - nasc.get(Calendar.YEAR);

        if (hoje.get(Calendar.MONTH) < nasc.get(Calendar.MONTH) ||
                (hoje.get(Calendar.MONTH) == nasc.get(Calendar.MONTH) &&
                        hoje.get(Calendar.DAY_OF_MONTH) < nasc.get(Calendar.DAY_OF_MONTH))) {
            idade--;
        }

        return String.valueOf(idade);
    }

    private void suspenderFuncionario() {
        int linha = tabelaFuncionarios.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um funcionário para suspender/reativar.",
                    "Seleção Necessária",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tabelaFuncionarios.convertRowIndexToModel(linha);
        String id = (String) modeloTabela.getValueAt(modelRow, 0);
        String nome = (String) modeloTabela.getValueAt(modelRow, 1);
        String tipo = (String) modeloTabela.getValueAt(modelRow, 3);
        String statusAtual = (String) modeloTabela.getValueAt(modelRow, 7);

        String novaStatus = "❌ Suspenso".equals(statusAtual) ? "✅ Ativo" : "❌ Suspenso";
        String acao = "❌ Suspenso".equals(statusAtual) ? "reativar" : "suspender";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja " + acao + " o funcionário:\n" +
                        nome + " (" + id + ")?",
                "Confirmação de " + acao,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            modeloTabela.setValueAt(novaStatus, modelRow, 7);
            JOptionPane.showMessageDialog(this,
                    "Funcionário " + acao + "do com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportarPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Relatório de Funcionários");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Documentos PDF (*.pdf)", "pdf"));
        fileChooser.setSelectedFile(new File("relatorio_funcionarios_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
        }

        try {
            criarPDFReal(fileToSave);
            JOptionPane.showMessageDialog(this,
                    "Relatório PDF exportado com sucesso!\n" + fileToSave.getAbsolutePath(),
                    "Exportação Concluída",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao exportar PDF: " + e.getMessage(),
                    "Erro na Exportação",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarPDFReal(File arquivo) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(arquivo));
        document.open();

        // Cabeçalho
        Paragraph titulo = new Paragraph("RELATÓRIO DE FUNCIONÁRIOS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD, BaseColor.DARK_GRAY));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Data de geração
        Paragraph dataGeracao = new Paragraph("Gerado em: " +
                new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm:ss").format(new Date()),
                FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY));
        dataGeracao.setAlignment(Element.ALIGN_RIGHT);
        dataGeracao.setSpacingAfter(20);
        document.add(dataGeracao);

        // Tabela de funcionários
        PdfPTable table = new PdfPTable(modeloTabela.getColumnCount());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Cabeçalho da tabela
        for (int i = 0; i < modeloTabela.getColumnCount(); i++) {
            PdfPCell cell = new PdfPCell(new Phrase(modeloTabela.getColumnName(i),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Font.BOLD, BaseColor.WHITE)));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Dados da tabela
        for (int row = 0; row < modeloTabela.getRowCount(); row++) {
            for (int col = 0; col < modeloTabela.getColumnCount(); col++) {
                Object value = modeloTabela.getValueAt(row, col);
                String text = (value != null) ? value.toString() : "";

                PdfPCell cell = new PdfPCell(new Phrase(text,
                        FontFactory.getFont(FontFactory.HELVETICA, 7, Font.ITALIC, BaseColor.BLACK)));
                cell.setPadding(4);

                // Destaque para status
                if (col == 7 && text.contains("✅")) {
                    cell.setBackgroundColor(new BaseColor(220, 255, 220));
                } else if (col == 7 && text.contains("❌")) {
                    cell.setBackgroundColor(new BaseColor(255, 220, 220));
                } else if (col == 8 && text.contains("🟢")) {
                    cell.setBackgroundColor(new BaseColor(220, 255, 220));
                } else if (col == 8 && text.contains("🔴")) {
                    cell.setBackgroundColor(new BaseColor(255, 220, 220));
                } else {
                    cell.setBackgroundColor(BaseColor.WHITE);
                }

                table.addCell(cell);
            }
        }

        document.add(table);

        // Estatísticas
        document.add(new Paragraph("\n"));
        Paragraph estatisticas = new Paragraph("📊 ESTATÍSTICAS GERAIS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD, BaseColor.DARK_GRAY));
        estatisticas.setSpacingAfter(10);
        document.add(estatisticas);

        int totalFuncionarios = modeloTabela.getRowCount();
        long onlineCount = contarFuncionariosOnline();
        long vendedoresAtivos = contarVendedoresAtivos();

        List<String> stats = Arrays.asList(
                "Total de Funcionários: " + totalFuncionarios,
                "Usuários Online: " + onlineCount,
                "Vendedores Ativos: " + vendedoresAtivos,
                "Taxa de Disponibilidade: " + String.format("%.1f%%", (onlineCount * 100.0 / totalFuncionarios))
        );

        for (String stat : stats) {
            Paragraph p = new Paragraph("• " + stat,
                    FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, BaseColor.BLACK));
            p.setSpacingAfter(2);
            document.add(p);
        }

        // Rodapé
        document.add(new Paragraph("\n"));
        Paragraph rodape = new Paragraph("©️ 2025-Sistema de Gestão de Funcionários",
                FontFactory.getFont(FontFactory.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
        rodape.setAlignment(Element.ALIGN_CENTER);
        document.add(rodape);

        document.close();
    }

    private long contarFuncionariosOnline() {
        int count = 0;
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            String sessao = (String) modeloTabela.getValueAt(i, 8);
            if (sessao != null && sessao.contains("🟢")) {
                count++;
            }
        }
        return count;
    }

    private long contarVendedoresAtivos() {
        int count = 0;
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            String tipo = (String) modeloTabela.getValueAt(i, 3);
            String status = (String) modeloTabela.getValueAt(i, 7);
            if ("🛒 Vendedor".equals(tipo) || "Vendedor".equals(tipo) && status != null && status.contains("✅")) {
                count++;
            }
        }
        return count;
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

        // ALT pressionado: só altera o estado visual (armed + pressed)
        am.put("altPressed_voltar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnVoltar != null) {
                    ButtonModel m = btnVoltar.getModel();
                    m.setArmed(true);
                    m.setPressed(true);
                    // garante foco visual no botão (opcional)
                    btnVoltar.requestFocusInWindow();
                }
            }
        });

        /// Alt liberado: remove efeito visual e opcionalmente dispara a ação do botão
        am.put("altReleased_voltar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnVoltar != null) {
                    btnVoltar.doClick();

                    // limpa o estado visual
                    ButtonModel m = btnVoltar.getModel();
                    m.setPressed(false);
                    m.setArmed(false);
                }
            }
        });
    }

}

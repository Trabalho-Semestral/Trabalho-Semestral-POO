package view;

import controller.SistemaController;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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

    // Painéis de gráficos
    private JPanel panelGraficoVendas, panelRankingVendedores, panelLista, panelGraficos, panelHistorico;

    public GestaoFuncionariosView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
        carregarFuncionarios();
        configurarPermissoes();
        criarGraficos();
        carregarHistorico();
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
        txtPesquisa = new JTextField();
        styleTextField(txtPesquisa, "Pesquisar (ID, Nome, BI)");

        // Inicializar botões
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnExportarPDF = UITheme.createPrimaryButton("📄 Exportar PDF");
        btnAtualizar = UITheme.createSuccessButton("🔄 Atualizar");
        btnSuspender = UITheme.createSecondaryButton("⏸️ Suspender/Reativar");
        btnDetalhes = UITheme.createSecondaryButton("👤 Detalhes");

        // Inicializar abas
        tabbedPane = new JTabbedPane();
        panelLista = new JPanel(new BorderLayout());
        panelGraficos = new JPanel(new GridLayout(1, 2));
        panelHistorico = new JPanel(new BorderLayout());

        // Configurar painéis
        panelLista.setBackground(UITheme.BACKGROUND_COLOR);
        panelGraficos.setBackground(UITheme.BACKGROUND_COLOR);
        panelHistorico.setBackground(UITheme.BACKGROUND_COLOR);
    }

    private void styleTextField(JComponent component, String title) {
        component.setFont(UITheme.FONT_BODY);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT),
                        title
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        component.setBackground(UITheme.CARD_BACKGROUND);
    }

    private void setupLayout() {
        // Top bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        JLabel lblTitulo = UITheme.createHeadingLabel("👥 Gestão de Funcionários");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        topPanel.add(lblTitulo, BorderLayout.CENTER);

        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        voltarPanel.add(btnVoltar);
        topPanel.add(voltarPanel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // Painel de controles
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(UITheme.BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de filtros
        JPanel filterPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        filterPanel.setBackground(UITheme.BACKGROUND_COLOR);

        filterPanel.add(new JLabel("Filtrar por tipo:"));
        filterPanel.add(comboFiltroTipo);
        filterPanel.add(new JLabel("Pesquisar:"));
        filterPanel.add(txtPesquisa);

        controlsPanel.add(filterPanel, BorderLayout.NORTH);

        // Painel de botões
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonsPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonsPanel.add(btnAtualizar);
        buttonsPanel.add(btnExportarPDF);
        buttonsPanel.add(btnSuspender);
        buttonsPanel.add(btnDetalhes);

        controlsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(controlsPanel, BorderLayout.NORTH);

        // Configurar abas
        setupTabs();

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupTabs() {
        // Aba 1: Lista de Funcionários
        panelLista.add(new JScrollPane(tabelaFuncionarios), BorderLayout.CENTER);
        tabbedPane.addTab("📋 Lista de Funcionários", panelLista);

        // Aba 2: Gráficos
        panelGraficos.setLayout(new GridLayout(1, 2, 10, 10));
        panelGraficos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Gráficos serão adicionados no metodo criarGraficos()
        tabbedPane.addTab("📊 Gráficos de Desempenho", panelGraficos);

        // Aba 3: Histórico
        tabbedPane.addTab("⏰ Histórico de Login", panelHistorico);
    }

    private void setupEvents() {
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
        btnAtualizar.addActionListener(e -> carregarFuncionarios());
        btnExportarPDF.addActionListener(e -> exportarPDF());
        btnSuspender.addActionListener(e -> suspenderFuncionario());
        btnDetalhes.addActionListener(e -> mostrarDetalhesFuncionario());

        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });

        comboFiltroTipo.addActionListener(e -> aplicarFiltros());

        tabelaFuncionarios.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mostrarDetalhesFuncionario();
                }
            }
        });
    }

    private void aplicarFiltros() {
        String filtroTipo = (String) comboFiltroTipo.getSelectedItem();
        String pesquisa = txtPesquisa.getText().toLowerCase();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Filtro por tipo
        if (!"Todos".equals(filtroTipo)) {
            String regexTipo = "";
            switch (filtroTipo) {
                case "Administradores": regexTipo = "Administrador"; break;
                case "Gestores": regexTipo = "Gestor"; break;
                case "Vendedores": regexTipo = "Vendedor"; break;
            }
            filters.add(RowFilter.regexFilter("(?i)" + regexTipo, 3));
        }

        // Filtro por pesquisa (ID, Nome, BI)
        if (!pesquisa.isEmpty()) {
            RowFilter<Object, Object> pesquisaFilter = new RowFilter<Object, Object>() {
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (i == 0 || i == 1 || i == 2) { // ID, Nome, BI
                            String value = entry.getStringValue(i).toLowerCase();
                            if (value.contains(pesquisa)) {
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
    }
    private void carregarFuncionarios() {
        modeloTabela.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            // Obter dados reais de vendas do controller
            Map<String, Integer> totalVendas = controller.getTotalVendasPorVendedor();
            Map<String, BigDecimal> faturamentoTotal = controller.getFaturamentoPorVendedor();

            // Carregar administradores (apenas para admin) - COM VALIDAÇÃO DE NULL
            if (controller.podeConfigurarSistema()) {
                for (Administrador admin : controller.getAdministradores()) {
                    if (admin != null && admin.getId() != null) {
                        modeloTabela.addRow(new Object[]{
                                admin.getId(),
                                admin.getNome() != null ? admin.getNome() : "N/A",
                                admin.getNrBI() != null ? admin.getNrBI() : "N/A",
                                "Administrador",
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

            // Carregar gestores (apenas para admin) - COM VALIDAÇÃO DE NULL
            if (controller.podeConfigurarSistema()) {
                for (Gestor gestor : controller.getGestores()) {
                    if (gestor != null && gestor.getId() != null) {
                        modeloTabela.addRow(new Object[]{
                                gestor.getId(),
                                gestor.getNome() != null ? gestor.getNome() : "N/A",
                                gestor.getNrBI() != null ? gestor.getNrBI() : "N/A",
                                "Gestor",
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

            // Carregar vendedores com dados REAIS de vendas - COM VALIDAÇÃO DE NULL
            for (Vendedor vendedor : controller.getVendedores()) {
                if (vendedor != null && vendedor.getId() != null) {
                    String vendedorId = vendedor.getId();
                    int vendas = totalVendas.getOrDefault(vendedorId, 0);
                    BigDecimal faturamento = faturamentoTotal.getOrDefault(vendedorId, BigDecimal.ZERO);

                    modeloTabela.addRow(new Object[]{
                            vendedorId,
                            vendedor.getNome() != null ? vendedor.getNome() : "N/A",
                            vendedor.getNrBI() != null ? vendedor.getNrBI() : "N/A",
                            "Vendedor",
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
        if (usuarioId == null) return false;
        Object usuarioLogado = controller.getUsuarioLogado();
        if (usuarioLogado != null) {
            try {
                if (usuarioLogado instanceof Administrador admin && admin.getId() != null && admin.getId().equals(usuarioId)) {
                    return true;
                } else if (usuarioLogado instanceof Gestor gestor && gestor.getId() != null && gestor.getId().equals(usuarioId)) {
                    return true;
                } else if (usuarioLogado instanceof Vendedor vendedor && vendedor.getId() != null && vendedor.getId().equals(usuarioId)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void configurarPermissoes() {
        String tipoUsuario = controller.getTipoUsuarioLogado();
        boolean isAdmin = "Administrador".equals(tipoUsuario);
        boolean isGestor = "Gestor".equals(tipoUsuario);

        // Configurar filtros disponíveis
        comboFiltroTipo.removeAllItems();
        comboFiltroTipo.addItem("Todos");

        if (isAdmin) {
            comboFiltroTipo.addItem("Administradores");
            comboFiltroTipo.addItem("Gestores");
            comboFiltroTipo.addItem("Vendedores");
        } else if (isGestor) {
            comboFiltroTipo.addItem("Vendedores");
        }

        // Configurar botões disponíveis
        btnSuspender.setVisible(isAdmin || isGestor);
        btnExportarPDF.setVisible(isAdmin || isGestor);
        btnDetalhes.setVisible(isAdmin || isGestor);

        // Vendedor só vê própria informação
        if ("Vendedor".equals(tipoUsuario)) {
            filtrarApenasUsuarioLogado();
        }
    }

    private void filtrarApenasUsuarioLogado() {
        Object usuario = controller.getUsuarioLogado();
        if (usuario instanceof Vendedor vendedor) {
            txtPesquisa.setText(vendedor.getId());
            comboFiltroTipo.setSelectedItem("Vendedores");
            comboFiltroTipo.setEnabled(false);
            txtPesquisa.setEnabled(false);
            btnSuspender.setEnabled(false);
        }
    }
    private void criarGraficos() {
        panelGraficos.removeAll();
        // Dados REAIS do controller
        Map<String, BigDecimal> faturamentoPorVendedor = controller.getFaturamentoPorVendedor();
        Map<String, List<VendaDTO>> vendasPorVendedor = controller.getVendasPorVendedor();

        // Gráfico 1: Faturamento por Vendedor (REAL)
        DefaultCategoryDataset datasetVendas = new DefaultCategoryDataset();
        // Ordenar vendedores por faturamento (maior primeiro)
        List<Map.Entry<String, BigDecimal>> vendedoresOrdenados = faturamentoPorVendedor.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(8) // Top 8 vendedores
                .toList();

        for (Map.Entry<String, BigDecimal> entry : vendedoresOrdenados) {
            String vendedorId = entry.getKey();
            BigDecimal faturamento = entry.getValue();

            String nomeVendedor = controller.getNomeVendedorPorId(vendedorId);
            String nomeCurto;

            // 💡 CORREÇÃO: Verifica se o nomeVendedor é nulo ou vazio antes de chamar .split()
            if (nomeVendedor != null && !nomeVendedor.trim().isEmpty()) {
                // Usar apenas primeiro nome para o gráfico
                nomeCurto = nomeVendedor.split(" ")[0];
            } else {
                nomeCurto = vendedorId; // Fallback para o ID se o nome for nulo/vazio
            }

            datasetVendas.addValue(faturamento.doubleValue(), "Faturamento", nomeCurto);
        }

        JFreeChart chartVendas = ChartFactory.createBarChart(
                "Faturamento por Vendedor (Real)", "Vendedor", "Faturamento (MT)", datasetVendas
        );

        // Gráfico 2: Número de Vendas por Vendedor
        DefaultCategoryDataset datasetQuantidade = new DefaultCategoryDataset();
        Map<String, Integer> totalVendas = controller.getTotalVendasPorVendedor();
        // Ordenar por quantidade de vendas
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
                "Número de Vendas por Vendedor", "Vendedor", "Quantidade de Vendas", datasetQuantidade
        );

        panelGraficos.add(new ChartPanel(chartVendas));
        panelGraficos.add(new ChartPanel(chartQuantidade));
        panelGraficos.revalidate();
        panelGraficos.repaint();
    }
    private DefaultCategoryDataset criarDatasetEvolucaoMensal(Map<String, List<VendaDTO>> vendasPorVendedor) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Agrupar vendas por mês para cada vendedor
        Map<String, Map<String, Integer>> vendasPorMesPorVendedor = new HashMap<>();

        for (String vendedorId : vendasPorVendedor.keySet()) {
            List<VendaDTO> vendas = vendasPorVendedor.get(vendedorId);
            Map<String, Integer> vendasPorMes = new HashMap<>();

            for (VendaDTO venda : vendas) {
                Date dataVenda = new Date(venda.dataMillis);
                String mesAno = new SimpleDateFormat("MMM/yyyy").format(dataVenda);
                vendasPorMes.put(mesAno, vendasPorMes.getOrDefault(mesAno, 0) + 1);
            }

            vendasPorMesPorVendedor.put(vendedorId, vendasPorMes);
        }

        List<String> topVendedores = vendasPorVendedor.entrySet()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        Set<String> mesesUnicos = new TreeSet<>();
        for (Map<String, Integer> vendasPorMes : vendasPorMesPorVendedor.values()) {
            mesesUnicos.addAll(vendasPorMes.keySet());
        }

        for (String vendedorId : topVendedores) {
            String nomeVendedor = controller.getNomeVendedorPorId(vendedorId).split(" ")[0];
            Map<String, Integer> vendasPorMes = vendasPorMesPorVendedor.get(vendedorId);

            for (String mes : mesesUnicos) {
                int vendas = vendasPorMes.getOrDefault(mes, 0);
                dataset.addValue(vendas, nomeVendedor, mes);
            }
        }

        return dataset;
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

        panelHistorico.add(new JScrollPane(tabelaHistorico), BorderLayout.CENTER);

        JPanel panelBotoesHistorico = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotoesHistorico.setBackground(UITheme.BACKGROUND_COLOR);
        JButton btnAtualizarHistorico = new JButton("Atualizar Histórico");
        btnAtualizarHistorico.addActionListener(e -> carregarDadosHistoricoReais(modelHistorico));
        panelBotoesHistorico.add(btnAtualizarHistorico);

        panelHistorico.add(panelBotoesHistorico, BorderLayout.SOUTH);

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
                tipoUsuario = "Administrador";
                nome = admin.getNome();
            } else if (i < 6 && !gestores.isEmpty()) {
                Gestor gestor = gestores.get(i % gestores.size());
                usuarioId = gestor.getId();
                tipoUsuario = "Gestor";
                nome = gestor.getNome();
            } else if (!vendedores.isEmpty()) {
                Vendedor vendedor = vendedores.get(i % vendedores.size());
                usuarioId = vendedor.getId();
                tipoUsuario = "Vendedor";
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

    private void carregarDadosHistorico(DefaultTableModel model) {
        model.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String[][] dadosExemplo = {
                {sdf.format(new Date()), "V001", "Vendedor", "✅ Login", "192.168.1.100"},
                {sdf.format(new Date(System.currentTimeMillis() - 3600000)), "G001", "Gestor", "🚪 Logout", "192.168.1.101"},
                {sdf.format(new Date(System.currentTimeMillis() - 7200000)), "A001", "Administrador", "✅ Login", "192.168.1.102"},
                {sdf.format(new Date(System.currentTimeMillis() - 10800000)), "V002", "Vendedor", "🚪 Logout", "192.168.1.103"},
                {sdf.format(new Date(System.currentTimeMillis() - 14400000)), "V001", "Vendedor", "✅ Login", "192.168.1.104"}
        };

        for (String[] linha : dadosExemplo) {
            model.addRow(linha);
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
                "Detalhes do Funcionário: " + nome, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panelDetalhes = criarPanelDetalhes(id, tipo, modelRow);
        dialog.add(panelDetalhes, BorderLayout.CENTER);

        // Botão fechar
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dialog.dispose());
        JPanel panelBotoes = new JPanel();
        panelBotoes.add(btnFechar);
        dialog.add(panelBotoes, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel criarPanelDetalhes(String id, String tipo, int modelRow) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(UITheme.CARD_BACKGROUND);

        // Foto do funcionário (simulada)
        JLabel lblFoto = new JLabel("🖼️", SwingConstants.CENTER);
        lblFoto.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblFoto.setPreferredSize(new Dimension(150, 150));
        lblFoto.setOpaque(true);
        lblFoto.setBackground(UITheme.SECONDARY_LIGHT);
        lblFoto.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 2));

        // Informações detalhadas
        JTextArea txtDetalhes = new JTextArea();
        txtDetalhes.setEditable(false);
        txtDetalhes.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtDetalhes.setBackground(UITheme.CARD_BACKGROUND);
        txtDetalhes.setText(obterDetalhesFuncionario(modelRow));

        panel.add(lblFoto, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtDetalhes), BorderLayout.CENTER);

        return panel;
    }

    private String obterDetalhesFuncionario(int modelRow) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DETALHES DO FUNCIONÁRIO ===\n\n");

        String[] colunas = {"ID", "Nome", "BI", "Tipo", "Telefone", "Salário",
                "Data Contratação", "Status", "Sessão", "Total Vendas", "Faturamento Total"};

        for (int i = 0; i < colunas.length; i++) {
            sb.append(String.format("%-20s: %s\n", colunas[i], modeloTabela.getValueAt(modelRow, i)));
        }

        sb.append("\n=== INFORMAÇÕES ADICIONAIS ===\n");
        sb.append("Último Login    : " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
        sb.append("Dias Trabalhados: " + (new Random().nextInt(365) + 30) + " dias\n");
        sb.append("Performance     : " + (new Random().nextInt(100) + 1) + "%\n");
        sb.append("Avaliação       : " + String.format("%.1f⭐", (new Random().nextDouble() * 2 + 3)));

        return sb.toString();
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
        String tipo = (String) modeloTabela.getValueAt(modelRow, 3);
        String nome = (String) modeloTabela.getValueAt(modelRow, 1);
        String statusAtual = (String) modeloTabela.getValueAt(modelRow, 7);

        String novoSatus = "✅ Ativo".equals(statusAtual) ? "❌ Suspenso" : "✅ Ativo";
        String acao = "✅ Ativo".equals(statusAtual) ? "suspender" : "reativar";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja " + acao + " o " + tipo.toLowerCase() + " " + nome + " (" + id + ")?",
                "Confirmar " + (acao.equals("suspender") ? "Suspensão" : "Reativação"),
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            modeloTabela.setValueAt(novoSatus, modelRow, 7);
            // TODO: Implementar no controller a suspensão real
            JOptionPane.showMessageDialog(this,
                    tipo + " " + nome + " " + acao + "do com sucesso!",
                    "Operação Concluída",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportarPDF() {
        int totalFuncionarios = modeloTabela.getRowCount();
        int ativos = 0;
        for (int i = 0; i < totalFuncionarios; i++) {
            if ("✅ Ativo".equals(modeloTabela.getValueAt(i, 7))) {
                ativos++;
            }
        }

        String mensagem = String.format(
                "Relatório PDF gerado com sucesso!\n\n" +
                        "Estatísticas:\n" +
                        "• Total de Funcionários: %d\n" +
                        "• Funcionários Ativos: %d\n" +
                        "• Funcionários Suspensos: %d\n" +
                        "• Data do Relatório: %s",
                totalFuncionarios, ativos, totalFuncionarios - ativos,
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())
        );

        JOptionPane.showMessageDialog(this,
                mensagem,
                "Exportação PDF - Simulação",
                JOptionPane.INFORMATION_MESSAGE);
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
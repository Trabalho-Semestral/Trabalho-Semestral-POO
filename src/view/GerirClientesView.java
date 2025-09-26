package view;

import controller.SistemaController;
import model.concretas.Cliente;
import util.UITheme;
import util.Validador;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * Tela para gestão de clientes com layout final, títulos e botões estilizados.
 */
public class GerirClientesView extends JPanel {
    private static final Color DARK_BLUE = new Color(19, 56, 94);
    private static final String[] COLUNAS_TABELA = {"ID", "Nome", "BI", "NUIT", "Telefone", "Endereço", "Email"};
    
    private final SistemaController controller;

    // Componentes da Interface
    private final JTextField[] camposTexto;
    private final JTable tabelaClientes;
    private final DefaultTableModel modeloTabela;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JButton btnCadastrar, btnEditar, btnRemover, btnLimpar, btnVoltar;

    public GerirClientesView(SistemaController controller) {
        this.controller = controller;
        this.camposTexto = new JTextField[6];
        initComponents();
        setupLayout();
        setupEvents();
        carregarClientes();
        atualizarEstadoBotoes(false);
    }

    private TitledBorder criarTitulo(String titulo) {
        TitledBorder border = BorderFactory.createTitledBorder(titulo);
        border.setTitleColor(DARK_BLUE);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        return border;
    }

    private void initComponents() {
        // Inicialização dos campos de texto em array para facilitar manipulação
        String[] titulos = {"Nome Completo", "Nº do BI", "NUIT", "Telefone", "Endereço", "Email"};
        for (int i = 0; i < camposTexto.length; i++) {
            camposTexto[i] = UITheme.createStyledTextField();
            camposTexto[i].setBorder(criarTitulo(titulos[i]));
        }
        
        txtPesquisar = UITheme.createStyledTextField();
        txtPesquisar.setBorder(criarTitulo("Pesquisar"));

        // Inicialização dos botões
        btnCadastrar = UITheme.createSuccessButton("➕ Cadastrar");
        btnEditar = UITheme.createSuccessButton("✏️ Editar");
        btnRemover = UITheme.createDangerButton("🗑️ Remover");
        btnLimpar = UITheme.createPrimaryButton("🧹 Limpar");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnVoltar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        // Aplicar estilo aos botões de ação
        Stream.of(btnCadastrar, btnEditar, btnRemover, btnLimpar).forEach(this::styleActionButton);

        // Configuração da tabela
        modeloTabela = new DefaultTableModel(COLUNAS_TABELA, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaClientes = createStyledTable();
        sorter = new TableRowSorter<>(modeloTabela);
        tabelaClientes.setRowSorter(sorter);
    }

    private JTable createStyledTable() {
        JTable table = new JTable(modeloTabela);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(false);
        table.setFocusable(false);
        table.setRowHeight(25);

        // Estilo do header
        JTableHeader header = table.getTableHeader();
        header.setBackground(DARK_BLUE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);

        return table;
    }

    private void styleActionButton(JButton button) {
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        button.setBackground(DARK_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(160, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        add(createTopBar(), BorderLayout.NORTH);
        add(createMainSplitPane(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        ocultarColunaID();
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        
        JLabel lblTitulo = UITheme.createHeadingLabel("👤 Gestão de Clientes");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);
        
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar);
        topBar.add(voltarPanel, BorderLayout.WEST);
        
        return topBar;
    }

    private JSplitPane createMainSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                criarPainelFormularioE_Acoes(), criarPainelTabela());
        splitPane.setResizeWeight(0.4);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        return splitPane;
    }

    private JPanel criarPainelFormularioE_Acoes() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel formPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        formPanel.setOpaque(false);
        Stream.of(camposTexto).forEach(formPanel::add);

        JPanel buttonGridPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonGridPanel.setOpaque(false);
        Stream.of(btnCadastrar, btnEditar, btnRemover, btnLimpar).forEach(buttonGridPanel::add);

        JPanel buttonWrapper = new JPanel(new GridBagLayout());
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(buttonGridPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonWrapper, BorderLayout.EAST);

        return mainPanel;
    }

    private JPanel criarPainelTabela() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        TitledBorder border = BorderFactory.createTitledBorder("Clientes Cadastrados");
        border.setTitleColor(DARK_BLUE);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.setBorder(border);

        JPanel pesquisaPanel = new JPanel(new BorderLayout(5, 5));
        pesquisaPanel.setOpaque(false);
        pesquisaPanel.add(txtPesquisar, BorderLayout.CENTER);
        panel.add(pesquisaPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        scrollPane.getViewport().setBackground(UITheme.BACKGROUND_COLOR);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(DARK_BLUE);
        JLabel lblCopyright = new JLabel("© 2025 Sistema de Venda de Equipamentos Informáticos");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblCopyright.setForeground(Color.GRAY);
        bottomPanel.add(lblCopyright);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UITheme.PRIMARY_COLOR));
        return bottomPanel;
    }

    private void ocultarColunaID() {
        TableColumn idColumn = tabelaClientes.getColumnModel().getColumn(0);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setWidth(0);
    }

    private void setupEvents() {
        // Ações dos botões
        btnCadastrar.addActionListener(e -> cadastrarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnRemover.addActionListener(e -> removerCliente());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());

        // Seleção na tabela
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean clienteSelecionado = tabelaClientes.getSelectedRow() != -1;
                atualizarEstadoBotoes(clienteSelecionado);
                if (clienteSelecionado) carregarClienteSelecionado();
            }
        });

        // Filtro de pesquisa
        txtPesquisar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarClientes(); }
            public void removeUpdate(DocumentEvent e) { filtrarClientes(); }
            public void changedUpdate(DocumentEvent e) { filtrarClientes(); }
        });
    }

    private void cadastrarCliente() {
        executarOperacaoCliente(() -> controller.adicionarCliente(criarClienteFromForm()), 
                              "cadastrado");
    }

    private void editarCliente() {
        executarOperacaoCliente(() -> {
            Cliente clienteAntigo = obterClienteSelecionado();
            return clienteAntigo != null && 
                   controller.atualizarCliente(clienteAntigo, criarClienteFromForm());
        }, "atualizado");
    }

    private void removerCliente() {
        int viewRow = tabelaClientes.getSelectedRow();
        if (viewRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja remover este cliente?",
                    "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                executarOperacaoCliente(() -> {
                    Cliente cliente = obterClienteSelecionado();
                    return cliente != null && controller.removerCliente(cliente);
                }, "removido");
            }
        }
    }

    private void executarOperacaoCliente(OperacaoCliente operacao, String operacaoTexto) {
        try {
            if (operacao.executar()) {
                JOptionPane.showMessageDialog(this, 
                    "Cliente " + operacaoTexto + " com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarClientes();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao " + operacaoTexto + " cliente.", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro de validação: " + ex.getMessage(), 
                "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    @FunctionalInterface
    private interface OperacaoCliente {
        boolean executar();
    }

    private Cliente obterClienteSelecionado() {
        int viewRow = tabelaClientes.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = tabelaClientes.convertRowIndexToModel(viewRow);
            String clienteId = (String) modeloTabela.getValueAt(modelRow, 0);
            return controller.getClientes().stream()
                    .filter(c -> c.getId().equals(clienteId))
                    .findFirst().orElse(null);
        }
        return null;
    }

    private void limparFormulario() {
        Stream.of(camposTexto).forEach(field -> field.setText(""));
        txtPesquisar.setText("");
        tabelaClientes.clearSelection();
    }

    private void voltarMenuPrincipal() {
        String tipoUsuario = controller.getTipoUsuarioLogado();
        String painel = (tipoUsuario == null) ? "Login" : "Menu" + tipoUsuario;
        controller.getCardLayoutManager().showPanel(painel);
    }

    private Cliente criarClienteFromForm() {
        String nome = camposTexto[0].getText().trim();
        String nrBI = camposTexto[1].getText().trim();
        String nuit = camposTexto[2].getText().trim();
        String telefone = camposTexto[3].getText().trim();
        String endereco = camposTexto[4].getText().trim();
        String email = camposTexto[5].getText().trim();

        // Validações
        if (!Validador.validarCampoObrigatorio(nome)) 
            throw new IllegalArgumentException("O campo Nome é obrigatório.");
        if (!Validador.validarBI(nrBI)) 
            throw new IllegalArgumentException("O BI é inválido. Formato: 12 dígitos e 1 letra maiúscula.");
        if (!Validador.validarNuit(nuit)) 
            throw new IllegalArgumentException("O NUIT é inválido. Formato: 9 dígitos.");
        if (!Validador.validarTelefone(telefone)) 
            throw new IllegalArgumentException("O Telefone é inválido. Formato: +2588[2/3/4/5/6/7]xxxxxxx.");
        if (!Validador.validarCampoObrigatorio(endereco)) 
            throw new IllegalArgumentException("O campo Endereço é obrigatório.");
        if (!Validador.validarEmail(email)) 
            throw new IllegalArgumentException("O Email é inválido.");

        return new Cliente(nome, nrBI, nuit, telefone, endereco, email);
    }

    private void carregarClientes() {
        modeloTabela.setRowCount(0);
        controller.getClientes().forEach(cli -> 
            modeloTabela.addRow(new Object[]{
                cli.getId(), cli.getNome(), cli.getNrBI(), cli.getNuit(),
                cli.getTelefone(), cli.getEndereco(), cli.getEmail()
            })
        );
    }

    private void carregarClienteSelecionado() {
        int viewRow = tabelaClientes.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = tabelaClientes.convertRowIndexToModel(viewRow);
            for (int i = 0; i < camposTexto.length; i++) {
                camposTexto[i].setText((String) modeloTabela.getValueAt(modelRow, i + 1));
            }
        }
    }

    private void filtrarClientes() {
        String texto = txtPesquisar.getText().trim();
        sorter.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + texto));
    }

    private void atualizarEstadoBotoes(boolean habilitar) {
        btnEditar.setEnabled(habilitar);
        btnRemover.setEnabled(habilitar);
    }
}

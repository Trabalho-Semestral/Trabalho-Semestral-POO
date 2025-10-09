package view;

import controller.SistemaController;
import model.concretas.Cliente;
import util.UITheme;
import util.Validador;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Tela para gestão de clientes com layout final, títulos e botões estilizados.
 */
public class GerirClientesView extends JPanel {

    private final SistemaController controller;

    // --- Componentes da Interface ---
    private JTextField txtNome, txtNrBI, txtNuit, txtTelefone, txtEndereco, txtEmail;
    private JTextField txtPesquisar;

    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private TableRowSorter<DefaultTableModel> sorter;

    private JButton btnCadastrar, btnEditar, btnRemover, btnLimpar, btnVoltar;

    public GerirClientesView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
        carregarClientes();
        atualizarEstadoBotoes(false);
    }

    private TitledBorder criarTitulo(String titulo) {
        TitledBorder border = BorderFactory.createTitledBorder(titulo);
        border.setTitleColor(new Color(19, 56, 94)); // Azul escuro da topbar
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        return border;
    }

    private void initComponents() {
        txtNome = UITheme.createStyledTextField();
        txtNome.setBorder(criarTitulo("Nome Completo"));

        txtNrBI = UITheme.createStyledTextField();
        txtNrBI.setBorder(criarTitulo("Nº do BI"));

        txtNuit = UITheme.createStyledTextField();
        txtNuit.setBorder(criarTitulo("NUIT"));

        txtTelefone = UITheme.createStyledTextField();
        txtTelefone.setBorder(criarTitulo("Telefone"));

        txtEndereco = UITheme.createStyledTextField();
        txtEndereco.setBorder(criarTitulo("Endereço"));

        txtEmail = UITheme.createStyledTextField();
        txtEmail.setBorder(criarTitulo("Email"));

        txtPesquisar = UITheme.createStyledTextField();
        txtPesquisar.setBorder(criarTitulo("Pesquisar"));

        /// Campos que serao afectados pelos efeitos
        JTextField[] campos = {txtNome, txtNrBI, txtNuit, txtTelefone, txtEndereco, txtEmail/*, txtPesquisar*/};
        for (JTextField tf : campos) {
            adicionarEfeitoHover(tf); 
        }

        // --- Botões ---


        btnCadastrar = UITheme.createSuccessButton("➕ Cadastrar");
        btnEditar = UITheme.createSuccessButton("✏️ Editar");
        btnRemover = UITheme.createDangerButton("🗑️ Remover");
        btnLimpar = UITheme.createPrimaryButton("🧹 Limpar");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnVoltar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));


        JButton[] actionButtons = {btnCadastrar, btnEditar, btnRemover, btnLimpar};
        for (JButton btn : actionButtons) {
            styleActionButton(btn);
        }

        // --- Tabela com visibilidade do header corrigida ---
        modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Nome", "BI", "NUIT", "Telefone", "Endereço", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaClientes = new JTable(modeloTabela);
        tabelaClientes.setBackground(Color.WHITE);
        tabelaClientes.setForeground(Color.BLACK);
        tabelaClientes.setSelectionBackground(new Color(173, 216, 230));
        tabelaClientes.setSelectionForeground(Color.BLACK);
        tabelaClientes.setRowSelectionAllowed(true);
        tabelaClientes.setCellSelectionEnabled(false);
        tabelaClientes.setFocusable(false);
        tabelaClientes.setRowHeight(25);

        // Estilo explícito para o Header da tabela para garantir visibilidade
        JTableHeader header = tabelaClientes.getTableHeader();
        header.setBackground(new Color(19, 56, 94));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        sorter = new TableRowSorter<>(modeloTabela);
        tabelaClientes.setRowSorter(sorter);
    }

    /**
     * Estilo de botão estático, sem efeito hover, para máxima clareza.
     */
    private void styleActionButton(JButton button) {
        Font emojiFont = new Font("Segoe UI Emoji", Font.BOLD, 15);
        Color baseColor = new Color(19, 56, 94); // Azul escuro e sóbrio

        button.setFont(emojiFont);
        button.setBackground(baseColor);
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
        setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Margem inferior removida para dar espaço ao rodapé

        // --- Barra Superior ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        JLabel lblTitulo = UITheme.createHeadingLabel("👤 Gestão de Clientes");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Sengoe UI Emoji", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar);
        topBar.add(voltarPanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // --- Painel principal com JSplitPane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                criarPainelFormularioE_Acoes(),
                criarPainelTabela());
        splitPane.setResizeWeight(0.4);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);

        // --- Rodapé ---
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Ocultar coluna de ID
        TableColumn idColumn = tabelaClientes.getColumnModel().getColumn(0);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setWidth(0);
    }

    private JPanel criarPainelFormularioE_Acoes() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel formPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        formPanel.setOpaque(false);
        formPanel.add(txtNome);
        formPanel.add(txtNrBI);
        formPanel.add(txtNuit);
        formPanel.add(txtTelefone);
        formPanel.add(txtEmail);
        formPanel.add(txtEndereco);

        JPanel buttonGridPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonGridPanel.setOpaque(false);
        buttonGridPanel.add(btnCadastrar);
        buttonGridPanel.add(btnEditar);
        buttonGridPanel.add(btnRemover);
        buttonGridPanel.add(btnLimpar);

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
        border.setTitleColor(new Color(19, 56, 94));
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

    /**
     * Cria o painel de rodapé com a nota de copyright.
     */
    private JPanel createFooterPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(19, 56, 94));
        JLabel lblCopyright = new JLabel("© 2025 Sistema de Venda de Equipamentos Informáticos");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblCopyright.setForeground(Color.GRAY);
        bottomPanel.add(lblCopyright);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UITheme.PRIMARY_COLOR));
        return bottomPanel;
    }

    private void setupEvents() {
        btnCadastrar.addActionListener(e -> cadastrarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnRemover.addActionListener(e -> removerCliente());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());

        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean clienteSelecionado = tabelaClientes.getSelectedRow() != -1;
                atualizarEstadoBotoes(clienteSelecionado);
                if (clienteSelecionado) {
                    carregarClienteSelecionado();
                }
            }
        });

        txtPesquisar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarClientes(); }
        });
    }

    private void cadastrarCliente() {
        Cliente cliente = criarClienteFromForm();
        if (cliente != null) {
            if (controller.adicionarCliente(cliente)) {
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarCliente() {
        int viewRow = tabelaClientes.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = tabelaClientes.convertRowIndexToModel(viewRow);
            String clienteId = (String) modeloTabela.getValueAt(modelRow, 0);
            Cliente clienteAntigo = controller.getClientes().stream()
                    .filter(c -> c.getId().equals(clienteId))
                    .findFirst().orElse(null);

            if (clienteAntigo == null) {
                JOptionPane.showMessageDialog(this, "Erro ao encontrar o cliente para atualizar.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cliente clienteNovo = criarClienteFromForm();
            if (clienteNovo != null) {
                if (controller.atualizarCliente(clienteAntigo, clienteNovo)) {
                    JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    carregarClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void removerCliente() {
        int viewRow = tabelaClientes.getSelectedRow();
        if (viewRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja remover este cliente?",
                    "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                int modelRow = tabelaClientes.convertRowIndexToModel(viewRow);
                String clienteId = (String) modeloTabela.getValueAt(modelRow, 0);
                Cliente cliente = controller.getClientes().stream()
                        .filter(c -> c.getId().equals(clienteId))
                        .findFirst().orElse(null);

                if (cliente != null && controller.removerCliente(cliente)) {
                    JOptionPane.showMessageDialog(this, "Cliente removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    carregarClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao remover cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtNrBI.setText("");
        txtNuit.setText("");
        txtTelefone.setText("");
        txtEndereco.setText("");
        txtEmail.setText("");
        txtPesquisar.setText("");
        tabelaClientes.clearSelection();
    }

    private void voltarMenuPrincipal() {
        String tipoUsuario = controller.getTipoUsuarioLogado();
        String painel = (tipoUsuario == null) ? "Login" : "Menu" + tipoUsuario;
        controller.getCardLayoutManager().showPanel(painel);
    }

    private Cliente criarClienteFromForm() {
        try {
            String nome = txtNome.getText().trim();
            String nrBI = txtNrBI.getText().trim();
            String nuit = txtNuit.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String endereco = txtEndereco.getText().trim();
            String email = txtEmail.getText().trim();

            if (!Validador.validarCampoObrigatorio(nome)) throw new IllegalArgumentException("O campo Nome é obrigatório.");
            if (!Validador.validarBI(nrBI)) throw new IllegalArgumentException("O BI é inválido. Formato: 12 dígitos e 1 letra maiúscula.");
            if (!Validador.validarNuit(nuit)) throw new IllegalArgumentException("O NUIT é inválido. Formato: 9 dígitos.");
            if (!Validador.validarTelefone(telefone)) throw new IllegalArgumentException("O Telefone é inválido. Formato: +2588[2/3/4/5/6/7]xxxxxxx.");
            if (!Validador.validarCampoObrigatorio(endereco)) throw new IllegalArgumentException("O campo Endereço é obrigatório.");
            if (!Validador.validarEmail(email)) throw new IllegalArgumentException("O Email é inválido.");

            return new Cliente(nome, nrBI, nuit, telefone, endereco, email);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro de validação: " + e.getMessage(), "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void carregarClientes() {
        modeloTabela.setRowCount(0);
        List<Cliente> clientes = controller.getClientes();
        for (Cliente cli : clientes) {
            modeloTabela.addRow(new Object[]{
                    cli.getId(), cli.getNome(), cli.getNrBI(), cli.getNuit(),
                    cli.getTelefone(), cli.getEndereco(), cli.getEmail()
            });
        }
    }

    private void carregarClienteSelecionado() {
        int viewRow = tabelaClientes.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = tabelaClientes.convertRowIndexToModel(viewRow);
            txtNome.setText((String) modeloTabela.getValueAt(modelRow, 1));
            txtNrBI.setText((String) modeloTabela.getValueAt(modelRow, 2));
            txtNuit.setText((String) modeloTabela.getValueAt(modelRow, 3));
            txtTelefone.setText((String) modeloTabela.getValueAt(modelRow, 4));
            txtEndereco.setText((String) modeloTabela.getValueAt(modelRow, 5));
            txtEmail.setText((String) modeloTabela.getValueAt(modelRow, 6));
        }
    }

    private void filtrarClientes() {
        String texto = txtPesquisar.getText();
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void atualizarEstadoBotoes(boolean habilitar) {
        btnEditar.setEnabled(habilitar);
        btnRemover.setEnabled(habilitar);
    }

    /// Metodo responsavel pelos eventos...
    private void adicionarEfeitoHover(JTextField campo){
        final Border bordaOriginal =  campo.getBorder();

        Border bordaHover = new CompoundBorder(
                new LineBorder(new Color(16, 234, 208), 3, true), // line border com cantos arredondados
                new EmptyBorder(3, 6, 3, 6)                        // espaçamento interno
        );

        /// Efeito ao passar o cursor
        campo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                campo.setBorder(bordaHover);
                campo.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!campo.hasFocus()) {
                    campo.setBorder(bordaOriginal);
                }
            }
        });

        campo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(bordaHover);
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(bordaOriginal);
            }
        });
    }
}

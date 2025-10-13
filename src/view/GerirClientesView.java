package view;

import controller.SistemaController;
import model.concretas.Cliente;
import util.UITheme;
import util.Validador;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private JButton btnCadastrar, btnEditar, btnRemover, btnVoltar;

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
        border.setTitleColor(new Color(19, 56, 94));
        border.setTitleFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        return border;
    }

    private void initComponents() {
        txtNome = UITheme.createStyledTextField();
        txtNome.setBorder(criarTitulo("🏢 Nome da Empresa"));
        txtNome.setPreferredSize(new Dimension(180, 45));

        txtNrBI = UITheme.createStyledTextField();
        txtNrBI.setBorder(criarTitulo("🆔 Nº do BI"));
        txtNrBI.setPreferredSize(new Dimension(180, 45));

        txtNuit = UITheme.createStyledTextField();
        txtNuit.setBorder(criarTitulo("💼 NUIT"));
        txtNuit.setPreferredSize(new Dimension(180, 45));

        txtTelefone = UITheme.createStyledTextField();
        txtTelefone.setBorder(criarTitulo("📞 Telefone"));
        txtTelefone.setPreferredSize(new Dimension(180, 45));

        txtEndereco = UITheme.createStyledTextField();
        txtEndereco.setBorder(criarTitulo("📍 Endereço"));
        txtEndereco.setPreferredSize(new Dimension(180, 45));

        txtEmail = UITheme.createStyledTextField();
        txtEmail.setBorder(criarTitulo("📧 Email"));
        txtEmail.setPreferredSize(new Dimension(180, 45));

        txtPesquisar = UITheme.createStyledTextField();
        txtPesquisar.setBorder(criarTitulo("🔍 Pesquisar"));
        txtPesquisar.setPreferredSize(new Dimension(180, 45));

        /// Campos que serao afectados pelos efeitos
        JTextField[] campos = { txtNome, txtNrBI, txtNuit, txtTelefone, txtEndereco, txtEmail/*, txtPesquisar*/};
        for (JTextField tf :campos){
            adicionarEfeitoHover(tf); /// Metodo houver
        }

        // --- Botões ---
        btnCadastrar = UITheme.createSuccessButton("➕ Cadastrar");
        btnEditar = UITheme.createSuccessButton("✏️ Editar");
        btnRemover = UITheme.createDangerButton("🗑️ Remover");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnVoltar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JButton[] actionButtons = {btnCadastrar, btnEditar, btnRemover};
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
        tabelaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Standard font for table cells

        // Estilo explícito para o Header da tabela para garantir visibilidade
        JTableHeader header = tabelaClientes.getTableHeader();
        header.setBackground(new Color(19, 56, 94));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13)); // Emoji font for header if needed
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        sorter = new TableRowSorter<>(modeloTabela);
        tabelaClientes.setRowSorter(sorter);
    }

    /**
     * Estilo de botão estático, sem efeito hover, para máxima clareza.
     */
    private void styleActionButton(JButton button) {
        Font emojiFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
        Color baseColor = new Color(19, 56, 94); // Azul escuro e sóbrio

        button.setFont(emojiFont);
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.setPreferredSize(new Dimension(140, 40));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
        JLabel lblTitulo = UITheme.createHeadingLabel("👥 Gestão de Clientes");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar);
        topBar.add(voltarPanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // --- Painel principal ---
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- PAINEL SUPERIOR (formulário, botões) ---
        JPanel topContentPanel = new JPanel(new BorderLayout(15, 15));
        topContentPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // Formulário no centro (como foto estava no oeste, form no centro)
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(UITheme.CARD_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.add(txtNome);
        formPanel.add(txtNrBI);
        formPanel.add(txtNuit);
        formPanel.add(txtTelefone);
        formPanel.add(txtEmail);
        formPanel.add(txtEndereco);
        topContentPanel.add(formPanel, BorderLayout.CENTER);

        // Botões à direita
        JPanel acoesPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        acoesPanel.setBackground(UITheme.BACKGROUND_COLOR);
        acoesPanel.add(btnCadastrar);
        acoesPanel.add(btnEditar);
        acoesPanel.add(btnRemover);
        topContentPanel.add(acoesPanel, BorderLayout.EAST);

        // --- PAINEL INFERIOR (tabela) ---
        JPanel tabelaPanel = new JPanel(new BorderLayout());
        tabelaPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT),
                "👥 Clientes Cadastrados",
                0, 0,
                new Font("Segoe UI Emoji", Font.BOLD, 14),
                UITheme.TEXT_SECONDARY
        ));
        tabelaPanel.setBackground(UITheme.CARD_BACKGROUND);
        JScrollPane scroll = new JScrollPane(tabelaClientes);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        tabelaPanel.add(scroll, BorderLayout.CENTER);

        mainPanel.add(topContentPanel, BorderLayout.NORTH);
        mainPanel.add(tabelaPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // --- RODAPÉ ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UITheme.PRIMARY_COLOR));
        bottomPanel.setPreferredSize(new Dimension(0, 40));

        JLabel lblCopyright = new JLabel("© 2025 Sistema de Venda de Equipamentos Informáticos");
        lblCopyright.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        lblCopyright.setForeground(UITheme.TEXT_WHITE);
        bottomPanel.add(lblCopyright);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        btnCadastrar.addActionListener(e -> cadastrarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnRemover.addActionListener(e -> removerCliente());
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

    /// Metodo responsavel pelo foco das bordas
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
}

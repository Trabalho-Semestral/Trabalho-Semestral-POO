
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
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GerirClientesView extends JPanel {

    private final SistemaController controller;

    // --- Componentes da Interface ---
    private JTextField txtNome, txtNrBI, txtNuit, txtTelefone, txtEndereco, txtEmail;
    private JTextField txtPesquisar;
    private JButton btnCadastrar, btnEditar, btnRemover, btnVoltar, btnLimpar;

    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private TableRowSorter<DefaultTableModel> sorter;

    public GerirClientesView(SistemaController controller) {
        this.controller = controller;
        try {
            initComponents();
            setupLayout();
            setupEvents();
            carregarClientes();
            installAltForVoltar();
            atualizarEstadoBotoes(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao inicializar a tela: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    private TitledBorder criarTitulo(String titulo) {
        TitledBorder border = BorderFactory.createTitledBorder(titulo);
        border.setTitleColor(UITheme.TEXT_SECONDARY); // mesma cor da tabela
        border.setTitleFont(new Font("Segoe UI Emoji", Font.BOLD, 14)); // mesma fonte e tamanho
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
        txtPesquisar.setPreferredSize(new Dimension(180, 35));

        // Campos que serão afetados pelos efeitos
        JTextField[] campos = { txtNome, txtNrBI, txtNuit, txtTelefone, txtEndereco, txtEmail, txtPesquisar };
        for (JTextField tf : campos) {
            adicionarEfeitoHover(tf);
        }

        // --- Botões ---
        btnCadastrar = UITheme.createSuccessButton("➕ Cadastrar");
        btnEditar = UITheme.createSuccessButton("✏ Editar");
        btnRemover = UITheme.createDangerButton("🗑 Remover");
        btnVoltar = UITheme.createSecondaryButton("⬅ Voltar");
        btnLimpar = UITheme.createSecondaryButton("🧹 Limpar");

        // Estilo reduzido dos botões
        JButton[] actionButtons = { btnCadastrar, btnEditar, btnRemover, btnVoltar, btnLimpar };
        for (JButton btn : actionButtons) {
            btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
            btn.setPreferredSize(new Dimension(120, 35));
            btn.setBackground(new Color(19, 56, 94));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
        // --- Estilo do cabeçalho da tabela (colunas) ---
        JTableHeader header = tabelaClientes.getTableHeader();
        header.setBackground(new Color(245, 245, 245)); // cinza claro
        header.setForeground(UITheme.TEXT_SECONDARY); // mesma cor dos títulos dos campos
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        header.setOpaque(true);

        tabelaClientes.setBackground(Color.WHITE);
        tabelaClientes.setForeground(Color.BLACK);
        tabelaClientes.setSelectionBackground(new Color(173, 216, 230));
        tabelaClientes.setSelectionForeground(Color.BLACK);
        tabelaClientes.setRowSelectionAllowed(true);
        tabelaClientes.setCellSelectionEnabled(false);
        tabelaClientes.setFocusable(false);
        tabelaClientes.setRowHeight(25);
        tabelaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sorter = new TableRowSorter<>(modeloTabela);
        tabelaClientes.setRowSorter(sorter);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND_COLOR);

        // --- Barra Superior ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        JLabel lblTitulo = UITheme.createHeadingLabel("👥 GESTÃO DE CLIENTES");
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

        // Formulário no centro
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
        JPanel acoesPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        acoesPanel.setBackground(UITheme.BACKGROUND_COLOR);
        acoesPanel.add(btnCadastrar);
        acoesPanel.add(btnEditar);
        acoesPanel.add(btnRemover);
        acoesPanel.add(btnLimpar);
        topContentPanel.add(acoesPanel, BorderLayout.EAST);

        // --- PAINEL INFERIOR (tabela e pesquisa) ---
        JPanel tabelaPanel = new JPanel(new BorderLayout(10, 10));
        tabelaPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT),
                "👥 Clientes Cadastrados",
                0, 0,
                new Font("Segoe UI Emoji", Font.BOLD, 14),
                UITheme.TEXT_SECONDARY
        ));
        tabelaPanel.setBackground(UITheme.CARD_BACKGROUND);

        // Painel de pesquisa (acima da tabela)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(txtPesquisar);
        tabelaPanel.add(searchPanel, BorderLayout.NORTH);

        // Tabela
        JScrollPane scroll = new JScrollPane(tabelaClientes);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        tabelaPanel.add(scroll, BorderLayout.CENTER);

        mainPanel.add(topContentPanel, BorderLayout.NORTH);
        mainPanel.add(tabelaPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // --- RODAPÉ ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UITheme.PRIMARY_LIGHT));
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
        btnLimpar.addActionListener(e -> limparCampos());
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
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarClientes();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarClientes();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarClientes();
            }
        });
    }

    private void cadastrarCliente() {
        Cliente cliente = criarClienteFromForm();
        if (cliente != null) {
            if (controller.adicionarCliente(cliente)) {
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
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
                    limparCampos();
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
                    limparCampos();
                    carregarClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao remover cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtNrBI.setText("");
        txtNuit.setText("");
        txtTelefone.setText("");
        txtEndereco.setText("");
        txtEmail.setText("");
        txtPesquisar.setText("");
        tabelaClientes.clearSelection();
        sorter.setRowFilter(null);
    }

    private void voltarMenuPrincipal() {
        try {
            String tipoUsuario = controller.getTipoUsuarioLogado();
            if (tipoUsuario == null) {
                System.err.println("Tipo de usuário é nulo. Redirecionando para Login.");
                controller.getCardLayoutManager().showPanel("Login");
                return;
            }

            String painel = switch (tipoUsuario) {
                case "Gestor" -> "MenuGestor";
                case "Vendedor" -> "MenuVendedor";
                case "Administrador" -> "MenuAdministrador";
                default -> {
                    System.err.println("Tipo de usuário desconhecido: " + tipoUsuario);
                    yield "Login";
                }
            };

            if (controller.getCardLayoutManager() == null) {
                System.err.println("CardLayoutManager é nulo.");
                JOptionPane.showMessageDialog(this, "Erro interno: Gerenciador de layout não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            controller.getCardLayoutManager().showPanel(painel);
        } catch (Exception ex) {
            System.err.println("Erro ao voltar para o menu principal: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao voltar para o menu principal: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            controller.getCardLayoutManager().showPanel("Login");
        }
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
        try {
            modeloTabela.setRowCount(0);
            List<Cliente> clientes = controller.getClientes();
            if (clientes != null) {
                for (Cliente cli : clientes) {
                    modeloTabela.addRow(new Object[]{
                            cli.getId(), cli.getNome(), cli.getNrBI(), cli.getNuit(),
                            cli.getTelefone(), cli.getEndereco(), cli.getEmail()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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

    private void adicionarEfeitoHover(JTextField campo) {
        final Border bordaOriginal = campo.getBorder();

        Border bordaHover = new CompoundBorder(
                new LineBorder(new Color(16, 234, 208), 3, true),
                new EmptyBorder(3, 6, 3, 6)
        );

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

    private void installAltForVoltar() {
        JComponent root = getRootPane();
        if (root == null) {
            root = this;
        }

        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        KeyStroke altPress = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, false);
        KeyStroke altRelease = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true);

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

    private void filtrarClientes() {
        String texto = txtPesquisar.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0, 1));
        }
    }

    private void atualizarEstadoBotoes(boolean habilitar) {
        btnEditar.setEnabled(habilitar);
        btnRemover.setEnabled(habilitar);
    }
}
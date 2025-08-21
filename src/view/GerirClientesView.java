package view;

import controller.SistemaController;
import model.concretas.Cliente;
import util.Validador;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

    /**
     * Tela para gestão de clientes com layout padronizado e emojis funcionais.
     */
    public class GerirClientesView extends JPanel {

        private SistemaController controller;

        // Componentes da interface
        private JTextField txtID;
        private JTextField txtNome;
        private JTextField txtNrBI;
        private JTextField txtNuit;
        private JTextField txtTelefone;
        private JTextField txtEndereco;
        private JTextField txtEmail;

        // Tabela de clientes
        private JTable tabelaClientes;
        private DefaultTableModel modeloTabela;

        // Botões
        private JButton btnCadastrar;
        private JButton btnEditar;
        private JButton btnRemover;
        private JButton btnLimpar;
        private JButton btnVoltar;

        public GerirClientesView(SistemaController controller) {
            this.controller = controller;
            initComponents();
            setupLayout();
            setupEvents();
            carregarClientes();
        }

        private void initComponents() {
            setLayout(new BorderLayout());
            setBackground(UITheme.BACKGROUND_COLOR);

            // Campos de texto com tema personalizado
            txtID = UITheme.createStyledTextField();
            txtID.setEditable(false);
            txtID.setBackground(UITheme.SECONDARY_LIGHT);
            txtNome = UITheme.createStyledTextField();
            txtNrBI = UITheme.createStyledTextField();
            txtNuit = UITheme.createStyledTextField();
            txtTelefone = UITheme.createStyledTextField();
            txtEndereco = UITheme.createStyledTextField();
            txtEmail = UITheme.createStyledTextField();

            // Botões com tema personalizado
            btnCadastrar = UITheme.createPrimaryButton("➕ Cadastrar");
            btnEditar = UITheme.createPrimaryButton("✏️ Editar");
            btnRemover = UITheme.createDangerButton("🗑️ Remover");
            btnLimpar = UITheme.createSecondaryButton("🧹 Limpar");
            btnVoltar = UITheme.createSecondaryButton("🔙 Voltar");

            // Fonte para emojis
            Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
            btnCadastrar.setFont(emojiFont);
            btnEditar.setFont(emojiFont);
            btnRemover.setFont(emojiFont);
            btnLimpar.setFont(emojiFont);
            btnVoltar.setFont(emojiFont);

            // Tabela
            String[] colunas = {"ID", "Nome", "BI", "NUIT", "Telefone", "Endereço", "Email"};
            modeloTabela = new DefaultTableModel(colunas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tabelaClientes = new JTable(modeloTabela);
            tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tabelaClientes.setFont(UITheme.FONT_BODY);
            tabelaClientes.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
            tabelaClientes.setRowHeight(30);
            tabelaClientes.setSelectionBackground(UITheme.PRIMARY_LIGHT);
            tabelaClientes.setSelectionForeground(UITheme.TEXT_PRIMARY);
        }

        private void setupLayout() {
            // Painel superior com título
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
            topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
            topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

            JLabel lblTitulo = UITheme.createHeadingLabel("👤 Gestão de Clientes");
            lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
            lblTitulo.setForeground(UITheme.TEXT_WHITE);
            lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            topPanel.add(lblTitulo, BorderLayout.CENTER);

            JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
            voltarPanel.add(btnVoltar);
            topPanel.add(voltarPanel, BorderLayout.WEST);

            add(topPanel, BorderLayout.NORTH);

            // Painel principal com BoxLayout vertical
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(UITheme.BACKGROUND_COLOR);

            // Painel de formulário
            JPanel formPanel = criarPainelFormulario();
            mainPanel.add(formPanel);

            // Painel de botões de ação
            JPanel buttonPanel = criarPainelBotoes();
            buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(buttonPanel);

            // Painel de tabela
            JPanel tablePanel = criarPainelTabela();
            mainPanel.add(tablePanel);

            add(mainPanel, BorderLayout.CENTER);
        }

        private JPanel criarPainelFormulario() {
            JPanel panel = UITheme.createCardPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel lblFormTitulo = UITheme.createSubtitleLabel("📋 Dados do Cliente");
            lblFormTitulo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            lblFormTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            panel.add(lblFormTitulo, BorderLayout.NORTH);

            JPanel formContent = new JPanel(new GridBagLayout());
            formContent.setBackground(UITheme.CARD_BACKGROUND);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.WEST;

            // Primeira linha
            gbc.gridx = 0; gbc.gridy = 0;
            formContent.add(UITheme.createBodyLabel("ID:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
            formContent.add(txtID, gbc);

            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
            formContent.add(UITheme.createBodyLabel("Nome:"), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            formContent.add(txtNome, gbc);

            gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
            formContent.add(UITheme.createBodyLabel("Nr. BI:"), gbc);
            gbc.gridx = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
            formContent.add(txtNrBI, gbc);

            // Segunda linha
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
            formContent.add(UITheme.createBodyLabel("NUIT:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
            formContent.add(txtNuit, gbc);

            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
            formContent.add(UITheme.createBodyLabel("Telefone:"), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
            formContent.add(txtTelefone, gbc);

            gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
            formContent.add(UITheme.createBodyLabel("Email:"), gbc);
            gbc.gridx = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            formContent.add(txtEmail, gbc);

            // Terceira linha
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
            formContent.add(UITheme.createBodyLabel("Endereço:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
            formContent.add(txtEndereco, gbc);
            panel.add(formContent, BorderLayout.CENTER);
            return panel;
        }

        private JPanel criarPainelBotoes() {
            JPanel panel = UITheme.createCardPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            btnCadastrar.setPreferredSize(new Dimension(140, 40));
            btnEditar.setPreferredSize(new Dimension(140, 40));
            btnRemover.setPreferredSize(new Dimension(140, 40));
            btnLimpar.setPreferredSize(new Dimension(140, 40));

            panel.add(btnCadastrar);
            panel.add(btnEditar);
            panel.add(btnRemover);
            panel.add(btnLimpar);

            return panel;
        }

        private JPanel criarPainelTabela() {
            JPanel panel = UITheme.createCardPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panel.setPreferredSize(new Dimension(0, 400));

            JLabel lblTabelaTitulo = UITheme.createSubtitleLabel("📊 Clientes Cadastrados");
            lblTabelaTitulo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            lblTabelaTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            panel.add(lblTabelaTitulo, BorderLayout.NORTH);

            JScrollPane scrollPane = new JScrollPane(tabelaClientes);
            scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
            scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(UITheme.CARD_BACKGROUND);
            JLabel lblInfo = UITheme.createBodyLabel("💡 Selecione um cliente na tabela para editar ou remover");
            lblInfo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            lblInfo.setForeground(UITheme.TEXT_SECONDARY);
            infoPanel.add(lblInfo);
            panel.add(infoPanel, BorderLayout.SOUTH);

            return panel;
        }

        private void setupEvents() {
            btnCadastrar.addActionListener(e -> cadastrarCliente());
            btnEditar.addActionListener(e -> editarCliente());
            btnRemover.addActionListener(e -> removerCliente());
            btnLimpar.addActionListener(e -> limparFormulario());
            btnVoltar.addActionListener(e -> voltarMenuPrincipal());

            tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    carregarClienteSelecionado();
                }
            });
        }


        /**
     * Cadastra um novo cliente.
     */
    private void cadastrarCliente() {
        try {
            Cliente cliente = criarClienteFromForm();
            if (cliente != null && controller.adicionarCliente(cliente)) {
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
                limparFormulario();
                carregarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar cliente. Verifique os dados.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
    
    /**
     * Edita o cliente selecionado.
     */
    private void editarCliente() {
        int selectedRow = tabelaClientes.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                Cliente clienteAntigo = controller.getClientes().get(selectedRow);
                Cliente clienteNovo = criarClienteFromForm();
                
                if (clienteNovo != null) {
                    clienteNovo.setId(clienteAntigo.getId()); // Manter o ID original
                    if (controller.atualizarCliente(clienteAntigo, clienteNovo)) {
                        JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
                        limparFormulario();
                        carregarClientes();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente.");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.");
        }
    }
    
    /**
     * Remove o cliente selecionado.
     */
    private void removerCliente() {
        int selectedRow = tabelaClientes.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja remover este cliente?", 
                "Confirmar Remoção", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                Cliente cliente = controller.getClientes().get(selectedRow);
                if (controller.removerCliente(cliente)) {
                    JOptionPane.showMessageDialog(this, "Cliente removido com sucesso!");
                    limparFormulario();
                    carregarClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao remover cliente.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para remover.");
        }
    }
    
    /**
     * Volta ao menu principal.
     */
    private void voltarMenuPrincipal() {
        controller.getCardLayoutManager().showPanel("MenuAdministrador");
    }
    
    /**
     * Cria um cliente a partir dos dados do formulário.
     * @return Cliente criado ou null se houver erro
     */
    private Cliente criarClienteFromForm() {
        try {
            String nome = txtNome.getText().trim();
            String nrBI = txtNrBI.getText().trim();
            String nuit = txtNuit.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String endereco = txtEndereco.getText().trim();
            String email = txtEmail.getText().trim();
            
            // Validações
            if (!Validador.validarCampoObrigatorio(nome)) {
                throw new IllegalArgumentException("Nome é obrigatório.");
            }
            if (!Validador.validarBI(nrBI)) {
                throw new IllegalArgumentException("Número de BI inválido. Formato: 12 dígitos + 1 letra maiúscula.");
            }
            if (!Validador.validarCampoObrigatorio(nuit)) {
                throw new IllegalArgumentException("NUIT é obrigatório.");
            }
            if (!Validador.validarTelefone(telefone)) {
                throw new IllegalArgumentException("Telefone inválido. Formato: +258 seguidos de 9 dígitos.");
            }
            if (!Validador.validarCampoObrigatorio(endereco)) {
                throw new IllegalArgumentException("Endereço é obrigatório.");
            }
            if (!Validador.validarCampoObrigatorio(email)) {
                throw new IllegalArgumentException("Email é obrigatório.");
            }
            
            return new Cliente(nome, nrBI, nuit, telefone, endereco, email);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro de validação: " + e.getMessage());
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar cliente: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Carrega os clientes na tabela.
     */
    private void carregarClientes() {
        modeloTabela.setRowCount(0);
        List<Cliente> clientes = controller.getClientes();
        
        for (Cliente cli : clientes) {
            Object[] row = {
                cli.getId(),
                cli.getNome(),
                cli.getNrBI(),
                cli.getNuit(),
                cli.getTelefone(),
                cli.getEndereco(),
                cli.getEmail()
            };
            modeloTabela.addRow(row);
        }
    }
    
    /**
     * Carrega os dados do cliente selecionado no formulário.
     */
    private void carregarClienteSelecionado() {
        int selectedRow = tabelaClientes.getSelectedRow();
        if (selectedRow >= 0) {
            Cliente cli = controller.getClientes().get(selectedRow);
            
            txtID.setText(cli.getId());
            txtNome.setText(cli.getNome());
            txtNrBI.setText(cli.getNrBI());
            txtNuit.setText(cli.getNuit());
            txtTelefone.setText(cli.getTelefone());
            txtEndereco.setText(cli.getEndereco());
            txtEmail.setText(cli.getEmail());
        }
    }
    
    /**
     * Limpa o formulário.
     */
    private void limparFormulario() {
        txtID.setText("");
        txtNome.setText("");
        txtNrBI.setText("");
        txtNuit.setText("");
        txtTelefone.setText("");
        txtEndereco.setText("");
        txtEmail.setText("");
        tabelaClientes.clearSelection();
    }
}


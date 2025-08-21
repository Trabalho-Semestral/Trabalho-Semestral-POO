package view;

import controller.SistemaController;
import model.concretas.Vendedor;
import util.Validador;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Tela para gestão de vendedores.
 */
public class GerirVendedoresView extends JPanel {
    
    private SistemaController controller;
    
    // Componentes da interface
    private JTextField txtID;
    private JTextField txtNome;
    private JTextField txtNrBI;
    private JTextField txtNuit;
    private JTextField txtTelefone;
    private JTextField txtSalario;
    private JPasswordField txtSenha;
    
    // Tabela de vendedores
    private JTable tabelaVendedores;
    private DefaultTableModel modeloTabela;
    
    // Botões
    private JButton btnCadastrar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnVoltar;
    
    public GerirVendedoresView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
        carregarVendedores();
    }
    
    /**
     * Inicializa os componentes da interface.
     */
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
        txtSalario = UITheme.createStyledTextField();
        txtSenha = UITheme.createStyledPasswordField();
        
        // Botões com tema personalizado
        btnCadastrar = UITheme.createSuccessButton("➕ Cadastrar");
        btnEditar = UITheme.createPrimaryButton("✏️ Editar");
        btnRemover = UITheme.createDangerButton("🗑️ Remover");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        
        // Tabela
        String[] colunas = {"ID", "Nome", "BI", "NUIT", "Telefone", "Salário"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaVendedores = new JTable(modeloTabela);
        tabelaVendedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendedores.setFont(UITheme.FONT_BODY);
        tabelaVendedores.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaVendedores.setRowHeight(30);
        tabelaVendedores.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        tabelaVendedores.setSelectionForeground(UITheme.TEXT_PRIMARY);
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
        
        JLabel lblTitulo = UITheme.createHeadingLabel("👥 Gestão de Vendedores");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        topPanel.add(lblTitulo, BorderLayout.CENTER);
        
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        voltarPanel.add(btnVoltar);
        topPanel.add(voltarPanel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Painel central dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(UITheme.BACKGROUND_COLOR);
        
        // Painel esquerdo - Formulário
        JPanel leftPanel = criarPainelFormulario();
        splitPane.setLeftComponent(leftPanel);
        
        // Painel direito - Tabela
        JPanel rightPanel = criarPainelTabela();
        splitPane.setRightComponent(rightPanel);
        
        splitPane.setDividerLocation(450);
        splitPane.setDividerSize(2);
        add(splitPane, BorderLayout.CENTER);
    }
    
    /**
     * Cria o painel do formulário.
     * @return JPanel configurado
     */
    private JPanel criarPainelFormulario() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        // Título do formulário
        JLabel lblFormTitulo = UITheme.createSubtitleLabel("Dados do Vendedor");
        lblFormTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(lblFormTitulo, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UITheme.createBodyLabel("ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtID, gbc);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UITheme.createBodyLabel("Nome:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNome, gbc);
        
        // BI
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UITheme.createBodyLabel("Nr. BI:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNrBI, gbc);
        
        // NUIT
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(UITheme.createBodyLabel("NUIT:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNuit, gbc);
        
        // Telefone
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(UITheme.createBodyLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefone, gbc);
        
        // Salário
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(UITheme.createBodyLabel("Salário (MT):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtSalario, gbc);
        
        // Senha
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(UITheme.createBodyLabel("Senha:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtSenha, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        buttonPanel.setBackground(UITheme.CARD_BACKGROUND);
        buttonPanel.add(btnCadastrar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnRemover);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Cria o painel da tabela.
     * @return JPanel configurado
     */
    private JPanel criarPainelTabela() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        // Título da tabela
        JLabel lblTabelaTitulo = UITheme.createSubtitleLabel("Vendedores Cadastrados");
        lblTabelaTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTabelaTitulo, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(tabelaVendedores);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de informações
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(UITheme.CARD_BACKGROUND);
        JLabel lblInfo = UITheme.createBodyLabel("💡 Selecione um vendedor na tabela para editar ou remover");
        lblInfo.setForeground(UITheme.TEXT_SECONDARY);
        infoPanel.add(lblInfo);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Configura os eventos da interface.
     */
    private void setupEvents() {
        // Botão cadastrar
        btnCadastrar.addActionListener(e -> cadastrarVendedor());
        
        // Botão editar
        btnEditar.addActionListener(e -> editarVendedor());
        
        // Botão remover
        btnRemover.addActionListener(e -> removerVendedor());
        
        // Botão voltar
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
        
        // Seleção na tabela
        tabelaVendedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarVendedorSelecionado();
            }
        });
    }
    
    /**
     * Cadastra um novo vendedor.
     */
    private void cadastrarVendedor() {
        try {
            Vendedor vendedor = criarVendedorFromForm();
            if (vendedor != null && controller.adicionarVendedor(vendedor)) {
                JOptionPane.showMessageDialog(this, "Vendedor cadastrado com sucesso!");
                limparFormulario();
                carregarVendedores();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar vendedor. Verifique os dados.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
    
    /**
     * Edita o vendedor selecionado.
     */
    private void editarVendedor() {
        int selectedRow = tabelaVendedores.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                Vendedor vendedorAntigo = controller.getVendedores().get(selectedRow);
                Vendedor vendedorNovo = criarVendedorFromForm();
                
                if (vendedorNovo != null) {
                    vendedorNovo.setId(vendedorAntigo.getId()); // Manter o ID original
                    if (controller.atualizarVendedor(vendedorAntigo, vendedorNovo)) {
                        JOptionPane.showMessageDialog(this, "Vendedor atualizado com sucesso!");
                        limparFormulario();
                        carregarVendedores();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao atualizar vendedor.");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um vendedor para editar.");
        }
    }
    
    /**
     * Remove o vendedor selecionado.
     */
    private void removerVendedor() {
        int selectedRow = tabelaVendedores.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja remover este vendedor?", 
                "Confirmar Remoção", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                Vendedor vendedor = controller.getVendedores().get(selectedRow);
                if (controller.removerVendedor(vendedor)) {
                    JOptionPane.showMessageDialog(this, "Vendedor removido com sucesso!");
                    limparFormulario();
                    carregarVendedores();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao remover vendedor.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um vendedor para remover.");
        }
    }
    
    /**
     * Volta ao menu principal.
     */
    private void voltarMenuPrincipal() {
        controller.getCardLayoutManager().showPanel("MenuAdministrador");
    }
    
    /**
     * Cria um vendedor a partir dos dados do formulário.
     * @return Vendedor criado ou null se houver erro
     */
    private Vendedor criarVendedorFromForm() {
        try {
            String nome = txtNome.getText().trim();
            String nrBI = txtNrBI.getText().trim();
            String nuit = txtNuit.getText().trim();
            String telefone = txtTelefone.getText().trim();
            double salario = Double.parseDouble(txtSalario.getText().trim());
            String senha = new String(txtSenha.getPassword());
            
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
            if (!Validador.validarValorPositivo(salario)) {
                throw new IllegalArgumentException("Salário deve ser um valor positivo.");
            }
            if (!Validador.validarCampoObrigatorio(senha)) {
                throw new IllegalArgumentException("Senha é obrigatória.");
            }
            
            return new Vendedor(nome, nrBI, nuit, telefone, salario, senha);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro nos dados numéricos. Verifique o salário.");
            return null;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro de validação: " + e.getMessage());
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar vendedor: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Carrega os vendedores na tabela.
     */
    private void carregarVendedores() {
        modeloTabela.setRowCount(0);
        List<Vendedor> vendedores = controller.getVendedores();
        
        for (Vendedor vend : vendedores) {
            Object[] row = {
                vend.getId(),
                vend.getNome(),
                vend.getNrBI(),
                vend.getNuit(),
                vend.getTelefone(),
                String.format("%.2f MT", vend.getSalario())
            };
            modeloTabela.addRow(row);
        }
    }
    
    /**
     * Carrega os dados do vendedor selecionado no formulário.
     */
    private void carregarVendedorSelecionado() {
        int selectedRow = tabelaVendedores.getSelectedRow();
        if (selectedRow >= 0) {
            Vendedor vend = controller.getVendedores().get(selectedRow);
            
            txtID.setText(vend.getId());
            txtNome.setText(vend.getNome());
            txtNrBI.setText(vend.getNrBI());
            txtNuit.setText(vend.getNuit());
            txtTelefone.setText(vend.getTelefone());
            txtSalario.setText(String.valueOf(vend.getSalario()));
            txtSenha.setText(vend.getSenha()); // Cuidado: em um sistema real, não se deve carregar a senha
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
        txtSalario.setText("");
        txtSenha.setText("");
        tabelaVendedores.clearSelection();
    }
}


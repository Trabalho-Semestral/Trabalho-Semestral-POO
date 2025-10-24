package view;

import controller.SistemaController;
import model.concretas.Administrador;
import model.concretas.Gestor;
import model.concretas.Vendedor;
import util.BCryptHasher;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;

public class AtualizarPerfilView extends JFrame {

    private SistemaController controller;
    private Object usuario;
    private boolean modoObrigatorio;
    private JTextField txtIdLogin; // NOVO CAMPO
    private JTextField txtNome;
    private JTextField txtTelefone;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;
    private JButton btnSalvar;
    private JButton btnCancelar;
    private JLabel lblMensagem;
    private JLabel lblInfoObrigatorio;

    public AtualizarPerfilView(SistemaController controller, Object usuario, boolean modoObrigatorio) {
        this.controller = controller;
        this.usuario = usuario;
        this.modoObrigatorio = modoObrigatorio;
        initComponents();
        setupLayout();
        setupEvents();
        preencherCampos();
        configurarModoObrigatorio();
    }

    public AtualizarPerfilView(SistemaController controller, Object usuario) {
        this(controller, usuario, false);
    }

    private void initComponents() {
        setTitle(modoObrigatorio ? "Primeiro Acesso - Atualizar Perfil" : "Atualizar Perfil");
        setIconImage(new ImageIcon("C:\\Users\\administrator\\Desktop\\Nova pasta\\Trabalho\\resources\\007.jpeg").getImage());

        setDefaultCloseOperation(modoObrigatorio ? JFrame.DO_NOTHING_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500); // Aumentei um pouco para caber o novo campo
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BACKGROUND_COLOR);

        // NOVO CAMPO - ID de Login
        txtIdLogin = UITheme.createStyledTextField();
        txtIdLogin.setPreferredSize(new Dimension(250, 35));

        txtNome = UITheme.createStyledTextField();
        txtNome.setPreferredSize(new Dimension(250, 35));
        txtTelefone = UITheme.createStyledTextField();
        txtTelefone.setPreferredSize(new Dimension(250, 35));
        txtSenha = UITheme.createStyledPasswordField();
        txtSenha.setPreferredSize(new Dimension(250, 35));
        txtConfirmarSenha = UITheme.createStyledPasswordField();
        txtConfirmarSenha.setPreferredSize(new Dimension(250, 35));

        btnSalvar = UITheme.createPrimaryButton("Salvar Alterações");
        btnCancelar = UITheme.createSecondaryButton("Cancelar");

        lblMensagem = new JLabel(" ");
        lblMensagem.setFont(UITheme.FONT_SMALL);
        lblMensagem.setForeground(UITheme.ACCENT_COLOR);
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);

        lblInfoObrigatorio = new JLabel("⚠️ Primeiro acesso: Você deve atualizar seus dados para continuar");
        lblInfoObrigatorio.setFont(UITheme.FONT_SMALL);
        lblInfoObrigatorio.setForeground(UITheme.ACCENT_COLOR);
        lblInfoObrigatorio.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfoObrigatorio.setVisible(modoObrigatorio);
    }

    private void configurarModoObrigatorio() {
        if (modoObrigatorio) {
            btnCancelar.setText("Sair do Sistema");
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = UITheme.createTitleLabel(modoObrigatorio ? "🔐 Primeiro Acesso" : "🧑‍💼 Atualizar Perfil");
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblTitulo, gbc);

        // Informação do modo obrigatório
        if (modoObrigatorio) {
            gbc.gridy = 1;
            formPanel.add(lblInfoObrigatorio, gbc);
        }

        // NOVO CAMPO - ID de Login
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UITheme.createBodyLabel("ID de Login*:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtIdLogin, gbc);

        // Nome
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(UITheme.createBodyLabel("Nome*:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtNome, gbc);

        // Telefone
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(UITheme.createBodyLabel("Telefone*:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtTelefone, gbc);

        // Senha
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(UITheme.createBodyLabel("Nova Senha*:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtSenha, gbc);

        // Confirmar Senha
        gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(UITheme.createBodyLabel("Confirmar Senha*:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtConfirmarSenha, gbc);

        // Mensagem
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(lblMensagem, gbc);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        btnSalvar.addActionListener(e -> salvarAlteracoes());

        if (modoObrigatorio) {
            btnCancelar.addActionListener(e -> {
                controller.logout();
                System.exit(0);
            });
        } else {
            btnCancelar.addActionListener(e -> dispose());
        }

        // Validação em tempo real
        KeyAdapter validarCampos = new KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validarCampo(txtIdLogin);
                validarCampo(txtNome);
                validarCampo(txtTelefone);
                validarCampo(txtSenha);
                validarCampo(txtConfirmarSenha);
                lblMensagem.setText(" ");
            }
        };
        txtIdLogin.addKeyListener(validarCampos);
        txtNome.addKeyListener(validarCampos);
        txtTelefone.addKeyListener(validarCampos);
        txtSenha.addKeyListener(validarCampos);
        txtConfirmarSenha.addKeyListener(validarCampos);

        // Enter para salvar
        txtConfirmarSenha.addActionListener(e -> salvarAlteracoes());
    }

    private void preencherCampos() {
        if (usuario instanceof Administrador admin) {
            txtIdLogin.setText(admin.getId()); // Preenche com ID atual
            txtNome.setText(admin.getNome());
            txtTelefone.setText(admin.getTelefone());
        } else if (usuario instanceof Gestor gestor) {
            txtIdLogin.setText(gestor.getId()); // Preenche com ID atual
            txtNome.setText(gestor.getNome());
            txtTelefone.setText(gestor.getTelefone());
        } else if (usuario instanceof Vendedor vendedor) {
            txtIdLogin.setText(vendedor.getId()); // Preenche com ID atual
            txtNome.setText(vendedor.getNome());
            txtTelefone.setText(vendedor.getTelefone());
        }
    }

    private void validarCampo(JTextField campo) {
        if (campo instanceof JPasswordField) {
            if (((JPasswordField) campo).getPassword().length == 0) {
                campo.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else {
                campo.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            }
        } else {
            if (campo.getText().trim().isEmpty()) {
                campo.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else {
                campo.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            }
        }
    }

    private void salvarAlteracoes() {
        try {
            // Validações
            String novoId = txtIdLogin.getText().trim();
            if (novoId.isEmpty()) {
                lblMensagem.setText("O ID de login não pode estar vazio!");
                lblMensagem.setForeground(UITheme.ACCENT_COLOR);
                txtIdLogin.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                return;
            }

            // Verificar se o ID já existe (apenas se foi alterado)
            String idAtual = getIdAtual();
            if (!novoId.equals(idAtual) && idJaExiste(novoId)) {
                lblMensagem.setText("Este ID de login já está em uso!");
                lblMensagem.setForeground(UITheme.ACCENT_COLOR);
                txtIdLogin.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                return;
            }

            if (txtNome.getText().trim().isEmpty()) {
                lblMensagem.setText("O nome não pode estar vazio!");
                lblMensagem.setForeground(UITheme.ACCENT_COLOR);
                txtNome.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                return;
            }
            if (txtTelefone.getText().trim().isEmpty()) {
                lblMensagem.setText("O telefone não pode estar vazio!");
                lblMensagem.setForeground(UITheme.ACCENT_COLOR);
                txtTelefone.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                return;
            }

            String senha = new String(txtSenha.getPassword());
            String confirmarSenha = new String(txtConfirmarSenha.getPassword());

            if (modoObrigatorio && senha.isEmpty()) {
                lblMensagem.setText("É necessário definir uma senha no primeiro acesso!");
                lblMensagem.setForeground(UITheme.ACCENT_COLOR);
                txtSenha.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                return;
            }

            if (!senha.isEmpty() && !senha.equals(confirmarSenha)) {
                lblMensagem.setText("As senhas não coincidem!");
                lblMensagem.setForeground(UITheme.ACCENT_COLOR);
                txtSenha.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                txtConfirmarSenha.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                return;
            }

            // Atualiza os dados do usuário
            boolean atualizado = false;
            if (usuario instanceof Administrador admin) {
                // Remove o usuário antigo se o ID mudou
                if (!novoId.equals(admin.getId())) {
                    controller.getAdministradorRepo().removeById(admin.getId());
                }

                admin.setId(novoId);
                admin.setNome(txtNome.getText().trim());
                admin.setTelefone(txtTelefone.getText().trim());
                if (!senha.isEmpty()) {
                    admin.setSenha(BCryptHasher.hashPassword(senha));
                }
                admin.setPrimeiroLogin(false);
                controller.getAdministradorRepo().upsert(admin);
                atualizado = true;

            } else if (usuario instanceof Gestor gestor) {
                // Remove o usuário antigo se o ID mudou
                if (!novoId.equals(gestor.getId())) {
                    controller.getGestorRepo().removeById(gestor.getId());
                }

                gestor.setId(novoId);
                gestor.setNome(txtNome.getText().trim());
                gestor.setTelefone(txtTelefone.getText().trim());
                if (!senha.isEmpty()) {
                    gestor.setSenha(BCryptHasher.hashPassword(senha));
                }
                gestor.setPrimeiroLogin(false);
                controller.getGestorRepo().upsert(gestor);
                atualizado = true;

            } else if (usuario instanceof Vendedor vendedor) {
                // Remove o usuário antigo se o ID mudou
                if (!novoId.equals(vendedor.getId())) {
                    controller.getVendedorRepo().removeById(vendedor.getId());
                }

                vendedor.setId(novoId);
                vendedor.setNome(txtNome.getText().trim());
                vendedor.setTelefone(txtTelefone.getText().trim());
                if (!senha.isEmpty()) {
                    vendedor.setSenha(BCryptHasher.hashPassword(senha));
                }
                vendedor.setPrimeiroLogin(false);
                controller.getVendedorRepo().upsert(vendedor);
                atualizado = true;
            }

            if (atualizado) {
                lblMensagem.setText("Perfil atualizado com sucesso!");
                lblMensagem.setForeground(UITheme.SUCCESS_COLOR);

                SwingUtilities.invokeLater(() -> {
                    dispose();
                });
            } else {
                lblMensagem.setText("Erro ao atualizar perfil.");
                lblMensagem.setForeground(UITheme.ACCENT_COLOR);
            }
        } catch (Exception e) {
            lblMensagem.setText("Erro ao atualizar perfil: " + e.getMessage());
            lblMensagem.setForeground(UITheme.ACCENT_COLOR);
            e.printStackTrace();
        }
    }

    private String getIdAtual() {
        if (usuario instanceof Administrador admin) {
            return admin.getId();
        } else if (usuario instanceof Gestor gestor) {
            return gestor.getId();
        } else if (usuario instanceof Vendedor vendedor) {
            return vendedor.getId();
        }
        return "";
    }

    private boolean idJaExiste(String novoId) {
        // Verifica se o ID já existe em algum dos repositórios
        return controller.getAdministradorRepo().findById(novoId).isPresent() ||
                controller.getGestorRepo().findById(novoId).isPresent() ||
                controller.getVendedorRepo().findById(novoId).isPresent();
    }
}
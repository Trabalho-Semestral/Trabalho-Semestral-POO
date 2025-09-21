
package view;

import controller.SistemaController;
import model.concretas.Vendedor;
import model.concretas.Gestor;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tela de login do sistema.
 */
public class LoginView extends JFrame {

    private SistemaController controller;

    // Componentes da interface
    private JTextField txtUsuarioId;
    private JButton btnLogin;
    private JLabel lblMensagem;
    private JPasswordField txtSenha = new JPasswordField(15);
    private JCheckBox chkMostrarSenha = new JCheckBox("👁");


    public LoginView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
    }

    private void initComponents() {

        setTitle("💻 Sistema de Venda de Equipamentos Informáticos");
        setIconImage(new ImageIcon("C:\\Users\\Nelson Wilson\\IdeaProjects\\Gestao Equipamentos\\Trabalho\\resources\\TECHNAE.jpg").getImage());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(870, 500);
        setLocationRelativeTo(null);
        setResizable(true);

        getContentPane().setBackground(UITheme.BACKGROUND_COLOR);

        txtUsuarioId = UITheme.createStyledTextField();
        txtUsuarioId.setPreferredSize(new Dimension(250, 35));
        txtUsuarioId.setMinimumSize(new Dimension(200, 30));

        txtSenha = UITheme.createStyledPasswordField();
        txtSenha.setPreferredSize(new Dimension(250, 35));
        txtSenha.setMinimumSize(new Dimension(200, 30));

        btnLogin = UITheme.createPrimaryButton("Entrar");

        lblMensagem = new JLabel(" ");
        lblMensagem.setFont(UITheme.FONT_SMALL);
        lblMensagem.setForeground(UITheme.ACCENT_COLOR);
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        // Layout principal
        setLayout(new BorderLayout());

        // Painel do logotipo
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(UITheme.BACKGROUND_COLOR);

        ImageIcon logoIcon = new ImageIcon("C:\\Users\\Nelson Wilson\\IdeaProjects\\Gestao Equipamentos\\Trabalho\\resources\\TECHNAE.jpg");
        int logoWidth = getWidth() / 2;
        int logoHeight = getHeight();
        Image logoImage = logoIcon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(logoImage);

        JLabel lblLogo = new JLabel(logoIcon);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setVerticalAlignment(SwingConstants.CENTER);
        logoPanel.add(lblLogo, BorderLayout.CENTER);

        // Painel do formulário
        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Título
        JLabel lblTitulo = UITheme.createTitleLabel("💻 Sistema de Vendas");
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 10, 10);
        formPanel.add(lblTitulo, gbc);

        // Subtítulo
        JLabel lblSubtitulo = UITheme.createBodyLabel("Equipamentos Informáticos");
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitulo.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridy = 1;
        formPanel.add(lblSubtitulo, gbc);

        // Usuário
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblUsuario = UITheme.createBodyLabel("Usuário");
        lblUsuario.setFont(new Font("Calibri", Font.BOLD, 20));
        formPanel.add(lblUsuario, gbc);

        gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.gridx = 1;
        formPanel.add(txtUsuarioId, gbc);

        gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblSenha = UITheme.createBodyLabel("Senha");
        lblSenha.setFont(new Font("Calibri", Font.BOLD, 20));
        formPanel.add(lblSenha, gbc);
        JPanel senhaPanel = new JPanel(new BorderLayout());
        senhaPanel.setOpaque(false);
        senhaPanel.setPreferredSize(new Dimension(250, 35));
        senhaPanel.setMinimumSize(new Dimension(250, 35));

        txtSenha.setPreferredSize(new Dimension(250, 35));
        txtSenha.setMinimumSize(new Dimension(250, 35));
        senhaPanel.add(txtSenha, BorderLayout.CENTER);

        JLabel lblVisualizar = new JLabel("👁");
        lblVisualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblVisualizar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblVisualizar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        senhaPanel.add(lblVisualizar, BorderLayout.EAST);

        // Ação do olho
        lblVisualizar.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean visivel = false;
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                visivel = !visivel;
                if (visivel) {
                    txtSenha.setEchoChar((char) 0);
                } else {
                    txtSenha.setEchoChar('•');
                }
            }
        });

        gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.gridx = 1;
        formPanel.add(senhaPanel, gbc);

        // Botão
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.insets = new Insets(30, 10, 10, 10);
        formPanel.add(btnLogin, gbc);
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.gridy = 6;
        formPanel.add(lblMensagem, gbc);
        add(logoPanel, BorderLayout.WEST);
        add(formPanel, BorderLayout.CENTER);

        // Rodapé
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        JLabel lblCopyright = new JLabel("© 2025    SISTEMA DE VENDA DE EQUIPAMENTOS");
        lblCopyright.setFont(UITheme.FONT_SMALL);
        lblCopyright.setForeground(UITheme.TEXT_WHITE);
        footerPanel.add(lblCopyright);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        btnLogin.addActionListener(e -> realizarLogin());

        txtSenha.addActionListener(e -> realizarLogin());

        txtUsuarioId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lblMensagem.setText(" ");
            }
        });

        txtSenha.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lblMensagem.setText(" ");
            }
        });
    }
    private void realizarLogin() {
        String usuarioId = txtUsuarioId.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (usuarioId.isEmpty()) {
            lblMensagem.setText("Por favor, digite o ID do utilizador.");
            return;
        }
        if (senha.isEmpty()) {
            lblMensagem.setText("Por favor, digite a senha.");
            return;
        }

        String tipoUsuario = controller.autenticarUsuario(usuarioId, senha);

        if (tipoUsuario != null) {
            lblMensagem.setText("Login realizado com sucesso!");
            lblMensagem.setForeground(UITheme.SUCCESS_COLOR);
            SwingUtilities.invokeLater(() -> abrirTelaPrincipal(tipoUsuario));
        } else {
            lblMensagem.setText("Credenciais inválidas. Tente novamente.");
            lblMensagem.setForeground(UITheme.ACCENT_COLOR);
        }
    }



    private void abrirTelaPrincipal(String tipoUsuario) {
        try {
            MainView mainView = new MainView(controller, tipoUsuario);
            controller.setCardLayoutManager(mainView.getCardLayoutManager());
            switch (tipoUsuario) {
                case "Administrador":
                    break;
                case "Gestor":
                    MenuGestorView gestorView = new MenuGestorView(controller, (Gestor) controller.getUsuarioLogado());
                    mainView.getCardLayoutManager().addPanel(gestorView, "MenuGestor");
                    mainView.getCardLayoutManager().showPanel("MenuGestor");
                    break;
                case "Vendedor":
                    MenuVendedorView vendedorView = new MenuVendedorView(controller, (Vendedor) controller.getUsuarioLogado());
                    mainView.getCardLayoutManager().addPanel(vendedorView, "MenuVendedor");
                    mainView.getCardLayoutManager().showPanel("MenuVendedor");
                    break;
            }
            mainView.setVisible(true);
            this.dispose();

        } catch (Exception e) {
            lblMensagem.setText("Erro ao abrir a tela principal: " + e.getMessage());
            lblMensagem.setForeground(UITheme.ACCENT_COLOR);
            e.printStackTrace();
        }
    }
}

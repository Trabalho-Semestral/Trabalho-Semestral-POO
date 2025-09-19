
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
    private JPasswordField txtSenha;
    private JComboBox<String> cmbTipoUsuario;
    private JButton btnLogin;
    private JLabel lblMensagem;

    public LoginView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
    }

    private void initComponents() {
        setTitle("Sistema de Venda de Equipamentos Informáticos - Login");
        setIconImage(new ImageIcon("C:\\Users\\TECNOCONTROL\\Desktop\\TECHNAE.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(true);

        getContentPane().setBackground(UITheme.BACKGROUND_COLOR);

        // Campos com tema e tamanho ajustado
        txtUsuarioId = UITheme.createStyledTextField();
        txtUsuarioId.setPreferredSize(new Dimension(250, 35));
        txtUsuarioId.setMinimumSize(new Dimension(200, 30));

        txtSenha = UITheme.createStyledPasswordField();
        txtSenha.setPreferredSize(new Dimension(250, 35));
        txtSenha.setMinimumSize(new Dimension(200, 30));

        cmbTipoUsuario = UITheme.createStyledComboBox(new String[]{"Administrador", "Gestor", "Vendedor"});
        cmbTipoUsuario.setPreferredSize(new Dimension(250, 35));
        cmbTipoUsuario.setMinimumSize(new Dimension(200, 30));

        btnLogin = UITheme.createPrimaryButton("Entrar");

        lblMensagem = new JLabel(" ");
        lblMensagem.setFont(UITheme.FONT_SMALL);
        lblMensagem.setForeground(UITheme.ACCENT_COLOR);
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = UITheme.createCardPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Logo
        //JLabel lblLogo = new JLabel(""C:\Users\TECNOCONTROL\Desktop\TECHNAE.png"");
        JLabel lblLogo = new JLabel(new ImageIcon("C:/Users/TECNOCONTROL/Desktop/TECHNAE.png"));
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(lblLogo, gbc);

        // Título
        JLabel lblTitulo = UITheme.createTitleLabel("Sistema de Vendas de Equipamentos Informáticos - Login");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 5, 10);
        mainPanel.add(lblTitulo, gbc);

        // Subtítulo
        JLabel lblSubtitulo = UITheme.createBodyLabel("🛠️ Equipamentos Informáticos");
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitulo.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 30, 10);
        mainPanel.add(lblSubtitulo, gbc);

        // ID do Utilizador
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblUsuario = UITheme.createBodyLabel("ID do Utilizador:");
        mainPanel.add(lblUsuario, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 1; gbc.gridy = 3;
        mainPanel.add(txtUsuarioId, gbc);

        // Senha
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblSenha = UITheme.createBodyLabel("Senha:");
        mainPanel.add(lblSenha, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 1; gbc.gridy = 4;
        mainPanel.add(txtSenha, gbc);

        // Tipo de Utilizador
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblTipo = UITheme.createBodyLabel("Tipo de Utilizador:");
        mainPanel.add(lblTipo, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 1; gbc.gridy = 5;
        mainPanel.add(cmbTipoUsuario, gbc);

        // Botão Login
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(30, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        mainPanel.add(btnLogin, gbc);

        // Mensagem
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        mainPanel.add(lblMensagem, gbc);

        // Centralizar
        JPanel containerPanel = new JPanel(new GridBagLayout());
        containerPanel.setBackground(UITheme.BACKGROUND_COLOR);
        containerPanel.add(mainPanel);

        add(containerPanel, BorderLayout.CENTER);

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
        String tipoUsuario = (String) cmbTipoUsuario.getSelectedItem();

        if (usuarioId.isEmpty()) {
            lblMensagem.setText("Por favor, digite o ID do utilizador.");
            return;
        }
        if (senha.isEmpty()) {
            lblMensagem.setText("Por favor, digite a senha.");
            return;
        }
        if (tipoUsuario == null) {
            lblMensagem.setText("Por favor, selecione o tipo de utilizador.");
            return;
        }

        if (controller.autenticarUsuario(usuarioId, senha, tipoUsuario)) {
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
            // 1. Crie uma única instância da MainView para todos os utilizadores.
            MainView mainView = new MainView(controller, tipoUsuario);

            // 2. Defina o CardLayoutManager no controlador. Este passo é essencial!
            controller.setCardLayoutManager(mainView.getCardLayoutManager());

            // 3. Adicione e mostre o painel de menu correto dentro da MainView.
            switch (tipoUsuario) {
                case "Administrador":
                    // A MainView provavelmente já lida com a exibição inicial para o Administrador,
                    // então apenas garantir que ela seja visível é suficiente.
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

            // 4. Torne a MainView visível e feche a janela de login.
            mainView.setVisible(true);
            this.dispose();

        } catch (Exception e) {
            lblMensagem.setText("Erro ao abrir a tela principal: " + e.getMessage());
            lblMensagem.setForeground(UITheme.ACCENT_COLOR);
            e.printStackTrace();
        }
    }
}

package view;

import controller.SistemaController;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Window;

public class MenuAdministradorView extends JPanel {

    private SistemaController controller;
    private JPanel sidebarPanel;
    private JButton btnToggleSidebar;

    public MenuAdministradorView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        // Botão toggle sidebar
        btnToggleSidebar = new JButton("☰");
        btnToggleSidebar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        btnToggleSidebar.setFocusPainted(false);
        btnToggleSidebar.setBorderPainted(false);
        btnToggleSidebar.setContentAreaFilled(false);
        btnToggleSidebar.setForeground(UITheme.TEXT_WHITE);
        btnToggleSidebar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleSidebar.setPreferredSize(new Dimension(50, UITheme.TOPBAR_HEIGHT));

        // Sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(UITheme.SIDEBAR_BACKGROUND);
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebarPanel.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));

        JLabel lblMenuTitulo = UITheme.createSubtitleLabel("🛠️ PAINEL ADMIN");
        lblMenuTitulo.setForeground(UITheme.TEXT_WHITE);
        lblMenuTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMenuTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        sidebarPanel.add(lblMenuTitulo);

        // Botões do menu com emojis
        sidebarPanel.add(criarBotaoMenu("👥 Gerir Vendedores", "GerirVendedores"));
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(criarBotaoMenu("👤 Gerir Clientes", "GerirClientes"));
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(criarBotaoMenu("💻 Gerir Equipamentos", "GerirEquipamentos"));
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(criarBotaoMenu("📋 Gerir Reservas", "GerirReservas"));
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(criarBotaoMenu("🛒 Registrar Venda", "RegistrarVenda"));
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(criarBotaoMenu("📊 Relatórios de Vendas", "RelatoriosVendas"));

        sidebarPanel.add(Box.createVerticalGlue());

        JButton btnLogout = criarBotaoMenu("🚪 Sair", "Logout");
        btnLogout.setBackground(UITheme.ACCENT_COLOR);
        sidebarPanel.add(btnLogout);
    }

    private void setupLayout() {
        // TopBar
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBarPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        topBarPanel.add(btnToggleSidebar, BorderLayout.WEST);

        JLabel lblTitulo = UITheme.createHeadingLabel("💻 Sistema de Venda de Equipamentos Informáticos");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBarPanel.add(lblTitulo, BorderLayout.CENTER);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        JLabel lblUserInfo = UITheme.createBodyLabel("👤 Administrador");
        lblUserInfo.setForeground(UITheme.TEXT_WHITE);
        userInfoPanel.add(lblUserInfo);
        topBarPanel.add(userInfoPanel, BorderLayout.EAST);

        add(topBarPanel, BorderLayout.NORTH);

        // SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(sidebarPanel);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // Cards de boas-vindas com emojis
        JPanel welcomePanel = UITheme.createCardPanel();
        welcomePanel.setLayout(new BorderLayout());

        JLabel lblWelcome = UITheme.createTitleLabel("🎉 Bem-vindo ao Painel Administrativo");
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        welcomePanel.add(lblWelcome, BorderLayout.NORTH);

        JLabel lblDescription = UITheme.createBodyLabel(
                "<html><center>Utilize o menu lateral para navegar pelas funcionalidades do sistema.<br>" +
                        "Aqui você pode gerir 👥 vendedores, 👤 clientes, 💻 equipamentos e visualizar 📊 relatórios.</center></html>"
        );
        lblDescription.setHorizontalAlignment(SwingConstants.CENTER);
        lblDescription.setForeground(UITheme.TEXT_SECONDARY);
        welcomePanel.add(lblDescription, BorderLayout.CENTER);

        contentPanel.add(welcomePanel, BorderLayout.CENTER);
        splitPane.setRightComponent(contentPanel);

        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(UITheme.SIDEBAR_WIDTH);

        add(splitPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        JLabel lblCopyright =
                new JLabel("© 2025 Sistema de Venda de Equipamentos Informáticos");
        lblCopyright.setFont(UITheme.FONT_SMALL);
        lblCopyright.setForeground(UITheme.TEXT_MUTED);
        bottomPanel.add(lblCopyright);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UITheme.PRIMARY_COLOR));

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton criarBotaoMenu(String texto, String actionCommand) {
        JButton botao = new JButton(texto);
        botao.setActionCommand(actionCommand);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fonte emoji
        botao.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        final Color corBase = new Color(70, 130, 180);
        final Color corHover = new Color(19, 56, 94);

        if (actionCommand.equals("Logout")) {
            botao.setBackground(UITheme.ACCENT_COLOR);
            botao.setForeground(UITheme.TEXT_BLACK);
        } else {
            botao.setBackground(corBase);
            botao.setForeground(UITheme.TEXT_PRIMARY);
        }

        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setOpaque(true);

        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (actionCommand.equals("Logout")) {
                    botao.setBackground(UITheme.ACCENT_COLOR.brighter());
                } else {
                    botao.setBackground(corHover);
                    botao.setForeground(UITheme.TEXT_WHITE);
                }
            }

            public void mouseExited(MouseEvent evt) {
                if (actionCommand.equals("Logout")) {
                    botao.setBackground(UITheme.ACCENT_COLOR);
                    botao.setForeground(UITheme.TEXT_WHITE);
                } else {
                    botao.setBackground(corBase);
                    botao.setForeground(UITheme.TEXT_PRIMARY);
                }
            }
        });

        return botao;
    }

    private void setupEvents() {
        btnToggleSidebar.addActionListener(e -> toggleSidebar());

        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton btn) {
                btn.addActionListener(e -> {
                    switch (btn.getActionCommand()) {
                        case "GerirVendedores" -> abrirGerirVendedores();
                        case "GerirClientes" -> abrirGerirClientes();
                        case "GerirEquipamentos" -> abrirGerirEquipamentos();
                        case "GerirReservas" -> abrirGerirReservas();
                        case "RegistrarVenda" -> abrirRegistrarVenda();
                        case "RelatoriosVendas" -> abrirRelatoriosVendas();
                        case "Logout" -> {
                            controller.logout();
                            Window window = SwingUtilities.getWindowAncestor(MenuAdministradorView.this);
                            if (window instanceof JFrame frame) {
                                frame.dispose();
                                new LoginView(controller).setVisible(true);
                            }
                        }
                    }
                });
            }
        }
    }

    private void toggleSidebar() {
        JSplitPane splitPane = (JSplitPane) getComponent(1);
        sidebarPanel.setVisible(!sidebarPanel.isVisible());
        splitPane.setDividerLocation(sidebarPanel.isVisible() ? UITheme.SIDEBAR_WIDTH : 0);
    }

    private void abrirGerirVendedores() {
        try {
            GerirVendedoresView view = new GerirVendedoresView(controller);
            controller.getCardLayoutManager().addPanel(view, "GerirVendedores");
            controller.getCardLayoutManager().showPanel("GerirVendedores");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de gestão de vendedores: " + e.getMessage());
        }
    }

    private void abrirGerirClientes() {
        try {
            GerirClientesView view = new GerirClientesView(controller);
            controller.getCardLayoutManager().addPanel(view, "GerirClientes");
            controller.getCardLayoutManager().showPanel("GerirClientes");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de gestão de clientes: " + e.getMessage());
        }
    }

    private void abrirGerirEquipamentos() {
        try {
            GerirEquipamentosView view = new GerirEquipamentosView(controller);
            controller.getCardLayoutManager().addPanel(view, "GerirEquipamentos");
            controller.getCardLayoutManager().showPanel("GerirEquipamentos");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de gestão de equipamentos: " + e.getMessage());
        }
    }

    private void abrirGerirReservas() {
        try {
            GerirReservasView view = new GerirReservasView(controller);
            controller.getCardLayoutManager().addPanel(view, "GerirReservas");
            controller.getCardLayoutManager().showPanel("GerirReservas");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de gestão de reservas: " + e.getMessage());
        }
    }

    private void abrirRegistrarVenda() {
        try {
            RegistrarVendaView view = new RegistrarVendaView(controller, null);
            controller.getCardLayoutManager().addPanel(view, "RegistrarVenda");
            controller.getCardLayoutManager().showPanel("RegistrarVenda");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de registro de venda: " + e.getMessage());
        }
    }

    private void abrirRelatoriosVendas() {
        try {
            RelatoriosVendasView view = new RelatoriosVendasView(controller);
            controller.getCardLayoutManager().addPanel(view, "RelatoriosVendas");
            controller.getCardLayoutManager().showPanel("RelatoriosVendas");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de relatórios de vendas: " + e.getMessage());
        }
    }
}

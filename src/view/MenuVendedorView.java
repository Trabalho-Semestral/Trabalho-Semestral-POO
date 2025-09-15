package view;

import controller.SistemaController;
import model.concretas.Vendedor;
import util.UITheme;

import javax.swing.*;
import java.awt.*;

public class MenuVendedorView extends JPanel {

    private SistemaController controller;
    private Vendedor vendedorLogado;

    private JPanel sidebarPanel;
    private JPanel contentPanel; // Panel com CardLayout
    private CardLayout cardLayout;

    public MenuVendedorView(SistemaController controller, Vendedor vendedorLogado) {
        this.controller = controller;
        this.vendedorLogado = vendedorLogado;

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        // Sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(UITheme.SIDEBAR_BACKGROUND);
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebarPanel.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));

        JLabel lblMenuTitulo = UITheme.createSubtitleLabel("MENU VENDEDOR");
        lblMenuTitulo.setForeground(UITheme.TEXT_WHITE);
        lblMenuTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMenuTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        sidebarPanel.add(lblMenuTitulo);

        JButton btnRegistrarCliente = criarBotaoMenu("👤 Registrar Cliente");
        JButton btnRegistrarVenda = criarBotaoMenu("🛒 Registrar Venda");
        JButton btnMinhasVendas = criarBotaoMenu("📋 Minhas Vendas");
        JButton btnVerReservas = criarBotaoMenu("📦 Gerir Reservas");
        JButton btnLogout = criarBotaoMenu("🚪 Sair");
        btnLogout.setBackground(UITheme.ACCENT_COLOR);

        sidebarPanel.add(btnRegistrarCliente);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnRegistrarVenda);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnMinhasVendas);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnVerReservas);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);

        // Conteúdo central com CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // Cards
        JPanel welcomeCard = criarWelcomeCard();
        JPanel registrarClienteCard = new GerirClientesView(controller);
        JPanel registrarVendaCard = new RegistrarVendaView(controller, vendedorLogado);
        JPanel minhasVendasCard = new JPanel();
        JPanel verReservasCard = new GerirReservasView(controller);

        contentPanel.add(welcomeCard, "Welcome");
        contentPanel.add(registrarClienteCard, "RegistrarCliente");
        contentPanel.add(registrarVendaCard, "RegistrarVenda");
        contentPanel.add(minhasVendasCard, "MinhasVendas");
        contentPanel.add(verReservasCard, "GerirReservas");

        // Botão eventos
        btnRegistrarCliente.addActionListener(e -> cardLayout.show(contentPanel, "RegistrarCliente"));
        btnRegistrarVenda.addActionListener(e -> cardLayout.show(contentPanel, "RegistrarVenda"));
        btnMinhasVendas.addActionListener(e -> cardLayout.show(contentPanel, "MinhasVendas"));
        btnVerReservas.addActionListener(e -> cardLayout.show(contentPanel, "GerirReservas"));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente sair do sistema?",
                    "Confirmar Logout",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.logout();
                controller.getCardLayoutManager().showPanel("Login");
            }
        });
    }

    private JButton criarBotaoMenu(String texto) {
        JButton botao = new JButton(texto);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setBackground(new Color(70, 130, 180));
        botao.setForeground(UITheme.TEXT_PRIMARY);
        botao.setFont(UITheme.FONT_SUBHEADING);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        return botao;
    }

    private JPanel criarWelcomeCard() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());

        JLabel lblWelcome = UITheme.createTitleLabel("Bem-vindo ao Sistema de Vendas");
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panel.add(lblWelcome, BorderLayout.NORTH);

        JLabel lblDescription = UITheme.createBodyLabel(
                "<html><center>Utilize o menu lateral para registrar clientes, vendas, consultar suas vendas e gerir reservas.</center></html>"
        );
        lblDescription.setHorizontalAlignment(SwingConstants.CENTER);
        lblDescription.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(lblDescription, BorderLayout.CENTER);

        return panel;
    }

    private void setupLayout() {
        // Topbar
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBarPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        JLabel lblTitulo = UITheme.createHeadingLabel("Sistema de Venda de Equipamentos Informáticos");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBarPanel.add(lblTitulo, BorderLayout.CENTER);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        JLabel lblUserInfo = UITheme.createBodyLabel("👤 " + vendedorLogado.getNome());
        lblUserInfo.setForeground(UITheme.TEXT_WHITE);
        userInfoPanel.add(lblUserInfo);
        topBarPanel.add(userInfoPanel, BorderLayout.EAST);

        add(topBarPanel, BorderLayout.NORTH);

        // Conteúdo com sidebar e card central
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, contentPanel);
        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(UITheme.SIDEBAR_WIDTH);
        splitPane.setEnabled(false);
        add(splitPane, BorderLayout.CENTER);
    }

    private void toggleSidebar() {
        JSplitPane splitPane = (JSplitPane) getComponent(1);
        if (sidebarPanel.isVisible()) {
            sidebarPanel.setVisible(false);
            splitPane.setDividerLocation(0);
        } else {
            sidebarPanel.setVisible(true);
            splitPane.setDividerLocation(UITheme.SIDEBAR_WIDTH);
        }
    }

    // Métodos que abrem novas janelas
    private void abrirGerirClientes() {
        JFrame frame = new JFrame("Gerir Clientes");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new GerirClientesView(controller));
        frame.setVisible(true);
    }

    private void abrirRegistrarVenda() {
        JFrame frame = new JFrame("Registrar Venda");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new RegistrarVendaView(controller, vendedorLogado));
        frame.setVisible(true);
    }

    private void abrirMinhasVendas() {

    }

    private void abrirVerReservas() {
        JFrame frame = new JFrame("Gerir Reservas");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new GerirReservasView(controller));
        frame.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair do sistema?",
                "Confirmar Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.logout();
            controller.getCardLayoutManager().showPanel("Login");
        }
    }
}

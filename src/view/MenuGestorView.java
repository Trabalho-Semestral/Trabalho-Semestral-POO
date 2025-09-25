package view;

import controller.SistemaController;
import model.concretas.Gestor;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuGestorView extends JPanel {

    private final SistemaController controller;
    private final Gestor gestorLogado;
    private final JPanel sidebarPanel;
    private final JButton btnToggleSidebar;

    public MenuGestorView(SistemaController controller, Gestor gestorLogado) {
        this.controller = controller;
        this.gestorLogado = gestorLogado;
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        btnToggleSidebar = criarBotaoToggle();
        sidebarPanel = criarSidebar();

        setupLayout();
        setupEvents();
    }

    /** ----------------- COMPONENTES ----------------- */
    private JButton criarBotaoToggle() {
        JButton btn = new JButton("☰");
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(UITheme.TEXT_WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(50, UITheme.TOPBAR_HEIGHT));
        return btn;
    }

    private JPanel criarSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.SIDEBAR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        panel.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));

        JLabel lblTitulo = UITheme.createSubtitleLabel("👨‍💼 PAINEL GESTOR");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(lblTitulo);

        String[][] opcoes = {
                {"👥 Gerir Vendedores", "GerirVendedores"},
                {"👤 Gerir Clientes", "GerirClientes"},
                {"💻 Gerir Equipamentos", "GerirEquipamentos"},
                {"📦 Gerir Reservas", "GerirReservas"},
                {"🛒 Registrar Venda", "RegistrarVenda"},
                {"📊 Relatórios de Vendas", "RelatoriosVendas"},
                {"🚪 Sair", "Logout"}
        };

        for (String[] opc : opcoes) {
            panel.add(criarBotaoMenu(opc[0], opc[1]));
            panel.add(Box.createVerticalStrut(10));
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JButton criarBotaoMenu(String texto, String action) {
        JButton btn = new JButton(texto);
        btn.setActionCommand(action);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        Color corBase = action.equals("Logout") ? UITheme.ACCENT_COLOR : new Color(70, 130, 180);
        Color corHover = new Color(19, 56, 94);
        Color txtCor = action.equals("Logout") ? UITheme.TEXT_BLACK : UITheme.TEXT_PRIMARY;

        btn.setBackground(corBase);
        btn.setForeground(txtCor);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(action.equals("Logout") ? corBase.brighter() : corHover);
                btn.setForeground(UITheme.TEXT_WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(corBase);
                btn.setForeground(txtCor);
            }
        });
        return btn;
    }

    /** ----------------- LAYOUT ----------------- */
    private void setupLayout() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        topBar.add(btnToggleSidebar, BorderLayout.WEST);
        JLabel lblTitulo = UITheme.createHeadingLabel("💻 SISTEMA DE VENDAS DE EQUIPAMENTOS INFORMÁTICOS");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);

        JLabel lblUser = UITheme.createBodyLabel("👨‍💼 " + gestorLogado.getNome() + " (Gestor)");
        lblUser.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblUser.setForeground(UITheme.TEXT_WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        userPanel.add(lblUser);
        topBar.add(userPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, criarContentPanel());
        split.setDividerSize(0);
        split.setDividerLocation(UITheme.SIDEBAR_WIDTH);
        add(split, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(UITheme.TOPBAR_BACKGROUND);
        JLabel lblCopy = new JLabel("© 2025 Sistema de Venda de Equipamentos Informáticos - Painel Gestor");
        lblCopy.setFont(UITheme.FONT_SMALL);
        lblCopy.setForeground(UITheme.TEXT_MUTED);
        bottom.add(lblCopy);
        bottom.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UITheme.PRIMARY_COLOR));
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel criarContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_COLOR);

        JPanel welcome = UITheme.createCardPanel(new BorderLayout());
        JLabel lblWelcome = UITheme.createTitleLabel("🎉 Bem-vindo ao Painel do Gestor");
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        welcome.add(lblWelcome, BorderLayout.NORTH);

        JLabel lblDesc = UITheme.createBodyLabel("<html><center>Olá, " + gestorLogado.getNome() +
                "!<br>Use o menu lateral para gerir vendedores, clientes, equipamentos, reservas e relatórios.</center></html>");
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);
        lblDesc.setForeground(UITheme.TEXT_SECONDARY);
        welcome.add(lblDesc, BorderLayout.CENTER);

        panel.add(welcome, BorderLayout.CENTER);
        return panel;
    }

    /** ----------------- EVENTOS ----------------- */
    private void setupEvents() {
        btnToggleSidebar.addActionListener(e -> toggleSidebar());
        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton btn)
                btn.addActionListener(e -> tratarAcao(btn.getActionCommand()));
        }
    }

    private void toggleSidebar() {
        JSplitPane split = (JSplitPane) getComponent(1);
        sidebarPanel.setVisible(!sidebarPanel.isVisible());
        split.setDividerLocation(sidebarPanel.isVisible() ? UITheme.SIDEBAR_WIDTH : 0);
    }

    private void tratarAcao(String acao) {
        try {
            switch (acao) {
                case "GerirVendedores" -> abrirPainel(new GerirVendedoresView(controller), acao);
                case "GerirClientes" -> abrirPainel(new GerirClientesView(controller), acao);
                case "GerirEquipamentos" -> abrirPainel(new GerirEquipamentosView(controller), acao);
                case "GerirReservas" -> abrirPainel(new GerirReservasView(controller), acao);
                case "RelatoriosVendas" -> abrirPainel(new RelatoriosVendasView(controller), acao);
                case "Logout" -> logout();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir: " + ex.getMessage());
        }
    }

    private void abrirPainel(JPanel view, String nome) {
        controller.getCardLayoutManager().addPanel(view, nome);
        controller.getCardLayoutManager().showPanel(nome);
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair do sistema?", "Confirmar Logout",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.logout();
            SwingUtilities.getWindowAncestor(this).dispose();
            new LoginView(controller).setVisible(true);
        }
    }
}

package view;

import controller.SistemaController;
import model.concretas.Vendedor;
import model.concretas.Gestor;

import javax.swing.*;
import java.awt.*;

/**
 * Tela principal do sistema com CardLayout para navegação.
 */
public class MainView extends JFrame {

    private SistemaController controller;
    private CardLayoutManager cardLayoutManager;
    private String tipoUsuario;

    // Painéis das diferentes telas
    private JPanel mainPanel;

    public MainView(SistemaController controller, String tipoUsuario) {
        this.controller = controller;
        this.tipoUsuario = tipoUsuario;
        initComponents();
        setupCardLayout();
        carregarTelaInicial();
    }

    /**
     * Inicializa os componentes da interface.
     */
    private void initComponents() {
        setTitle("Sistema de Venda de Equipamentos Informáticos - " + tipoUsuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Painel principal com CardLayout
        mainPanel = new JPanel(new CardLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Configura o CardLayout e o gerenciador.
     */
    private void setupCardLayout() {
        cardLayoutManager = new CardLayoutManager(mainPanel);
        controller.setCardLayoutManager(cardLayoutManager);
    }


    /**
     * Carrega a tela inicial baseada no tipo de usuário.
     */
    private void carregarTelaInicial() {
        switch (tipoUsuario) {
            case "Administrador" -> {
                MenuAdministradorView menuAdmin = new MenuAdministradorView(controller);
                cardLayoutManager.addPanel(menuAdmin, "MenuAdministrador");
                cardLayoutManager.showPanel("MenuAdministrador");
            }
            case "Gestor" -> {
                Gestor gestorLogado = (Gestor) controller.getUsuarioLogado();
                MenuGestorView menuGestor = new MenuGestorView(controller, gestorLogado);
                cardLayoutManager.addPanel(menuGestor, "MenuGestor");
                cardLayoutManager.showPanel("MenuGestor");
            }
            case "Vendedor" -> {
                Vendedor vendedorLogado = (Vendedor) controller.getUsuarioLogado();
                MenuVendedorView menuVendedor = new MenuVendedorView(controller, vendedorLogado);
                cardLayoutManager.addPanel(menuVendedor, "MenuVendedor");
                cardLayoutManager.showPanel("MenuVendedor");
            }
            default -> JOptionPane.showMessageDialog(this,
                    "Tipo de usuário não reconhecido: " + tipoUsuario,
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cria um painel simples com título e mensagem.
     */
    private JPanel criarPainelSimples(String titulo, String mensagem) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblMensagem = new JLabel(mensagem);
        lblMensagem.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblMensagem, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Sair");
        btnLogout.addActionListener(e -> {
            controller.logout();
            new LoginView(controller).setVisible(true);
            this.dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(btnLogout);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public CardLayoutManager getCardLayoutManager() {
        return cardLayoutManager;
    }
}

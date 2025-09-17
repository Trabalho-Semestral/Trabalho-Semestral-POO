package util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Classe utilitária para gerenciar o tema visual do sistema.
 * Define cores, fontes e estilos padronizados.
 */
public class UITheme {

    // Paleta de cores principal
    public static final Color PRIMARY_COLOR = new Color(135, 206, 250);       // Azul claro (SkyBlue)
    public static final Color PRIMARY_DARK = new Color(30, 144, 255);         // Azul escuro (DodgerBlue)
    public static final Color PRIMARY_LIGHT = new Color(173, 216, 230);       // Azul muito claro (LightBlue)

    public static final Color SECONDARY_COLOR = new Color(52, 73, 94);        // Cinza azulado
    public static final Color SECONDARY_DARK = new Color(44, 62, 80);         // Cinza azulado escuro
    public static final Color SECONDARY_LIGHT = new Color(149, 165, 166);     // Cinza azulado claro

    public static final Color ACCENT_COLOR = new Color(231, 76, 60);          // Vermelho de destaque
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);         // Verde de sucesso
    public static final Color WARNING_COLOR = new Color(243, 156, 18);        // Amarelo de aviso
    public static final Color INFO_COLOR = new Color(52, 152, 219);           // Azul de informação

    // Cores de fundo
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241);    // Fundo principal
    public static final Color CARD_BACKGROUND = Color.WHITE;                  // Fundo de cartões
    public static final Color SIDEBAR_BACKGROUND = new Color(44, 62, 80);     // Fundo da barra lateral
    public static final Color TOPBAR_BACKGROUND = new Color(52, 73, 94);      // Fundo da barra superior

    // Cores de texto
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);           // Texto principal
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);      // Texto secundário
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color TEXT_BLACK = Color.BLACK;
    public static final Color TEXT_MUTED = new Color(149, 165, 166);          // Texto esmaecido

    // Cores de botões
    public static final Color BUTTON_PRIMARY = PRIMARY_COLOR;
    public static final Color BUTTON_PRIMARY_HOVER = PRIMARY_DARK;
    public static final Color BUTTON_SECONDARY = SECONDARY_COLOR;
    public static final Color BUTTON_SECONDARY_HOVER = SECONDARY_DARK;
    public static final Color BUTTON_SUCCESS = SUCCESS_COLOR;
    public static final Color BUTTON_DANGER = ACCENT_COLOR;

    // Fontes
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    // Bordas
    public static final Border BORDER_CARD = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );

    public static final Border BORDER_INPUT = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );

    public static final Border BORDER_BUTTON = BorderFactory.createEmptyBorder(10, 20, 10, 20);

    // Dimensões
    public static final Dimension BUTTON_SIZE = new Dimension(150, 40);
    public static final Dimension INPUT_SIZE = new Dimension(200, 35);
    public static final int SIDEBAR_WIDTH = 250;
    public static final int TOPBAR_HEIGHT = 60;

    /**
     * Cria um botão primário com estilo padronizado.
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BUTTON_PRIMARY, BUTTON_PRIMARY_HOVER, TEXT_WHITE);
        return button;
    }

    /**
     * Cria um botão secundário com estilo padronizado.
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER, TEXT_WHITE);
        return button;
    }

    /**
     * Cria um botão de sucesso com estilo padronizado.
     */
    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BUTTON_SUCCESS, SUCCESS_COLOR.darker(), TEXT_WHITE);
        return button;
    }

    /**
     * Cria um botão de perigo com estilo padronizado.
     */
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BUTTON_DANGER, ACCENT_COLOR.darker(), TEXT_WHITE);
        return button;
    }

    /**
     * Aplica estilo a um botão com efeitos hover melhorados.
     */
    private static void styleButton(JButton button, Color bgColor, Color hoverColor, Color textColor) {
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(FONT_BUTTON);
        button.setBorder(BORDER_BUTTON);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(BUTTON_SIZE);
        button.setOpaque(true);
        button.setBorderPainted(false);

        // Adicionar sombra sutil
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        // Efeito hover suave com transição
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                // Adicionar efeito de elevação
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(6, 14, 10, 18)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                // Restaurar borda original
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Efeito de clique
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLoweredBevelBorder(),
                        BorderFactory.createEmptyBorder(9, 17, 7, 15)
                ));
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // Restaurar após clique
                if (button.contains(evt.getPoint())) {
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createRaisedBevelBorder(),
                            BorderFactory.createEmptyBorder(6, 14, 10, 18)
                    ));
                } else {
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createRaisedBevelBorder(),
                            BorderFactory.createEmptyBorder(8, 16, 8, 16)
                    ));
                }
            }
        });
    }

    /**
     * Cria um campo de texto com estilo padronizado.
     */
    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(FONT_BODY);
        textField.setBorder(BORDER_INPUT);
        textField.setPreferredSize(INPUT_SIZE);
        return textField;
    }

    /**
     * Cria um campo de senha com estilo padronizado.
     */
    public static JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(FONT_BODY);
        passwordField.setBorder(BORDER_INPUT);
        passwordField.setPreferredSize(INPUT_SIZE);
        return passwordField;
    }

    /**
     * Cria um ComboBox com estilo padronizado.
     */
    public static <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(FONT_BODY);
        comboBox.setPreferredSize(INPUT_SIZE);
        return comboBox;
    }

    /**
     * Cria um label de título com estilo padronizado.
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Cria um label de subtítulo com estilo padronizado.
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Cria um label de cabeçalho com estilo padronizado.
     */
    public static JLabel createHeadingLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Cria um label de corpo com estilo padronizado.
     */
    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Cria um painel com fundo de cartão.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BORDER_CARD);
        return panel;
    }
// Adicione estes métodos à sua classe UITheme.java

    public static void setFieldAsDisplay(JTextField field, String title) {
        field.setEditable(false);
        field.setBackground(new Color(230, 230, 230));
        field.setBorder(BorderFactory.createTitledBorder(title));
    }

    public static void setFieldAsInput(JTextField field, String title) {
        field.setEditable(true);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createTitledBorder(title));
    }
    /**
     * Aplica um estilo padronizado a um JRadioButton.
     * @param radioButton O botão de rádio a ser estilizado.
     */
    public static void styleRadioButton(JRadioButton radioButton) {
        radioButton.setFont(FONT_BODY);
        radioButton.setForeground(TEXT_PRIMARY);
        radioButton.setOpaque(false);
    }
    public static JPanel createTopbar(String title, JButton backButton) {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, TOPBAR_HEIGHT));

        JLabel lblTitulo = createHeadingLabel(title);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);

        if (backButton != null) {
            JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            voltarPanel.setOpaque(false);
            voltarPanel.add(backButton);
            topBar.add(voltarPanel, BorderLayout.WEST);
        }
        return topBar;
    }
    /**
     * Aplica um estilo padronizado a uma JTable.
     * @param table A tabela a ser estilizada
     */
    public static void applyTableStyle(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setGridColor(new Color(224, 224, 224));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_BLACK);
        table.getTableHeader().setFont(FONT_SUBHEADING);
        table.getTableHeader().setBackground(SECONDARY_COLOR);
        table.getTableHeader().setForeground(TEXT_WHITE);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    // Adicione este metodo dentro da sua classe UITheme

    public static JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Aplica o tema global ao Look and Feel.
     */
    public static void applyGlobalTheme() {
        try {
            // Configurar UIManager para cores personalizadas
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Button.background", BUTTON_PRIMARY);
            UIManager.put("Button.foreground", TEXT_WHITE);
            UIManager.put("Button.font", FONT_BUTTON);
            UIManager.put("TextField.font", FONT_BODY);
            UIManager.put("Label.font", FONT_BODY);
            UIManager.put("ComboBox.font", FONT_BODY);
            UIManager.put("Table.font", FONT_BODY);
            UIManager.put("TableHeader.font", FONT_SUBHEADING);

            // Aplicar Look and Feel do sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
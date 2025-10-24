package util;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
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
    public static final Color PRIMARY_LIGHT = new Color(173, 216, 230);

    public static final Color TITLE_COLOR = new Color(19, 56, 94);

    public static final Color SECONDARY_COLOR = new Color(52, 73, 94);        // Cinza azulado
    public static final Color SECONDARY_DARK = new Color(44, 62, 80);         // Cinza azulado escuro
    public static final Color SECONDARY_LIGHT = new Color(0,0,0);             // Preto

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
    public static final Color TEXT_WHITE = Color.CYAN;
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

    // ---- MÉTODOS DE CRIAÇÃO DE COMPONENTES (MANTIDOS) ---- //
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BUTTON_PRIMARY, BUTTON_PRIMARY_HOVER, TEXT_PRIMARY);
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER, TEXT_PRIMARY);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        return button;
    }

    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BUTTON_SUCCESS, SUCCESS_COLOR.darker(), TEXT_PRIMARY);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        return button;
    }

    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BUTTON_DANGER, ACCENT_COLOR.darker(), TEXT_PRIMARY);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        return button;
    }


    private static void styleButton(JButton button, Color bgColor, Color hoverColor, Color textColor) {
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(BUTTON_SIZE);
        button.setOpaque(true);

        Border raisedBevel = BorderFactory.createRaisedBevelBorder();
        Border loweredBevel = BorderFactory.createLoweredBevelBorder();
        Border padding = BorderFactory.createEmptyBorder(8, 16, 8, 16);
        button.setBorder(BorderFactory.createCompoundBorder(raisedBevel, padding));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter().brighter());
                button.setForeground(hoverColor);
                button.setBorder(BorderFactory.createCompoundBorder(raisedBevel, padding));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setForeground(textColor);
                button.setBorder(BorderFactory.createCompoundBorder(raisedBevel, padding));
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createCompoundBorder(loweredBevel, padding));
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createCompoundBorder(raisedBevel, padding));
            }
        });
    }
    public static void apply() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.selectionBackground", new java.awt.Color(0, 120, 215));
        } catch (Exception e) {
            System.err.println("Erro ao aplicar tema moderno: " + e.getMessage());
        }
    }
    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(FONT_BODY);
        textField.setBorder(BORDER_INPUT);
        textField.setPreferredSize(INPUT_SIZE);
        return textField;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(FONT_BODY);
        passwordField.setBorder(BORDER_INPUT);
        passwordField.setPreferredSize(INPUT_SIZE);
        return passwordField;
    }

    public static <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(FONT_BODY);
        comboBox.setPreferredSize(INPUT_SIZE);
        return comboBox;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createHeadingLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BORDER_CARD);
        return panel;
    }

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
            // 1. Primeiro aplica o LookAndFeel do sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // 2. Agora sobrescreve com o tema customizado
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Button.font", FONT_BUTTON);
            UIManager.put("TextField.font", FONT_BODY);
            UIManager.put("Label.font", FONT_BODY);
            UIManager.put("ComboBox.font", FONT_BODY);
            UIManager.put("Table.font", FONT_BODY);
            UIManager.put("TableHeader.font", FONT_SUBHEADING);

            // 3. Força atualização da UI em todas as janelas abertas
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

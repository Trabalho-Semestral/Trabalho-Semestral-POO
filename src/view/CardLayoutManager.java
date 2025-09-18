package view;

import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 * Gerenciador de layout de cartões para navegação entre telas no Swing.
 * Utiliza o CardLayout nativo do Swing para exibir apenas uma tela por vez.
 */
public class CardLayoutManager {

    private JPanel mainPanel;
    private CardLayout cardLayout;

    public CardLayoutManager(JPanel mainPanel) {
        this.mainPanel = mainPanel;
        this.cardLayout = (CardLayout) mainPanel.getLayout();
    }

    /**
     * Adiciona um painel ao gerenciador com um nome específico.
     * Se já existir um painel com esse nome, não adiciona de novo.
     * @param panel O painel a ser adicionado
     * @param name O nome do painel para referência
     */
    public void addPanel(JPanel panel, String name) {
        boolean exists = false;
        for (java.awt.Component comp : mainPanel.getComponents()) {
            if (name.equals(comp.getName())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            panel.setName(name);
            mainPanel.add(panel, name);
        }
    }

    /**
     * Exibe o painel especificado pelo nome.
     * @param panelName O nome do painel a ser exibido
     */
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    /**
     * Obtém o painel principal.
     * @return O painel principal
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Obtém o CardLayout.
     * @return O CardLayout
     */
    public CardLayout getCardLayout() {
        return cardLayout;
    }
}

import view.LoginView;
import controller.SistemaController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Classe principal da aplicação.
 * Ponto de entrada do sistema de venda de equipamentos informáticos.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                SistemaController controller = new SistemaController();
                LoginView loginView = new LoginView(controller);
                loginView.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}


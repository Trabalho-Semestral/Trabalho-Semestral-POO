package view;

import controller.SistemaController;
import model.concretas.Administrador;
import util.BCryptHasher;
import util.GeradorID;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CadastrarAdministradorView extends JPanel {
    private SistemaController controller;
    private JTextField txtNome;
    private JTextField txtBI;
    private JTextField txtTelefone;
    private JTextField txtSalario;
    private JPasswordField txtSenha;
    private JCheckBox chkPodeCadastrarAdmin; // Nova caixa de seleção
    private JButton btnCadastrar;
    private JButton btnVoltar;

    public CadastrarAdministradorView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        txtNome = UITheme.createStyledTextField();
        txtBI = UITheme.createStyledTextField();
        txtTelefone = UITheme.createStyledTextField();
        txtSalario = UITheme.createStyledTextField();
        txtSenha = UITheme.createStyledPasswordField();
        chkPodeCadastrarAdmin = new JCheckBox("Permitir cadastrar outros administradores");
        chkPodeCadastrarAdmin.setBackground(UITheme.BACKGROUND_COLOR);
        chkPodeCadastrarAdmin.setForeground(UITheme.TEXT_PRIMARY);

        btnCadastrar = UITheme.createPrimaryButton("Cadastrar Administrador");
        btnVoltar = UITheme.createSecondaryButton("Voltar para Menu");
    }

    private void setupLayout() {
        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UITheme.createBodyLabel("Nome:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UITheme.createBodyLabel("BI:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtBI, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UITheme.createBodyLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(UITheme.createBodyLabel("Salário:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtSalario, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(UITheme.createBodyLabel("Senha:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(chkPodeCadastrarAdmin, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnVoltar);
        buttonPanel.add(btnCadastrar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        btnCadastrar.addActionListener(e -> cadastrarAdministrador());

        btnVoltar.addActionListener(e -> {
            controller.getCardLayoutManager().showPanel("MenuAdministrador");
        });
    }

    private void cadastrarAdministrador() {
        try {
            if (!(controller.getUsuarioLogado() instanceof Administrador adminLogado) || !adminLogado.podeCadastrarAdmin()) {
                JOptionPane.showMessageDialog(this,
                        "Você não tem permissão para cadastrar administradores!",
                        "Permissão Negada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Administrador admin = new Administrador(
                    txtNome.getText().trim(),
                    txtBI.getText().trim(),
                    GeradorID.gerarID(),
                    txtTelefone.getText().trim(),
                    Double.parseDouble(txtSalario.getText().trim()),
                    BCryptHasher.hashPassword(new String(txtSenha.getPassword()))
            );

            admin.setPrimeiroLogin(true);
            admin.setPodeCadastrarAdmin(chkPodeCadastrarAdmin.isSelected()); // Usa o valor da checkbox

            if (controller.adicionarAdministrador(admin)) {
                JOptionPane.showMessageDialog(this, "Administrador cadastrado com sucesso!");
                controller.getCardLayoutManager().showPanel("MenuAdministrador");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar administrador.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
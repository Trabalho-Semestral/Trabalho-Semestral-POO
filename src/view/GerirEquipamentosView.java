package view;

import controller.SistemaController;
import model.abstractas.Equipamento;
import model.concretas.Computador;
import model.concretas.Periferico;
import util.UITheme;
import util.Validador;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public class GerirEquipamentosView extends JPanel {

    private static final String FOTOS_EQUIPAMENTOS_PATH = "resources/fotos/equipamentos/";
    private final SistemaController controller;

    // Formulário
    private JComboBox<String> cmbTipoEquipamento;
    private JTextField txtMarca, txtPreco, txtQuantidade, txtProcessador, txtMemoriaRAM, txtArmazenamento, txtPlacaGrafica, txtTipoPeriferico;
    private JComboBox<Equipamento.EstadoEquipamento> cmbEstado;
    private JPanel especificacoesPanel;

    // Foto
    private JLabel lblFoto;
    private String caminhoFotoAtual;

    // Tabela
    private JTable tabelaEquipamentos;
    private DefaultTableModel modeloTabela;

    // Botões
    private JButton btnAdicionarFoto, btnRemoverFoto, btnCadastrar, btnEditar, btnRemover, btnVoltar;

    public GerirEquipamentosView(SistemaController controller) {
        this.controller = controller;
        new File(FOTOS_EQUIPAMENTOS_PATH).mkdirs();
        initComponents();
        setupLayout();
        setupEvents();
        carregarEquipamentos();
    }

    private void initComponents() {
        setBackground(UITheme.BACKGROUND_COLOR);

        cmbTipoEquipamento = new JComboBox<>(new String[]{"Computador", "Periférico"});
        txtMarca = new JTextField(); txtPreco = new JTextField(); txtQuantidade = new JTextField();
        cmbEstado = new JComboBox<>(Equipamento.EstadoEquipamento.values());
        styleTextField(txtMarca,"Marca"); styleTextField(txtPreco,"Preço (MT)"); styleTextField(txtQuantidade,"Quantidade");

        txtProcessador = new JTextField(); txtMemoriaRAM = new JTextField();
        txtArmazenamento = new JTextField(); txtPlacaGrafica = new JTextField();
        styleTextField(txtProcessador,"Processador"); styleTextField(txtMemoriaRAM,"Memória RAM");
        styleTextField(txtArmazenamento,"Armazenamento"); styleTextField(txtPlacaGrafica,"Placa Gráfica");

        txtTipoPeriferico = new JTextField(); styleTextField(txtTipoPeriferico,"Tipo de Periférico");

        lblFoto = new JLabel("Sem Foto", SwingConstants.CENTER);
        lblFoto.setPreferredSize(new Dimension(250, 250));
        lblFoto.setOpaque(true); lblFoto.setBackground(UITheme.CARD_BACKGROUND);
        lblFoto.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));

        btnAdicionarFoto = UITheme.createPrimaryButton("Adicionar Foto");
        btnRemoverFoto = UITheme.createSecondaryButton("Remover Foto");
        btnCadastrar = UITheme.createSuccessButton("Cadastrar");
        btnEditar = UITheme.createPrimaryButton("Editar");
        btnRemover = UITheme.createDangerButton("Remover");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Tipo", "Marca", "Preço", "Qtd.", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tabelaEquipamentos = new JTable(modeloTabela);
    }

    private void styleTextField(JComponent c, String title) {
        Border line = BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT);
        c.setFont(UITheme.FONT_BODY);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(line,title,
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        UITheme.FONT_SUBHEADING,UITheme.TEXT_SECONDARY),
                BorderFactory.createEmptyBorder(2,5,2,5)));
        c.setBackground(UITheme.CARD_BACKGROUND);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar=new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0,0,2,0,UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0,UITheme.TOPBAR_HEIGHT));
        JLabel lblTitulo=UITheme.createHeadingLabel("💻 Gestão de Equipamentos");
        lblTitulo.setFont(new Font("Segoe UI Emoji",Font.PLAIN,18)); lblTitulo.setForeground(Color.WHITE);
        topBar.add(lblTitulo,BorderLayout.CENTER);
        JPanel voltarPanel=new JPanel(new FlowLayout(FlowLayout.LEFT)); voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar); topBar.add(voltarPanel,BorderLayout.WEST);
        add(topBar,BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel=new JPanel(new BorderLayout(15,15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // Foto
        JPanel fotoPanel=new JPanel(new BorderLayout(10,10)); fotoPanel.setOpaque(false);
        fotoPanel.add(lblFoto,BorderLayout.CENTER);
        JPanel fotoBtns=new JPanel(new GridLayout(1,2,10,0)); fotoBtns.setOpaque(false);
        fotoBtns.add(btnAdicionarFoto); fotoBtns.add(btnRemoverFoto);
        fotoPanel.add(fotoBtns,BorderLayout.SOUTH);

        // Formulário
        JPanel formWrapper=new JPanel(new BorderLayout(10,10));
        formWrapper.setBackground(UITheme.CARD_BACKGROUND);
        formWrapper.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));

        JPanel commonFields=new JPanel(new GridLayout(0,2,10,10));
        commonFields.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        commonFields.setOpaque(false);
        commonFields.add(cmbTipoEquipamento); commonFields.add(cmbEstado);
        commonFields.add(txtMarca); commonFields.add(txtPreco); commonFields.add(txtQuantidade);
        formWrapper.add(commonFields,BorderLayout.NORTH);

        JPanel panelComputador=new JPanel(new GridLayout(0,2,10,10));
        panelComputador.setOpaque(false);
        panelComputador.add(txtProcessador); panelComputador.add(txtMemoriaRAM);
        panelComputador.add(txtArmazenamento); panelComputador.add(txtPlacaGrafica);

        JPanel panelPeriferico=new JPanel(new GridLayout(0,1,10,10));
        panelPeriferico.setOpaque(false); panelPeriferico.add(txtTipoPeriferico);

        especificacoesPanel=new JPanel(new CardLayout());
        especificacoesPanel.add(panelComputador,"Computador");
        especificacoesPanel.add(panelPeriferico,"Periférico");
        formWrapper.add(especificacoesPanel,BorderLayout.CENTER);

        JPanel acoesPanel=new JPanel(new GridLayout(0,1,10,10)); acoesPanel.setOpaque(false);
        acoesPanel.add(btnCadastrar); acoesPanel.add(btnEditar); acoesPanel.add(btnRemover);

        JPanel topContent=new JPanel(new BorderLayout(15,15)); topContent.setOpaque(false);
        topContent.add(fotoPanel,BorderLayout.WEST);
        topContent.add(formWrapper,BorderLayout.CENTER);
        topContent.add(acoesPanel,BorderLayout.EAST);

        JPanel tabelaPanel=new JPanel(new BorderLayout());
        tabelaPanel.setBorder(BorderFactory.createTitledBorder("Equipamentos Cadastrados"));
        tabelaPanel.setBackground(UITheme.CARD_BACKGROUND);
        tabelaPanel.add(new JScrollPane(tabelaEquipamentos),BorderLayout.CENTER);

        mainPanel.add(topContent,BorderLayout.NORTH);
        mainPanel.add(tabelaPanel,BorderLayout.CENTER);
        add(mainPanel,BorderLayout.CENTER);
    }

    private void setupEvents() {
        btnAdicionarFoto.addActionListener(e->adicionarFoto());
        btnRemoverFoto.addActionListener(e->removerFoto());
        btnCadastrar.addActionListener(e->cadastrarEquipamento());
        btnEditar.addActionListener(e->editarEquipamento());
        btnRemover.addActionListener(e->removerEquipamento());
        btnVoltar.addActionListener(e->voltarMenuPrincipal());

        cmbTipoEquipamento.addActionListener(e->((CardLayout)especificacoesPanel.getLayout())
                .show(especificacoesPanel,(String)cmbTipoEquipamento.getSelectedItem()));

        tabelaEquipamentos.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()) carregarEquipamentoSelecionado();
        });
    }

    // --- Métodos auxiliares ---
    private Equipamento buscarEquipamentoPorId(String id){
        return controller.getEquipamentos().stream().filter(e->e.getId().equals(id)).findFirst().orElse(null);
    }
    private void mensagemInfo(String msg){JOptionPane.showMessageDialog(this,msg,"Info",JOptionPane.INFORMATION_MESSAGE);}
    private void mensagemErro(String msg){JOptionPane.showMessageDialog(this,msg,"Erro",JOptionPane.ERROR_MESSAGE);}
    private void mensagemAviso(String msg){JOptionPane.showMessageDialog(this,msg,"Aviso",JOptionPane.WARNING_MESSAGE);}
    private boolean mensagemConfirmacao(String msg){return JOptionPane.showConfirmDialog(this,msg,"Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;}

    private void adicionarFoto() {
        JFileChooser fc=new JFileChooser();
        fc.setDialogTitle("Selecione uma foto");
        fc.setFileFilter(new FileNameExtensionFilter("Imagens (JPG, PNG)","jpg","png"));
        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            try{
                File arq=fc.getSelectedFile();
                String novoNome=UUID.randomUUID()+arq.getName().substring(arq.getName().lastIndexOf("."));
                File destino=new File(FOTOS_EQUIPAMENTOS_PATH+novoNome);
                Files.copy(arq.toPath(),destino.toPath(),StandardCopyOption.REPLACE_EXISTING);
                caminhoFotoAtual=destino.getPath(); exibirImagem(caminhoFotoAtual);
            }catch(IOException ex){mensagemErro("Erro ao salvar foto: "+ex.getMessage());}
        }
    }
    private void removerFoto(){caminhoFotoAtual=null; exibirImagem(null);}
    private void exibirImagem(String caminho){
        if(caminho!=null&&new File(caminho).exists()){
            Image img=new ImageIcon(caminho).getImage().getScaledInstance(lblFoto.getWidth(),lblFoto.getHeight(),Image.SCALE_SMOOTH);
            lblFoto.setIcon(new ImageIcon(img)); lblFoto.setText("");
        }else{lblFoto.setIcon(null); lblFoto.setText("Sem Foto");}
    }

    private void cadastrarEquipamento() {
        try{
            Equipamento eq=criarEquipamentoFromForm();
            if(eq!=null){eq.setFotoPath(caminhoFotoAtual);
                if(controller.adicionarEquipamento(eq)){mensagemInfo("Equipamento cadastrado!"); limparFormulario(); carregarEquipamentos();}
                else mensagemErro("Falha ao cadastrar.");
            }
        }catch(Exception ex){mensagemAviso("Erro: "+ex.getMessage());}
    }

    private void editarEquipamento() {
        int row=tabelaEquipamentos.getSelectedRow();
        if(row<0){mensagemAviso("Selecione um equipamento.");return;}
        Equipamento eqAntigo=buscarEquipamentoPorId((String)modeloTabela.getValueAt(row,0));
        try{
            Equipamento eqNovo=criarEquipamentoFromForm();
            if(eqAntigo!=null&&eqNovo!=null){
                eqNovo.setId(eqAntigo.getId()); eqNovo.setFotoPath(caminhoFotoAtual);
                if(controller.atualizarEquipamento(eqAntigo,eqNovo)){mensagemInfo("Atualizado com sucesso!"); limparFormulario(); carregarEquipamentos();}
                else mensagemErro("Falha ao atualizar.");
            }
        }catch(Exception ex){mensagemAviso("Erro: "+ex.getMessage());}
    }

    private void removerEquipamento() {
        int row=tabelaEquipamentos.getSelectedRow();
        if(row<0){mensagemAviso("Selecione um equipamento.");return;}
        Equipamento eq=buscarEquipamentoPorId((String)modeloTabela.getValueAt(row,0));
        if(eq!=null&&mensagemConfirmacao("Remover '"+eq.getMarca()+"'?")){
            if(controller.removerEquipamento(eq)){
                try{if(eq.getFotoPath()!=null) Files.deleteIfExists(Paths.get(eq.getFotoPath()));}catch(IOException ignored){}
                mensagemInfo("Removido com sucesso!"); limparFormulario(); carregarEquipamentos();
            }else mensagemErro("Falha ao remover.");
        }
    }

    private Equipamento criarEquipamentoFromForm() {
        try{
            String marca=txtMarca.getText().trim();
            double preco=Double.parseDouble(txtPreco.getText().trim());
            int qtd=Integer.parseInt(txtQuantidade.getText().trim());
            Equipamento.EstadoEquipamento estado=(Equipamento.EstadoEquipamento)cmbEstado.getSelectedItem();
            String tipo=(String)cmbTipoEquipamento.getSelectedItem();

            if(!Validador.validarCampoObrigatorio(marca)) throw new IllegalArgumentException("Marca obrigatória.");
            if(!Validador.validarValorPositivo(preco)) throw new IllegalArgumentException("Preço deve ser positivo.");
            if(qtd<0) throw new IllegalArgumentException("Quantidade não pode ser negativa.");

            return "Computador".equals(tipo)
                    ? new Computador(marca,preco,qtd,estado,null,
                    txtProcessador.getText().trim(),txtMemoriaRAM.getText().trim(),
                    txtArmazenamento.getText().trim(),txtPlacaGrafica.getText().trim())
                    : new Periferico(marca,preco,qtd,estado,null,txtTipoPeriferico.getText().trim());
        }catch(NumberFormatException e){throw new IllegalArgumentException("Preço e Quantidade devem ser números.");}
    }

    private void carregarEquipamentos() {
        modeloTabela.setRowCount(0);
        controller.getEquipamentos().forEach(eq->modeloTabela.addRow(new Object[]{
                eq.getId(),
                eq instanceof Computador?"Computador":"Periférico",
                eq.getMarca(),
                String.format("%.2f MT",eq.getPreco()),
                eq.getQuantidadeEstoque(),
                eq.getEstado()
        }));
    }

    private void carregarEquipamentoSelecionado() {
        int row=tabelaEquipamentos.getSelectedRow(); if(row<0) return;
        Equipamento eq=buscarEquipamentoPorId((String)modeloTabela.getValueAt(row,0));
        if(eq!=null){
            txtMarca.setText(eq.getMarca()); txtPreco.setText(String.valueOf(eq.getPreco()));
            txtQuantidade.setText(String.valueOf(eq.getQuantidadeEstoque())); cmbEstado.setSelectedItem(eq.getEstado());
            caminhoFotoAtual=eq.getFotoPath(); exibirImagem(caminhoFotoAtual);
            if(eq instanceof Computador c){
                cmbTipoEquipamento.setSelectedItem("Computador");
                txtProcessador.setText(c.getProcessador()); txtMemoriaRAM.setText(c.getMemoriaRAM());
                txtArmazenamento.setText(c.getArmazenamento()); txtPlacaGrafica.setText(c.getPlacaGrafica());
            }else if(eq instanceof Periferico p){
                cmbTipoEquipamento.setSelectedItem("Periférico"); txtTipoPeriferico.setText(p.getTipo());
            }
        }
    }

    private void limparFormulario() {
        txtMarca.setText(""); txtPreco.setText(""); txtQuantidade.setText("");
        txtProcessador.setText(""); txtMemoriaRAM.setText(""); txtArmazenamento.setText("");
        txtPlacaGrafica.setText(""); txtTipoPeriferico.setText("");
        cmbTipoEquipamento.setSelectedIndex(0); cmbEstado.setSelectedIndex(0);
        tabelaEquipamentos.clearSelection(); removerFoto();
    }

    private void voltarMenuPrincipal() {
        String tipo=controller.getTipoUsuarioLogado();
        String painel=switch(tipo){case"Gestor"->"MenuGestor";case"Vendedor"->"MenuVendedor";case"Administrador"->"MenuAdministrador";default->"Login";};
        controller.getCardLayoutManager().showPanel(painel);
    }
}

package view;

import controller.SistemaController;
import model.abstractas.Equipamento;
import model.concretas.*;
import util.UITheme;
import util.Validador;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrarVendaView extends JPanel {

    private final SistemaController controller;
    private final Vendedor vendedorLogado;

    // --- Cliente ---
    private final JRadioButton rbClienteCorp = new JRadioButton("Cliente Corporativo", true);
    private final JRadioButton rbClienteBalcao = new JRadioButton("Cliente de Balcão");
    private final CardLayout cardLayoutCliente = new CardLayout();
    private final JPanel painelCamposCliente = new JPanel(cardLayoutCliente);
    private final JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private final JTextArea txtDetalhesCliente = new JTextArea("Selecione um cliente para ver os detalhes.");
    private final JTextField txtNomeBalcao = UITheme.createStyledTextField();
    private final JTextField txtBiBalcao = UITheme.createStyledTextField();
    private final JTextField txtTelBalcao = UITheme.createStyledTextField();

    // --- Produtos ---
    private final JTable tabelaEquipamentos;
    private final DefaultTableModel modeloTabelaEquipamentos;
    private final JLabel lblFoto = new JLabel("Selecione um produto", SwingConstants.CENTER);
    private final JTextArea txtDetalhesEquipamento = new JTextArea("Detalhes do produto...");
    private final JTextField txtQuantidade = UITheme.createStyledTextField();
    private final JButton btnAdicionar = UITheme.createPrimaryButton("➕ Adicionar");

    // --- Carrinho ---
    private final JTable tabelaItens;
    private final DefaultTableModel modeloItens;
    private final JLabel lblTotal = UITheme.createTitleLabel("TOTAL: 0,00 MT");
    private final JButton btnRemover = UITheme.createDangerButton("Remover Item");
    private final JButton btnFinalizar = UITheme.createSuccessButton("✅ Finalizar Venda");
    private final JButton btnLimpar = UITheme.createSecondaryButton("Limpar Tudo");
    private final JButton btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");

    private final List<ItemVenda> itensVenda = new ArrayList<>();
    private double totalVenda = 0.0;

    private static class ItemVenda {
        Equipamento equipamento;
        int quantidade;
        double subtotal;
        ItemVenda(Equipamento eq, int qtd) { equipamento = eq; quantidade = qtd; subtotal = eq.getPreco()*qtd; }
    }

    public RegistrarVendaView(SistemaController controller, Vendedor vendedorLogado) {
        this.controller = controller;
        this.vendedorLogado = vendedorLogado;
        setBackground(UITheme.BACKGROUND_COLOR);

        initClienteComponents();
        initProdutoComponents();
        initCarrinhoComponents();
        setupLayout();
        setupEvents();
        carregarDadosIniciais();
    }

    private void initClienteComponents() {
        UITheme.styleRadioButton(rbClienteCorp);
        UITheme.styleRadioButton(rbClienteBalcao);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbClienteCorp); bg.add(rbClienteBalcao);

        painelCamposCliente.setOpaque(false);
        // Corporativo
        JPanel painelCorp = new JPanel(new BorderLayout(5,5)); painelCorp.setOpaque(false);
        painelCorp.add(cmbCliente, BorderLayout.NORTH);
        txtDetalhesCliente.setEditable(false); txtDetalhesCliente.setFont(UITheme.FONT_BODY);
        txtDetalhesCliente.setOpaque(false); txtDetalhesCliente.setLineWrap(true); txtDetalhesCliente.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtDetalhesCliente);
        scroll.setBorder(BorderFactory.createEmptyBorder(5,0,0,0)); scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        painelCorp.add(scroll, BorderLayout.CENTER);
        painelCamposCliente.add(painelCorp,"CORPORATIVO");

        // Balcão
        JPanel painelBalcao = new JPanel(new GridLayout(0,1,5,5)); painelBalcao.setOpaque(false);
        UITheme.setFieldAsInput(txtNomeBalcao,"Nome"); UITheme.setFieldAsInput(txtBiBalcao,"Nº BI"); UITheme.setFieldAsInput(txtTelBalcao,"Telefone");
        painelBalcao.add(txtNomeBalcao); painelBalcao.add(txtBiBalcao); painelBalcao.add(txtTelBalcao);
        painelCamposCliente.add(painelBalcao,"BALCAO");
    }

    private void initProdutoComponents() {
        modeloTabelaEquipamentos = new DefaultTableModel(new String[]{"Marca","Tipo","Preço","Estoque"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tabelaEquipamentos = new JTable(modeloTabelaEquipamentos);
        lblFoto.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));
        lblFoto.setPreferredSize(new Dimension(150,150));
        txtDetalhesEquipamento.setEditable(false); txtDetalhesEquipamento.setFont(UITheme.FONT_BODY);
        txtDetalhesEquipamento.setLineWrap(true); txtDetalhesEquipamento.setWrapStyleWord(true);
        txtDetalhesEquipamento.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        txtQuantidade.setText("1"); txtQuantidade.setPreferredSize(new Dimension(60,UITheme.INPUT_SIZE.height));
    }

    private void initCarrinhoComponents() {
        modeloItens = new DefaultTableModel(new String[]{"Qtd","Produto","Preço","Subtotal"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tabelaItens = new JTable(modeloItens);
        lblTotal.setForeground(UITheme.SUCCESS_COLOR);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(UITheme.createTopbar("🛒 Registrar Nova Venda",btnVoltar),BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.6); split.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        split.setLeftComponent(createLeftPanel());
        split.setRightComponent(createRightPanel());
        add(split,BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10)); panel.setOpaque(false);

        JPanel painelCliente = UITheme.createCardPanel();
        painelCliente.setLayout(new BorderLayout(10,10));
        painelCliente.setBorder(BorderFactory.createTitledBorder("1. Cliente"));
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT)); radios.setOpaque(false);
        radios.add(rbClienteCorp); radios.add(rbClienteBalcao);
        painelCliente.add(radios,BorderLayout.NORTH); painelCliente.add(painelCamposCliente,BorderLayout.CENTER);

        JPanel painelCarrinho = UITheme.createCardPanel();
        painelCarrinho.setLayout(new BorderLayout(10,10));
        painelCarrinho.setBorder(BorderFactory.createTitledBorder("3. Carrinho"));
        painelCarrinho.add(new JScrollPane(tabelaItens),BorderLayout.CENTER);
        JPanel acoes = new JPanel(new BorderLayout(10,10)); acoes.setOpaque(false);
        acoes.add(lblTotal,BorderLayout.WEST);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0)); botoes.setOpaque(false);
        botoes.add(btnRemover); botoes.add(btnLimpar); botoes.add(btnFinalizar);
        acoes.add(botoes,BorderLayout.EAST);
        painelCarrinho.add(acoes,BorderLayout.SOUTH);

        panel.add(painelCliente,BorderLayout.NORTH); panel.add(painelCarrinho,BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = UITheme.createCardPanel(); panel.setLayout(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createTitledBorder("2. Catálogo"));

        JPanel detalhes = new JPanel(new BorderLayout(10,10)); detalhes.setOpaque(false); detalhes.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        detalhes.add(lblFoto,BorderLayout.WEST); detalhes.add(new JScrollPane(txtDetalhesEquipamento),BorderLayout.CENTER);
        JPanel ações = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0)); ações.setOpaque(false);
        ações.add(new JLabel("Qtd:")); ações.add(txtQuantidade); ações.add(btnAdicionar);
        detalhes.add(ações,BorderLayout.SOUTH);

        panel.add(new JScrollPane(tabelaEquipamentos),BorderLayout.CENTER);
        panel.add(detalhes,BorderLayout.SOUTH);
        return panel;
    }

    private void setupEvents() {
        rbClienteCorp.addActionListener(e -> toggleCliente(false));
        rbClienteBalcao.addActionListener(e -> toggleCliente(true));
        cmbCliente.addActionListener(e -> exibirCliente());
        tabelaEquipamentos.getSelectionModel().addListSelectionListener(this::exibirEquipamento);
        btnAdicionar.addActionListener(e -> addCarrinho());
        btnRemover.addActionListener(e -> removerCarrinho());
        btnLimpar.addActionListener(this::limparVenda);
        btnFinalizar.addActionListener(e -> finalizarVenda());
        btnVoltar.addActionListener(e -> voltarMenu());
    }

    private void carregarDadosIniciais() {
        controller.getClientes().forEach(cmbCliente::addItem);
        modeloTabelaEquipamentos.setRowCount(0);
        controller.getEquipamentos().stream().filter(eq->eq.getQuantidadeEstoque()>0).forEach(eq->
                modeloTabelaEquipamentos.addRow(new Object[]{
                        eq.getMarca(), eq instanceof Computador?"Computador":"Periférico",
                        String.format("%.2f MT",eq.getPreco()), eq.getQuantidadeEstoque()
                }));
        toggleCliente(false);
    }

    // --- CLIENTE ---
    private void toggleCliente(boolean balcao){ cardLayoutCliente.show(painelCamposCliente, balcao?"BALCAO":"CORPORATIVO"); if(balcao) limparCamposBalcao(); else exibirCliente(); }
    private void limparCamposBalcao(){ txtNomeBalcao.setText(""); txtBiBalcao.setText(""); txtTelBalcao.setText(""); }
    private void exibirCliente(){ if(!rbClienteCorp.isSelected()) return; Cliente c = (Cliente)cmbCliente.getSelectedItem(); if(c!=null) txtDetalhesCliente.setText(
            "Nome:\t"+c.getNome()+"\nNº BI/NIF:\t"+c.getNrBI()+"\nTelefone:\t"+c.getTelefone()+
                    (c.getEmail()!=null?"\nEmail:\t"+c.getEmail():"")+(c.getEndereco()!=null?"\nEndereço:\t"+c.getEndereco():"")
    ); else txtDetalhesCliente.setText("Nenhum cliente corporativo selecionado."); }

    // --- EQUIPAMENTO ---
    private void exibirEquipamento(ListSelectionEvent e){
        if(e.getValueIsAdjusting() || tabelaEquipamentos.getSelectedRow()<0) return;
        int idx = tabelaEquipamentos.convertRowIndexToModel(tabelaEquipamentos.getSelectedRow());
        Equipamento eq = controller.getEquipamentos().stream().filter(q->q.getQuantidadeEstoque()>0).toList().get(idx);
        // Foto
        if(eq.getFotoPath()!=null && new File(eq.getFotoPath()).exists()){
            ImageIcon icon = new ImageIcon(new ImageIcon(eq.getFotoPath()).getImage().getScaledInstance(150,150,Image.SCALE_SMOOTH));
            lblFoto.setIcon(icon); lblFoto.setText("");
        } else { lblFoto.setIcon(null); lblFoto.setText("Sem Foto"); }
        // Detalhes
        StringBuilder sb = new StringBuilder("Marca: "+eq.getMarca()+"\nEstado: "+eq.getEstado()+"\nPreço: "+String.format("%.2f MT",eq.getPreco())+"\n");
        if(eq instanceof Computador c){ sb.append("Processador: ").append(c.getProcessador()).append("\nRAM: ").append(c.getMemoriaRAM()).append("\nArmazenamento: ").append(c.getArmazenamento()); }
        else if(eq instanceof Periferico p){ sb.append("Tipo: ").append(p.getTipo()); }
        txtDetalhesEquipamento.setText(sb.toString());
    }

    // --- CARRINHO ---
    private void addCarrinho(){
        int row = tabelaEquipamentos.getSelectedRow();
        if(row<0){ JOptionPane.showMessageDialog(this,"Selecione um produto.","Aviso",JOptionPane.WARNING_MESSAGE); return; }
        int idx = tabelaEquipamentos.convertRowIndexToModel(row);
        Equipamento eq = controller.getEquipamentos().stream().filter(q->q.getQuantidadeEstoque()>0).toList().get(idx);
        try{
            int qtd = Integer.parseInt(txtQuantidade.getText().trim());
            if(qtd<=0) throw new NumberFormatException();
            if(qtd>eq.getQuantidadeEstoque()){ JOptionPane.showMessageDialog(this,"Quantidade em estoque insuficiente.","Estoque",JOptionPane.WARNING_MESSAGE); return; }
            for(ItemVenda i:itensVenda) if(i.equipamento.getId().equals(eq.getId())){ 
                if(i.quantidade+qtd>i.equipamento.getQuantidadeEstoque()){ JOptionPane.showMessageDialog(this,"Quantidade total excede o estoque.","Estoque",JOptionPane.WARNING_MESSAGE); return; }
                i.quantidade+=qtd; i.subtotal=i.quantidade*i.equipamento.getPreco(); atualizarCarrinho(); return; 
            }
            itensVenda.add(new ItemVenda(eq,qtd)); atualizarCarrinho();
        }catch(NumberFormatException ex){ JOptionPane.showMessageDialog(this,"Quantidade deve ser número inteiro positivo.","Erro",JOptionPane.ERROR_MESSAGE);}
    }

    private void removerCarrinho(){ int r = tabelaItens.getSelectedRow(); if(r>=0){ itensVenda.remove(r); atualizarCarrinho(); } }

    private Cliente getClienteVenda(){
        if(rbClienteCorp.isSelected()){ Cliente c=(Cliente)cmbCliente.getSelectedItem(); if(c==null) JOptionPane.showMessageDialog(this,"Selecione um cliente corporativo.","Erro",JOptionPane.ERROR_MESSAGE); return c; }
        else{
            try{
                String n=txtNomeBalcao.getText().trim(),bi=txtBiBalcao.getText().trim(),tel=txtTelBalcao.getText().trim();
                if(!Validador.validarCampoObrigatorio(n)) throw new IllegalArgumentException("Nome obrigatório.");
                if(!Validador.validarBI(bi)) throw new IllegalArgumentException("BI inválido.");
                if(!Validador.validarTelefone(tel)) throw new IllegalArgumentException("Telefone inválido.");
                Cliente novo = new Cliente(n,bi,null,tel,null,null);
                controller.adicionarCliente(novo); return novo;
            }catch(IllegalArgumentException e){ JOptionPane.showMessageDialog(this,e.getMessage(),"Cliente Inválido",JOptionPane.WARNING_MESSAGE); return null; }
        }
    }

    private void finalizarVenda(){
        if(itensVenda.isEmpty()){ JOptionPane.showMessageDialog(this,"Carrinho vazio.","Erro",JOptionPane.ERROR_MESSAGE); return; }
        Cliente c = getClienteVenda(); if(c==null) return;
        int confirm = JOptionPane.showConfirmDialog(this,"Finalizar venda para "+c.getNome()+"?\nTotal: "+String.format("%.2f MT",totalVenda),"Confirmar Venda",JOptionPane.YES_NO_OPTION);
        if(confirm==JOptionPane.YES_OPTION){
            List<Equipamento> vendidos = new ArrayList<>();
            itensVenda.forEach(it-> {for(int i=0;i<it.quantidade;i++) vendidos.add(it.equipamento);});
            Venda v = new Venda(new Date(),vendedorLogado,c,vendidos,totalVenda);
            if(controller.registrarVenda(v)){ JOptionPane.showMessageDialog(this,"Venda registrada!","Sucesso",JOptionPane.INFORMATION_MESSAGE); limparVenda(); carregarDadosIniciais(); }
            else JOptionPane.showMessageDialog(this,"Erro ao registrar a venda.","Erro",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparVenda(){
        itensVenda.clear(); limparCamposBalcao(); txtDetalhesCliente.setText("Selecione um cliente para ver os detalhes.");
        if(cmbCliente.getItemCount()>0) cmbCliente.setSelectedIndex(0); atualizarCarrinho();
    }

    private void atualizarCarrinho(){
        modeloItens.setRowCount(0); totalVenda=0;
        itensVenda.forEach(it->{
            modeloItens.addRow(new Object[]{it.quantidade,it.equipamento.getMarca(),String.format("%.2f",it.equipamento.getPreco()),String.format("%.2f",it.subtotal)});
            totalVenda+=it.subtotal;
        });
        lblTotal.setText("TOTAL: "+String.format("%.2f MT",totalVenda));
        txtQuantidade.setText("1"); tabelaEquipamentos.clearSelection(); txtDetalhesEquipamento.setText("Detalhes do produto..."); lblFoto.setIcon(null); lblFoto.setText("Selecione um produto");
    }

    private void voltarMenu(){
        String tipo = controller.getTipoUsuarioLogado();
        if(tipo==null){ controller.getCardLayoutManager().showPanel("Login"); return; }
        switch(tipo){
            case "Gestor" -> controller.getCardLayoutManager().showPanel("MenuGestor");
            case "Vendedor" -> controller.getCardLayoutManager().showPanel("MenuVendedor");
            default -> controller.getCardLayoutManager().showPanel("MenuAdministrador");
        }
    }
}

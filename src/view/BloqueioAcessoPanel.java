package view;

import dao.BloqueioAcessoDAO;
import model.BloqueioAcesso;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class BloqueioAcessoPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtCliente, txtContexto, txtNomeAcesso, txtData, txtFiltro;
    private JButton btnAdicionar, btnEditar, btnExcluir, btnLimpar, btnFiltrar;
    private JLabel lblTotalBloqueios;
    private int bloqueioSelecionadoId = -1;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public BloqueioAcessoPanel() {
        initComponents();
        carregarDados();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        
        // Painel de formulário
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Bloqueio"));
        
        formPanel.add(new JLabel("Cliente:"));
        txtCliente = new JTextField();
        formPanel.add(txtCliente);
        
        formPanel.add(new JLabel("Contexto:"));
        txtContexto = new JTextField();
        formPanel.add(txtContexto);
        
        formPanel.add(new JLabel("Nome do Acesso:"));
        txtNomeAcesso = new JTextField();
        formPanel.add(txtNomeAcesso);
        
        formPanel.add(new JLabel("Data (dd/mm/aaaa):"));
        txtData = new JTextField();
        formPanel.add(txtData);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");
        
        buttonPanel.add(btnAdicionar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnLimpar);
        
        // Painel de filtro
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtrar"));
        
        filterPanel.add(new JLabel("Filtro:"));
        txtFiltro = new JTextField(20);
        filterPanel.add(txtFiltro);
        
        btnFiltrar = new JButton("Filtrar");
        filterPanel.add(btnFiltrar);
        
        // Tabela de dados
        tableModel = new DefaultTableModel(new Object[]{"ID", "Cliente", "Contexto", "Nome Acesso", "Data"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                selecionarBloqueio();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Painel do rodapé com total
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotalBloqueios = new JLabel("Total: 0");
        lblTotalBloqueios.setFont(new Font("Arial", Font.BOLD, 12));
        footerPanel.add(lblTotalBloqueios);
        
        // Painel central que agrupa tabela e rodapé
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Painel superior que agrupa formulário e botões
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Adiciona componentes ao painel principal
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Configura ações dos botões
        btnAdicionar.addActionListener(this::adicionarBloqueio);
        btnEditar.addActionListener(this::editarBloqueio);
        btnExcluir.addActionListener(this::excluirBloqueio);
        btnLimpar.addActionListener(e -> limparFormulario());
        btnFiltrar.addActionListener(this::filtrarBloqueios);
    }
    
    private void carregarDados() {
        try {
            List<BloqueioAcesso> bloqueios = new BloqueioAcessoDAO().listarTodos();
            atualizarTabela(bloqueios);
            atualizarTotalBloqueios(bloqueios.size());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar bloqueios: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarTotalBloqueios(int total) {
        lblTotalBloqueios.setText("Total: " + total);
    }
    
    private void atualizarTabela(List<BloqueioAcesso> bloqueios) {
        tableModel.setRowCount(0);
        
        for (BloqueioAcesso bloqueio : bloqueios) {
            tableModel.addRow(new Object[]{
                bloqueio.getId(),
                bloqueio.getCliente(),
                bloqueio.getContexto(),
                bloqueio.getNomeAcesso(),
                bloqueio.getData().format(dateFormatter)
            });
        }
    }
    
    private void selecionarBloqueio() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            bloqueioSelecionadoId = (int) tableModel.getValueAt(selectedRow, 0);
            
            try {
                BloqueioAcesso bloqueio = new BloqueioAcessoDAO().buscarPorId(bloqueioSelecionadoId);
                if (bloqueio != null) {
                    txtCliente.setText(bloqueio.getCliente());
                    txtContexto.setText(bloqueio.getContexto());
                    txtNomeAcesso.setText(bloqueio.getNomeAcesso());
                    txtData.setText(bloqueio.getData().format(dateFormatter));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao carregar bloqueio: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void adicionarBloqueio(ActionEvent e) {
        try {
            if (txtCliente.getText().trim().isEmpty() || txtNomeAcesso.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Cliente e Nome do Acesso são obrigatórios!", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            LocalDate data;
            try {
                data = LocalDate.parse(txtData.getText(), dateFormatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Data inválida. Use o formato dd/mm/aaaa", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            BloqueioAcesso bloqueio = new BloqueioAcesso(
                txtCliente.getText(),
                txtContexto.getText(),
                txtNomeAcesso.getText(),
                data
            );
            
            new BloqueioAcessoDAO().inserir(bloqueio);
            carregarDados();
            limparFormulario();
            
            JOptionPane.showMessageDialog(this, 
                "Bloqueio adicionado com sucesso!", 
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao adicionar bloqueio: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarBloqueio(ActionEvent e) {
        if (bloqueioSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um bloqueio para editar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (txtCliente.getText().trim().isEmpty() || txtNomeAcesso.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Cliente e Nome do Acesso são obrigatórios!", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            LocalDate data = LocalDate.parse(txtData.getText(), dateFormatter);
            
            BloqueioAcesso bloqueio = new BloqueioAcesso(
                txtCliente.getText(),
                txtContexto.getText(),
                txtNomeAcesso.getText(),
                data
            );
            bloqueio.setId(bloqueioSelecionadoId);
            
            new BloqueioAcessoDAO().atualizar(bloqueio);
            carregarDados();
            limparFormulario();
            
            JOptionPane.showMessageDialog(this, 
                "Bloqueio atualizado com sucesso!", 
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                "Data inválida. Use o formato dd/mm/aaaa", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao atualizar bloqueio: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirBloqueio(ActionEvent e) {
        if (bloqueioSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um bloqueio para excluir", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este bloqueio?", 
            "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new BloqueioAcessoDAO().excluir(bloqueioSelecionadoId);
                carregarDados();
                limparFormulario();
                
                JOptionPane.showMessageDialog(this, 
                    "Bloqueio excluído com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao excluir bloqueio: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void filtrarBloqueios(ActionEvent e) {
        try {
            String filtro = txtFiltro.getText().trim();
            
            List<BloqueioAcesso> bloqueios;
            if (filtro.isEmpty()) {
                bloqueios = new BloqueioAcessoDAO().listarTodos();
            } else {
                bloqueios = new BloqueioAcessoDAO().filtrar(filtro);
            }
            
            atualizarTabela(bloqueios);
            atualizarTotalBloqueios(bloqueios.size());
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao filtrar bloqueios: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparFormulario() {
        txtCliente.setText("");
        txtContexto.setText("");
        txtNomeAcesso.setText("");
        txtData.setText("");
        bloqueioSelecionadoId = -1;
        table.clearSelection();
    }
}
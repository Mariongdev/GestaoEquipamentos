package view;

import dao.ClienteDAO;
import model.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class ClientePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNome, txtContexto, txtFuncionarios, txtEquipModelo, txtEquipQuantidade, txtFiltro;
    private JComboBox<String> cmbBH, cmbStatus, cmbFiltroStatus;
    private JButton btnAdicionar, btnEditar, btnExcluir, btnLimpar, btnFiltrar;
    private JLabel lblTotalClientes;
    private int clienteSelecionadoId = -1;
    
    public ClientePanel() {
        initComponents();
        carregarDados();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        
        // Painel de formulário
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
        
        formPanel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);
        
        formPanel.add(new JLabel("Contexto:"));
        txtContexto = new JTextField();
        formPanel.add(txtContexto);
        
        formPanel.add(new JLabel("BH (Sim/Não):"));
        cmbBH = new JComboBox<>(new String[]{"Sim", "Não"});
        formPanel.add(cmbBH);
        
        formPanel.add(new JLabel("Funcionários:"));
        txtFuncionarios = new JTextField();
        formPanel.add(txtFuncionarios);
        
        formPanel.add(new JLabel("Modelo Equipamento:"));
        txtEquipModelo = new JTextField();
        formPanel.add(txtEquipModelo);
        
        formPanel.add(new JLabel("Quantidade Equipamento:"));
        txtEquipQuantidade = new JTextField();
        formPanel.add(txtEquipQuantidade);
        
        formPanel.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Ativo", "Inativo"});
        formPanel.add(cmbStatus);
        
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
        
        filterPanel.add(new JLabel("Status:"));
        cmbFiltroStatus = new JComboBox<>(new String[]{"", "Ativo", "Inativo"});
        filterPanel.add(cmbFiltroStatus);
        
        btnFiltrar = new JButton("Filtrar");
        filterPanel.add(btnFiltrar);
        
        // Tabela de dados
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Contexto", "BH", "Funcionários", 
            "Equip. Modelo", "Equip. Quant.", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                selecionarCliente();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Painel do rodapé com total
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotalClientes = new JLabel("Total: 0");
        lblTotalClientes.setFont(new Font("Arial", Font.BOLD, 12));
        footerPanel.add(lblTotalClientes);
        
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
        btnAdicionar.addActionListener(this::adicionarCliente);
        btnEditar.addActionListener(this::editarCliente);
        btnExcluir.addActionListener(this::excluirCliente);
        btnLimpar.addActionListener(e -> limparFormulario());
        btnFiltrar.addActionListener(this::filtrarClientes);
    }
    
    private void carregarDados() {
        try {
            List<Cliente> clientes = new ClienteDAO().listarTodos();
            atualizarTabela(clientes);
            atualizarTotalClientes(clientes.size());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar clientes: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarTotalClientes(int total) {
        lblTotalClientes.setText("Total: " + total);
    }
    
    private void atualizarTabela(List<Cliente> clientes) {
        tableModel.setRowCount(0);
        
        for (Cliente cliente : clientes) {
            tableModel.addRow(new Object[]{
                cliente.getId(),
                cliente.getNome(),
                cliente.getContexto(),
                cliente.getBh(),
                cliente.getFuncionarios(),
                cliente.getEquipamentoModelo(),
                cliente.getEquipamentoQuantidade(),
                cliente.getStatus()
            });
        }
    }
    
    private void selecionarCliente() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            clienteSelecionadoId = (int) tableModel.getValueAt(selectedRow, 0);
            
            try {
                Cliente cliente = new ClienteDAO().buscarPorId(clienteSelecionadoId);
                if (cliente != null) {
                    txtNome.setText(cliente.getNome());
                    txtContexto.setText(cliente.getContexto());
                    cmbBH.setSelectedItem(cliente.getBh());
                    txtFuncionarios.setText(String.valueOf(cliente.getFuncionarios()));
                    txtEquipModelo.setText(cliente.getEquipamentoModelo());
                    txtEquipQuantidade.setText(String.valueOf(cliente.getEquipamentoQuantidade()));
                    cmbStatus.setSelectedItem(cliente.getStatus());
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao carregar cliente: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void adicionarCliente(ActionEvent e) {
        try {
            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Nome é obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (txtFuncionarios.getText().trim().isEmpty() || 
                !txtFuncionarios.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this, 
                    "Número de funcionários inválido", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (txtEquipQuantidade.getText().trim().isEmpty() || 
                !txtEquipQuantidade.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this, 
                    "Quantidade de equipamentos inválida", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Cliente cliente = new Cliente(
                txtNome.getText(),
                txtContexto.getText(),
                cmbBH.getSelectedItem().toString(),
                Integer.parseInt(txtFuncionarios.getText()),
                txtEquipModelo.getText(),
                Integer.parseInt(txtEquipQuantidade.getText()),
                cmbStatus.getSelectedItem().toString()
            );
            
            new ClienteDAO().inserir(cliente);
            carregarDados();
            limparFormulario();
            
            JOptionPane.showMessageDialog(this, 
                "Cliente adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao adicionar cliente: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarCliente(ActionEvent e) {
        if (clienteSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um cliente para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Nome é obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Cliente cliente = new Cliente(
                txtNome.getText(),
                txtContexto.getText(),
                cmbBH.getSelectedItem().toString(),
                Integer.parseInt(txtFuncionarios.getText()),
                txtEquipModelo.getText(),
                Integer.parseInt(txtEquipQuantidade.getText()),
                cmbStatus.getSelectedItem().toString()
            );
            cliente.setId(clienteSelecionadoId);
            
            new ClienteDAO().atualizar(cliente);
            carregarDados();
            limparFormulario();
            
            JOptionPane.showMessageDialog(this, 
                "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Funcionários e Quantidade devem ser números válidos", 
                "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao atualizar cliente: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirCliente(ActionEvent e) {
        if (clienteSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um cliente para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este cliente?", "Confirmação", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new ClienteDAO().excluir(clienteSelecionadoId);
                carregarDados();
                limparFormulario();
                
                JOptionPane.showMessageDialog(this, 
                    "Cliente excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao excluir cliente: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void filtrarClientes(ActionEvent e) {
        try {
            String filtro = txtFiltro.getText().trim();
            String status = cmbFiltroStatus.getSelectedItem().toString();
            
            if (status.isEmpty()) {
                status = null;
            }
            
            List<Cliente> clientes = new ClienteDAO().filtrar(filtro, status);
            atualizarTabela(clientes);
            atualizarTotalClientes(clientes.size());
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao filtrar clientes: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparFormulario() {
        txtNome.setText("");
        txtContexto.setText("");
        cmbBH.setSelectedIndex(0);
        txtFuncionarios.setText("");
        txtEquipModelo.setText("");
        txtEquipQuantidade.setText("");
        cmbStatus.setSelectedIndex(0);
        clienteSelecionadoId = -1;
        table.clearSelection();
    }
}
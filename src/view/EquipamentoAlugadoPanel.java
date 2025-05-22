package view;

import dao.EquipamentoAlugadoDAO;
import model.EquipamentoAlugado;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EquipamentoAlugadoPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNome, txtEquipModelo, txtEquipQuantidade, txtData, txtFiltro;
    private JComboBox<String> cmbStatus, cmbFiltroStatus;
    private JButton btnAdicionar, btnEditar, btnExcluir, btnLimpar, btnFiltrar;
    private JLabel lblTotalEquipamentos;
    private int equipamentoSelecionadoId = -1;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public EquipamentoAlugadoPanel() {
        initComponents();
        carregarDados();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        
        // Painel de formulário
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Equipamento Alugado"));
        
        formPanel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);
        
        formPanel.add(new JLabel("Modelo Equipamento:"));
        txtEquipModelo = new JTextField();
        formPanel.add(txtEquipModelo);
        
        formPanel.add(new JLabel("Quantidade Equipamento:"));
        txtEquipQuantidade = new JTextField();
        formPanel.add(txtEquipQuantidade);
        
        formPanel.add(new JLabel("Data (dd/mm/aaaa):"));
        txtData = new JTextField();
        formPanel.add(txtData);
        
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
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Equip. Modelo", 
            "Equip. Quant.", "Data", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                selecionarEquipamento();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Painel do rodapé com total
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotalEquipamentos = new JLabel("Total: 0");
        lblTotalEquipamentos.setFont(new Font("Arial", Font.BOLD, 12));
        footerPanel.add(lblTotalEquipamentos);
        
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
        btnAdicionar.addActionListener(this::adicionarEquipamento);
        btnEditar.addActionListener(this::editarEquipamento);
        btnExcluir.addActionListener(this::excluirEquipamento);
        btnLimpar.addActionListener(e -> limparFormulario());
        btnFiltrar.addActionListener(this::filtrarEquipamentos);
    }
    
    private void carregarDados() {
        try {
            List<EquipamentoAlugado> equipamentos = new EquipamentoAlugadoDAO().listarTodos();
            atualizarTabela(equipamentos);
            atualizarTotalEquipamentos(equipamentos.size());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar equipamentos: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarTotalEquipamentos(int total) {
        lblTotalEquipamentos.setText("Total: " + total);
    }
    
    private void atualizarTabela(List<EquipamentoAlugado> equipamentos) {
        tableModel.setRowCount(0);
        
        for (EquipamentoAlugado equipamento : equipamentos) {
            tableModel.addRow(new Object[]{
                equipamento.getId(),
                equipamento.getNome(),
                equipamento.getEquipamentoModelo(),
                equipamento.getEquipamentoQuantidade(),
                equipamento.getData().format(dateFormatter),
                equipamento.getStatus()
            });
        }
    }
    
    private void selecionarEquipamento() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            equipamentoSelecionadoId = (int) tableModel.getValueAt(selectedRow, 0);
            
            try {
                EquipamentoAlugado equipamento = new EquipamentoAlugadoDAO().buscarPorId(equipamentoSelecionadoId);
                if (equipamento != null) {
                    txtNome.setText(equipamento.getNome());
                    txtEquipModelo.setText(equipamento.getEquipamentoModelo());
                    txtEquipQuantidade.setText(String.valueOf(equipamento.getEquipamentoQuantidade()));
                    txtData.setText(equipamento.getData().format(dateFormatter));
                    cmbStatus.setSelectedItem(equipamento.getStatus());
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao carregar equipamento: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void adicionarEquipamento(ActionEvent e) {
        try {
            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Nome é obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (txtEquipQuantidade.getText().trim().isEmpty() || 
                !txtEquipQuantidade.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this, 
                    "Quantidade de equipamentos inválida", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            LocalDate data;
            try {
                data = LocalDate.parse(txtData.getText(), dateFormatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Data inválida. Use o formato dd/mm/aaaa", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            EquipamentoAlugado equipamento = new EquipamentoAlugado(
                txtNome.getText(),
                txtEquipModelo.getText(),
                Integer.parseInt(txtEquipQuantidade.getText()),
                data,
                cmbStatus.getSelectedItem().toString()
            );
            
            new EquipamentoAlugadoDAO().inserir(equipamento);
            carregarDados();
            limparFormulario();
            
            JOptionPane.showMessageDialog(this, 
                "Equipamento adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao adicionar equipamento: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarEquipamento(ActionEvent e) {
        if (equipamentoSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um equipamento para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Nome é obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            LocalDate data;
            try {
                data = LocalDate.parse(txtData.getText(), dateFormatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Data inválida. Use o formato dd/mm/aaaa", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            EquipamentoAlugado equipamento = new EquipamentoAlugado(
                txtNome.getText(),
                txtEquipModelo.getText(),
                Integer.parseInt(txtEquipQuantidade.getText()),
                data,
                cmbStatus.getSelectedItem().toString()
            );
            equipamento.setId(equipamentoSelecionadoId);
            
            new EquipamentoAlugadoDAO().atualizar(equipamento);
            carregarDados();
            limparFormulario();
            
            JOptionPane.showMessageDialog(this, 
                "Equipamento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao atualizar equipamento: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirEquipamento(ActionEvent e) {
        if (equipamentoSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um equipamento para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este equipamento?", "Confirmação", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new EquipamentoAlugadoDAO().excluir(equipamentoSelecionadoId);
                carregarDados();
                limparFormulario();
                
                JOptionPane.showMessageDialog(this, 
                    "Equipamento excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao excluir equipamento: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void filtrarEquipamentos(ActionEvent e) {
        try {
            String filtro = txtFiltro.getText().trim();
            String status = cmbFiltroStatus.getSelectedItem().toString();
            
            if (status.isEmpty()) {
                status = null;
            }
            
            List<EquipamentoAlugado> equipamentos = new EquipamentoAlugadoDAO().filtrar(filtro, status);
            atualizarTabela(equipamentos);
            atualizarTotalEquipamentos(equipamentos.size());
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao filtrar equipamentos: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparFormulario() {
        txtNome.setText("");
        txtEquipModelo.setText("");
        txtEquipQuantidade.setText("");
        txtData.setText("");
        cmbStatus.setSelectedIndex(0);
        equipamentoSelecionadoId = -1;
        table.clearSelection();
    }
}
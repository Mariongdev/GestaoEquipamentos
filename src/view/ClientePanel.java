package view;

import dao.ClienteDAO;
import model.Cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Aplica fonte padrão 15pt ao painel inteiro
        setFonte(this, new Font("SansSerif", Font.PLAIN, 14));
    }

    private void setFonte(Component comp, Font fonte) {
        comp.setFont(fonte);
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                setFonte(child, fonte);
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Formulário
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        txtNome = new JTextField();
        txtContexto = new JTextField();
        txtFuncionarios = new JTextField();
        txtEquipModelo = new JTextField();
        txtEquipQuantidade = new JTextField();
        cmbBH = new JComboBox<>(new String[]{"Sim", "Não"});
        cmbStatus = new JComboBox<>(new String[]{"Ativo", "Inativo"});

        formPanel.add(new JLabel("Nome:"));
        formPanel.add(txtNome);
        formPanel.add(new JLabel("Contexto:"));
        formPanel.add(txtContexto);

        formPanel.add(new JLabel("BH:"));
        formPanel.add(cmbBH);
        formPanel.add(new JLabel("Funcionários:"));
        formPanel.add(txtFuncionarios);

        formPanel.add(new JLabel("Equipamento Modelo:"));
        formPanel.add(txtEquipModelo);
        formPanel.add(new JLabel("Equipamento Quantidade:"));
        formPanel.add(txtEquipQuantidade);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(cmbStatus);
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");

        btnAdicionar.addActionListener(this::adicionarCliente);
        btnEditar.addActionListener(this::editarCliente);
        btnExcluir.addActionListener(this::excluirCliente);
        btnLimpar.addActionListener(e -> limparFormulario());

        buttonPanel.add(btnAdicionar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnLimpar);

        // Tabela
        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Nome", "Contexto", "BH", "Funcionários", "Equipamento Modelo", "Quantidade", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::selecionarCliente);

        // Estilo da tabela
        Font tabelaFonte = new Font("SansSerif", Font.PLAIN, 15);
        table.setFont(tabelaFonte); // Fonte 15pt para células
        table.setRowHeight(28); // Altura da linha
        table.setDefaultRenderer(Object.class, new ZebraRenderer()); // Linhas zebradas
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer()); // Renderer status

        // Fonte e estilo negrito para o header da tabela e centraliza o texto
        Font headerFont = new Font("SansSerif", Font.BOLD, 15);
        JTableHeader header = table.getTableHeader();
        header.setFont(headerFont);
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane tableScroll = new JScrollPane(table);

        // Painel filtro
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtFiltro = new JTextField(20);
        cmbFiltroStatus = new JComboBox<>(new String[]{"", "Ativo", "Inativo"});
        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(this::filtrarClientes);
        lblTotalClientes = new JLabel("Total: 0");

        filtroPanel.add(new JLabel("Buscar por nome:"));
        filtroPanel.add(txtFiltro);
        filtroPanel.add(new JLabel("Status:"));
        filtroPanel.add(cmbFiltroStatus);
        filtroPanel.add(btnFiltrar);
        filtroPanel.add(Box.createHorizontalStrut(20));
        filtroPanel.add(lblTotalClientes);

        // Organização dos painéis na tela
        JPanel painelTopo = new JPanel(new BorderLayout(5, 5));
        painelTopo.add(formPanel, BorderLayout.NORTH);
        painelTopo.add(buttonPanel, BorderLayout.CENTER);
        painelTopo.add(filtroPanel, BorderLayout.SOUTH);

        add(painelTopo, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    private void carregarDados() {
        try {
            List<Cliente> clientes = new ClienteDAO().listarTodos();
            atualizarTabela(clientes);
            atualizarTotalClientes(clientes.size());
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar clientes: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTabela(List<Cliente> clientes) {
        tableModel.setRowCount(0);
        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    c.getContexto(),
                    c.getBh(),
                    c.getFuncionarios(),
                    c.getEquipamentoModelo(),
                    c.getEquipamentoQuantidade(),
                    c.getStatus()
            });
        }
    }

    private void atualizarTotalClientes(int total) {
        lblTotalClientes.setText("Total: " + total);
    }

    private void selecionarCliente(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
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
                } catch (ClassNotFoundException | SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erro ao carregar cliente: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void adicionarCliente(ActionEvent e) {
        try {
            Cliente cliente = obterClienteFormulario();
            if (cliente == null) return;
            new ClienteDAO().inserir(cliente);
            JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!");
            limparFormulario();
            carregarDados();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao adicionar cliente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarCliente(ActionEvent e) {
        if (clienteSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.");
            return;
        }
        try {
            Cliente cliente = obterClienteFormulario();
            if (cliente == null) return;
            cliente.setId(clienteSelecionadoId);
            new ClienteDAO().atualizar(cliente);
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            limparFormulario();
            carregarDados();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar cliente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirCliente(ActionEvent e) {
        if (clienteSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o cliente selecionado?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                new ClienteDAO().excluir(clienteSelecionadoId);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
                limparFormulario();
                carregarDados();
            } catch (ClassNotFoundException | SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir cliente: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filtrarClientes(ActionEvent e) {
        try {
            String nome = txtFiltro.getText().trim();
            String status = (String) cmbFiltroStatus.getSelectedItem();
            if (status != null && status.isEmpty()) status = null;
            List<Cliente> clientes = new ClienteDAO().filtrar(nome, status);
            atualizarTabela(clientes);
            atualizarTotalClientes(clientes.size());
        } catch (ClassNotFoundException | SQLException ex) {
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

    private Cliente obterClienteFormulario() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.");
            return null;
        }
        String contexto = txtContexto.getText().trim();
        String bh = (String) cmbBH.getSelectedItem();
        int funcionarios;
        try {
            funcionarios = Integer.parseInt(txtFuncionarios.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Funcionários deve ser um número.");
            return null;
        }
        String equipModelo = txtEquipModelo.getText().trim();
        int equipQuantidade;
        try {
            equipQuantidade = Integer.parseInt(txtEquipQuantidade.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade de equipamento deve ser um número.");
            return null;
        }
        String status = (String) cmbStatus.getSelectedItem();

        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setContexto(contexto);
        cliente.setBh(bh);
        cliente.setFuncionarios(funcionarios);
        cliente.setEquipamentoModelo(equipModelo);
        cliente.setEquipamentoQuantidade(equipQuantidade);
        cliente.setStatus(status);

        return cliente;
    }

    // Renderer para efeito zebrado na tabela
    private static class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
            }
            setHorizontalAlignment(CENTER);
            return this;
        }
    }

    // Renderer para colorir a coluna Status
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String status = (String) value;
            if (!isSelected) {
                if ("Ativo".equalsIgnoreCase(status)) {
                    setBackground(new Color(198, 239, 206)); // Verde claro
                    setForeground(new Color(0, 97, 0));
                } else if ("Inativo".equalsIgnoreCase(status)) {
                    setBackground(new Color(255, 199, 206)); // Vermelho claro
                    setForeground(new Color(156, 0, 6));
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
            }
            setHorizontalAlignment(CENTER);
            return this;
        }
    }
}

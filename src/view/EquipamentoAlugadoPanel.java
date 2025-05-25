package view;

import dao.EquipamentoAlugadoDAO;
import model.EquipamentoAlugado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
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
    private int totalEquipamentos = 0; // total geral sem filtro

    public EquipamentoAlugadoPanel() {
        initComponents();
        try {
            carregarDados();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar driver do banco de dados: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Painel superior principal com layout vertical
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Painel do formulário (labels e campos)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblNome = new JLabel("Nome:");
        JLabel lblEquipModelo = new JLabel("Modelo:");
        JLabel lblEquipQuantidade = new JLabel("Quantidade:");
        JLabel lblData = new JLabel("Data (dd/MM/yyyy):");
        JLabel lblStatus = new JLabel("Status:");

        txtNome = new JTextField(15);
        txtEquipModelo = new JTextField(15);
        txtEquipQuantidade = new JTextField(5);
        txtData = new JTextField(10);
        cmbStatus = new JComboBox<>(new String[]{"Ativo", "Inativo"});

        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 0
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblNome, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtNome, gbc);

        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(lblEquipModelo, gbc);
        gbc.gridx = 3; gbc.gridy = 0; formPanel.add(txtEquipModelo, gbc);

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblEquipQuantidade, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtEquipQuantidade, gbc);

        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(lblData, gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(txtData, gbc);

        // Linha 2
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblStatus, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(cmbStatus, gbc);

        topPanel.add(formPanel);

        // Painel dos botões CRUD
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");

        buttonsPanel.add(btnAdicionar);
        buttonsPanel.add(btnEditar);
        buttonsPanel.add(btnExcluir);
        buttonsPanel.add(btnLimpar);

        topPanel.add(buttonsPanel);

        // Painel de filtro + botão filtrar + total equipamentos
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtroPanel.add(new JLabel("Filtro:"));
        txtFiltro = new JTextField(15);
        filtroPanel.add(txtFiltro);

        filtroPanel.add(new JLabel("Status:"));
        cmbFiltroStatus = new JComboBox<>(new String[]{"", "Ativo", "Inativo"});
        filtroPanel.add(cmbFiltroStatus);

        btnFiltrar = new JButton("Filtrar");
        filtroPanel.add(btnFiltrar);

        lblTotalEquipamentos = new JLabel("Mostrando 0 de 0 equipamentos");
        filtroPanel.add(Box.createHorizontalStrut(20)); // espaçamento
        filtroPanel.add(lblTotalEquipamentos);

        topPanel.add(filtroPanel);

        add(topPanel, BorderLayout.NORTH);

        // Tabela
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Modelo", "Quantidade", "Data", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Listeners
        btnAdicionar.addActionListener(this::adicionarEquipamento);
        btnEditar.addActionListener(this::editarEquipamento);
        btnExcluir.addActionListener(this::excluirEquipamento);
        btnLimpar.addActionListener(e -> limparFormulario());
        btnFiltrar.addActionListener(this::filtrarEquipamentos);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarEquipamento();
            }
        });
    }

    private void carregarDados() throws ClassNotFoundException {
        try {
            List<EquipamentoAlugado> equipamentos = new EquipamentoAlugadoDAO().listarTodos();
            totalEquipamentos = equipamentos.size();
            atualizarTabela(equipamentos);
            atualizarTotalEquipamentos(equipamentos.size(), totalEquipamentos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar equipamentos: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTotalEquipamentos(int mostrados, int total) {
        lblTotalEquipamentos.setText("Mostrando " + mostrados + " de " + total + " equipamentos");
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
            } catch (ClassNotFoundException | SQLException ex) {
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
                        "Data inválida. Use o formato dd/MM/yyyy", "Aviso", JOptionPane.WARNING_MESSAGE);
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

        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao adicionar equipamento: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantidade deve ser um número válido", "Erro", JOptionPane.ERROR_MESSAGE);
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
                        "Data inválida. Use o formato dd/MM/yyyy", "Aviso", JOptionPane.WARNING_MESSAGE);
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

        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar equipamento: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantidade deve ser um número válido", "Erro", JOptionPane.ERROR_MESSAGE);
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
            } catch (ClassNotFoundException | SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir equipamento: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filtrarEquipamentos(ActionEvent e) {
        try {
            String filtroNome = txtFiltro.getText().trim();
            String filtroStatus = (String) cmbFiltroStatus.getSelectedItem();

            List<EquipamentoAlugado> equipamentosFiltrados =
                    new EquipamentoAlugadoDAO().filtrar(filtroNome, filtroStatus);
            atualizarTabela(equipamentosFiltrados);
            atualizarTotalEquipamentos(equipamentosFiltrados.size(), totalEquipamentos);

        } catch (ClassNotFoundException | SQLException ex) {
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

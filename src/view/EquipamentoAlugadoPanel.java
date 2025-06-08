package view;

import dao.EquipamentoAlugadoDAO;
import model.EquipamentoAlugado;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EquipamentoAlugadoPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNome, txtEquipModelo, txtEquipQuantidade, txtData, txtFiltro;
    private JComboBox<String> cmbStatus, cmbFiltroStatus;
    private JButton btnAdicionar, btnEditar, btnExcluir, btnLimpar, btnFiltrar;
    private JLabel lblTotalEquipamentos;
    private int equipamentoSelecionadoId = -1;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private int totalEquipamentos = 0;

    public EquipamentoAlugadoPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        Font fontePadrao = new Font("SansSerif", Font.PLAIN, 14);
        UIManager.put("Label.font", fontePadrao);
        UIManager.put("Button.font", fontePadrao);
        UIManager.put("ComboBox.font", fontePadrao);
        UIManager.put("TextField.font", fontePadrao);
        UIManager.put("Table.font", fontePadrao);
        UIManager.put("TableHeader.font", new Font("SansSerif", Font.BOLD, 14)); // Cabeçalho em negrito

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
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

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

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblNome, gbc);
        gbc.gridx = 1; formPanel.add(txtNome, gbc);
        gbc.gridx = 2; formPanel.add(lblEquipModelo, gbc);
        gbc.gridx = 3; formPanel.add(txtEquipModelo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblEquipQuantidade, gbc);
        gbc.gridx = 1; formPanel.add(txtEquipQuantidade, gbc);
        gbc.gridx = 2; formPanel.add(lblData, gbc);
        gbc.gridx = 3; formPanel.add(txtData, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblStatus, gbc);
        gbc.gridx = 1; formPanel.add(cmbStatus, gbc);

        topPanel.add(formPanel);

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
        filtroPanel.add(Box.createHorizontalStrut(20));
        filtroPanel.add(lblTotalEquipamentos);

        topPanel.add(filtroPanel);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Modelo", "Quantidade", "Data", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                } else {
                    comp.setBackground(getSelectionBackground());
                }

                if (column == 5) { // coluna status
                    String status = (String) getValueAt(row, column);
                    if ("Ativo".equalsIgnoreCase(status)) {
                        comp.setBackground(new Color(200, 255, 200)); // verde claro
                        comp.setForeground(new Color(0, 100, 0)); // verde escuro no texto
                    } else if ("Inativo".equalsIgnoreCase(status)) {
                        comp.setBackground(new Color(255, 200, 200)); // vermelho claro
                        comp.setForeground(new Color(139, 0, 0)); // vermelho escuro no texto
                    } else {
                        comp.setForeground(Color.BLACK);
                    }
                } else {
                    comp.setForeground(Color.BLACK);
                }

                return comp;
            }
        };

        // Centraliza texto da coluna "Status"
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

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
                JOptionPane.showMessageDialog(this, "Nome é obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int quantidade = Integer.parseInt(txtEquipQuantidade.getText().trim());
            LocalDate data = LocalDate.parse(txtData.getText().trim(), dateFormatter);

            EquipamentoAlugado equipamento = new EquipamentoAlugado();
            equipamento.setNome(txtNome.getText().trim());
            equipamento.setEquipamentoModelo(txtEquipModelo.getText().trim());
            equipamento.setEquipamentoQuantidade(quantidade);
            equipamento.setData(data);
            equipamento.setStatus((String) cmbStatus.getSelectedItem());

            new EquipamentoAlugadoDAO().adicionar(equipamento);

            carregarDados();
            limparFormulario();
            
            // Mensagem de sucesso ao adicionar
            JOptionPane.showMessageDialog(this, "Equipamento cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser um número inteiro", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar equipamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            Logger.getLogger(EquipamentoAlugadoPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void editarEquipamento(ActionEvent e) {
        if (equipamentoSelecionadoId < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int quantidade = Integer.parseInt(txtEquipQuantidade.getText().trim());
            LocalDate data = LocalDate.parse(txtData.getText().trim(), dateFormatter);

            EquipamentoAlugado equipamento = new EquipamentoAlugado();
            equipamento.setId(equipamentoSelecionadoId);
            equipamento.setNome(txtNome.getText().trim());
            equipamento.setEquipamentoModelo(txtEquipModelo.getText().trim());
            equipamento.setEquipamentoQuantidade(quantidade);
            equipamento.setData(data);
            equipamento.setStatus((String) cmbStatus.getSelectedItem());

            new EquipamentoAlugadoDAO().atualizar(equipamento);

            carregarDados();
            limparFormulario();

            // Mensagem de sucesso ao editar
            JOptionPane.showMessageDialog(this, "Equipamento editado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser um número inteiro", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar equipamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirEquipamento(ActionEvent e) {
        if (equipamentoSelecionadoId < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o equipamento selecionado?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new EquipamentoAlugadoDAO().excluir(equipamentoSelecionadoId);
                carregarDados();
                limparFormulario();

                // Mensagem de sucesso ao excluir
                JOptionPane.showMessageDialog(this, "Equipamento excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir equipamento: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
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

    private void filtrarEquipamentos(ActionEvent e) {
        String filtroTexto = txtFiltro.getText().trim().toLowerCase();
        String filtroStatus = (String) cmbFiltroStatus.getSelectedItem();

        try {
            List<EquipamentoAlugado> todosEquipamentos = new EquipamentoAlugadoDAO().listarTodos();
            List<EquipamentoAlugado> filtrados = todosEquipamentos.stream()
                    .filter(eq -> {
                        boolean textoOk = filtroTexto.isEmpty()
                                || eq.getNome().toLowerCase().contains(filtroTexto)
                                || eq.getEquipamentoModelo().toLowerCase().contains(filtroTexto)
                                || String.valueOf(eq.getEquipamentoQuantidade()).contains(filtroTexto)
                                || eq.getData().format(dateFormatter).contains(filtroTexto);
                        boolean statusOk = filtroStatus == null || filtroStatus.isEmpty() || eq.getStatus().equalsIgnoreCase(filtroStatus);
                        return textoOk && statusOk;
                    })
                    .toList();

            atualizarTabela(filtrados);
            atualizarTotalEquipamentos(filtrados.size(), totalEquipamentos);

        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao filtrar equipamentos: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

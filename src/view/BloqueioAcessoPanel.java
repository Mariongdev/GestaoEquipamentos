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
        try {
            carregarDados();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar driver do banco de dados: " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));

        // Inicializar componentes
        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");
        btnFiltrar = new JButton("Filtrar");

        txtCliente = new JTextField(20);
        txtContexto = new JTextField(20);
        txtNomeAcesso = new JTextField(20);
        txtData = new JTextField(10);
        txtFiltro = new JTextField(15);

        lblTotalBloqueios = new JLabel("Total de bloqueios: 0");

        // Modelo da tabela e tabela
        tableModel = new DefaultTableModel(new String[]{"ID", "Cliente", "Contexto", "Nome Acesso", "Data"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Não permite edição direta
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Listener para seleção na tabela
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarBloqueio();
            }
        });

        // Ações dos botões
        btnAdicionar.addActionListener(this::adicionarBloqueio);
        btnEditar.addActionListener(this::editarBloqueio);
        btnExcluir.addActionListener(this::excluirBloqueio);
        btnLimpar.addActionListener(e -> limparFormulario());
        btnFiltrar.addActionListener(this::filtrarBloqueios);

        // Painel do formulário (dados do bloqueio)
        JPanel painelFormulario = new JPanel(new GridLayout(2, 4, 10, 5));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados do Bloqueio"));
        painelFormulario.add(new JLabel("Cliente:"));
        painelFormulario.add(txtCliente);
        painelFormulario.add(new JLabel("Contexto:"));
        painelFormulario.add(txtContexto);
        painelFormulario.add(new JLabel("Nome Acesso:"));
        painelFormulario.add(txtNomeAcesso);
        painelFormulario.add(new JLabel("Data (dd/MM/yyyy):"));
        painelFormulario.add(txtData);

        // Painel dos botões CRUD + limpar
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        // Painel do filtro
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        painelFiltro.add(new JLabel("Filtro:"));
        painelFiltro.add(txtFiltro);
        painelFiltro.add(btnFiltrar);

        // Painel superior que junta botões + filtro + total de bloqueios
        JPanel painelSuperior = new JPanel(new BorderLayout(10, 5));
        painelSuperior.add(painelBotoes, BorderLayout.WEST);
        painelSuperior.add(painelFiltro, BorderLayout.EAST);
        painelSuperior.add(lblTotalBloqueios, BorderLayout.SOUTH);

        // Painel topo geral com formulário e painelSuperior (botoes + filtro)
        JPanel topoGeral = new JPanel(new BorderLayout(5,5));
        topoGeral.add(painelFormulario, BorderLayout.CENTER);
        topoGeral.add(painelSuperior, BorderLayout.SOUTH);

        // Adicionar componentes no painel principal
        add(topoGeral, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void carregarDados() throws ClassNotFoundException {
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

    private void atualizarTabela(List<BloqueioAcesso> bloqueios) {
        tableModel.setRowCount(0); // limpa linhas antigas
        for (BloqueioAcesso b : bloqueios) {
            tableModel.addRow(new Object[]{
                b.getId(),
                b.getCliente(),
                b.getContexto(),
                b.getNomeAcesso(),
                b.getData().format(dateFormatter)
            });
        }
    }

    private void atualizarTotalBloqueios(int total) {
        lblTotalBloqueios.setText("Total de bloqueios: " + total);
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
            } catch (ClassNotFoundException | SQLException ex) {
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

        } catch (ClassNotFoundException | SQLException ex) {
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
        } catch (ClassNotFoundException | SQLException ex) {
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

            } catch (ClassNotFoundException | SQLException ex) {
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

        } catch (ClassNotFoundException | SQLException ex) {
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

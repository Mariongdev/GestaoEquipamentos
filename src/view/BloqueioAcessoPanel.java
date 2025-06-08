package view;

import dao.BloqueioAcessoDAO;
import model.BloqueioAcesso;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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

        // Fonte base SansSerif 14 para todo painel
        Font fonteBase = new Font("SansSerif", Font.PLAIN, 14);
        setFonte(this, fonteBase);

        // Fonte negrito para título da tabela
        Font fonteNegrito = fonteBase.deriveFont(Font.BOLD);
        table.getTableHeader().setFont(fonteNegrito);

        // Fonte negrito e tamanho 14 para botões
        Font fonteBotoes = fonteBase.deriveFont(Font.BOLD, 14f);
        setFonteBotoes(fonteBotoes);

        ajustarAlturaLinhasTabela();
        aplicarEstiloZebradoTabela();

        try {
            carregarDados();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar driver do banco: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        btnAdicionar = criarBotao("Adicionar");
        btnEditar = criarBotao("Editar");
        btnExcluir = criarBotao("Excluir");
        btnLimpar = criarBotao("Limpar");
        btnFiltrar = criarBotao("Filtrar");

        txtCliente = new JTextField(20);
        txtContexto = new JTextField(20);
        txtNomeAcesso = new JTextField(20);
        txtData = new JTextField(10);
        txtFiltro = new JTextField(15);

        lblTotalBloqueios = new JLabel("Total de bloqueios: 0");

        tableModel = new DefaultTableModel(new String[]{"ID", "Cliente", "Contexto", "Nome Acesso", "Data"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ajustarAlturaLinhasTabela();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarBloqueio();
            }
        });

        btnAdicionar.addActionListener(this::adicionarBloqueio);
        btnEditar.addActionListener(this::editarBloqueio);
        btnExcluir.addActionListener(this::excluirBloqueio);
        btnLimpar.addActionListener(e -> limparFormulario());
        btnFiltrar.addActionListener(this::filtrarBloqueios);

        // Fonte base para o painel, será usada no título da borda também
        Font fonteBase = getFont();

        // Cria o painel formulário
        JPanel painelFormulario = new JPanel(new GridLayout(2, 4, 10, 5));

        // Cria o título da borda em negrito com tamanho da fonte do painel
        Font fonteTituloBorda = fonteBase.deriveFont(Font.BOLD);
        TitledBorder border = BorderFactory.createTitledBorder("Dados do Bloqueio");
        border.setTitleFont(fonteTituloBorda);
        painelFormulario.setBorder(border);

        // Labels alinhados à esquerda, fonte negrito e tamanho da fonte base
        Font fonteLabel = fonteBase.deriveFont(Font.BOLD);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setHorizontalAlignment(SwingConstants.LEFT);
        lblCliente.setFont(fonteLabel);

        JLabel lblContexto = new JLabel("Contexto:");
        lblContexto.setHorizontalAlignment(SwingConstants.LEFT);
        lblContexto.setFont(fonteLabel);

        JLabel lblNomeAcesso = new JLabel("Nome Acesso:");
        lblNomeAcesso.setHorizontalAlignment(SwingConstants.LEFT);
        lblNomeAcesso.setFont(fonteLabel);

        JLabel lblData = new JLabel("Data (dd/MM/yyyy):");
        lblData.setHorizontalAlignment(SwingConstants.LEFT);
        lblData.setFont(fonteLabel);

        painelFormulario.add(lblCliente);
        painelFormulario.add(txtCliente);
        painelFormulario.add(lblContexto);
        painelFormulario.add(txtContexto);
        painelFormulario.add(lblNomeAcesso);
        painelFormulario.add(txtNomeAcesso);
        painelFormulario.add(lblData);
        painelFormulario.add(txtData);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelFiltro.add(new JLabel("Filtro:"));
        painelFiltro.add(txtFiltro);
        painelFiltro.add(btnFiltrar);

        JPanel painelSuperior = new JPanel(new BorderLayout(10, 5));
        painelSuperior.add(painelBotoes, BorderLayout.WEST);
        painelSuperior.add(painelFiltro, BorderLayout.EAST);
        painelSuperior.add(lblTotalBloqueios, BorderLayout.SOUTH);

        JPanel topoGeral = new JPanel(new BorderLayout(5, 5));
        topoGeral.add(painelFormulario, BorderLayout.CENTER);
        topoGeral.add(painelSuperior, BorderLayout.SOUTH);

        add(topoGeral, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Depois de criar a tabela, aplicamos o alinhamento desejado:
        aplicarEstiloZebradoTabela();
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setHorizontalAlignment(SwingConstants.CENTER);
        botao.setVerticalAlignment(SwingConstants.CENTER);
        botao.setFont(new Font("SansSerif", Font.BOLD, 16)); // negrito e tamanho 16
        botao.setBorder(new EmptyBorder(6, 14, 6, 14));

        estiloBotaoSuave(botao);
        return botao;
    }

    private void setFonte(Component component, Font fonte) {
        component.setFont(fonte);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                setFonte(child, fonte);
            }
        }
    }

    private void setFonteBotoes(Font fonte) {
        btnAdicionar.setFont(fonte);
        btnEditar.setFont(fonte);
        btnExcluir.setFont(fonte);
        btnLimpar.setFont(fonte);
        btnFiltrar.setFont(fonte);
    }

    private void estiloBotaoSuave(JButton botao) {
        Color corNormal = new Color(176, 224, 230); // azul suave
        Color corHover = new Color(135, 206, 250);  // azul claro hover
        Color corPressionado = new Color(70, 130, 180); // azul steel pressionado

        botao.setBackground(corNormal);
        botao.setForeground(Color.BLACK);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 149, 237)),
            botao.getBorder()
        ));
        botao.setOpaque(true);

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(corHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(corNormal);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                botao.setBackground(corPressionado);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (botao.getBounds().contains(e.getPoint())) {
                    botao.setBackground(corHover);
                } else {
                    botao.setBackground(corNormal);
                }
            }
        });
    }

    private void ajustarAlturaLinhasTabela() {
        Font font = table.getFont();
        FontMetrics fm = table.getFontMetrics(font);
        int alturaLinha = fm.getHeight() + 16;  // linha mais alta para conforto visual
        table.setRowHeight(alturaLinha);
    }

    private void aplicarEstiloZebradoTabela() {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color corClaro = new Color(245, 245, 245);
            private final Color corEscura = Color.WHITE;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(row % 2 == 0 ? corClaro : corEscura);
                    setForeground(Color.BLACK);
                }

                setHorizontalAlignment(SwingConstants.LEFT);

                return this;
            }
        });

        // Centralizar títulos do header também
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void carregarDados() throws ClassNotFoundException {
        try {
            List<BloqueioAcesso> bloqueios = new BloqueioAcessoDAO().listarTodos();
            atualizarTabela(bloqueios);
            atualizarTotalBloqueios(bloqueios.size());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar bloqueios: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTabela(List<BloqueioAcesso> bloqueios) {
        tableModel.setRowCount(0);
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
                JOptionPane.showMessageDialog(this, "Erro ao buscar bloqueio: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void adicionarBloqueio(ActionEvent e) {
        try {
            BloqueioAcesso novoBloqueio = lerDadosFormulario();
            if (novoBloqueio == null) return;

            BloqueioAcessoDAO dao = new BloqueioAcessoDAO();
            dao.inserir(novoBloqueio);
            carregarDados();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Bloqueio adicionado com sucesso!");
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar bloqueio: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarBloqueio(ActionEvent e) {
        if (bloqueioSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um bloqueio para editar.");
            return;
        }
        try {
            BloqueioAcesso bloqueioEditado = lerDadosFormulario();
            if (bloqueioEditado == null) return;

            bloqueioEditado.setId(bloqueioSelecionadoId);
            BloqueioAcessoDAO dao = new BloqueioAcessoDAO();
            dao.atualizar(bloqueioEditado);
            carregarDados();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Bloqueio editado com sucesso!");
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar bloqueio: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirBloqueio(ActionEvent e) {
        if (bloqueioSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um bloqueio para excluir.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this, "Confirma a exclusão do bloqueio selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                BloqueioAcessoDAO dao = new BloqueioAcessoDAO();
                dao.excluir(bloqueioSelecionadoId);
                carregarDados();
                limparFormulario();
                JOptionPane.showMessageDialog(this, "Bloqueio excluído com sucesso!");
            } catch (ClassNotFoundException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir bloqueio: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filtrarBloqueios(ActionEvent e) {
        String filtro = txtFiltro.getText().trim();
        BloqueioAcessoDAO dao = new BloqueioAcessoDAO();
        List<BloqueioAcesso> filtrados = null;
        try {
            filtrados = dao.filtrarPorClienteContextoNome(filtro);
        } catch (SQLException ex) {
            Logger.getLogger(BloqueioAcessoPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BloqueioAcessoPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        atualizarTabela(filtrados);
        atualizarTotalBloqueios(filtrados.size());
    }

    private BloqueioAcesso lerDadosFormulario() {
        String cliente = txtCliente.getText().trim();
        String contexto = txtContexto.getText().trim();
        String nomeAcesso = txtNomeAcesso.getText().trim();
        String dataStr = txtData.getText().trim();

        if (cliente.isEmpty() || contexto.isEmpty() || nomeAcesso.isEmpty() || dataStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            LocalDate data = LocalDate.parse(dataStr, dateFormatter);
            BloqueioAcesso bloqueio = new BloqueioAcesso();
            bloqueio.setCliente(cliente);
            bloqueio.setContexto(contexto);
            bloqueio.setNomeAcesso(nomeAcesso);
            bloqueio.setData(data);
            return bloqueio;
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;
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

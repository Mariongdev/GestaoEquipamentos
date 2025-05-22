package view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import dao.ClienteDAO;
import dao.EquipamentoAlugadoDAO;
import dao.BloqueioAcessoDAO;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RelatoriosPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JButton btnGerarPDFClientes, btnGerarPDFEquipamentos, btnGerarPDFBloqueios;
    private JComboBox<String> cmbStatusClientes, cmbStatusEquipamentos;
    
    public RelatoriosPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
        // Aba de relatório de clientes
        JPanel clientesPanel = new JPanel(new BorderLayout());
        
        // Filtros
        JPanel filtrosClientesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtrosClientesPanel.add(new JLabel("Status:"));
        cmbStatusClientes = new JComboBox<>(new String[]{"Todos", "Ativo", "Inativo"});
        filtrosClientesPanel.add(cmbStatusClientes);
        
        btnGerarPDFClientes = new JButton("Gerar PDF de Clientes");
        btnGerarPDFClientes.addActionListener(this::gerarPDFClientes);
        filtrosClientesPanel.add(btnGerarPDFClientes);
        
        clientesPanel.add(filtrosClientesPanel, BorderLayout.NORTH);
        
        // Aba de relatório de equipamentos
        JPanel equipamentosPanel = new JPanel(new BorderLayout());
        
        // Filtros
        JPanel filtrosEquipamentosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtrosEquipamentosPanel.add(new JLabel("Status:"));
        cmbStatusEquipamentos = new JComboBox<>(new String[]{"Todos", "Ativo", "Inativo"});
        filtrosEquipamentosPanel.add(cmbStatusEquipamentos);
        
        btnGerarPDFEquipamentos = new JButton("Gerar PDF de Equipamentos");
        btnGerarPDFEquipamentos.addActionListener(this::gerarPDFEquipamentos);
        filtrosEquipamentosPanel.add(btnGerarPDFEquipamentos);
        
        equipamentosPanel.add(filtrosEquipamentosPanel, BorderLayout.NORTH);
        
        // Nova aba de relatório de bloqueios (sem tabela de visualização)
        JPanel bloqueiosPanel = new JPanel(new BorderLayout());
        
        // Botão para gerar PDF
        btnGerarPDFBloqueios = new JButton("Gerar PDF de Bloqueios");
        btnGerarPDFBloqueios.addActionListener(this::gerarPDFBloqueios);
        
        JPanel bloqueiosTopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 20));
        bloqueiosTopPanel.add(btnGerarPDFBloqueios);
        bloqueiosPanel.add(bloqueiosTopPanel, BorderLayout.CENTER);
        
        // Adiciona abas
        tabbedPane.addTab("Clientes", clientesPanel);
        tabbedPane.addTab("Equipamentos Alugados", equipamentosPanel);
        tabbedPane.addTab("Bloqueios", bloqueiosPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void gerarPDFClientes(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório de Clientes");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos PDF (*.pdf)", "pdf"));
        fileChooser.setSelectedFile(new File("relatorio_clientes.pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            try {
                String status = cmbStatusClientes.getSelectedItem().toString();
                if ("Todos".equals(status)) {
                    status = null;
                }
                
                List<model.Cliente> clientes = new ClienteDAO().filtrar("", status);
                
                if (clientes.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Nenhum cliente encontrado com os filtros selecionados",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                criarPDFClientes(clientes, filePath);
                
                // Abre o PDF após gerar
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(filePath));
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Relatório de clientes gerado com sucesso!\nArquivo: " + filePath, 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao gerar PDF: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void criarPDFClientes(List<model.Cliente> clientes, String filePath) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // Configurações de cor
        BaseColor accentColor = new BaseColor(0, 102, 204);
        BaseColor lightGray = new BaseColor(240, 240, 240);
        
        // Metadados
        document.addAuthor("Sistema de Gestão");
        document.addTitle("Relatório de Clientes");
        
        try {
            Image logo = Image.getInstance(getClass().getResource("/assets/logo.png"));
            logo.scaleToFit(120, 120);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("Logo não encontrada: " + e.getMessage());
        }
        
        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, accentColor);
        Paragraph title = new Paragraph("RELATÓRIO DE CLIENTES", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);
        
        // Data de emissão
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph date = new Paragraph("Emitido em: " + java.time.LocalDate.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), smallFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);
        
        // Espaçamento
        document.add(new Paragraph(" "));
        
        // Tabela de dados
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);
        table.setSpacingAfter(20);
        
        // Cabeçalho da tabela
        String[] headers = {"Nome", "Contexto", "BH", "Funcionários", "Modelo Equip.", "Qtd Equip.", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            cell.setBackgroundColor(accentColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Dados dos clientes
        for (model.Cliente cliente : clientes) {
            addTableCell(table, cliente.getNome());
            addTableCell(table, cliente.getContexto());
            addTableCell(table, cliente.getBh());
            addTableCell(table, String.valueOf(cliente.getFuncionarios()));
            addTableCell(table, cliente.getEquipamentoModelo());
            addTableCell(table, String.valueOf(cliente.getEquipamentoQuantidade()));
            
            // Status com cor condicional
            PdfPCell statusCell = new PdfPCell(new Phrase(cliente.getStatus()));
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setPadding(5);
            if ("Ativo".equals(cliente.getStatus())) {
                statusCell.setBackgroundColor(new BaseColor(200, 255, 200));
            } else {
                statusCell.setBackgroundColor(new BaseColor(255, 200, 200));
            }
            table.addCell(statusCell);
        }
            
        document.add(table);
        
        // Estatísticas
        long ativos = clientes.stream().filter(c -> "Ativo".equals(c.getStatus())).count();
        long inativos = clientes.size() - ativos;
        
        Paragraph stats = new Paragraph(
            String.format("Total de Clientes: %d   |   Ativos: %d   |   Inativos: %d", 
                clientes.size(), ativos, inativos),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, accentColor)
        );
        stats.setAlignment(Element.ALIGN_CENTER);
        document.add(stats);
        
        document.close();
    }
    
    private void addTableCell(PdfPTable table, String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private void gerarPDFEquipamentos(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório de Equipamentos");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos PDF (*.pdf)", "pdf"));
        fileChooser.setSelectedFile(new File("relatorio_equipamentos.pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            try {
                String status = cmbStatusEquipamentos.getSelectedItem().toString();
                if ("Todos".equals(status)) {
                    status = null;
                }
                
                List<model.EquipamentoAlugado> equipamentos = new EquipamentoAlugadoDAO().filtrar("", status);
                
                if (equipamentos.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Nenhum equipamento encontrado com os filtros selecionados",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                criarPDFEquipamentos(equipamentos, filePath);
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(filePath));
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Relatório de equipamentos gerado com sucesso!\nArquivo: " + filePath, 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao gerar PDF: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void criarPDFEquipamentos(List<model.EquipamentoAlugado> equipamentos, String filePath) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        BaseColor accentColor = new BaseColor(0, 102, 204);
        BaseColor lightGray = new BaseColor(240, 240, 240);
        
        document.addAuthor("Sistema de Gestão");
        document.addTitle("Relatório de Equipamentos Alugados");
        
        try {
            Image logo = Image.getInstance(getClass().getResource("/assets/logo.png"));
            logo.scaleToFit(120, 120);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("Logo não encontrada: " + e.getMessage());
        }
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, accentColor);
        Paragraph title = new Paragraph("RELATÓRIO DE EQUIPAMENTOS", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);
        
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph date = new Paragraph("Emitido em: " + java.time.LocalDate.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), smallFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);
        
        document.add(new Paragraph(" "));
        
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);
        table.setSpacingAfter(20);
        
        // Cabeçalho da tabela
        String[] headers = {"Nome", "Modelo Equip.", "Qtd Equip.", "Data", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            cell.setBackgroundColor(accentColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (model.EquipamentoAlugado equipamento : equipamentos) {
            addTableCell(table, equipamento.getNome());
            addTableCell(table, equipamento.getEquipamentoModelo());
            addTableCell(table, String.valueOf(equipamento.getEquipamentoQuantidade()));
            addTableCell(table, equipamento.getData().format(dateFormatter));
            
            PdfPCell statusCell = new PdfPCell(new Phrase(equipamento.getStatus()));
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setPadding(5);
            if ("Ativo".equals(equipamento.getStatus())) {
                statusCell.setBackgroundColor(new BaseColor(200, 255, 200));
            } else {
                statusCell.setBackgroundColor(new BaseColor(255, 200, 200));
            }
            table.addCell(statusCell);
        }
            
        document.add(table);
        
        long ativos = equipamentos.stream().filter(e -> "Ativo".equals(e.getStatus())).count();
        long inativos = equipamentos.size() - ativos;
        
        Paragraph stats = new Paragraph(
            String.format("Total de Equipamentos: %d   |   Ativos: %d   |   Inativos: %d", 
                equipamentos.size(), ativos, inativos),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, accentColor)
        );
        stats.setAlignment(Element.ALIGN_CENTER);
        document.add(stats);
        
        document.close();
    }
    
    private void gerarPDFBloqueios(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório de Bloqueios");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos PDF (*.pdf)", "pdf"));
        fileChooser.setSelectedFile(new File("relatorio_bloqueios.pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            try {
                List<model.BloqueioAcesso> bloqueios = new BloqueioAcessoDAO().listarTodos();
                criarPDFBloqueios(bloqueios, filePath);
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(filePath));
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Relatório de bloqueios gerado com sucesso!\nArquivo: " + filePath, 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao gerar PDF: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void criarPDFBloqueios(List<model.BloqueioAcesso> bloqueios, String filePath) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // Configurações de cor
        BaseColor accentColor = new BaseColor(0, 102, 204);
        
        // Metadados
        document.addAuthor("Sistema de Gestão");
        document.addTitle("Relatório de Bloqueios de Acesso");
        
        // Logo (opcional)
        try {
            Image logo = Image.getInstance(getClass().getResource("/assets/logo.png"));
            logo.scaleToFit(120, 120);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("Logo não encontrada: " + e.getMessage());
        }
        
        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, accentColor);
        Paragraph title = new Paragraph("RELATÓRIO DE BLOQUEIOS DE ACESSO", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);
        
        // Data de emissão
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph date = new Paragraph("Emitido em: " + java.time.LocalDate.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), smallFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);
        
        document.add(new Paragraph(" "));
        
        // Tabela de dados (4 colunas sem ID)
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);
        table.setSpacingAfter(20);
        
        // Cabeçalho da tabela
        String[] headers = {"Cliente", "Contexto", "Nome Acesso", "Data"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            cell.setBackgroundColor(accentColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Dados dos bloqueios (sem ID)
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (model.BloqueioAcesso bloqueio : bloqueios) {
            addTableCell(table, bloqueio.getCliente());
            addTableCell(table, bloqueio.getContexto());
            addTableCell(table, bloqueio.getNomeAcesso());
            addTableCell(table, bloqueio.getData().format(dateFormatter));
        }
        
        document.add(table);
        
        // Estatísticas
        Paragraph stats = new Paragraph(
            String.format("Total de Bloqueios: %d", bloqueios.size()),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, accentColor)
        );
        stats.setAlignment(Element.ALIGN_CENTER);
        document.add(stats);
        
        document.close();
    }
}
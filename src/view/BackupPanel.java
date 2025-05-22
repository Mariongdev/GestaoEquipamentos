package view;

import model.Database;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.SQLException;

public class BackupPanel extends JPanel {
    private JButton btnExportar, btnImportar;
    private JFileChooser fileChooser;
    
    public BackupPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        
        btnExportar = new JButton("Exportar Backup");
        btnExportar.addActionListener(this::exportarBackup);
        buttonPanel.add(btnExportar);
        
        btnImportar = new JButton("Importar Backup");
        btnImportar.addActionListener(this::importarBackup);
        buttonPanel.add(btnImportar);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(buttonPanel, gbc);
    }
    
    private void exportarBackup(ActionEvent e) {
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Database.backup(file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, 
                    "Backup exportado com sucesso para: " + file.getAbsolutePath(), 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao exportar backup: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importarBackup(ActionEvent e) {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja importar o backup? Todos os dados atuais serão substituídos.", 
                "Confirmação", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Database.restore(file.getAbsolutePath());
                    JOptionPane.showMessageDialog(this, 
                        "Backup importado com sucesso a partir de: " + file.getAbsolutePath(), 
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Erro ao importar backup: " + ex.getMessage(), 
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
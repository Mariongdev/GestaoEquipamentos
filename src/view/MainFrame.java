package view;

import javax.swing.*;
import java.awt.*;
import model.Database;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;

    public MainFrame() throws ClassNotFoundException {
        super("Gestão de Clientes e Equipamentos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initComponents();
    }

   private void initComponents() throws ClassNotFoundException {
    tabbedPane = new JTabbedPane();

    // Adiciona as abas
    tabbedPane.addTab("Clientes", new ClientePanel());
    tabbedPane.addTab("Equipamentos Alugados", new EquipamentoAlugadoPanel());
    tabbedPane.addTab("Bloqueio de Acesso", new BloqueioAcessoPanel());
    tabbedPane.addTab("Relatórios", new RelatoriosPanel());
    tabbedPane.addTab("Backup", new BackupPanel());

    Font tabFont = new Font("Arial", Font.BOLD, 14);
    tabbedPane.setFont(tabFont);

    // Aplica espaçamento nas abas
    int tabCount = tabbedPane.getTabCount();
    for (int i = 0; i < tabCount; i++) {
        JLabel tabLabel = new JLabel(tabbedPane.getTitleAt(i));
        tabLabel.setFont(tabFont);
        tabLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // padding: top,left,bottom,right
        tabbedPane.setTabComponentAt(i, tabLabel);
    }

    add(tabbedPane);
}
    /*
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Database.initialize();

            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Erro ao carregar aplicação:\n" + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erro na inicialização: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    */
}

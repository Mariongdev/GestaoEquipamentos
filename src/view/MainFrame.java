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

        add(tabbedPane);
    }



   /* public static void main(String[] args) {
        try {
            // Configura o look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Inicializa o banco de dados
            System.out.println("Inicializando banco de dados...");
            Database.initialize();
            System.out.println("Banco de dados inicializado com sucesso!");

            // Exibe a janela
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            });

        }catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Erro ao configurar interface gráfica: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro fatal na inicialização: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erro fatal na inicialização: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }*/
}

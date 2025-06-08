package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JCheckBox chkMostrarSenha;
    private JButton btnLogin;

    public LoginFrame() {
        super("Login - Sistema de Gestão");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setupEnterKeyListener();
        setupMostrarSenhaListener();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Título
        JLabel lblTitulo = new JLabel("Bem-vindo ao Sistema");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0, 102, 204));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        centerPanel.add(lblTitulo);

        // Usuário
        JLabel lblUsuario = new JLabel("Usuário:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 16));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblUsuario);

        txtUsuario = new JTextField(20);
        txtUsuario.setMaximumSize(new Dimension(250, 35));
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(txtUsuario);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Senha
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Arial", Font.BOLD, 16));
        lblSenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblSenha);

        txtSenha = new JPasswordField(20);
        txtSenha.setMaximumSize(new Dimension(250, 35));
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(txtSenha);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Mostrar senha
        chkMostrarSenha = new JCheckBox("Mostrar senha");
        chkMostrarSenha.setFont(new Font("Arial", Font.PLAIN, 12));
        chkMostrarSenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(chkMostrarSenha);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogin.setPreferredSize(new Dimension(100, 35));
        btnLogin.addActionListener(this::realizarLogin);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.addActionListener(e -> System.exit(0));

        panelBotoes.add(btnLogin);
        panelBotoes.add(btnCancelar);

        centerPanel.add(panelBotoes);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void setupMostrarSenhaListener() {
        chkMostrarSenha.addItemListener(e -> {
            txtSenha.setEchoChar(chkMostrarSenha.isSelected() ? (char) 0 : '•');
        });
    }

    private void setupEnterKeyListener() {
        txtUsuario.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    txtSenha.requestFocus();
            }
        });

        txtSenha.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    btnLogin.doClick();
            }
        });
    }

    private void realizarLogin(ActionEvent e) {
        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (usuario.equals("admin") && senha.equals("963741")) {
            this.dispose();
            try {
                new MainFrame().setVisible(true);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha incorretos!",
                    "Erro de Login", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtUsuario.requestFocus();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        try {
            model.Database.initialize();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao conectar com o banco de dados:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erro ao iniciar o sistema:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginFrame extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnLogin;
    private JCheckBox chkMostrarSenha;

    public LoginFrame() {
        super("Login - Sistema de Gestão");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250); // Aumentei a altura para acomodar a checkbox
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setupEnterKeyListener();
        setupMostrarSenhaListener();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("Acesso ao Sistema");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblTitulo, gbc);

        // Usuário
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Usuário:"), gbc);

        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        panel.add(txtUsuario, gbc);

        // Senha
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        txtSenha = new JPasswordField(15);
        panel.add(txtSenha, gbc);

        // Checkbox Mostrar Senha
        gbc.gridx = 1;
        gbc.gridy++;
        chkMostrarSenha = new JCheckBox("Mostrar senha");
        panel.add(chkMostrarSenha, gbc);

        // Botões
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnLogin = new JButton("Login");
        btnLogin.addActionListener(this::realizarLogin);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> System.exit(0));

        botoesPanel.add(btnLogin);
        botoesPanel.add(btnCancelar);
        panel.add(botoesPanel, gbc);

        add(panel);
    }

    private void setupMostrarSenhaListener() {
        chkMostrarSenha.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (chkMostrarSenha.isSelected()) {
                    txtSenha.setEchoChar((char) 0); // Mostra a senha
                } else {
                    txtSenha.setEchoChar('•'); // Oculta a senha
                }
            }
        });
    }

    private void setupEnterKeyListener() {
        txtSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });

        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSenha.requestFocus();
                }
            }
        });
    }

    // ✅ Removido "throws ClassNotFoundException"
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            model.Database.initialize();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao conectar com o banco de dados:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Lambda com tratamento de exceção
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erro ao iniciar o sistema:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

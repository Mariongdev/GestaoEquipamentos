package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Validacao {
    public static boolean validarCampoObrigatorio(JTextField campo, String nomeCampo) {
        if (campo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                nomeCampo + " é obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            campo.requestFocus();
            return false;
        }
        return true;
    }
    
    public static boolean validarNumero(JTextField campo, String nomeCampo) {
        try {
            Integer.parseInt(campo.getText());
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, 
                nomeCampo + " deve ser um número válido", "Aviso", JOptionPane.WARNING_MESSAGE);
            campo.requestFocus();
            return false;
        }
    }
    
    public static boolean validarData(JTextField campo, String nomeCampo) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(campo.getText(), formatter);
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, 
                nomeCampo + " inválida. Use o formato dd/mm/aaaa", "Aviso", JOptionPane.WARNING_MESSAGE);
            campo.requestFocus();
            return false;
        }
    }
}
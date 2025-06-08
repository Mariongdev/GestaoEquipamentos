package model;

import java.time.LocalDate;

public class BloqueioAcesso {
    private int id;
    private String cliente;
    private String contexto;
    private String nomeAcesso;
    private LocalDate data;
    
    public BloqueioAcesso() {}
    
    public BloqueioAcesso(String cliente, String contexto, String nomeAcesso, LocalDate data) {
        this.cliente = cliente;
        this.contexto = contexto;
        this.nomeAcesso = nomeAcesso;
        this.data = data;
    }

    public BloqueioAcesso(int bloqueioSelecionadoId, String text, String text0, String text1, LocalDate data) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getContexto() { return contexto; }
    public void setContexto(String contexto) { this.contexto = contexto; }
    public String getNomeAcesso() { return nomeAcesso; }
    public void setNomeAcesso(String nomeAcesso) { this.nomeAcesso = nomeAcesso; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public void setUsuario(String usuario) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setMotivo(String motivo) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Object getUsuario() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Object getMotivo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
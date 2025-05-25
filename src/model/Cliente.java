package model;

public class Cliente {
    private int id;
    private String nome;
    private String contexto;
    private String bh;
    private int funcionarios;
    private String equipamentoModelo;
    private int equipamentoQuantidade;
    private String status;

    public Cliente() {
    }

    public Cliente(String nome, String contexto, String bh, int funcionarios, 
            String equipamentoModelo, int equipamentoQuantidade, String status) {
        this.nome = nome;
        this.contexto = contexto;
        this.bh = bh;
        this.funcionarios = funcionarios;
        this.equipamentoModelo = equipamentoModelo;
        this.equipamentoQuantidade = equipamentoQuantidade;
        this.status = status;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public int getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(int funcionarios) {
        this.funcionarios = funcionarios;
    }

    public String getEquipamentoModelo() {
        return equipamentoModelo;
    }

    public void setEquipamentoModelo(String equipamentoModelo) {
        this.equipamentoModelo = equipamentoModelo;
    }

    public int getEquipamentoQuantidade() {
        return equipamentoQuantidade;
    }

    public void setEquipamentoQuantidade(int equipamentoQuantidade) {
        this.equipamentoQuantidade = equipamentoQuantidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEquipModelo(String equipModelo) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setEquipQuantidade(int equipQuantidade) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Object getEquipQuantidade() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getEquipModelo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

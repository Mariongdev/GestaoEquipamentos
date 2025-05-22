package model;

import java.time.LocalDate;

public class EquipamentoAlugado {
    private int id;
    private String nome;
    private String equipamentoModelo;
    private int equipamentoQuantidade;
    private LocalDate data;
    private String status;

    public EquipamentoAlugado() {
    }

    public EquipamentoAlugado(String nome, String equipamentoModelo, 
            int equipamentoQuantidade, LocalDate data, String status) {
        this.nome = nome;
        this.equipamentoModelo = equipamentoModelo;
        this.equipamentoQuantidade = equipamentoQuantidade;
        this.data = data;
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
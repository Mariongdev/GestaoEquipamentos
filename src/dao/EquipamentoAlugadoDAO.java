package dao;

import model.EquipamentoAlugado;
import model.Database;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoAlugadoDAO {
    public void inserir(EquipamentoAlugado equipamento) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO equipamentos_alugados (nome, equipamento_modelo, "
                + "equipamento_quantidade, data, status) VALUES (?, ?, ?, ?, ?)";
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, equipamento.getNome());
            stmt.setString(2, equipamento.getEquipamentoModelo());
            stmt.setInt(3, equipamento.getEquipamentoQuantidade());
            stmt.setString(4, equipamento.getData().toString());
            stmt.setString(5, equipamento.getStatus());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    equipamento.setId(rs.getInt(1));
                }
            }
        }
    }
    
    public void atualizar(EquipamentoAlugado equipamento) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE equipamentos_alugados SET nome = ?, equipamento_modelo = ?, "
                + "equipamento_quantidade = ?, data = ?, status = ? WHERE id = ?";
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipamento.getNome());
            stmt.setString(2, equipamento.getEquipamentoModelo());
            stmt.setInt(3, equipamento.getEquipamentoQuantidade());
            stmt.setString(4, equipamento.getData().toString());
            stmt.setString(5, equipamento.getStatus());
            stmt.setInt(6, equipamento.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void excluir(int id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM equipamentos_alugados WHERE id = ?";
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public EquipamentoAlugado buscarPorId(int id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM equipamentos_alugados WHERE id = ?";
        EquipamentoAlugado equipamento = null;
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    equipamento = new EquipamentoAlugado();
                    equipamento.setId(rs.getInt("id"));
                    equipamento.setNome(rs.getString("nome"));
                    equipamento.setEquipamentoModelo(rs.getString("equipamento_modelo"));
                    equipamento.setEquipamentoQuantidade(rs.getInt("equipamento_quantidade"));
                    equipamento.setData(LocalDate.parse(rs.getString("data")));
                    equipamento.setStatus(rs.getString("status"));
                }
            }
        }
        
        return equipamento;
    }
    
    public List<EquipamentoAlugado> listarTodos() throws SQLException, ClassNotFoundException {
        List<EquipamentoAlugado> equipamentos = new ArrayList<>();
        String sql = "SELECT * FROM equipamentos_alugados";
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                EquipamentoAlugado equipamento = new EquipamentoAlugado();
                equipamento.setId(rs.getInt("id"));
                equipamento.setNome(rs.getString("nome"));
                equipamento.setEquipamentoModelo(rs.getString("equipamento_modelo"));
                equipamento.setEquipamentoQuantidade(rs.getInt("equipamento_quantidade"));
                equipamento.setData(LocalDate.parse(rs.getString("data")));
                equipamento.setStatus(rs.getString("status"));
                
                equipamentos.add(equipamento);
            }
        }
        
        return equipamentos;
    }
    
    public List<EquipamentoAlugado> filtrar(String filtro, String status) throws SQLException, ClassNotFoundException {
        List<EquipamentoAlugado> equipamentos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM equipamentos_alugados WHERE 1=1");
        
        if (filtro != null && !filtro.isEmpty()) {
            sql.append(" AND (nome LIKE ? OR equipamento_modelo LIKE ? OR data LIKE ?)");
        }
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
        }
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (filtro != null && !filtro.isEmpty()) {
                String likePattern = "%" + filtro + "%";
                stmt.setString(paramIndex++, likePattern);
                stmt.setString(paramIndex++, likePattern);
                stmt.setString(paramIndex++, likePattern);
            }
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EquipamentoAlugado equipamento = new EquipamentoAlugado();
                    equipamento.setId(rs.getInt("id"));
                    equipamento.setNome(rs.getString("nome"));
                    equipamento.setEquipamentoModelo(rs.getString("equipamento_modelo"));
                    equipamento.setEquipamentoQuantidade(rs.getInt("equipamento_quantidade"));
                    equipamento.setData(LocalDate.parse(rs.getString("data")));
                    equipamento.setStatus(rs.getString("status"));
                    
                    equipamentos.add(equipamento);
                }
            }
        }
        
        return equipamentos;
    }
}
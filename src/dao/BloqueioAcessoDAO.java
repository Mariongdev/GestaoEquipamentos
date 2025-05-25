package dao;

import model.BloqueioAcesso;
import model.Database;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BloqueioAcessoDAO {
    public void inserir(BloqueioAcesso bloqueio) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO bloqueios_acesso (cliente, contexto, nome_acesso, data) VALUES (?, ?, ?, ?)";
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, bloqueio.getCliente());
            stmt.setString(2, bloqueio.getContexto());
            stmt.setString(3, bloqueio.getNomeAcesso());
            stmt.setString(4, bloqueio.getData().toString());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    bloqueio.setId(rs.getInt(1));
                }
            }
        }
    }
    
    public void atualizar(BloqueioAcesso bloqueio) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE bloqueios_acesso SET cliente = ?, contexto = ?, nome_acesso = ?, data = ? WHERE id = ?";
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bloqueio.getCliente());
            stmt.setString(2, bloqueio.getContexto());
            stmt.setString(3, bloqueio.getNomeAcesso());
            stmt.setString(4, bloqueio.getData().toString());
            stmt.setInt(5, bloqueio.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void excluir(int id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM bloqueios_acesso WHERE id = ?";
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public BloqueioAcesso buscarPorId(int id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM bloqueios_acesso WHERE id = ?";
        BloqueioAcesso bloqueio = null;
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bloqueio = new BloqueioAcesso();
                    bloqueio.setId(rs.getInt("id"));
                    bloqueio.setCliente(rs.getString("cliente"));
                    bloqueio.setContexto(rs.getString("contexto"));
                    bloqueio.setNomeAcesso(rs.getString("nome_acesso"));
                    bloqueio.setData(LocalDate.parse(rs.getString("data")));
                }
            }
        }
        
        return bloqueio;
    }
    
    public List<BloqueioAcesso> listarTodos() throws SQLException, ClassNotFoundException {
        List<BloqueioAcesso> bloqueios = new ArrayList<>();
        String sql = "SELECT * FROM bloqueios_acesso";
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                BloqueioAcesso bloqueio = new BloqueioAcesso();
                bloqueio.setId(rs.getInt("id"));
                bloqueio.setCliente(rs.getString("cliente"));
                bloqueio.setContexto(rs.getString("contexto"));
                bloqueio.setNomeAcesso(rs.getString("nome_acesso"));
                bloqueio.setData(LocalDate.parse(rs.getString("data")));
                
                bloqueios.add(bloqueio);
            }
        }
        
        return bloqueios;
    }
    
    public List<BloqueioAcesso> filtrar(String filtro) throws SQLException, ClassNotFoundException {
        List<BloqueioAcesso> bloqueios = new ArrayList<>();
        String sql = "SELECT * FROM bloqueios_acesso WHERE cliente LIKE ? OR contexto LIKE ? OR nome_acesso LIKE ? OR data LIKE ?";
        
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeFilter = "%" + filtro + "%";
            stmt.setString(1, likeFilter);
            stmt.setString(2, likeFilter);
            stmt.setString(3, likeFilter);
            stmt.setString(4, likeFilter);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BloqueioAcesso bloqueio = new BloqueioAcesso();
                    bloqueio.setId(rs.getInt("id"));
                    bloqueio.setCliente(rs.getString("cliente"));
                    bloqueio.setContexto(rs.getString("contexto"));
                    bloqueio.setNomeAcesso(rs.getString("nome_acesso"));
                    bloqueio.setData(LocalDate.parse(rs.getString("data")));
                    
                    bloqueios.add(bloqueio);
                }
            }
        }
        
        return bloqueios;
    }
}
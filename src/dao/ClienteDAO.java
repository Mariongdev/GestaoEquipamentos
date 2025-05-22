package dao;

import model.Cliente;
import model.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nome, contexto, bh, funcionarios, "
                + "equipamento_modelo, equipamento_quantidade, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getContexto());
            stmt.setString(3, cliente.getBh());
            stmt.setInt(4, cliente.getFuncionarios());
            stmt.setString(5, cliente.getEquipamentoModelo());
            stmt.setInt(6, cliente.getEquipamentoQuantidade());
            stmt.setString(7, cliente.getStatus());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
        }
    }
    
    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nome = ?, contexto = ?, bh = ?, "
                + "funcionarios = ?, equipamento_modelo = ?, "
                + "equipamento_quantidade = ?, status = ? WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getContexto());
            stmt.setString(3, cliente.getBh());
            stmt.setInt(4, cliente.getFuncionarios());
            stmt.setString(5, cliente.getEquipamentoModelo());
            stmt.setInt(6, cliente.getEquipamentoQuantidade());
            stmt.setString(7, cliente.getStatus());
            stmt.setInt(8, cliente.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        Cliente cliente = null;
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setContexto(rs.getString("contexto"));
                    cliente.setBh(rs.getString("bh"));
                    cliente.setFuncionarios(rs.getInt("funcionarios"));
                    cliente.setEquipamentoModelo(rs.getString("equipamento_modelo"));
                    cliente.setEquipamentoQuantidade(rs.getInt("equipamento_quantidade"));
                    cliente.setStatus(rs.getString("status"));
                }
            }
        }
        
        return cliente;
    }
    
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setContexto(rs.getString("contexto"));
                cliente.setBh(rs.getString("bh"));
                cliente.setFuncionarios(rs.getInt("funcionarios"));
                cliente.setEquipamentoModelo(rs.getString("equipamento_modelo"));
                cliente.setEquipamentoQuantidade(rs.getInt("equipamento_quantidade"));
                cliente.setStatus(rs.getString("status"));
                
                clientes.add(cliente);
            }
        }
        
        return clientes;
    }
    
    public List<Cliente> filtrar(String filtro, String status) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM clientes WHERE 1=1");
        
        if (filtro != null && !filtro.isEmpty()) {
            sql.append(" AND (nome LIKE ? OR contexto LIKE ? OR bh LIKE ? OR "
                    + "equipamento_modelo LIKE ?)");
        }
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
        }
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (filtro != null && !filtro.isEmpty()) {
                String likePattern = "%" + filtro + "%";
                stmt.setString(paramIndex++, likePattern);
                stmt.setString(paramIndex++, likePattern);
                stmt.setString(paramIndex++, likePattern);
                stmt.setString(paramIndex++, likePattern);
            }
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setContexto(rs.getString("contexto"));
                    cliente.setBh(rs.getString("bh"));
                    cliente.setFuncionarios(rs.getInt("funcionarios"));
                    cliente.setEquipamentoModelo(rs.getString("equipamento_modelo"));
                    cliente.setEquipamentoQuantidade(rs.getInt("equipamento_quantidade"));
                    cliente.setStatus(rs.getString("status"));
                    
                    clientes.add(cliente);
                }
            }
        }
        
        return clientes;
    }
}
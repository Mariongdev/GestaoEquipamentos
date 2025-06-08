package dao;

import model.BloqueioAcesso;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BloqueioAcessoDAO {

    private static final String URL = "jdbc:sqlite:database.db";

    // Método para conectar
    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(URL);
    }

    public void inserir(BloqueioAcesso bloqueio) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO bloqueios_acesso (cliente, contexto, nome_acesso, data) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
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
        try (Connection conn = getConnection();
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
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public BloqueioAcesso buscarPorId(int id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM bloqueios_acesso WHERE id = ?";
        BloqueioAcesso bloqueio = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bloqueio = mapResultSetToBloqueio(rs);
                }
            }
        }
        return bloqueio;
    }

    public List<BloqueioAcesso> listarTodos() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM bloqueios_acesso ORDER BY cliente ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<BloqueioAcesso> bloqueios = new ArrayList<>();
            while (rs.next()) {
                bloqueios.add(mapResultSetToBloqueio(rs));
            }
            return bloqueios;
        }
    }

    // Método genérico para filtro livre em várias colunas
    public List<BloqueioAcesso> filtrar(String filtro) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM bloqueios_acesso WHERE cliente LIKE ? OR contexto LIKE ? OR nome_acesso LIKE ? OR data LIKE ? ORDER BY cliente ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeFilter = "%" + filtro + "%";
            stmt.setString(1, likeFilter);
            stmt.setString(2, likeFilter);
            stmt.setString(3, likeFilter);
            stmt.setString(4, likeFilter);

            try (ResultSet rs = stmt.executeQuery()) {
                List<BloqueioAcesso> bloqueios = new ArrayList<>();
                while (rs.next()) {
                    bloqueios.add(mapResultSetToBloqueio(rs));
                }
                return bloqueios;
            }
        }
    }

    // Implementa filtragem só pelo campo cliente
    public List<BloqueioAcesso> filtrarPorCliente(String filtro) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM bloqueios_acesso WHERE cliente LIKE ? ORDER BY cliente ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + filtro + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                List<BloqueioAcesso> bloqueios = new ArrayList<>();
                while (rs.next()) {
                    bloqueios.add(mapResultSetToBloqueio(rs));
                }
                return bloqueios;
            }
        }
    }

    // Implementa filtragem só pelo campo contexto (supondo que seja descrição)
    public List<BloqueioAcesso> filtrarPorDescricao(String filtro) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM bloqueios_acesso WHERE contexto LIKE ? ORDER BY cliente ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + filtro + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                List<BloqueioAcesso> bloqueios = new ArrayList<>();
                while (rs.next()) {
                    bloqueios.add(mapResultSetToBloqueio(rs));
                }
                return bloqueios;
            }
        }
    }

    // Buscar todos (igual ao listarTodos, mas sem throws - opcional)
    public List<BloqueioAcesso> buscarTodos() throws SQLException, ClassNotFoundException {
        return listarTodos();
    }

    // Filtra por cliente OU contexto OU nome_acesso (igual ao filtrar mas nomeando explicitamente)
    public List<BloqueioAcesso> filtrarPorClienteContextoNome(String filtro) throws SQLException, ClassNotFoundException {
        return filtrar(filtro);
    }

    // Método auxiliar para mapear ResultSet para objeto BloqueioAcesso
    private BloqueioAcesso mapResultSetToBloqueio(ResultSet rs) throws SQLException {
        BloqueioAcesso bloqueio = new BloqueioAcesso();
        bloqueio.setId(rs.getInt("id"));
        bloqueio.setCliente(rs.getString("cliente"));
        bloqueio.setContexto(rs.getString("contexto"));
        bloqueio.setNomeAcesso(rs.getString("nome_acesso"));
        bloqueio.setData(LocalDate.parse(rs.getString("data")));
        return bloqueio;
    }
}

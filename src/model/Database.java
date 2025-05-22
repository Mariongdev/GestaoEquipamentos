package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:database.db";
    private static Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite não encontrado: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    public static void initialize() throws SQLException {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = getConnection();

            // Verifica se a tabela clientes já existe
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "clientes", null);

            if (!tables.next()) {
                stmt = conn.createStatement();
                String sqlClientes = "CREATE TABLE clientes ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "nome TEXT NOT NULL, "
                        + "contexto TEXT, "
                        + "bh TEXT CHECK(bh IN ('Sim', 'Não')), "
                        + "funcionarios INTEGER, "
                        + "equipamento_modelo TEXT, "
                        + "equipamento_quantidade INTEGER, "
                        + "status TEXT CHECK(status IN ('Ativo', 'Inativo')))";
                stmt.execute(sqlClientes);
            }

            // Verifica se a tabela equipamentos_alugados já existe
            tables = meta.getTables(null, null, "equipamentos_alugados", null);
            if (!tables.next()) {
                if (stmt == null) {
                    stmt = conn.createStatement();
                }
                String sqlEquipamentos = "CREATE TABLE equipamentos_alugados ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "nome TEXT NOT NULL, "
                        + "equipamento_modelo TEXT, "
                        + "equipamento_quantidade INTEGER, "
                        + "data TEXT, "
                        + "status TEXT CHECK(status IN ('Ativo', 'Inativo')))";
                stmt.execute(sqlEquipamentos);
            }

            // Verifica se a tabela equipamentos_alugados já existe
            tables = meta.getTables(null, null, "bloqueios_acesso", null);
            if (!tables.next()) {
                if (stmt == null) {
                    stmt = conn.createStatement();
                }

                String sqlBloqueios = "CREATE TABLE IF NOT EXISTS bloqueios_acesso ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "cliente TEXT NOT NULL,"
                        + "contexto TEXT,"
                        + "nome_acesso TEXT NOT NULL,"
                        + "data TEXT)";

                stmt.execute(sqlBloqueios);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    public static void backup(String filePath) throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String sql = "BACKUP TO '" + filePath + "'";
            stmt.execute(sql);
        }
    }

    public static void restore(String filePath) throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String sql = "RESTORE FROM '" + filePath + "'";
            stmt.execute(sql);
        }
    }
}

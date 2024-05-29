package model;

import java.sql.*;
import java.util.ArrayList;
import web.AppListener;

public class Users {

    private long rowid;
    private String name;
    private String login;
    private String role;
    private String passwordHash;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS users("
                + "login VARHCAR(50) UNIQUE NOT NULL,"
                + "name VARCHAR(200) NOT NULL,"
                + "role VARCHAR(20) NOT NULL,"
                + "password_hash VARCHAR NOT NULL"
                + ")";
    }

    public static int getTotalUsers() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM users";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        int total = 0;
        if (rs.next()) {
            total = rs.getInt("total");
        }
        rs.close();
        stmt.close();
        con.close();
        return total;
    }

    public static ArrayList<Users> getUsers(int page, int recordsPerPage, int column, int sort) throws Exception {
        ArrayList<Users> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        int startIndex = (page - 1) * recordsPerPage;
        String sql = "";
        if (sort == 0) {
            column = 0;
        }
        switch (column) {
            case 1:
                if (sort == 1) {
                    sql = "SELECT rowid, * from users ORDER BY login ASC LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT rowid, * from users ORDER BY login DESC LIMIT ?,?";
                }
                break;
            case 2:
                if (sort == 1) {
                    sql = "SELECT rowid, * from users ORDER BY name ASC LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT rowid, * from users ORDER BY name DESC LIMIT ?,?";
                }
                break;
            case 3:
                if (sort == 1) {
                    sql = "SELECT rowid, * from users ORDER BY role ASC LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT rowid, * from users ORDER BY role DESC LIMIT ?,?";
                }
                break;
            default:
                sql = "SELECT rowid, * from users ORDER BY name LIMIT ?,?";
                break;
        }

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowId = rs.getLong("rowid");
            String login = rs.getString("login");
            String name = rs.getString("name");
            String role = rs.getString("role");
            String passwordHash = rs.getString("password_hash");
            list.add(new Users(rowId, name, login, role, passwordHash));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Users> getUsersAll() throws Exception {
        ArrayList<Users> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        // Executando o SQL para resgatar todos os registros da tabela
        String sql = "SELECT rowid, * from users ORDER BY name";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        // Enquanto houver registros irá adicionar ao array o novo objeto contendo os dados do usuario
        while (rs.next()) {
            long rowId = rs.getLong("rowid");
            String login = rs.getString("login");
            String name = rs.getString("name");
            String role = rs.getString("role");
            String passwordHash = rs.getString("password_hash");
            list.add(new Users(rowId, name, login, role, passwordHash));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static Users getUser(String login, String password) throws Exception {
        Users user = null;
        Connection con = AppListener.getConnection();
        // Buscando no banco o ID do usuario que corresponde ao login e senha
        String sql = "SELECT rowid, * from users WHERE login=? AND password_hash=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        // Setando as "?" como login e o password recebido como parametros
        stmt.setString(1, login);
        stmt.setString(2, AppListener.getMd5Hash(password));
        ResultSet rs = stmt.executeQuery();
        // Verificando se retornou um dado, se retornou cria um objeto com os dados do usuario
        if (rs.next()) {
            long rowId = rs.getLong("rowid");
            String name = rs.getString("name");
            String role = rs.getString("role");
            String passwordHash = rs.getString("password_hash");
            user = new Users(rowId, name, login, role, passwordHash);
        }
        rs.close();
        stmt.close();
        con.close();
        return user;
    }

    public static void insertUser(String login, String name, String role, String password) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO users(login, name, role, password_hash)"
                + "VALUES(?,?,?,?)";
        // Preparando a string de sql a ser executado e setando as "?" com os parametros
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, login);
        stmt.setString(2, name);
        stmt.setString(3, role);
        stmt.setString(4, AppListener.getMd5Hash(password));
        // Try catch para pegar erro de criar login já existente 
        try {
            stmt.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) { // O código de erro de violação de chave unica no SQLite é 19
                throw new Exception("Error: Login existente, selecione outro login", e);
            } else {
                throw new Exception("Error: Falha ao executar o SQL", e); // Tratar qualquer outro erro 

            }
        }
        stmt.close();
        con.close();
    }

    public static void updateUser(long rowid, String login, String name, String role) throws Exception {
        Connection con = AppListener.getConnection();
        // Identico ao insert com a diferença de que o login seja igual ao do usuario logado
        String sql = "UPDATE users SET login=?, name=?, role=? WHERE rowid=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, login);
        stmt.setString(2, name);
        stmt.setString(3, role);
        stmt.setLong(4, rowid);
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public static void updatePassword(long rowid, String password) throws Exception {
        Connection con = AppListener.getConnection();
        // Identico ao insert com a diferença de que o login seja igual ao do usuario logado
        String sql = "UPDATE users SET password_hash=? WHERE rowid=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, AppListener.getMd5Hash(password));
        stmt.setLong(2, rowid);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void deleteUser(long rowId) throws Exception {
        Connection con = AppListener.getConnection();
        // Deleta todos os dados do usuario que corresponde ao id do parametro
        String sql = "DELETE FROM users WHERE rowid=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, rowId);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public Users(long rowid, String name, String login, String role, String passwordHash) {
        this.rowid = rowid;
        this.name = name;
        this.login = login;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public long getRowid() {
        return rowid;
    }

    public void setRowid(long rowid) {
        this.rowid = rowid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}

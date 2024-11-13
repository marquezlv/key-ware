package model;

import java.sql.*;
import java.util.ArrayList;
import web.AppListener;

public class Employees {

    private long rowid;
    private String name;
    private String type;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS employees("
                + "cd_employee INTEGER PRIMARY KEY,"
                + "nm_employee VARCHAR(50) NOT NULL,"
                + "nm_type VARCHAR(50) NOT NULL"
                + ")";
    }

    public static ArrayList<Employees> getEmployees() throws Exception {
        ArrayList<Employees> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM employees ORDER BY nm_employee");
        while (rs.next()) {
            long rowId = rs.getLong("cd_employee");
            String name = rs.getString("nm_employee");
            String type = rs.getString("nm_type");
            list.add(new Employees(rowId, name, type));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static int getTotalEmployees(String search, String searchSubject) throws Exception {
        Connection con = AppListener.getConnection();
        String baseSQL = "SELECT COUNT(DISTINCT employees.cd_employee) AS total FROM employees "
                + "LEFT JOIN employees_subjects es ON employees.cd_employee = es.cd_employee "
                + "LEFT JOIN subjects s ON es.cd_subject = s.cd_subject ";
        String whereClause = "";

        // Adiciona o filtro de pesquisa para `nm_employee`, se `search` não for vazio
        if (search != null && !search.isEmpty()) {
            whereClause += "WHERE employees.nm_employee LIKE ? ";
        }

        // Adiciona o filtro de pesquisa para `nm_subject`, se `searchSubject` não for vazio
        if (searchSubject != null && !searchSubject.isEmpty()) {
            whereClause += (whereClause.isEmpty() ? "WHERE " : "AND ") + "s.nm_subject LIKE ? ";
        }

        String sql = baseSQL + whereClause;

        PreparedStatement stmt = con.prepareStatement(sql);
        int paramIndex = 1;

        // Adiciona parâmetros de pesquisa para `nm_employee` e `nm_subject`, se aplicável
        if (search != null && !search.isEmpty()) {
            stmt.setString(paramIndex++, "%" + search + "%");
        }
        if (searchSubject != null && !searchSubject.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchSubject + "%");
        }

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

    public static ArrayList<Employees> getEmployeesPages(int page, int recordsPerPage, int column, int sort, String search, String searchSubject) throws Exception {
        ArrayList<Employees> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        int startIndex = (page - 1) * recordsPerPage;

        String baseSQL = "SELECT DISTINCT employees.cd_employee, employees.nm_employee, employees.nm_type "
                + "FROM employees "
                + "LEFT JOIN employees_subjects es ON employees.cd_employee = es.cd_employee "
                + "LEFT JOIN subjects s ON es.cd_subject = s.cd_subject ";
        String whereClause = "";
        String orderClause = "";

        // Adiciona o filtro de pesquisa para `nm_employee`, se `search` não for vazio
        if (search != null && !search.isEmpty()) {
            whereClause += "WHERE employees.nm_employee LIKE ? ";
        }

        // Adiciona o filtro de pesquisa para `nm_subject`, se `searchSubject` não for vazio
        if (searchSubject != null && !searchSubject.isEmpty()) {
            whereClause += (whereClause.isEmpty() ? "WHERE " : "AND ") + "s.nm_subject LIKE ? ";
        }

        // Define a cláusula de ordenação com base nos valores de `column` e `sort`
        switch (column) {
            case 1:
                orderClause = "ORDER BY employees.nm_employee " + (sort == 2 ? "DESC" : "ASC") + " ";
                break;
            case 2:
                orderClause = "ORDER BY employees.nm_type " + (sort == 2 ? "DESC" : "ASC") + " ";
                break;
            default:
                orderClause = "ORDER BY employees.nm_employee ASC ";
                break;
        }

        // Monta a consulta final com filtro, ordenação e paginação
        String sql = baseSQL + whereClause + orderClause + "LIMIT ?, ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        int paramIndex = 1;

        // Adiciona parâmetros de pesquisa para `nm_employee` e `nm_subject`, se aplicável
        if (search != null && !search.isEmpty()) {
            stmt.setString(paramIndex++, "%" + search + "%");
        }
        if (searchSubject != null && !searchSubject.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchSubject + "%");
        }

        // Parâmetros de paginação
        stmt.setInt(paramIndex++, startIndex);
        stmt.setInt(paramIndex, recordsPerPage);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowId = rs.getLong("cd_employee");
            String name = rs.getString("nm_employee");
            String type = rs.getString("nm_type");
            list.add(new Employees(rowId, name, type));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static Employees getEmployee(long id) throws Exception {
        Employees employee = null;
        Connection con = AppListener.getConnection();
        String sql = "SELECT * FROM employees WHERE cd_employee = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String name = rs.getString("nm_employee");
            String type = rs.getString("nm_type");
            employee = new Employees(id, name, type);
        }
        rs.close();
        stmt.close();
        con.close();
        return employee;
    }

    public static void insertEmployee(String name, String type) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO employees(nm_employee, nm_type)"
                + "VALUES(?,?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, type);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void updateEmployee(long id, String name, String type) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE employees SET nm_employee=?, nm_type=? WHERE cd_employee=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, type);
        stmt.setLong(3, id);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void deleteEmployee(long id) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "DELETE FROM employees WHERE cd_employee=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, id);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public Employees(long id, String name, String type) {
        this.rowid = id;
        this.name = name;
        this.type = type;
    }

    public long getRowid() {
        return rowid;
    }

    public void setRowid(long id) {
        this.rowid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

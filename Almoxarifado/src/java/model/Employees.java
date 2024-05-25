package model;

import java.sql.*;
import java.util.ArrayList;
import web.AppListener;

public class Employees {

    private long rowid;
    private String name;
    private String type;

    public static String getCreateStatement() {
        return "CREATE OR REPLACE TABLE employees("
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
    
    public static int getTotalEmployees() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM employees";
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
    
    public static ArrayList<Employees> getEmployeesPages(int page, int recordsPerPage) throws Exception {
        ArrayList<Employees> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        int startIndex = (page - 1) * recordsPerPage;

        String sql = "SELECT * FROM employees ORDER BY nm_employee LIMIT ?,?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);

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

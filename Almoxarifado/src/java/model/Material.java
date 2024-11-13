package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import web.AppListener;

public class Material {

    private long rowid;
    private String name;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS material("
                + "cd_material INTEGER PRIMARY KEY,"
                + "nm_material VARCHAR(50) NOT NULL"
                + ")";
    }

    public Material(long rowid, String name) {
        this.rowid = rowid;
        this.name = name;
    }

    public static ArrayList<Material> getMaterials() throws Exception {
        ArrayList<Material> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM material ORDER BY nm_material");

        while (rs.next()) {
            long rowId = rs.getLong("cd_material");
            String name = rs.getString("nm_material");
            list.add(new Material(rowId, name));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Material> getMaterialPages(int page, int recordsPerPage, int column, int sort, String search) throws Exception {
        ArrayList<Material> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        int startIndex = (page - 1) * recordsPerPage;

        String baseSQL = "SELECT * FROM material ";
        String searchFilter = "";
        String orderClause = "";

        // Adiciona o filtro de pesquisa, se `search` não for nulo ou vazio
        if (search != null && !search.isEmpty()) {
            searchFilter = "WHERE nm_material LIKE ? ";
        }

        // Define a cláusula de ordenação com base no valor de `column` e `sort`
        switch (column) {
            case 1:
                orderClause = "ORDER BY nm_material " + (sort == 2 ? "DESC" : "ASC") + " ";
                break;
            default:
                orderClause = "ORDER BY nm_material ASC ";
                break;
        }

        // Monta a consulta final com filtro e paginação
        String sql = baseSQL + searchFilter + orderClause + "LIMIT ?, ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        int paramIndex = 1;

        // Adiciona os parâmetros de pesquisa, se o filtro não estiver vazio
        if (!searchFilter.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
        }

        // Parâmetros de paginação
        stmt.setInt(paramIndex++, startIndex);
        stmt.setInt(paramIndex, recordsPerPage);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowId = rs.getLong("cd_material");
            String name = rs.getString("nm_material");
            list.add(new Material(rowId, name));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static int getTotalMaterial(String search) throws Exception {
        Connection con = AppListener.getConnection();
        String baseSQL = "SELECT COUNT(*) AS total FROM material ";
        String searchFilter = "";

        // Adiciona o filtro de pesquisa, se `search` não for nulo ou vazio
        if (search != null && !search.isEmpty()) {
            searchFilter = "WHERE nm_material LIKE ? ";
        }

        String sql = baseSQL + searchFilter;

        PreparedStatement stmt = con.prepareStatement(sql);
        int paramIndex = 1;

        // Adiciona os parâmetros de pesquisa, se o filtro não estiver vazio
        if (!searchFilter.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
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

    public static Material getMaterial(long id) throws Exception {
        Material material = null;
        Connection con = AppListener.getConnection();

        String sql = "SELECT * FROM material where cd_material = ?";
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            long rowId = rs.getLong("cd_material");
            String name = rs.getString("nm_material");
            material = new Material(rowId, name);
        }
        rs.close();
        stmt.close();
        con.close();
        return material;
    }

    public static void insertMaterial(String name) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO material(nm_material) VALUES(?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void updateMaterial(long id, String name) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE material SET nm_material = ? WHERE cd_material = ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setLong(2, id);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void deleteMaterial(long id) throws Exception {
        Connection con = AppListener.getConnection();

        String sql = "DELETE FROM material WHERE cd_material = ?";
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setLong(1, id);
        stmt.execute();

        stmt.close();
        con.close();
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

}

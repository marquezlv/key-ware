package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import web.AppListener;

public class Filters {

    private long rowid;
    private String type;
    private String desc;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS filters("
                + "cd_filter INTEGER PRIMARY KEY,"
                + "nm_type VARCHAR(50) NOT NULL,"
                + "ds_filter VARCHAR(200) NOT NULL"
                + ")";
    }

    public Filters(long rowid, String type, String desc) {
        this.rowid = rowid;
        this.type = type;
        this.desc = desc;
    }

    public long getRowid() {
        return rowid;
    }

    public void setRowid(long rowid) {
        this.rowid = rowid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ArrayList<Filters> getFilters() throws Exception {
        ArrayList<Filters> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM filters ORDER BY nm_type");

        while (rs.next()) {
            long rowId = rs.getLong("cd_filter");
            String type = rs.getString("nm_type");
            String desc = rs.getString("ds_filter");
            list.add(new Filters(rowId, type, desc));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Filters> getFiltersPages(int page, int recordsPerPage, int column, int sort) throws Exception {
        ArrayList<Filters> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        int startIndex = (page - 1) * recordsPerPage;

        String sql = "";
        if (sort == 0) {
            column = 0;
        }
        switch (column) {
            case 1:
                if (sort == 1) {
                    sql = "SELECT * FROM filters ORDER BY nm_type ASC LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT * FROM filters ORDER BY nm_type DESC LIMIT ?,?";
                }
                break;
            case 2:
                if (sort == 1) {
                    sql = "SELECT * FROM filters ORDER BY ds_filter ASC LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT * FROM filters ORDER BY ds_filter DESC LIMIT ?,?";
                }
                break;
            default:
                sql = "SELECT * FROM filters ORDER BY nm_type LIMIT ?,?";
                break;
        }

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowId = rs.getLong("cd_filter");
            String type = rs.getString("nm_type");
            String desc = rs.getString("ds_filter");
            list.add(new Filters(rowId, type, desc));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static int getTotalFilters() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM filters";
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

    public static Filters getFilter(long id) throws Exception {
        Filters filter = null;
        Connection con = AppListener.getConnection();

        String sql = "SELECT * FROM filters where cd_filter = ?";
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            long rowId = rs.getLong("cd_filter");
            String type = rs.getString("nm_type");
            String desc = rs.getString("ds_filter");
            filter = new Filters(rowId, type, desc);
        }
        rs.close();
        stmt.close();
        con.close();
        return filter;
    }

    public static void insertFilter(String type, String desc) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO filters(nm_type, ds_filter) VALUES(?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, type);
        stmt.setString(2, desc);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void updateFilter(long id, String type, String desc) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE filters SET nm_type = ?, ds_filter = ? WHERE cd_filter = ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, type);
        stmt.setString(2, desc);
        stmt.setLong(3, id);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void deleteFilter(long id) throws Exception {
        Connection con = AppListener.getConnection();

        String sql = "DELETE FROM filters WHERE cd_filter = ?";
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setLong(1, id);
        stmt.execute();

        stmt.close();
        con.close();
    }

}

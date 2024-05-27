package model;

import java.sql.*;
import java.util.ArrayList;
import web.AppListener;

public class Rooms {

    private long rowid;
    private String name;
    private String location;
    private String status;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS rooms("
                + "cd_room INTEGER PRIMARY KEY,"
                + "nm_room VARCHAR(50) NOT NULL,"
                + "nm_location VARCHAR(100) NOT NULL,"
                + "nm_status VARCHAR(50) NOT NULL"
                + ")";
    }

    public Rooms(long rowid, String name, String location, String status) {
        this.rowid = rowid;
        this.name = name;
        this.location = location;
        this.status = status;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static int getTotalRooms() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM rooms";
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

    public static ArrayList<Rooms> getRooms(int page, int recordsPerPage, int column, int sort) throws Exception {
        ArrayList<Rooms> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        int startIndex = (page - 1) * recordsPerPage;

        String sql = "";
        // Se for Sort 0 ele volta para o sql default
        if (sort == 0) {
            column = 0;
        }
        switch (column) {
            // Ordenando pela coluna 1 (nm_employee)
            case 1:
                // Sort 1 é ASCENDENTE e Sort 2 é DESCENDENTE
                if (sort == 1) {
                    sql = "SELECT * FROM rooms "
                            + "ORDER BY nm_room ASC "
                            + "LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT * FROM rooms "
                            + "ORDER BY nm_room DESC "
                            + "LIMIT ?,?";
                }
                break;
            // Ordenando pela coluna 2 (nm_subject)
            case 2:
                if (sort == 1) {
                    sql = "SELECT * FROM rooms "
                            + "ORDER BY nm_location ASC "
                            + "LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT * FROM rooms "
                            + "ORDER BY nm_location DESC "
                            + "LIMIT ?,?";
                }
                break;
            case 4:
                if (sort == 1) {
                    sql = "SELECT * FROM rooms "
                + "ORDER BY nm_status ASC "
                + "LIMIT ?,?";  
                } else if (sort == 2) {
                    sql = "SELECT * FROM rooms "
                + "ORDER BY nm_status DESC "
                + "LIMIT ?,?";
                }
                break;
            default:
                sql = "SELECT * FROM rooms "
                        + "ORDER BY nm_location, nm_room "
                        + "LIMIT ?,?";
                break;
        }

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowid = rs.getLong("cd_room");
            String name = rs.getString("nm_room");
            String location = rs.getString("nm_location");
            String status = rs.getString("nm_status");
            list.add(new Rooms(rowid, name, location, status));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Rooms> getRoomsAll() throws Exception {
        ArrayList<Rooms> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        String sql = "SELECT * FROM rooms "
                + "ORDER BY nm_location, nm_room";

        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowid = rs.getLong("cd_room");
            String name = rs.getString("nm_room");
            String location = rs.getString("nm_location");
            String status = rs.getString("nm_status");
            list.add(new Rooms(rowid, name, location, status));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertRoom(String name, String location, String status) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO rooms(nm_room, nm_location, nm_status) VALUES(?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, location);
        stmt.setString(3, status);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void updateRoom(long id, String name, String location, String status) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE rooms SET nm_room = ?,nm_location = ?, nm_status = ? WHERE cd_room = ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, location);
        stmt.setString(3, status);
        stmt.setLong(4, id);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void updateStatus(long id, String status) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE rooms SET nm_status = ? WHERE cd_room = ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, status);
        stmt.setLong(2, id);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void deleteRoom(long id) throws Exception {
        Connection con = AppListener.getConnection();

        String sql = "DELETE FROM rooms WHERE cd_room = ?";
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setLong(1, id);
        stmt.execute();

        stmt.close();
        con.close();
    }

}

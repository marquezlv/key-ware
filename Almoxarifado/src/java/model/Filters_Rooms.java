package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import web.AppListener;

public class Filters_Rooms {

    private long rowid;
    private long roomid;
    private long filterid;
    private String filterName;
    private String filterDesc;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS filters_rooms("
                + "cd_dumb INTEGER PRIMARY KEY,"
                + "cd_room INTEGER NOT NULL,"
                + "cd_filter INTEGER NOT NULL,"
                + "FOREIGN KEY(cd_room) REFERENCES rooms(cd_room),"
                + "FOREIGN KEY(cd_filter) REFERENCES filters(cd_filter)"
                + ")";
    }

    public static ArrayList<Filters_Rooms> getFiltersRoom(int column, int sort) throws Exception {
        ArrayList<Filters_Rooms> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        String sql = "";
        // Se for Sort 0 ele volta para o sql default
        if (sort == 0) {
            column = 0;
        }
        switch (column) {
            // Ordenando pela coluna 1 (nm_employee)
            case 3:
                // Sort 1 é ASCENDENTE e Sort 2 é DESCENDENTE
                if (sort == 1) {
                    sql = "SELECT fr.*, f.nm_type, f.ds_filter FROM filters_rooms fr "
                            + "LEFT JOIN filters f ON f.cd_filter = fr.cd_filter "
                            + "LEFT JOIN rooms r ON r.cd_room = fr.cd_room "
                            + "ORDER BY f.nm_type ASC";
                } else if (sort == 2) {
                    sql = "SELECT fr.*, f.nm_type, f.ds_filter FROM filters_rooms fr "
                            + "LEFT JOIN filters f ON f.cd_filter = fr.cd_filter "
                            + "LEFT JOIN rooms r ON r.cd_room = fr.cd_room "
                            + "ORDER BY f.nm_type DESC";
                }
                break;
            default:
                sql = "SELECT fr.*, f.nm_type, f.ds_filter FROM filters_rooms fr "
                        + "LEFT JOIN filters f ON f.cd_filter = fr.cd_filter "
                        + "LEFT JOIN rooms r ON r.cd_room = fr.cd_room "
                        + "ORDER BY f.nm_type";
                break;
        }

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            long rowId = rs.getLong("cd_dumb");
            long filter = rs.getLong("cd_filter");
            long room = rs.getLong("cd_room");
            String filterName = rs.getString("nm_type");
            String filterDesc = rs.getString("ds_filter");
            list.add(new Filters_Rooms(rowId, room, filter, filterName, filterDesc));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertFiltersRoom(long filter, long room) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO filters_rooms(cd_room, cd_filter) VALUES(?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, room);
        stmt.setLong(2, filter);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void deleteFiltersRoom(long id) throws Exception {
        Connection con = AppListener.getConnection();

        String sql = "DELETE FROM filters_rooms WHERE cd_dumb = ?";
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setLong(1, id);
        stmt.execute();

        stmt.close();
        con.close();
    }

    public Filters_Rooms(long rowid, long roomid, long filterid, String filterName, String filterDesc) {
        this.rowid = rowid;
        this.roomid = roomid;
        this.filterid = filterid;
        this.filterName = filterName;
        this.filterDesc = filterDesc;
    }

    public long getRowid() {
        return rowid;
    }

    public void setRowid(long rowid) {
        this.rowid = rowid;
    }

    public long getRoomid() {
        return roomid;
    }

    public void setRoomid(long roomid) {
        this.roomid = roomid;
    }

    public long getFilterid() {
        return filterid;
    }

    public void setFilterid(long filterid) {
        this.filterid = filterid;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterDesc() {
        return filterDesc;
    }

    public void setFilterDesc(String filterDesc) {
        this.filterDesc = filterDesc;
    }

}

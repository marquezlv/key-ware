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

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS filters_rooms("
                + "cd_dumb INTEGER PRIMARY KEY,"
                + "cd_room INTEGER NOT NULL,"
                + "cd_filter INTEGER NOT NULL,"
                + "FOREIGN KEY(cd_room) REFERENCES rooms(cd_room),"
                + "FOREIGN KEY(cd_filter) REFERENCES filters(cd_filter)"
                + ")";
    }

    public static ArrayList<Filters_Rooms> getFiltersRoom() throws Exception {
        ArrayList<Filters_Rooms> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        Statement stmt = con.createStatement();
        String query = "SELECT fr.*, f.nm_type FROM filters_rooms fr "
                + "LEFT JOIN filters f ON f.cd_filter = fr.cd_filter "
                + "LEFT JOIN rooms r ON r.cd_room = fr.cd_room "
                + "ORDER BY f.nm_type";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            long rowId = rs.getLong("cd_dumb");
            long filter = rs.getLong("cd_filter");
            long room = rs.getLong("cd_room");
            String filterName = rs.getString("nm_type");
            list.add(new Filters_Rooms(rowId, room, filter, filterName));
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

    public Filters_Rooms(long rowid, long roomid, long filterid, String filterName) {
        this.rowid = rowid;
        this.roomid = roomid;
        this.filterid = filterid;
        this.filterName = filterName;
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

}

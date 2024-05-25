package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import web.AppListener;

public class CurrentKey {
    private long rowid;
    private long room;
    private long employee;
    private String start;
    private String employeeName;
    
    public static String getCreateStatement() {
        return "CREATE TABLE currentKey("
                + "cd_key INTEGER PRIMARY KEY,"
                + "cd_room INTEGER,"
                + "cd_employee INTEGER,"
                + "dt_start DATETIME,"
                + "FOREIGN KEY(cd_room) REFERENCES rooms(cd_room),"
                + "FOREIGN KEY(cd_employee) REFERENCES employees(cd_employee)"
                + ")";
    }
    public static ArrayList<CurrentKey> getKeys() throws Exception {
        ArrayList<CurrentKey> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        String sql = "SELECT c.*, e.cd_employee, e.nm_employee, r.cd_room "
                + "FROM currentKey c "
                + "LEFT JOIN employees e ON e.cd_employee = c.cd_employee "
                + "LEFT JOIN rooms r ON r.cd_room = c.cd_room";
        PreparedStatement stmt = con.prepareStatement(sql);
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowid = rs.getLong("cd_key");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            String employeeName = rs.getString("nm_employee");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Date datetime = new Date(timestamp.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            list.add(new CurrentKey(rowid, room, employee, date, employeeName));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }
    
    /* public static CurrentKey getCurrentKey(long id) throws Exception {
        CurrentKey key = null;
        Connection con = AppListener.getConnection();

        String sql = "SELECT c.*, e.cd_employee, e.nm_employee, r.cd_room "
                + "FROM currentKey c "
                + "LEFT JOIN employees e ON e.cd_employee = c.cd_employee "
                + "LEFT JOIN rooms r ON r.cd_room = c.cd_room "
                + "WHERE c.cd_room = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        
        stmt.setLong(1, id);
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowid = rs.getLong("cd_key");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            String employeeName = rs.getString("nm_employee");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Date datetime = new Date(timestamp.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            key = new CurrentKey(rowid, room, employee, date, employeeName);
        }
        rs.close();
        stmt.close();
        con.close();
        return key;
    }
    */
    
     public static void insertKey(long employee, long room, Date date) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO currentKey(cd_room, cd_employee, dt_start) VALUES(?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, room);
        stmt.setLong(2, employee);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        stmt.setDate(3, sqlDate);
        stmt.execute();
        Rooms.updateStatus(room, "OCUPADO");

        stmt.close();
        con.close();
    }
     
     public static void deleteKey(long id, long room) throws Exception{
        Connection con = AppListener.getConnection();
        String sql = "DELETE FROM currentKey WHERE cd_key=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, id);
        stmt.execute();
        Rooms.updateStatus(room, "DISPONIVEL");
        stmt.close();
        con.close();       
    }

    public CurrentKey(long rowid, long room, long employee, String start, String employeeName) {
        this.rowid = rowid;
        this.room = room;
        this.employee = employee;
        this.start = start;
        this.employeeName = employeeName;
    }

    public long getRowid() {
        return rowid;
    }

    public void setRowid(long rowid) {
        this.rowid = rowid;
    }

    public long getRoom() {
        return room;
    }

    public void setRoom(long room) {
        this.room = room;
    }

    public long getEmployee() {
        return employee;
    }

    public void setEmployee(long employee) {
        this.employee = employee;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
    
    
}

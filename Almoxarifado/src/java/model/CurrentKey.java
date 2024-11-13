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
    private long subject;
    private String start;
    private String employeeName;
    private String employeeType;
    private String roomName;
    private String location;
    private String subjectName;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS currentKey("
                + "cd_key INTEGER PRIMARY KEY,"
                + "cd_room INTEGER,"
                + "cd_employee INTEGER,"
                + "cd_subject INTEGER,"
                + "dt_start DATETIME,"
                + "FOREIGN KEY(cd_room) REFERENCES rooms(cd_room),"
                + "FOREIGN KEY(cd_employee) REFERENCES employees(cd_employee)"
                + "FOREIGN KEY(cd_subject) REFERENCES subjects(cd_subject)"
                + ")";
    }

    public static int getTotalCurrentKey() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM currentKey";
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

    public static ArrayList<CurrentKey> getKeys() throws Exception {
        ArrayList<CurrentKey> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        String sql = "SELECT c.*, s.nm_subject, r.nm_location ,e.cd_employee, e.nm_employee, e.nm_type, r.cd_room, r.nm_room "
                + "FROM currentKey c "
                + "LEFT JOIN employees e ON e.cd_employee = c.cd_employee "
                + "LEFT JOIN rooms r ON r.cd_room = c.cd_room "
                + "LEFT JOIN subjects s ON s.cd_subject = c.cd_subject "
                + "ORDER BY r.nm_room, e.nm_employee";
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowid = rs.getLong("cd_key");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            long subject = rs.getLong("cd_subject");
            String employeeName = rs.getString("nm_employee");
            String employeeType = rs.getString("nm_type");
            String roomName = rs.getString("nm_room");
            String location = rs.getString("nm_location");
            String subjectName = rs.getString("nm_subject");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Date datetime = new Date(timestamp.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            list.add(new CurrentKey(rowid, room, employee, subject, date, employeeName, roomName, employeeType, subjectName, location));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<CurrentKey> getKeysPages(int page, int recordsPerPage) throws Exception {
        ArrayList<CurrentKey> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        int startIndex = (page - 1) * recordsPerPage;

        String sql = "SELECT c.*, s.nm_subject, r.nm_location ,e.cd_employee, e.nm_employee, e.nm_type, r.cd_room, r.nm_room "
                + "FROM currentKey c "
                + "LEFT JOIN employees e ON e.cd_employee = c.cd_employee "
                + "LEFT JOIN rooms r ON r.cd_room = c.cd_room "
                + "LEFT JOIN subjects s ON s.cd_subject = c.cd_subject "
                + "ORDER BY r.nm_room, e.nm_employee "
                + "LIMIT ?,?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowid = rs.getLong("cd_key");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            long subject = rs.getLong("cd_subject");
            String employeeName = rs.getString("nm_employee");
            String employeeType = rs.getString("nm_type");
            String roomName = rs.getString("nm_room");
            String location = rs.getString("nm_location");
            String subjectName = rs.getString("nm_subject");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Date datetime = new Date(timestamp.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            list.add(new CurrentKey(rowid, room, employee, subject, date, employeeName, roomName, employeeType, subjectName, location));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertKey(long employee, long room, long subject, Date date) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO currentKey(cd_room, cd_employee, cd_subject, dt_start) VALUES(?,?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, room);
        stmt.setLong(2, employee);
        stmt.setLong(3, subject);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        stmt.setDate(4, sqlDate);
        stmt.execute();
        Rooms.updateStatus(room, "OCUPADO");

        stmt.close();
        con.close();
    }

    public static void deleteKey(long id, long room) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "DELETE FROM currentKey WHERE cd_key=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, id);
        stmt.execute();
        Rooms.updateStatus(room, "DISPONIVEL");
        stmt.close();
        con.close();
    }

    public CurrentKey(long rowid, long room, long employee, long subject, String start, String employeeName, String roomName, String employeeType, String subjectName, String location) {
        this.rowid = rowid;
        this.room = room;
        this.employee = employee;
        this.start = start;
        this.employeeName = employeeName;
        this.subject = subject;
        this.roomName = roomName;
        this.employeeType = employeeType;
        this.subjectName = subjectName;
        this.location = location;
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

    public long getSubject() {
        return subject;
    }

    public void setSubject(long subject) {
        this.subject = subject;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}

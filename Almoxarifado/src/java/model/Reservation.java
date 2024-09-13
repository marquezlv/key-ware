package model;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import web.AppListener;

public class Reservation {

    private long rowid;
    private long employeeid;
    private String employee;
    private long roomid;
    private String roomName;
    private String location;
    private String start;
    private String end;
    private long subject;
    private String subjectName;
    private String subjectPeriod;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS reservations("
                + "cd_reservation INTEGER PRIMARY KEY,"
                + "cd_employee INTEGER,"
                + "cd_room INTEGER,"
                + "cd_subject INTEGER,"
                + "dt_start DATETIME,"
                + "dt_end DATETIME,"
                + "FOREIGN KEY(cd_employee) REFERENCES employees(cd_employee),"
                + "FOREIGN KEY(cd_room) REFERENCES rooms(cd_room),"
                + "FOREIGN KEY(cd_subject) REFERENCES subjects(cd_subject)"
                + ")";
    }

    public static int getTotalReservations() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM reservations";
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

    public static ArrayList<Reservation> getReservations(int page, int recordsPerPage, int column, int sort) throws Exception {
        ArrayList<Reservation> list = new ArrayList<>();
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
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY e.nm_employee ASC "
                            + "LIMIT ?, ?";
                } else if (sort == 2) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY e.nm_employee DESC "
                            + "LIMIT ?, ?";
                }
                break;
            // Ordenando pela coluna 2 (nm_subject)
            case 2:
                if (sort == 1) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY s.nm_subject ASC "
                            + "LIMIT ?, ?";
                } else if (sort == 2) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY s.nm_subject DESC "
                            + "LIMIT ?, ?";
                }
                break;
            case 3:
                if (sort == 1) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY ro.nm_room ASC "
                            + "LIMIT ?, ?";
                } else if (sort == 2) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY ro.nm_room DESC "
                            + "LIMIT ?, ?";
                }
                break;
            case 4:
                if (sort == 1) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY ro.nm_location ASC "
                            + "LIMIT ?, ?";
                } else if (sort == 2) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY ro.nm_location DESC "
                            + "LIMIT ?, ?";
                }
                break;
            case 5:
                if (sort == 1) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY dt_start ASC "
                            + "LIMIT ?, ?";
                } else if (sort == 2) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY dt_start DESC "
                            + "LIMIT ?, ?";
                }
                break;
                case 6:
                if (sort == 1) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY dt_end ASC "
                            + "LIMIT ?, ?";
                } else if (sort == 2) {
                    sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                            + "FROM reservations r "
                            + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                            + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                            + "ORDER BY dt_end DESC "
                            + "LIMIT ?, ?";
                }
                break;
            default:
                sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                        + "FROM reservations r "
                        + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                        + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                        + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                        + "ORDER BY r.dt_start, e.nm_employee "
                        + "LIMIT ?, ?";
                break;
        }

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowid = rs.getLong("cd_reservation");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            long subject = rs.getLong("cd_subject");
            String employeeName = rs.getString("nm_employee");
            String roomName = rs.getString("nm_room");
            String roomLocation = rs.getString("nm_location");
            String subjectName = rs.getString("nm_subject");
            String subjectPeriod = rs.getString("nm_period");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Timestamp timestampend = rs.getTimestamp("dt_end");
            Date datetime = new Date(timestamp.getTime());
            Date datetimeEnd = new Date(timestampend.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            String dateEnd = dateFormat.format(datetimeEnd);
            list.add(new Reservation(rowid, employee, employeeName, room, roomName, roomLocation, subject, subjectName, subjectPeriod, date, dateEnd));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }
    
    public static ArrayList<Reservation> getReservationsAll() throws Exception {
        ArrayList<Reservation> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        
        String sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject, s.nm_period "
                        + "FROM reservations r "
                        + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                        + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                        + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                        + "ORDER BY r.dt_start, e.nm_employee";

        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowid = rs.getLong("cd_reservation");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            long subject = rs.getLong("cd_subject");
            String employeeName = rs.getString("nm_employee");
            String roomName = rs.getString("nm_room");
            String roomLocation = rs.getString("nm_location");
            String subjectName = rs.getString("nm_subject");
            String subjectPeriod = rs.getString("nm_period");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Timestamp timestampend = rs.getTimestamp("dt_end");
            Date datetime = new Date(timestamp.getTime());
            Date datetimeEnd = new Date(timestampend.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            String dateEnd = dateFormat.format(datetimeEnd);
            list.add(new Reservation(rowid, employee, employeeName, room, roomName, roomLocation, subject, subjectName, subjectPeriod, date, dateEnd));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertReservation(long employee, long room, long subject, Date date, Date end) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO reservations(cd_employee, cd_room, cd_subject, dt_start, dt_end) VALUES(?,?,?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, employee);
        stmt.setLong(2, room);
        stmt.setLong(3, subject);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        stmt.setDate(4, sqlDate);
        java.sql.Date sqlDateEnd = new java.sql.Date(end.getTime());
        stmt.setDate(5, sqlDateEnd);
        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void updateReservation(long id, long employee, long room, long subject, Date date, Date end) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE reservations SET cd_employee = ?, cd_room = ?, cd_subject = ?, dt_start = ?, dt_end = ? WHERE cd_reservation = ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, employee);
        stmt.setLong(2, room);
        stmt.setLong(3, subject);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        stmt.setDate(4, sqlDate);
        java.sql.Date sqlDateEnd = new java.sql.Date(end.getTime());
        stmt.setDate(5, sqlDateEnd);
        stmt.setLong(6, id);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void deleteReservation(long id) throws Exception {
        Connection con = AppListener.getConnection();

        String sql = "DELETE FROM reservations WHERE cd_reservation = ?";
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setLong(1, id);
        stmt.execute();

        stmt.close();
        con.close();
    }

    public Reservation(long rowid, long employeeid, String employee, long roomid, String roomName, String location, long subject, String subjectName, String subjectPeriod, String start, String end) {
        this.rowid = rowid;
        this.employeeid = employeeid;
        this.employee = employee;
        this.roomid = roomid;
        this.roomName = roomName;
        this.location = location;
        this.start = start;
        this.subject = subject;
        this.subjectName = subjectName;
        this.subjectPeriod = subjectPeriod;
        this.end = end;
    }

    public long getRowid() {
        return rowid;
    }

    public void setRowid(long rowid) {
        this.rowid = rowid;
    }

    public long getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(long employeeid) {
        this.employeeid = employeeid;
    }

    public long getRoomid() {
        return roomid;
    }

    public void setRoomid(long roomid) {
        this.roomid = roomid;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public long getSubject() {
        return subject;
    }

    public void setSubject(long subject) {
        this.subject = subject;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectPeriod() {
        return subjectPeriod;
    }

    public void setSubjectPeriod(String subjectPeriod) {
        this.subjectPeriod = subjectPeriod;
    }

}

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

public class History {

    private long rowid;
    private long employee;
    private long room;
    private long subject;
    private long user;
    private String type;
    private String employeeName;
    private String roomName;
    private String subjectName;
    private String courseName;
    private String userName;
    private String date;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS history("
                + "cd_history INTEGER PRIMARY KEY,"
                + "cd_employee INTEGER,"
                + "cd_room INTEGER,"
                + "cd_subject INTEGER,"
                + "cd_user INTEGER,"
                + "nm_type VARCHAR(30),"
                + "dt_history DATETIME,"
                + "FOREIGN KEY(cd_room) REFERENCES rooms(cd_room),"
                + "FOREIGN KEY(cd_employee) REFERENCES employees(cd_employee),"
                + "FOREIGN KEY(cd_subject) REFERENCES subjects(cd_subject),"
                + "FOREIGN KEY(cd_user) REFERENCES users(rowid)"
                + ")";
    }

    public static int getTotalHistory() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM history";
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

    public static ArrayList<History> getHistory(int page, int recordsPerPage, int column, int sort) throws Exception {
        ArrayList<History> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        int startIndex = (page - 1) * recordsPerPage;

        String sql = "";
        if (sort == 0) {
            column = 0;
        }
        switch (column) {
            case 1:
                if (sort == 1) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY h.dt_history ASC "
                            + "LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY h.dt_history DESC "
                            + "LIMIT ?,?";
                }
                break;
            case 2:
                if (sort == 1) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY e.nm_employee ASC "
                            + "LIMIT ?,?";

                } else if (sort == 2) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY e.nm_employee DESC "
                            + "LIMIT ?,?";
                }
                break;
            case 3:
                if (sort == 1) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY s.nm_subject ASC "
                            + "LIMIT ?,?";

                } else if (sort == 2) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY s.nm_subject DESC "
                            + "LIMIT ?,?";
                }
                break;
            case 4:
                if (sort == 1) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY c.nm_course ASC "
                            + "LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY c.nm_course DESC "
                            + "LIMIT ?,?";
                }
                break;
            case 5:
                if (sort == 1) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY r.nm_room ASC "
                            + "LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY r.nm_room DESC "
                            + "LIMIT ?,?";
                }
                break;
            case 6:
                if (sort == 1) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY h.nm_type ASC "
                            + "LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY h.nm_type DESC "
                            + "LIMIT ?,?";
                }
                break;
            case 7:
                if (sort == 1) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY u.name ASC "
                            + "LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                            + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                            + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                            + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                            + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                            + "LEFT JOIN users u ON u.rowid = h.cd_user "
                            + "ORDER BY u.name DESC "
                            + "LIMIT ?,?";
                }
                break;
            default:
                sql = "SELECT h.*, e.nm_employee, r.nm_room, s.nm_subject, c.nm_course, u.rowid, u.name FROM history h "
                        + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                        + "LEFT JOIN rooms r ON r.cd_room = h.cd_room "
                        + "LEFT JOIN subjects s ON s.cd_subject = h.cd_subject "
                        + "LEFT JOIN courses c ON c.cd_course = s.cd_course "
                        + "LEFT JOIN users u ON u.rowid = h.cd_user "
                        + "ORDER BY h.dt_history DESC "
                        + "LIMIT ?,?";
                break;
        }

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowId = rs.getLong("cd_history");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            long subject = rs.getLong("cd_subject");
            long user = rs.getLong("rowid");
            String type = rs.getString("nm_type");
            String employeeName = rs.getString("nm_employee");
            String roomName = rs.getString("nm_room");
            String subjectName = rs.getString("nm_subject");
            String courseName = rs.getString("nm_course");
            String userName = rs.getString("name");
            Timestamp timestamp = rs.getTimestamp("dt_history");
            Date datetime = new Date(timestamp.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            list.add(new History(rowId, employee, room, subject, type, employeeName, roomName, subjectName, courseName, date, user, userName));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertHistory(long employee, long room, long subject, String type, Date date, long user) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO history(cd_employee, cd_room, cd_subject, nm_type, dt_history, cd_user) VALUES(?,?,?,?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, employee);
        stmt.setLong(2, room);
        stmt.setLong(3, subject);
        stmt.setString(4, type);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        stmt.setDate(5, sqlDate);
        stmt.setLong(6, user);
        stmt.execute();

        stmt.close();
        con.close();
    }

    public History(long rowid, long employee, long room, long subject, String type, String employeeName, String roomName, String subjectName, String courseName, String date, long user, String userName) {
        this.rowid = rowid;
        this.employee = employee;
        this.room = room;
        this.type = type;
        this.employeeName = employeeName;
        this.roomName = roomName;
        this.date = date;
        this.subject = subject;
        this.subjectName = subjectName;
        this.courseName = courseName;
        this.user = user;
        this.userName = userName;
    }
    
    public long getRowid() {
        return rowid;
    }

    public void setRowid(long rowid) {
        this.rowid = rowid;
    }

    public long getEmployee() {
        return employee;
    }

    public void setEmployee(long employee) {
        this.employee = employee;
    }

    public long getRoom() {
        return room;
    }

    public void setRoom(long room) {
        this.room = room;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

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
                + "nm_employee VARCHAR(60),"
                + "nm_room VARCHAR(50),"
                + "nm_subject VARCHAR(60),"
                + "nm_course VARCHAR(60),"
                + "nm_user VARCHAR(60),"
                + "nm_type VARCHAR(30),"
                + "dt_history DATETIME"
                + ")";
    }

    public static int getTotalHistory(String searchParam, String searchSubject, String searchCourse, String strDateStart, String strDateEnd) throws Exception {
        Connection con = AppListener.getConnection();

        String baseSql = "SELECT COUNT(*) AS total FROM history h ";

        StringBuilder whereClause = new StringBuilder("WHERE 1=1 ");

        if (searchParam != null && !searchParam.isEmpty()) {
            whereClause.append("AND h.nm_employee LIKE ? ");
        }
        if (searchSubject != null && !searchSubject.isEmpty()) {
            whereClause.append("AND h.nm_subject LIKE ? ");
        }
        if (searchCourse != null && !searchCourse.isEmpty()) {
            whereClause.append("AND h.nm_course LIKE ? ");
        }
        if (strDateStart != null && !strDateStart.isEmpty() && strDateEnd != null && !strDateEnd.isEmpty()) {
            whereClause.append("AND h.dt_history BETWEEN ? AND ? ");
        }

        String sql = baseSql + whereClause.toString();

        PreparedStatement stmt = con.prepareStatement(sql);

        int paramIndex = 1;

        if (searchParam != null && !searchParam.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchParam + "%");
        }
        if (searchSubject != null && !searchSubject.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchSubject + "%");
        }
        if (searchCourse != null && !searchCourse.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchCourse + "%");
        }
        if (strDateStart != null && !strDateStart.isEmpty() && strDateEnd != null && !strDateEnd.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(strDateStart);
            Date endDate = dateFormat.parse(strDateEnd);
            stmt.setTimestamp(paramIndex++, new java.sql.Timestamp(startDate.getTime()));
            stmt.setTimestamp(paramIndex++, new java.sql.Timestamp(endDate.getTime()));
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
    
    public static ArrayList<History> getHistory(int page, int recordsPerPage, int column, int sort, String searchParam, String searchSubject, String searchCourse, String strDateStart, String strDateEnd) throws Exception {
        ArrayList<History> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        int startIndex = (page - 1) * recordsPerPage;

        String baseSql = "SELECT h.* FROM history h ";

        StringBuilder whereClause = new StringBuilder("WHERE 1=1 ");

        if (searchParam != null && !searchParam.isEmpty()) {
            whereClause.append("AND h.nm_employee LIKE ? ");
        }
        if (searchSubject != null && !searchSubject.isEmpty()) {
            whereClause.append("AND h.nm_subject LIKE ? ");
        }
        if (searchCourse != null && !searchCourse.isEmpty()) {
            whereClause.append("AND h.nm_course LIKE ? ");
        }
        if (strDateStart != null && !strDateStart.isEmpty() && strDateEnd != null && !strDateEnd.isEmpty()) {
            whereClause.append("AND h.dt_history BETWEEN ? AND ? ");
        }

        String sql = baseSql + whereClause.toString();

        switch (column) {
            case 1:
                sql += (sort == 1 ? "ORDER BY h.dt_history ASC " : "ORDER BY h.dt_history DESC ");
                break;
            case 2:
                sql += (sort == 1 ? "ORDER BY h.nm_employee ASC " : "ORDER BY h.nm_employee DESC ");
                break;
            case 3:
                sql += (sort == 1 ? "ORDER BY h.nm_subject ASC " : "ORDER BY h.nm_subject DESC ");
                break;
            case 4:
                sql += (sort == 1 ? "ORDER BY h.nm_course ASC " : "ORDER BY h.nm_course DESC ");
                break;
            case 5:
                sql += (sort == 1 ? "ORDER BY h.nm_room ASC " : "ORDER BY h.nm_room DESC ");
                break;
            case 6:
                sql += (sort == 1 ? "ORDER BY h.nm_type ASC " : "ORDER BY h.nm_type DESC ");
                break;
            case 7:
                sql += (sort == 1 ? "ORDER BY h.name ASC " : "ORDER BY h.name DESC ");
                break;
            default:
                sql += "ORDER BY h.dt_history DESC ";
                break;
        }

        sql += "LIMIT ?,?";

        PreparedStatement stmt = con.prepareStatement(sql);

        int paramIndex = 1;

        if (searchParam != null && !searchParam.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchParam + "%");  
        }
        if (searchSubject != null && !searchSubject.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchSubject + "%");  
        }
        if (searchCourse != null && !searchCourse.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchCourse + "%"); 
        }
        if (strDateStart != null && !strDateStart.isEmpty() && strDateEnd != null && !strDateEnd.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(strDateStart);
            Date endDate = dateFormat.parse(strDateEnd);
            stmt.setTimestamp(paramIndex++, new java.sql.Timestamp(startDate.getTime()));
            stmt.setTimestamp(paramIndex++, new java.sql.Timestamp(endDate.getTime()));
        }

        stmt.setInt(paramIndex++, startIndex);
        stmt.setInt(paramIndex++, recordsPerPage);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowId = rs.getLong("cd_history");
            String type = rs.getString("nm_type");
            String employeeName = rs.getString("nm_employee");
            String roomName = rs.getString("nm_room");
            String subjectName = rs.getString("nm_subject");
            String courseName = rs.getString("nm_course");
            String userName = rs.getString("nm_user");
            Timestamp timestamp = rs.getTimestamp("dt_history");
            Date datetime = new Date(timestamp.getTime());
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = displayFormat.format(datetime);
            list.add(new History(rowId, type, employeeName, roomName, subjectName, courseName, date, userName));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertHistory(String employee, String room, String subject, String type, Date date, String user, String course) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO history(nm_employee, nm_room, nm_subject, nm_type, dt_history, nm_user, nm_course) VALUES(?,?,?,?,?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, employee);
        stmt.setString(2, room);
        stmt.setString(3, subject);
        stmt.setString(4, type);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        stmt.setDate(5, sqlDate);
        stmt.setString(6, user);
        stmt.setString(7, course);
        stmt.execute();

        stmt.close();
        con.close();
    }

    public History(long rowid, String type, String employeeName, String roomName, String subjectName, String courseName, String date, String userName) {
        this.rowid = rowid;
        this.type = type;
        this.employeeName = employeeName;
        this.roomName = roomName;
        this.date = date;
        this.subjectName = subjectName;
        this.courseName = courseName;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

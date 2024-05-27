package model;

import java.sql.*;
import java.util.ArrayList;
import web.AppListener;

public class Subjects {

    private long rowid;
    private String name;
    private long course;
    private String period;
    private String courseName;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS subjects("
                + "cd_subject INTEGER PRIMARY KEY,"
                + "nm_subject VARCHAR(50) NOT NULL,"
                + "nm_period VARCHAR(50) NOT NULL,"
                + "cd_course INTEGER,"
                + "FOREIGN KEY(cd_course) REFERENCES courses(cd_course)"
                + ")";
    }

    public static ArrayList<Subjects> getSubjects() throws Exception {
        ArrayList<Subjects> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT s.*, c.nm_course FROM subjects s "
                + "JOIN courses c ON c.cd_course = s.cd_course "
                + "ORDER BY s.nm_subject");
        while (rs.next()) {
            long rowId = rs.getLong("cd_subject");
            String name = rs.getString("nm_subject");
            long course = rs.getLong("cd_course");
            String period = rs.getString("nm_period");
            String courseName = rs.getString("nm_course");
            list.add(new Subjects(rowId, name, course, courseName, period));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Subjects> getSubjectsPages(int page, int recordsPerPage) throws Exception {
        ArrayList<Subjects> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        int startIndex = (page - 1) * recordsPerPage;
        String sql = "SELECT s.*, c.nm_course FROM subjects s "
                + "JOIN courses c ON c.cd_course = s.cd_course "
                + "ORDER BY s.nm_subject "
                + "LIMIT ?,?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowId = rs.getLong("cd_subject");
            String name = rs.getString("nm_subject");
            long course = rs.getLong("cd_course");
            String period = rs.getString("nm_period");
            String courseName = rs.getString("nm_course");
            list.add(new Subjects(rowId, name, course, courseName, period));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static int getTotalSubjects() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM subjects";
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

    public static void insertSubject(String name, long course, String period) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO subjects(nm_subject, cd_course, nm_period)"
                + "VALUES(?,?,?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setLong(2, course);
        stmt.setString(3, period);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void updateSubject(long id, String name, long course, String period) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE subjects SET nm_subject=?, cd_course=?, nm_period=? WHERE cd_subject=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setLong(2, course);
        stmt.setString(3, period);
        stmt.setLong(4, id);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void deleteSubject(long id) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "DELETE FROM subjects WHERE cd_subject=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, id);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public Subjects(long id, String name, long course, String courseName, String period) {
        this.rowid = id;
        this.name = name;
        this.course = course;
        this.courseName = courseName;
        this.period = period;
    }

    public long getRowid() {
        return rowid;
    }

    public void setRowid(long id) {
        this.rowid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCourse() {
        return course;
    }

    public void setCourse(long course) {
        this.course = course;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}

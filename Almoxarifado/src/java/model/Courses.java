package model;

import java.sql.*;
import java.util.ArrayList;
import web.AppListener;

public class Courses {

    private long rowid;
    private String name;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS courses("
                + "cd_course INTEGER PRIMARY KEY,"
                + "nm_course VARCHAR(50) NOT NULL"
                + ")";
    }

    public static ArrayList<Courses> getCourses() throws Exception {
        ArrayList<Courses> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM courses "
                + "ORDER BY nm_course");
        while (rs.next()) {
            long rowId = rs.getLong("cd_course");
            String name = rs.getString("nm_course");
            list.add(new Courses(rowId, name));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Courses> getCoursesPages(int page, int recordsPerPage, int column, int sort) throws Exception {
        ArrayList<Courses> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        int startIndex = (page - 1) * recordsPerPage;
        String sql = "";
        if (sort == 0) {
            column = 0;
        }
        switch (column) {
            case 1:
                if (sort == 1) {
                    sql = "SELECT * FROM courses "
                        + "ORDER BY nm_course ASC "
                        + "LIMIT ?,?";
                } else if (sort == 2) {
                    sql = "SELECT * FROM courses "
                        + "ORDER BY nm_course DESC "
                        + "LIMIT ?,?";
                }
                break;
            default:
                sql = "SELECT * FROM courses "
                        + "ORDER BY nm_course "
                        + "LIMIT ?,?";
                break;
        }

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowId = rs.getLong("cd_course");
            String name = rs.getString("nm_course");
            list.add(new Courses(rowId, name));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static int getTotalCourses() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM courses";
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

    public static void insertCourse(String name) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO courses(nm_course)"
                + "VALUES(?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void updateCourse(long id, String name) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE courses SET nm_course=? WHERE cd_course=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setLong(2, id);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void deleteCourse(long id) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "DELETE FROM courses WHERE cd_course=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, id);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public Courses(long id, String name) {
        this.rowid = id;
        this.name = name;
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
}

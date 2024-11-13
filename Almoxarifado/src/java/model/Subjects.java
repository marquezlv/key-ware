package model;

import java.sql.*;
import java.util.ArrayList;
import web.AppListener;

public class Subjects {

    private long rowid;
    private String name;
    private long course;
    private String courseName;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS subjects("
                + "cd_subject INTEGER PRIMARY KEY,"
                + "nm_subject VARCHAR(50) NOT NULL,"
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
            String courseName = rs.getString("nm_course");
            list.add(new Subjects(rowId, name, course, courseName));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Subjects> getSubjectsPages(int page, int recordsPerPage, int column, int sort, String search) throws Exception {
        ArrayList<Subjects> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        int startIndex = (page - 1) * recordsPerPage;

        StringBuilder sql = new StringBuilder("SELECT s.*, c.nm_course FROM subjects s ")
                .append("JOIN courses c ON c.cd_course = s.cd_course ");

        // Adiciona a condição de busca nas colunas nm_subject e nm_course, se 'search' não for nulo ou vazio
        if (search != null && !search.isEmpty()) {
            sql.append("WHERE s.nm_subject LIKE ? OR c.nm_course LIKE ? ");
        }

        String orderColumn;
        switch (column) {
            case 1:
                orderColumn = "s.nm_subject";
                break;
            case 2:
                orderColumn = "c.nm_course";
                break;
            default:
                orderColumn = "s.nm_subject";
        }

        String orderDirection = (sort == 2) ? "DESC" : "ASC";

        sql.append("ORDER BY ").append(orderColumn).append(" ").append(orderDirection)
                .append(" LIMIT ?, ?");

        PreparedStatement stmt = con.prepareStatement(sql.toString());
        int paramIndex = 1;

        // Define os parâmetros de pesquisa, se aplicável
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
        }

        // Parâmetros de paginação
        stmt.setInt(paramIndex++, startIndex);
        stmt.setInt(paramIndex, recordsPerPage);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowId = rs.getLong("cd_subject");
            String name = rs.getString("nm_subject");
            long course = rs.getLong("cd_course");
            String courseName = rs.getString("nm_course");

            list.add(new Subjects(rowId, name, course, courseName));
        }

        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static int getTotalSubjects(String search) throws Exception {
        Connection con = AppListener.getConnection();
        String baseSQL = "SELECT COUNT(*) AS total FROM subjects s "
                + "JOIN courses c ON c.cd_course = s.cd_course ";
        String searchFilter = "";

        // Adiciona o filtro de pesquisa, se `search` não for nulo ou vazio
        if (search != null && !search.isEmpty()) {
            searchFilter = "WHERE s.nm_subject LIKE ? OR c.nm_course LIKE ? ";
        }

        String sql = baseSQL + searchFilter;

        PreparedStatement stmt = con.prepareStatement(sql);
        int paramIndex = 1;

        // Adiciona os parâmetros de pesquisa, se o filtro não estiver vazio
        if (!searchFilter.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
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

    public static void insertSubject(String name, long course) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO subjects(nm_subject, cd_course)"
                + "VALUES(?,?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setLong(2, course);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void updateSubject(long id, String name, long course) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE subjects SET nm_subject=?, cd_course=? WHERE cd_subject=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setLong(2, course);
        stmt.setLong(3, id);
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

    public Subjects(long id, String name, long course, String courseName) {
        this.rowid = id;
        this.name = name;
        this.course = course;
        this.courseName = courseName;
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
}

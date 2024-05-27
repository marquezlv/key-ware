package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import web.AppListener;

public class Employees_Subjects {
    private long rowid;
    private long employee;
    private long subject;
    private String subjectName;
    private String subjectPeriod;
    
    public static String getCreateStatement(){
        return "CREATE TABLE IF NOT EXISTS employees_subjects("
                + "cd_dumb INTEGER PRIMARY KEY,"
                + "cd_employee INTEGER NOT NULL,"
                + "cd_subject INTEGER NOT NULL,"
                + "FOREIGN KEY(cd_employee) REFERENCES employees(cd_employee),"
                + "FOREIGN KEY(cd_subject) REFERENCES subjects(cd_subject)"
                + ")";
    }
    
    public static ArrayList<Employees_Subjects> getEmployeesSubjects() throws Exception{
        ArrayList<Employees_Subjects> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        Statement stmt = con.createStatement();
        String query = "SELECT es.*, s.nm_subject, s.nm_period FROM employees_subjects es " +
                       "LEFT JOIN subjects s ON s.cd_subject = es.cd_subject " +
                       "LEFT JOIN employees e ON e.cd_employee = es.cd_employee";
        ResultSet rs = stmt.executeQuery(query);

        while(rs.next()){
            long rowId = rs.getLong("cd_dumb");
            long employee = rs.getLong("cd_employee");
            long subject = rs.getLong("cd_subject");
            String subjectName = rs.getString("nm_subject");
            String period = rs.getString("nm_period");
            list.add(new Employees_Subjects(rowId, employee, subject, subjectName, period));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }
    
    public static void insertEmployeeSubject(long employee, long subject) throws Exception{
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO employees_subjects(cd_employee, cd_subject) VALUES(?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, employee);
        stmt.setLong(2, subject);

        stmt.execute();

        stmt.close();
        con.close();
    }
    
    public static void deleteEmployeeSubject(long id) throws Exception{
        Connection con = AppListener.getConnection();

        String sql = "DELETE FROM employees_subjects WHERE cd_dumb = ?";
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setLong(1, id);
        stmt.execute();

        stmt.close();
        con.close();
    }

    public Employees_Subjects(long rowid, long employee, long subject, String subjectName, String subjectPeriod) {
        this.rowid = rowid;
        this.employee = employee;
        this.subject = subject;
        this.subjectName = subjectName;
        this.subjectPeriod = subjectPeriod;
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

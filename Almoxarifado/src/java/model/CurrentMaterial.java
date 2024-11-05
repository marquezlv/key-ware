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

public class CurrentMaterial {
    private long rowid;
    private long employee;
    private long material;
    private long room;
    private String start;
    private String employeeName;
    private String materialName;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS currentMaterial("
                + "cd_current_material INTEGER PRIMARY KEY,"
                + "cd_material INTEGER,"
                + "cd_employee INTEGER,"
                + "cd_room INTEGER,"
                + "dt_start DATETIME,"
                + "FOREIGN KEY(cd_material) REFERENCES material(cd_material),"
                + "FOREIGN KEY(cd_employee) REFERENCES employees(cd_employee),"
                + "FOREIGN KEY(cd_room) REFERENCES rooms(cd_room)"
                + ")";
    }

    public static int getTotalCurrentMaterial() throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "SELECT COUNT(*) AS total FROM currentMaterial";
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

    public static ArrayList<CurrentMaterial> getMaterials() throws Exception {
        ArrayList<CurrentMaterial> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        String sql = "SELECT c.*, r.cd_room ,m.cd_material, m.nm_material, e.cd_employee, e.nm_employee "
                + "FROM currentMaterial c "
                + "LEFT JOIN employees e ON e.cd_employee = c.cd_employee "
                + "LEFT JOIN material m ON m.cd_material = c.cd_material "
                + "LEFT JOIN rooms r ON r.cd_room = c.cd_room "
                + "ORDER BY e.nm_employee, m.nm_material";
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowid = rs.getLong("cd_current_material");
            long employee = rs.getLong("cd_employee");
            long material = rs.getLong("cd_material");
            long room = rs.getLong("cd_room");
            String employeeName = rs.getString("nm_employee");
            String materialName = rs.getString("nm_material");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Date datetime = new Date(timestamp.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            list.add(new CurrentMaterial(rowid, material, employee, room , date, employeeName, materialName));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<CurrentMaterial> getMaterialPages(int page, int recordsPerPage) throws Exception {
        ArrayList<CurrentMaterial> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        int startIndex = (page - 1) * recordsPerPage;

        String sql = "SELECT c.*, r.cd_room ,m.cd_material ,m.nm_material ,e.cd_employee, e.nm_employee "
                + "FROM currentMaterial c "
                + "LEFT JOIN employees e ON e.cd_employee = c.cd_employee "
                + "LEFT JOIN material m ON m.cd_material = c.cd_material "
                + "LEFT JOIN room r ON r.cd_room = c.cd_room "
                + "ORDER BY e.nm_employee, m.nm_material "
                + "LIMIT ?,?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, startIndex);
        stmt.setInt(2, recordsPerPage);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowid = rs.getLong("cd_current_material");
            long employee = rs.getLong("cd_employee");
            long material = rs.getLong("cd_material");
            long room = rs.getLong("cd_room");
            String employeeName = rs.getString("nm_employee");
            String materialName = rs.getString("nm_material");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Date datetime = new Date(timestamp.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            list.add(new CurrentMaterial(rowid, material, employee, room, date, employeeName, materialName));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertCurrentMaterial(long employee, long material, long room, Date date) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO currentMaterial(cd_employee, cd_material, cd_room, dt_start) VALUES(?,?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, employee);
        stmt.setLong(2, material);
        stmt.setLong(3, room);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        stmt.setDate(4, sqlDate);
        stmt.execute();
        
        stmt.close();
        con.close();
    }

    public static void deleteCurrentMaterial(long id) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "DELETE FROM currentMaterial WHERE cd_current_material=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, id);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public CurrentMaterial(long rowid, long material, long employee, long room, String start, String employeeName, String materialName) {
        this.rowid = rowid;
        this.material = material;
        this.employee = employee;
        this.start = start;
        this.employeeName = employeeName;
        this.materialName = materialName;
        this.room = room;
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

    public long getMaterial() {
        return material;
    }

    public void setMaterial(long material) {
        this.material = material;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public long getRoom() {
        return room;
    }

    public void setRoom(long room) {
        this.room = room;
    }

}
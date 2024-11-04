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

public class HistoryMaterial {

    private long rowid;
    private long employee;
    private long material;
    private long user;
    private String employeeName;
    private String materialName;
    private String userName;
    private String date;
    private String type;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS historyMaterial("
                + "cd_history INTEGER PRIMARY KEY,"
                + "cd_employee INTEGER,"
                + "cd_material INTEGER,"
                + "cd_user INTEGER,"
                + "dt_history DATETIME,"
                + "nm_type VARCHAR(30)"
                + "FOREIGN KEY(cd_material) REFERENCES material(cd_material),"
                + "FOREIGN KEY(cd_employee) REFERENCES employees(cd_employee),"
                + "FOREIGN KEY(cd_user) REFERENCES users(rowid)"
                + ")";
    }

    public static int getTotalHistoryMaterial(String searchParam, String searchMaterial, String strDateStart, String strDateEnd) throws Exception {
        Connection con = AppListener.getConnection();

        String baseSql = "SELECT COUNT(*) AS total FROM historyMaterial h "
                + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                + "LEFT JOIN material m ON m.cd_room = h.cd_material "
                + "LEFT JOIN users u ON u.rowid = h.cd_user ";

        StringBuilder whereClause = new StringBuilder("WHERE 1=1 ");

        if (searchParam != null && !searchParam.isEmpty()) {
            whereClause.append("AND e.nm_employee LIKE ? ");
        }
        if (searchMaterial != null && !searchMaterial.isEmpty()) {
            whereClause.append("AND m.nm_material LIKE ? ");
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
        if (searchMaterial != null && !searchMaterial.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchMaterial + "%");
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

    public static ArrayList<HistoryMaterial> getHistoryMaterial(int page, int recordsPerPage, int column, int sort, String searchParam, String searchMaterial, String strDateStart, String strDateEnd) throws Exception {
        ArrayList<HistoryMaterial> list = new ArrayList<>();
        Connection con = AppListener.getConnection();
        int startIndex = (page - 1) * recordsPerPage;

        String baseSql = "SELECT h.*, e.nm_employee, m.nm_material, u.rowid, u.name FROM history h "
                + "LEFT JOIN employees e ON e.cd_employee = h.cd_employee "
                + "LEFT JOIN material m ON m.cd_material = h.cd_material "
                + "LEFT JOIN users u ON u.rowid = h.cd_user ";

        StringBuilder whereClause = new StringBuilder("WHERE 1=1 ");

        if (searchParam != null && !searchParam.isEmpty()) {
            whereClause.append("AND e.nm_employee LIKE ? ");
        }
        if (searchMaterial != null && !searchMaterial.isEmpty()) {
            whereClause.append("AND m.nm_material LIKE ? ");
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
                sql += (sort == 1 ? "ORDER BY e.nm_employee ASC " : "ORDER BY e.nm_employee DESC ");
                break;
            case 3:
                sql += (sort == 1 ? "ORDER BY s.nm_subject ASC " : "ORDER BY m.nm_material DESC ");
                break;
            case 4:
                sql += (sort == 1 ? "ORDER BY u.name ASC " : "ORDER BY u.name DESC ");
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
        if (searchMaterial != null && !searchMaterial.isEmpty()) {
            stmt.setString(paramIndex++, "%" + searchMaterial + "%");
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
            long employee = rs.getLong("cd_employee");
            long material = rs.getLong("cd_material");
            long user = rs.getLong("rowid");
            String employeeName = rs.getString("nm_employee");
            String materialName = rs.getString("nm_material");
            String userName = rs.getString("name");
            String type = rs.getString("nm_type");
            Timestamp timestamp = rs.getTimestamp("dt_history");
            Date datetime = new Date(timestamp.getTime());
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = displayFormat.format(datetime);
            list.add(new HistoryMaterial(rowId, employee, material, employeeName, materialName, date, user, userName, type));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertHistoryMaterial(long employee, long material,String type, Date date, long user) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO historyMaterial(cd_employee, nm_type ,cd_material, dt_history, cd_user) VALUES(?,?,?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, employee);
        stmt.setString(2,type);
        stmt.setLong(3, material);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        stmt.setDate(4, sqlDate);
        stmt.setLong(5, user);
        stmt.execute();

        stmt.close();
        con.close();
    }

    public HistoryMaterial(long rowid, long employee, long material, String employeeName, String materialName, String date, long user, String userName, String type) {
        this.rowid = rowid;
        this.employee = employee;
        this.material = material;
        this.employeeName = employeeName;
        this.materialName = materialName;
        this.date = date;
        this.user = user;
        this.userName = userName;
        this.type = type;
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
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
}

package model;

import java.sql.*;
import java.util.ArrayList;
import web.AppListener;

public class Rooms {

    private long rowid;
    private String name;
    private String location;
    private String status;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS rooms("
                + "cd_room INTEGER PRIMARY KEY,"
                + "nm_room VARCHAR(50) NOT NULL,"
                + "nm_location VARCHAR(100) NOT NULL,"
                + "nm_status VARCHAR(50) NOT NULL"
                + ")";
    }

    public Rooms(long rowid, String name, String location, String status) {
        this.rowid = rowid;
        this.name = name;
        this.location = location;
        this.status = status;
    }

    public long getRowid() {
        return rowid;
    }

    public void setRowid(long rowid) {
        this.rowid = rowid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static int getTotalRooms(String search, String filter) throws Exception {
        Connection con = AppListener.getConnection();
        String baseSQL = "SELECT COUNT(DISTINCT rooms.cd_room) AS total FROM rooms "
                + "LEFT JOIN filters_rooms ON rooms.cd_room = filters_rooms.cd_room "
                + "LEFT JOIN filters ON filters_rooms.cd_filter = filters.cd_filter ";
        String whereClause = "";

        // Filtro de pesquisa
        if (search != null && !search.isEmpty()) {
            whereClause += "WHERE (rooms.nm_room LIKE ? OR rooms.nm_location LIKE ?) ";
        }

        // Filtro por nm_type em filters
        if (filter != null && !filter.isEmpty()) {
            if (!whereClause.isEmpty()) {
                whereClause += "AND ";
            } else {
                whereClause += "WHERE ";
            }
            whereClause += "filters.nm_type LIKE ? ";
        }

        // Construir SQL final
        String sql = baseSQL + whereClause;

        PreparedStatement stmt = con.prepareStatement(sql);
        int paramIndex = 1;

        // Parâmetros de pesquisa
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
        }

        // Parâmetro de filtro
        if (filter != null && !filter.isEmpty()) {
            stmt.setString(paramIndex++, "%" + filter + "%");
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

    public static ArrayList<Rooms> getRooms(int page, int recordsPerPage, int column, int sort, String search, String filter) throws Exception {
        ArrayList<Rooms> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        int startIndex = (page - 1) * recordsPerPage;
        String baseSQL = "SELECT DISTINCT rooms.cd_room, rooms.nm_room, rooms.nm_location, rooms.nm_status "
                + "FROM rooms "
                + "LEFT JOIN filters_rooms ON rooms.cd_room = filters_rooms.cd_room "
                + "LEFT JOIN filters ON filters_rooms.cd_filter = filters.cd_filter ";
        String whereClause = "";
        String orderClause = "";

        // Ordenação por coluna
        switch (column) {
            case 1:
                orderClause = " ORDER BY rooms.nm_room ";
                break;
            case 2:
                orderClause = " ORDER BY rooms.nm_location ";
                break;
            case 4:
                orderClause = " ORDER BY rooms.nm_status ";
                break;
            default:
                orderClause = " ORDER BY rooms.nm_location, CAST(SUBSTR(rooms.nm_room, INSTR(rooms.nm_room, ' ') + 1) AS INTEGER) ";
                break;
        }

        // Direção da ordenação
        if (sort == 2) {
            orderClause += "DESC ";
        } else {
            orderClause += "ASC ";
        }

        // Filtro de pesquisa
        if (search != null && !search.isEmpty()) {
            whereClause += "WHERE (rooms.nm_room LIKE ? OR rooms.nm_location LIKE ?) ";
        }

        // Filtro por nm_type em filters
        if (filter != null && !filter.isEmpty()) {
            if (!whereClause.isEmpty()) {
                whereClause += "AND ";
            } else {
                whereClause += "WHERE ";
            }
            whereClause += "filters.nm_type LIKE ? ";
        }

        // Construir SQL final com paginação
        String sql = baseSQL + whereClause + orderClause + "LIMIT ?, ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        int paramIndex = 1;

        // Adicionar parâmetros de pesquisa
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
        }

        // Adicionar filtro
        if (filter != null && !filter.isEmpty()) {
            stmt.setString(paramIndex++, "%" + filter + "%");
        }

        // Adicionar parâmetros de paginação
        stmt.setInt(paramIndex++, startIndex);
        stmt.setInt(paramIndex, recordsPerPage);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowid = rs.getLong("cd_room");
            String name = rs.getString("nm_room");
            String location = rs.getString("nm_location");
            String status = rs.getString("nm_status");
            list.add(new Rooms(rowid, name, location, status));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Rooms> getRoomsAll() throws Exception {
        ArrayList<Rooms> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        String sql = "SELECT * FROM rooms " +
             "ORDER BY nm_location, CAST(SUBSTR(nm_room, INSTR(nm_room, ' ') + 1) AS INTEGER)";

        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            long rowid = rs.getLong("cd_room");
            String name = rs.getString("nm_room");
            String location = rs.getString("nm_location");
            String status = rs.getString("nm_status");
            list.add(new Rooms(rowid, name, location, status));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertRoom(String name, String location, String status) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO rooms(nm_room, nm_location, nm_status) VALUES(?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, location);
        stmt.setString(3, status);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void updateRoom(long id, String name, String location, String status) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE rooms SET nm_room = ?,nm_location = ?, nm_status = ? WHERE cd_room = ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, location);
        stmt.setString(3, status);
        stmt.setLong(4, id);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void updateStatus(long id, String status) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE rooms SET nm_status = ? WHERE cd_room = ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, status);
        stmt.setLong(2, id);

        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void deleteRoom(long id) throws Exception {
        Connection con = AppListener.getConnection();

        String sql = "DELETE FROM rooms WHERE cd_room = ?";
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setLong(1, id);
        stmt.execute();

        stmt.close();
        con.close();
    }

}

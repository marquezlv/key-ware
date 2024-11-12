package model;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    private int active;
    private long subject;
    private String subjectName;

    public static String getCreateStatement() {
        return "CREATE TABLE IF NOT EXISTS reservations("
                + "cd_reservation INTEGER PRIMARY KEY,"
                + "cd_employee INTEGER,"
                + "cd_room INTEGER,"
                + "cd_subject INTEGER,"
                + "dt_start DATETIME,"
                + "dt_end DATETIME,"
                + "ic_active INTEGER,"
                + "FOREIGN KEY(cd_employee) REFERENCES employees(cd_employee),"
                + "FOREIGN KEY(cd_room) REFERENCES rooms(cd_room),"
                + "FOREIGN KEY(cd_subject) REFERENCES subjects(cd_subject)"
                + ")";
    }

    public static int getTotalReservations(int order, String filter) throws Exception {
        Connection con = AppListener.getConnection();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        String baseSQL = "SELECT COUNT(*) AS total FROM reservations r "
                + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject ";

        String dateFilter = "";
        String searchFilter = "";

        // Condição de data baseada no parâmetro 'order'
        if (order == 1) {
            dateFilter = "WHERE r.dt_start > ? ";
        } else if (order == 2) {
            dateFilter = "WHERE r.dt_start < ? ";
        }

        // Condição de filtro geral, se o parâmetro 'filter' tiver valor
        if (filter != null && !filter.isEmpty()) {
            searchFilter = (dateFilter.isEmpty() ? "WHERE " : "AND ")
                    + "(e.nm_employee LIKE ? OR s.nm_subject LIKE ? OR ro.nm_room LIKE ? OR ro.nm_location LIKE ?) ";
        }

        String sql = baseSQL + dateFilter + searchFilter;

        PreparedStatement stmt = con.prepareStatement(sql);
        int paramIndex = 1;

        // Definir o parâmetro de data, se o filtro de data estiver presente
        if (!dateFilter.isEmpty()) {
            stmt.setTimestamp(paramIndex++, currentTimestamp);
        }

        // Definir parâmetros de filtro, se o filtro de pesquisa estiver presente
        if (!searchFilter.isEmpty()) {
            String searchPattern = "%" + filter + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
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

    public static ArrayList<Reservation> getSearchReservations(String employeeSearch, String subjectSearch, Date day) throws Exception {
        ArrayList<Reservation> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        // Montar o SQL dinamicamente
        StringBuilder sql = new StringBuilder("SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject ")
                .append("FROM reservations r ")
                .append("LEFT JOIN employees e ON e.cd_employee = r.cd_employee ")
                .append("LEFT JOIN rooms ro ON ro.cd_room = r.cd_room ")
                .append("LEFT JOIN subjects s ON s.cd_subject = r.cd_subject ");

        boolean whereAdded = false;

        // Construção dinâmica do WHERE
        if (employeeSearch != null) {
            sql.append("WHERE e.nm_employee LIKE ? ");
            whereAdded = true;
        }
        if (subjectSearch != null) {
            sql.append(whereAdded ? "AND " : "WHERE ").append("s.nm_subject LIKE ? ");
            whereAdded = true;
        }
        if (day != null) {
            sql.append(whereAdded ? "AND " : "WHERE ").append("r.dt_start BETWEEN ? AND ? ");
        }

        // Ordenação
        sql.append("ORDER BY r.dt_start, e.nm_employee");

        PreparedStatement stmt = con.prepareStatement(sql.toString());

        // Substituir os placeholders com base nos parâmetros presentes
        int paramIndex = 1;
        if (employeeSearch != null) {
            stmt.setString(paramIndex++, "%" + employeeSearch + "%");
        }
        if (subjectSearch != null) {
            stmt.setString(paramIndex++, "%" + subjectSearch + "%");
        }
        if (day != null) {
            long millis = day.getTime();
            long millisEnd = day.getTime() + 24 * 60 * 60 * 1000;
            stmt.setLong(paramIndex++, millis);
            stmt.setLong(paramIndex++, millisEnd);
            System.out.println("Data para o SQL em milissegundos: " + millis);
        }

        ResultSet rs = stmt.executeQuery();

        // Processa os resultados
        while (rs.next()) {
            long rowid = rs.getLong("cd_reservation");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            long subject = rs.getLong("cd_subject");
            int active = rs.getInt("ic_active");
            String employeeName = rs.getString("nm_employee");
            String roomName = rs.getString("nm_room");
            String roomLocation = rs.getString("nm_location");
            String subjectName = rs.getString("nm_subject");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Timestamp timestampEnd = rs.getTimestamp("dt_end");

            // Converte os timestamps para Date, mantendo as horas
            Date datetime = new Date(timestamp.getTime());
            Date datetimeEnd = new Date(timestampEnd.getTime());

            // Formatação das datas para exibição
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            String dateEnd = dateFormat.format(datetimeEnd);

            // Adiciona a reserva na lista
            list.add(new Reservation(rowid, employee, employeeName, room, roomName, roomLocation, subject, subjectName, date, dateEnd, active));
        }

        return list;
    }

    public static ArrayList<Reservation> getReservations(int page, int recordsPerPage, int column, int sort, int order, String filter) throws Exception {
        ArrayList<Reservation> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        int startIndex = (page - 1) * recordsPerPage;

        String baseSQL = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject "
                + "FROM reservations r "
                + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject ";

        String orderClause = "";
        String dateFilter = "";
        String searchFilter = "";

        // Aplicar filtro de data se 'order' for 1 (futuro) ou 2 (passado)
        if (order == 1) {
            dateFilter = " WHERE r.dt_start >= ? ";
        } else if (order == 2) {
            dateFilter = " WHERE r.dt_start < ? ";
        }

        // Definir a cláusula de ordenação com base na coluna especificada
        switch (column) {
            case 1:
                orderClause = " ORDER BY e.nm_employee ";
                break;
            case 2:
                orderClause = " ORDER BY s.nm_subject ";
                break;
            case 3:
                orderClause = " ORDER BY ro.nm_room ";
                break;
            case 4:
                orderClause = " ORDER BY ro.nm_location ";
                break;
            case 5:
                orderClause = " ORDER BY r.dt_start ";
                break;
            case 6:
                orderClause = " ORDER BY r.dt_end ";
                break;
            default:
                orderClause = " ORDER BY r.dt_start, e.nm_employee ";
                break;
        }

        // Definir a direção da ordenação
        if (sort == 2) {
            orderClause += "DESC ";
        } else {
            orderClause += "ASC ";
        }

        // Aplicar filtro de pesquisa geral, se presente
        if (filter != null && !filter.isEmpty()) {
            searchFilter = " AND (e.nm_employee LIKE ? OR s.nm_subject LIKE ? OR ro.nm_room LIKE ? OR ro.nm_location LIKE ?) ";
        }

        String sql = baseSQL + dateFilter + searchFilter + orderClause + "LIMIT ?, ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        int paramIndex = 1;

        // Adicionar o filtro de data, se presente
        if (!dateFilter.isEmpty()) {
            stmt.setTimestamp(paramIndex++, currentTimestamp);
        }

        // Adicionar parâmetros de pesquisa, se o filtro não estiver vazio
        if (!searchFilter.isEmpty()) {
            String searchPattern = "%" + filter + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
        }

        // Adicionar os parâmetros de paginação
        stmt.setInt(paramIndex++, startIndex);
        stmt.setInt(paramIndex, recordsPerPage);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowid = rs.getLong("cd_reservation");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            long subject = rs.getLong("cd_subject");
            int active = rs.getInt("ic_active");
            String employeeName = rs.getString("nm_employee");
            String roomName = rs.getString("nm_room");
            String roomLocation = rs.getString("nm_location");
            String subjectName = rs.getString("nm_subject");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Timestamp timestampend = rs.getTimestamp("dt_end");
            Date datetime = new Date(timestamp.getTime());
            Date datetimeEnd = new Date(timestampend.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            String dateEnd = dateFormat.format(datetimeEnd);
            list.add(new Reservation(rowid, employee, employeeName, room, roomName, roomLocation, subject, subjectName, date, dateEnd, active));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Reservation> getReservationsToday() throws Exception {
        ArrayList<Reservation> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        String sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject "
                + "FROM reservations r "
                + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                + "WHERE r.dt_start >= ? AND r.dt_start < ? "
                + "ORDER BY r.dt_start, e.nm_employee";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setTimestamp(1, startOfDay);
        stmt.setTimestamp(2, endOfDay);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowid = rs.getLong("cd_reservation");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            long subject = rs.getLong("cd_subject");
            int active = rs.getInt("ic_active");
            String employeeName = rs.getString("nm_employee");
            String roomName = rs.getString("nm_room");
            String roomLocation = rs.getString("nm_location");
            String subjectName = rs.getString("nm_subject");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Timestamp timestampend = rs.getTimestamp("dt_end");
            Date datetime = new Date(timestamp.getTime());
            Date datetimeEnd = new Date(timestampend.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            String dateEnd = dateFormat.format(datetimeEnd);
            list.add(new Reservation(rowid, employee, employeeName, room, roomName, roomLocation, subject, subjectName, date, dateEnd, active));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static ArrayList<Reservation> getReservationsAll() throws Exception {
        ArrayList<Reservation> list = new ArrayList<>();
        Connection con = AppListener.getConnection();

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp fourWeeksLater = Timestamp.valueOf(LocalDate.now().plusWeeks(4).atStartOfDay());

        String sql = "SELECT r.*, e.cd_employee, e.nm_employee, ro.cd_room, ro.nm_room, ro.nm_location, s.nm_subject "
                + "FROM reservations r "
                + "LEFT JOIN employees e ON e.cd_employee = r.cd_employee "
                + "LEFT JOIN rooms ro ON ro.cd_room = r.cd_room "
                + "LEFT JOIN subjects s ON s.cd_subject = r.cd_subject "
                + "WHERE r.dt_start >= ? AND r.dt_start < ? "
                + "ORDER BY r.dt_start, e.nm_employee";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setTimestamp(1, now);
        stmt.setTimestamp(2, fourWeeksLater);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            long rowid = rs.getLong("cd_reservation");
            long employee = rs.getLong("cd_employee");
            long room = rs.getLong("cd_room");
            long subject = rs.getLong("cd_subject");
            int active = rs.getInt("ic_active");
            String employeeName = rs.getString("nm_employee");
            String roomName = rs.getString("nm_room");
            String roomLocation = rs.getString("nm_location");
            String subjectName = rs.getString("nm_subject");
            Timestamp timestamp = rs.getTimestamp("dt_start");
            Timestamp timestampend = rs.getTimestamp("dt_end");
            Date datetime = new Date(timestamp.getTime());
            Date datetimeEnd = new Date(timestampend.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy - HH:mm", new Locale("pt", "BR"));
            String date = dateFormat.format(datetime);
            String dateEnd = dateFormat.format(datetimeEnd);
            list.add(new Reservation(rowid, employee, employeeName, room, roomName, roomLocation, subject, subjectName, date, dateEnd, active));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public static void insertReservation(long employee, long room, long subject, Date date, Date end, int active) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "INSERT INTO reservations(cd_employee, cd_room, cd_subject, dt_start, dt_end, ic_active) VALUES(?,?,?,?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setLong(1, employee);
        stmt.setLong(2, room);
        stmt.setLong(3, subject);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        stmt.setDate(4, sqlDate);
        java.sql.Date sqlDateEnd = new java.sql.Date(end.getTime());
        stmt.setDate(5, sqlDateEnd);
        stmt.setInt(6, active);
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

    public static void updateStatus(long id, int active) throws Exception {
        Connection con = AppListener.getConnection();
        String sql = "UPDATE reservations SET ic_active = ? WHERE cd_reservation = ?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, active);
        stmt.setLong(2, id);

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

    public Reservation(long rowid, long employeeid, String employee, long roomid, String roomName, String location, long subject, String subjectName, String start, String end, int active) {
        this.rowid = rowid;
        this.employeeid = employeeid;
        this.employee = employee;
        this.roomid = roomid;
        this.roomName = roomName;
        this.location = location;
        this.start = start;
        this.subject = subject;
        this.subjectName = subjectName;
        this.end = end;
        this.active = active;
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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

}

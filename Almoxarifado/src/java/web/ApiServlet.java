package web;

import jakarta.servlet.ServletException;
import java.sql.Time;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;
import model.Users;
import model.Filters;
import model.Rooms;
import model.Subjects;
import java.time.LocalDate;
import java.time.LocalTime;
import model.Courses;
import model.Employees;
import model.Employees_Subjects;
import model.Filters_Rooms;
import model.Reservation;
import model.CurrentKey;
import model.History;
import org.json.JSONArray;

@WebServlet(name = "ApiServlet", urlPatterns = {"/api/*"})
public class ApiServlet extends HttpServlet {

    public JSONObject getJSONBODY(BufferedReader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return new JSONObject(buffer.toString());
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject file = new JSONObject();

        try {
            if (request.getRequestURI().endsWith("/api/session")) {
                processSession(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/users")) {
                processUsers(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/filters")) {
                processFilters(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/rooms")) {
                processRooms(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/subjects")) {
                processSubjects(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/courses")) {
                processCourses(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/employees")) {
                processEmployees(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/filters_room")) {
                processFiltersRoom(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/reservations")) {
                processReservation(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/employee_subject")) {
                processEmployeesSubjects(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/keys")) {
                processKeys(file, request, response);
            } else if (request.getRequestURI().endsWith("/api/history")) {
                processHistory(file, request, response);
            }
 
        } catch (Exception ex) {
            response.sendError(500, "Internal Error: " + ex.getLocalizedMessage());
        }
        response.getWriter().print(file.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void processSession(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getMethod().toLowerCase().equals("put")) {
            JSONObject body = getJSONBODY(request.getReader());
            String login = body.getString("login");
            String password = body.getString("password");
            Users u = Users.getUser(login, password);
            if (u == null) {
                response.sendError(403, "Login or password incorrect");
            } else {
                // Setando a sessão do usuario
                request.getSession().setAttribute("users", u);
                file.put("id", u.getRowid());
                file.put("login", u.getLogin());
                file.put("name", u.getName());
                file.put("role", u.getRole());
                file.put("passwordHash", u.getPasswordHash());
                file.put("message", "Logged in");
            }
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            // Removendo a sessão do usuario
            request.getSession().removeAttribute("users");
            file.put("message", "Logged out");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            // Verificando se existe sessão do usuario
            if (request.getSession().getAttribute("users") == null) {
                response.sendError(403, "No Session");
            } else {
                // Se houver resgata os atributos
                Users u = (Users) request.getSession().getAttribute("users");
                file.put("id", u.getRowid());
                file.put("login", u.getLogin());
                file.put("name", u.getName());
                file.put("role", u.getRole());
                file.put("passwordHash", u.getPasswordHash());
            }
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processUsers(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (!((Users) request.getSession().getAttribute("users")).getRole().equals("ADMIN")) {
            response.sendError(401, "Unauthorized: Only admin can manage users");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            int page = Integer.parseInt(request.getParameter("page"));
            int itemPage = 5;
            file.put("list", new JSONArray(Users.getUsers(page, itemPage)));
            file.put("total", Users.getTotalUsers());
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            String login = body.getString("login");
            String name = body.getString("name");
            String role = body.getString("role");
            String password = body.getString("password");
            Users.insertUser(login, name, role, password);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            JSONObject body = getJSONBODY(request.getReader());
            String login = body.getString("login");
            String name = body.getString("name");
            String role = body.getString("role");
            String password = body.getString("password");
            Long id = Long.parseLong(request.getParameter("id"));
            Users.updateUser(id ,login, name, role, password);
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Users.deleteUser(id);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processFilters(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            String pageParam = request.getParameter("page");
            if (pageParam == null) {
                file.put("list", new JSONArray(Filters.getFilters()));
            } else {
                int page = Integer.parseInt(pageParam);
                int itemsPerPage = 5;
                file.put("list", new JSONArray(Filters.getFiltersPages(page, itemsPerPage)));
                file.put("total", Filters.getTotalFilters());
            }
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            String type = body.getString("type");
            String desc = body.getString("desc");
            Filters.insertFilter(type, desc);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            JSONObject body = getJSONBODY(request.getReader());
            String type = body.getString("type");
            String desc = body.getString("desc");
            Long id = Long.parseLong(request.getParameter("id"));
            Filters.updateFilter(id, type, desc);
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Filters.deleteFilter(id);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processRooms(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            String pageParam = request.getParameter("page");
            if (pageParam == null) {
                file.put("list", new JSONArray(Rooms.getRoomsAll()));
            } else {
                int itemsPerPage = Integer.parseInt(request.getParameter("items"));
                int column = Integer.parseInt(request.getParameter("column"));
                int sort = Integer.parseInt(request.getParameter("sort"));
                int page = Integer.parseInt(pageParam);
                file.put("list", new JSONArray(Rooms.getRooms(page, itemsPerPage, column, sort)));
                file.put("total", Rooms.getTotalRooms());
            }
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            String name = body.getString("name");
            String location = body.getString("location");
            String status = body.getString("status");
            Rooms.insertRoom(name, location, status);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            JSONObject body = getJSONBODY(request.getReader());
            String name = body.getString("name");
            String location = body.getString("location");
            String status = body.getString("status");
            Long id = Long.parseLong(request.getParameter("id"));
            Rooms.updateRoom(id, name, location, status);
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Rooms.deleteRoom(id);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processSubjects(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            String pageParam = request.getParameter("page");
            if (pageParam == null) {
                file.put("list", new JSONArray(Subjects.getSubjects()));
            } else {
                int page = Integer.parseInt(pageParam);
                int itemsPerPage = 5;
                file.put("list", new JSONArray(Subjects.getSubjectsPages(page, itemsPerPage)));
                file.put("total", Subjects.getTotalSubjects());
            }
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            String name = body.getString("name");
            long course = body.getLong("course");
            String period = body.getString("period");
            Subjects.insertSubject(name, course, period);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            JSONObject body = getJSONBODY(request.getReader());
            String name = body.getString("name");
            long course = body.getLong("course");
            String period = body.getString("period");
            Long id = Long.parseLong(request.getParameter("id"));
            Subjects.updateSubject(id, name, course, period);
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Subjects.deleteSubject(id);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processCourses(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            String pageParam = request.getParameter("page");
            if (pageParam == null) {
                file.put("list", new JSONArray(Courses.getCourses()));
            } else {
                int page = Integer.parseInt(pageParam);
                int itemsPerPage = 5;
                file.put("list", new JSONArray(Courses.getCoursesPages(page, itemsPerPage)));
                file.put("total", Courses.getTotalCourses());
            }           
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            String name = body.getString("name");
            Courses.insertCourse(name);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            JSONObject body = getJSONBODY(request.getReader());
            String name = body.getString("name");
            Long id = Long.parseLong(request.getParameter("id"));
            Courses.updateCourse(id, name);
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Courses.deleteCourse(id);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processEmployees(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
             String pageParam = request.getParameter("page");
            if (pageParam == null) {
                file.put("list", new JSONArray(Employees.getEmployees()));
            } else {
                int page = Integer.parseInt(pageParam);
                int itemsPerPage = 5;
                file.put("list", new JSONArray(Employees.getEmployeesPages(page, itemsPerPage)));
                file.put("total", Employees.getTotalEmployees());
            }
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            String name = body.getString("name");
            String type = body.getString("type");
            Employees.insertEmployee(name, type);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            JSONObject body = getJSONBODY(request.getReader());
            String name = body.getString("name");
            String type = body.getString("type");
            Long id = Long.parseLong(request.getParameter("id"));
            Employees.updateEmployee(id, name, type);
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Employees.deleteEmployee(id);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processFiltersRoom(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            int column = Integer.parseInt(request.getParameter("column"));
            int sort = Integer.parseInt(request.getParameter("sort"));
            file.put("list", new JSONArray(Filters_Rooms.getFiltersRoom(column, sort)));
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            long room = body.getLong("room");
            long filter = body.getLong("filter");
            Filters_Rooms.insertFiltersRoom(filter, room);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            response.sendError(401, "Update: This table cannot be update");
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Filters_Rooms.deleteFiltersRoom(id);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processReservation(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            int page = Integer.parseInt(request.getParameter("page"));
            int itemPage = Integer.parseInt(request.getParameter("items"));
            int column = Integer.parseInt(request.getParameter("column"));
            int sort = Integer.parseInt(request.getParameter("sort"));
            file.put("list", new JSONArray(Reservation.getReservations(page, itemPage, column, sort)));
            file.put("total", Reservation.getTotalReservations());
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            long employee = body.getLong("employee");
            long room = body.getLong("room");
            long subject = body.getLong("subject");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String strDateTime = body.getString("date");
            String strDateTimeEnd = body.getString("end");
            Date dateTime = dateFormat.parse(strDateTime);
            Date dateTimeEnd = dateFormat.parse(strDateTimeEnd);
            Reservation.insertReservation(employee, room, subject, dateTime, dateTimeEnd);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            JSONObject body = getJSONBODY(request.getReader());
            long employee = body.getLong("employee");
            long room = body.getLong("room");
            long subject = body.getLong("subject");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String strDate = body.getString("date");
            String strDateEnd = body.getString("end");
            Date date = dateFormat.parse(strDate);
            Date dateEnd = dateFormat.parse(strDateEnd);
            Long id = Long.parseLong(request.getParameter("id"));
            Reservation.updateReservation(id, employee, room, subject, date, dateEnd);
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Reservation.deleteReservation(id);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processEmployeesSubjects(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            file.put("list", new JSONArray(Employees_Subjects.getEmployeesSubjects()));
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            long employee = body.getLong("employee");
            long subject = body.getLong("subject");
            Employees_Subjects.insertEmployeeSubject(employee, subject);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            response.sendError(401, "Update: This table cannot be update");
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Employees_Subjects.deleteEmployeeSubject(id);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

    private void processKeys(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            file.put("list", new JSONArray(CurrentKey.getKeys()));
        } else if (request.getMethod().toLowerCase().equals("post")) {
            JSONObject body = getJSONBODY(request.getReader());
            long room = body.getLong("room");
            long employee = body.getLong("employee");
            long subject = body.getLong("subject");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String strDate = body.getString("start");
            Date date = dateFormat.parse(strDate);
            History.insertHistory(employee, room, subject ,"Retirada", date);
            CurrentKey.insertKey(employee, room, subject, date);
        } else if (request.getMethod().toLowerCase().equals("put")) {
            response.sendError(401, "Update: This table cannot be update");
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Long employee = Long.parseLong(request.getParameter("employee"));
            Long room = Long.parseLong(request.getParameter("room"));
            Long subject = Long.parseLong(request.getParameter("subject"));
            History.insertHistory(employee, room, subject ,"Devolvido", new Date());
            CurrentKey.deleteKey(id, room);
        } else {
            response.sendError(405, "Method not allowed");
        }
    }
    
    private void processHistory(JSONObject file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("users") == null) {
            response.sendError(401, "Unauthorized: No session");
        } else if (request.getMethod().toLowerCase().equals("get")) {
            int page = Integer.parseInt(request.getParameter("page"));
            int itemPage = 5;
            file.put("list", new JSONArray(History.getHistory(page, itemPage)));
            file.put("total", History.getTotalHistory());
        } else if (request.getMethod().toLowerCase().equals("post")) {
            response.sendError(401, "Insert: This table cannot be inserted directly");
        } else if (request.getMethod().toLowerCase().equals("put")) {
            response.sendError(401, "Update: This table cannot be update");
        } else if (request.getMethod().toLowerCase().equals("delete")) {
            response.sendError(401, "Delete: History cannot be deleted directly");
        } else {
            response.sendError(405, "Method not allowed");
        }
    }

}

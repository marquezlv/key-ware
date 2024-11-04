package web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;
import model.Users;
import model.Material;
import model.Filters;
import model.Rooms;
import model.Subjects;
import model.Courses;
import model.Employees;
import model.Employees_Subjects;
import model.Filters_Rooms;
import model.Reservation;
import model.CurrentKey;
import model.CurrentMaterial;
import model.History;
import model.HistoryMaterial;

@WebListener
public class AppListener implements ServletContextListener {

    public static final String CLASS_NAME = "org.sqlite.JDBC";
    public static final String URL = "jdbc:sqlite:almoxarifado.db";
    public static String initializeLog = "";
    public static Exception exception = null;

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        try {
            Connection c = AppListener.getConnection();
            Statement s = c.createStatement();
            s.execute("PRAGMA encoding = 'UTF-8'");

            // Deletar todas as tabelas existentes
//            try {
//                s.execute("DROP TABLE IF EXISTS employees_subjects");
//                s.execute("DROP TABLE IF EXISTS filters_rooms");
//                s.execute("DROP TABLE IF EXISTS reservations");
//                s.execute("DROP TABLE IF EXISTS users");
//                s.execute("DROP TABLE IF EXISTS filters");
//                s.execute("DROP TABLE IF EXISTS rooms");
//                s.execute("DROP TABLE IF EXISTS subjects");
//                s.execute("DROP TABLE IF EXISTS courses");
//                s.execute("DROP TABLE IF EXISTS employees");
//                s.execute("DROP TABLE IF EXISTS currentKey");
//                s.execute("DROP TABLE IF EXISTS history");
//            } catch (SQLException e) {
//                initializeLog += new Date() + ": Error during table deletion: " + e.getMessage();
//                e.printStackTrace();
//            }
            // Criar as tabelas novamente
            s.execute(Employees.getCreateStatement());

            s.execute(Rooms.getCreateStatement());

            s.execute(History.getCreateStatement());
            
            s.execute(HistoryMaterial.getCreateStatement());

            s.execute(Users.getCreateStatement());

            s.execute(CurrentKey.getCreateStatement());
            
            s.execute(CurrentMaterial.getCreateStatement());

            s.execute(Employees_Subjects.getCreateStatement());

            s.execute(Reservation.getCreateStatement());

            s.execute(Filters_Rooms.getCreateStatement());

            s.execute(Filters.getCreateStatement());
            
            s.execute(Material.getCreateStatement());

            s.execute(Subjects.getCreateStatement());

            s.execute(Courses.getCreateStatement());
            
            // Inserir usuário administrador, se não houver usuários
            if (Users.getUsersAll().isEmpty()) {
                Users.insertUser("admin", "Administrador", "ADMIN", "1234");
            }
        } catch (Exception ex) {
            initializeLog += new Date() + ": Error during database initialization: " + ex.getMessage();
            exception = ex;
        }
    }

    public static String getMd5Hash(String text) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(text.getBytes(), 0, text.length());
        return new BigInteger(1, m.digest()).toString();
    }

    public static Connection getConnection() throws Exception {
        Class.forName(CLASS_NAME);
        return DriverManager.getConnection(URL);
    }

}

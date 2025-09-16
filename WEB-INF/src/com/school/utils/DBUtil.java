package com.school.utils;

import java.sql.*;

public class DBUtil {
    private static final String url = "jdbc:mysql://localhost:3306/school";
    private static final String user = "root";
    private static final String pass = "DineshArjun@2004";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}

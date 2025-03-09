//package com.taskmanager.dao;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DatabaseConnection {
//    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//    private static final String URL = "jdbc:sqlserver://localhost:50666;databaseName=taskmanager;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
//
//
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL);
//    }
//}
//package com.taskmanager.dao;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DatabaseConnection {
//    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//    private static final String URL = "jdbc:sqlserver://localhost:50666;databaseName=taskmanager;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
//    private static final String AUTH_DLL_PATH = "C:\\Users\\FahmaouiMohamed\\Documents\\sqljdbc_12.8.1.0_enu\\sqljdbc_12.8\\enu\\auth\\x64\\mssql-jdbc_auth-12.8.1.x64.dll";
//
//    static {
//        try {
//            // Load the driver
//            Class.forName(DRIVER);
//
//            // Set the auth DLL location
//            System.setProperty("java.sql.lib", AUTH_DLL_PATH);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException("SQL Server JDBC Driver not found", e);
//        }
//    }
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL);
//    }
//}
package com.taskmanager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/taskmanager";
    private static final String USER = "root";  // replace with your MySQL username
    private static final String PASSWORD = "051203";   // replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
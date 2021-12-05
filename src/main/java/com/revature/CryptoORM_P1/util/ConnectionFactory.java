package com.revature.CryptoORM_P1.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton Design Pattern
 *  - Creational pattern
 *  - Restricts a class so that only a single instance of it can be made within an application
 *  - Constructor cannot be invoked outside of the class
 *
 *  Factory Design Pattern
 *   - Creational pattern
 *   - Used to abstract away the creation/instantiation logic of an object
 */
public class ConnectionFactory {

    private static final ConnectionFactory connectionFactory = new ConnectionFactory();
    private Properties props;

    static {
        try {
            // Forcibly load the PostgreSQL Driver into JVM memory so that it can create a connection
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionFactory getInstance() {
        return connectionFactory;
    }

    /**
     *  Necessary to inject properties before using class
     * @param props
     */
    public void addProperties (Properties props) {
        this.props = props;
    }

    public Connection getConnection() throws SQLException {

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(props.getProperty("url"), props.getProperty("username"), props.getProperty("password"));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("ConnectionFactory#getConnection: " + e.getMessage());
        }

        return conn;

    }

}
package main.java.config;

import main.java.audit.AuditService;
import main.java.exceptions.ConnectionException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionProvider {

    private ConnectionProvider() {}

    public static Connection getConnection() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("db.proprieties")) {
            props.load(input);
        } catch (FileNotFoundException e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot load db.proprieties"));
        } catch (IOException e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot read db.proprieties"));
        }
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database"));
        }
        return null;
    }
}

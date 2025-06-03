package com.bitbites.bitbites2.backend.config;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.exceptions.ConnectionException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import org.postgresql.*;

public class ConnectionProvider {

    private ConnectionProvider() {}

    public static Connection getConnection() {
        Properties props = new Properties();
        try (InputStream input = ConnectionProvider.class.getClassLoader().getResourceAsStream("db.properties")) {
            props.load(input);
        } catch (FileNotFoundException e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot load db.proprieties"));
        } catch (IOException e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot read db.proprieties"));
        }
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        AuditService.getInstance().log("Connecting to " + url);
        AuditService.getInstance().log("User: " + user);
        AuditService.getInstance().log("Password: " + password);
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database" + e.getMessage()));
        }
        return null;
    }
}

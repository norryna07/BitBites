package main.java.audit;

import main.java.exceptions.AuditException;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static AuditService instance;

    private static final String FILE_PATH = "logs.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditService() {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.append("Timestamp,Action, Thread\n");
        } catch (IOException e) {
            throw new AuditException("Cannot write to audit log");
        }
    }

    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void log(String action) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            String time = LocalDateTime.now().format(formatter);
            String thread = Thread.currentThread().getName();
            writer.append(String.format("%s,%s,%s\n", time, action, thread));
        } catch (IOException e) {
            throw new AuditException("Cannot write to audit log");
        }
    }

    public void logInsert(String table, int id) {
        log(String.format("INSERTED into %s with id %d", table, id));
    }

    public void logInsert(String table, String name) {
        log(String.format("INSERTED into %s with id %s", table, name));
    }

    public void logDelete(String table, int id) {
        log(String.format("DELETED from %s with id %d", table, id));
    }

    public void logDelete(String table, String name) {
        log(String.format("DELETED from %s with id %s", table, name));
    }

    public void logUpdate(String table, int id) {
        log(String.format("UPDATED %s with id %d", table, id));
    }

    public void logUpdate(String table, String name) {
        log(String.format("UPDATED %s with id %s", table, name));
    }

    public void logException(Exception e) {
        String exceptionName = e.getClass().getName();
        exceptionName = exceptionName.substring(exceptionName.lastIndexOf('.') + 1);
        log(String.format("%s: %s", exceptionName, e.getMessage()));
    }
}

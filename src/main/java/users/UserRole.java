package main.java.users;

public enum UserRole {
    COOKER,
    ADMIN,
    WRITER;

    public String toString() {
        return name().toLowerCase();
    }
}

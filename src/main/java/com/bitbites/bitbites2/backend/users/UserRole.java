package com.bitbites.bitbites2.backend.users;

public enum UserRole {
    COOKER,
    ADMIN,
    WRITER;

    public String toString() {
        return name().toLowerCase();
    }
}

package com.bitbites.bitbites2.backend.repositories;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.exceptions.UserException;
import com.bitbites.bitbites2.backend.users.User;
import com.bitbites.bitbites2.backend.users.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersRepository implements BitBitesRepository<User>{
    private static UsersRepository instance = null;

    private UsersRepository() {}

    public static UsersRepository getInstance() {
        if (instance == null) {
            instance = new UsersRepository();
        }
        return instance;
    }

    @Override
    public int add(Connection connection, User item) {
        String sql = """
                INSERT INTO users (username, hashed_password, role)
                VALUES (?, ?, ?::role_type);
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getUsername());
            ps.setString(2, item.getHashedPassword());
            ps.setString(3, item.getRole().toString());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    AuditService.getInstance().logInsert("users", rs.getInt("id"));
                    return rs.getInt("id");
                } else {
                    AuditService.getInstance().logException(new UserException("Cannot add user to database: " + item.getUsername()));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new UserException("Cannot add user to database: " + item.getUsername()));
        }
        return 0;
    }

    @Override
    public Optional<User> getById(Connection connection, int id) {
        String sql = """
                SELECT username, hashed_password, role
                FROM users
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            id,
                            rs.getString("username"),
                            rs.getString("hashed_password"),
                            UserRole.valueOf(rs.getString("role").toUpperCase())
                    ));
                } else {
                    AuditService.getInstance().logException(new UserException("Cannot get user from database: " + id));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new UserException("Cannot get user from database: " + id));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getByName(Connection connection, String name) {
        String sql = """
                SELECT id, username, hashed_password, role
                FROM users
                WHERE username = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getInt("id"),
                            name,
                            rs.getString("hashed_password"),
                            UserRole.valueOf(rs.getString("role").toUpperCase())
                    ));
                } else  {
                    AuditService.getInstance().logException(new UserException("Cannot get user from database: " + name));
                }
            }


        } catch (SQLException e) {
            AuditService.getInstance().logException(new UserException("Cannot get user from database: " + name));
        }
        return Optional.empty();
    }

    @Override
    public void update(Connection connection, User item) {
        String sql = """
                UPDATE users
                SET username = ?, hashed_password = ?, role = ?::role_type
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.getUsername());
            ps.setString(2, item.getHashedPassword());
            ps.setString(3, item.getRole().toString());
            ps.setInt(4, item.getId());

            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                AuditService.getInstance().logException(new UserException("Cannot update user in database: " + item.getId()));
            } else {
                AuditService.getInstance().logUpdate("users", item.getId());
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new UserException("Cannot update user in database: " + item.getId()));
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteById(Connection connection, int id) {
        String sql = """
                DELETE FROM users
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int deleteRows = ps.executeUpdate();
            if (deleteRows == 0) {
                AuditService.getInstance().logException(new UserException("Cannot delete user from database: " + connection));
            }
            else {
                AuditService.getInstance().logDelete("users", id);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new UserException("Cannot delete user from database: " + connection));
        }
    }

    @Override
    public void deleteByName(Connection connection, String name) {
        String sql = """
                DELETE FROM users
                WHERE username = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            int deleteRows = ps.executeUpdate();
            if (deleteRows == 0) {
                AuditService.getInstance().logException(new UserException("Cannot delete user from database: " + connection));
            }
            else {
                AuditService.getInstance().logDelete("users", name);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new UserException("Cannot delete user from database: " + connection));
        }
    }

    @Override
    public Optional<List<User>> getAll(Connection connection) {
        String sql = """
                SELECT id, username, hashed_password, role
                FROM users
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("hashed_password"),
                        UserRole.valueOf(rs.getString("role").toUpperCase())
                ));
            }
            return Optional.of(users);
        } catch (SQLException e) {
            AuditService.getInstance().logException(new UserException("Cannot get all users from database"));
        }
        return Optional.empty();
    }
}

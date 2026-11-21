package repository;

import domain.exceptions.RepositoryException;

import java.sql.SQLException;

public class SQLErrors {
    public static RepositoryException toRepositoryException(String action, SQLException e) {
        String state = e.getSQLState();
        if (state != null && state.startsWith("08")) {
            return new RepositoryException(
                    "Cannot connect to the database. Check that PostgreSQL is running "
                            + "and that the settings in config.properties are correct.", e);
        }
        if (state != null && state.startsWith("28")) {
            return new RepositoryException(
                    "Database login failed. Check db.user and db.password in config.properties.", e);
        }
        if ("23503".equals(state)) {
            return new RepositoryException(
                    "Operation refused: the data is still referenced by other records.", e);
        }
        return new RepositoryException("Database error while trying to " + action + ".", e);
    }
}

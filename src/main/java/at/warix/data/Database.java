package at.warix.data;

import at.warix.data.entities.Vote;
import at.warix.data.repositories.VoteRepository;
import at.warix.exceptions.VoteException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database implements VoteRepository {

    private static final String VENDOR = "mysql";
    private Connection conn = null;
    private static Database db;

    public final String getConnectionString() {
        return "jdbc:" + VENDOR + "://" + DatabaseConnectionDetails.getHost() + ":" + DatabaseConnectionDetails.getPort() + "/" + DatabaseConnectionDetails.getDatabase();
    }

    //<editor-fold defaultstate="collapsed" desc="Initialization">
    private Database() throws SQLException {
        createConnection(getConnectionString());
        setupDatabase();
    }

    private void createConnection(String connectionString) throws SQLException {
        if (conn == null) {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        }
        conn = DriverManager.getConnection(connectionString, DatabaseConnectionDetails.getUsername(), DatabaseConnectionDetails.getPassword());
    }

    private void closeConnection() throws SQLException {
        conn.close();
        conn = null;
    }

    private void setupDatabase() throws SQLException {
        Statement setup = conn.createStatement();
        // As it is a pain in the butt to ask Mojang all the time for the name by the uuid,
        // I will save the player name as well.
        // setup.addBatch("CREATE DATABASE IF NOT EXISTS example_plugin");
        setup.addBatch("CREATE TABLE IF NOT EXISTS votes ( id INT PRIMARY KEY AUTO_INCREMENT, uuid VARCHAR(36) UNIQUE, player_name VARCHAR(16), voted_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        setup.executeBatch();
    }

    public static Database newInstance() throws SQLException {
        if (db == null) {
            db = new Database();
        }
        return db;
    }

    //</editor-fold>

    //<editor-fold desc="Repository Method Implementations">

    @Override
    public synchronized List<Vote> getVotes() throws SQLException {
        return executeSelectQuery(VoteRepository.SQL_GET_VOTES, Vote.class);
    }

    @Override
    public synchronized Vote getVoteByUuid(UUID uuid) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(VoteRepository.SQL_GET_VOTE_BY_UUID);
        stmt.setString(1, uuid.toString());
        List<Vote> votes = executeSelectQuery(stmt, Vote.class);
        return votes.isEmpty() ? null : votes.get(0);
    }

    @Override
    public synchronized Vote getVoteByPlayerName(String playerName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(VoteRepository.SQL_GET_VOTE_BY_NAME);
        stmt.setString(1, playerName);
        List<Vote> votes = executeSelectQuery(stmt, Vote.class);
        return votes.isEmpty() ? null : votes.get(0);
    }

    @Override
    public synchronized void addVote(Vote vote) throws SQLException, VoteException {
        try {
            PreparedStatement stmt = conn.prepareStatement(VoteRepository.SQL_INSERT_VOTE);
            stmt.setString(1, vote.getPlayerUuid().toString());
            stmt.setString(2, vote.getPlayerName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            // 23000 is the error, if a unique constraint is violated
            if (ex.getSQLState().equals("23000")) {
                throw new VoteException("You already have voted for the server!");
            } else {
                throw ex;
            }
        }
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Boilerplate Code">
    private <T> List<T> executeSelectQuery(String select, Class<T> targetClass) throws SQLException {
        List<T> values;
        try (ResultSet resultSet = conn.createStatement().executeQuery(select)) {
            values = processResultSet(resultSet, targetClass);
        }
        return values;
    }

    private <T> List<T> executeSelectQuery(PreparedStatement statement, Class<T> targetClass) throws SQLException {
        List<T> values;
        try (ResultSet resultSet = statement.executeQuery()) {
            values = processResultSet(resultSet, targetClass);
        }
        return values;
    }

    private <T> List<T> processResultSet(ResultSet resultSet, Class<T> targetClass) throws SQLException {
        ArrayList<T> result = new ArrayList<>();
        if (targetClass.equals(Vote.class)) {
            while (resultSet.next()) {
                result.add((T) createVoteFromResultSet(resultSet));
            }
        } else {
            throw new IllegalArgumentException("Class " + targetClass + " is not supported");
        }
        return result;
    }
//</editor-fold>

    //<editor-fold desc="ResultSet --> Objects">
    private Vote createVoteFromResultSet(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("votes_id");
        UUID uuid = UUID.fromString(resultSet.getString("votes_player_uuid"));
        String name = resultSet.getString("votes_player_name");
        LocalDateTime votedOn = resultSet.getTimestamp("votes_voted_on").toLocalDateTime();
        return new Vote(id, uuid, name, votedOn);
    }


//</editor-fold>

}

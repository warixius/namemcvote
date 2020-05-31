package at.warix.data.repositories;

import at.warix.data.entities.Vote;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface VoteRepository {

    String SQL_GET_VOTES = "SELECT id AS votes_id, uuid as votes_player_uuid, player_name as votes_player_name, voted_on as votes_voted_on FROM example_plugin.votes";
    String SQL_GET_VOTE_BY_NAME = "SELECT id AS votes_id, uuid as votes_player_uuid, player_name as votes_player_name, voted_on as votes_voted_on FROM example_plugin.votes WHERE player_name LIKE ?";
    String SQL_GET_VOTE_BY_UUID = "SELECT id AS votes_id, uuid as votes_player_uuid, player_name as votes_player_name, voted_on as votes_voted_on FROM example_plugin.votes WHERE votes.uuid = ?";
    String SQL_INSERT_VOTE = "INSERT INTO votes (uuid, player_name) VALUE (?, ?)";

    List<Vote> getVotes() throws SQLException;
    Vote getVoteByUuid(UUID uuid) throws SQLException;
    Vote getVoteByPlayerName(String playerName) throws SQLException;
    void addVote(Vote vote) throws SQLException;
}

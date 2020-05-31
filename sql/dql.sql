USE example_plugin;


SELECT id AS votes_id, uuid as votes_player_uuid, player_name as votes_player_name, voted_on as votes_voted_on FROM example_plugin.votes;

SELECT id AS votes_id, uuid as votes_player_uuid, player_name as votes_player_name, voted_on as votes_voted_on FROM example_plugin.votes WHERE player_name LIKE 'warix';
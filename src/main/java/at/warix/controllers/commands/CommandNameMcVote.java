package at.warix.controllers.commands;

import at.warix.ExamplePlugin;
import at.warix.data.Database;
import at.warix.data.NameMcAccessController;
import at.warix.data.entities.Vote;
import at.warix.data.repositories.VoteRepository;
import at.warix.exceptions.HTTPException;
import at.warix.exceptions.PermissionException;
import at.warix.exceptions.VoteException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandNameMcVote implements CommandExecutor {

    //<editor-fold desc="Field Variables">
    private ExamplePlugin plugin;
    private final VoteRepository voteRepository;
    private double reward;
    private Logger logger;

    private String prefix;
    //</editor-fold>

    //<editor-fold desc="Initialization">
    public CommandNameMcVote(ExamplePlugin plugin) throws SQLException {
        voteRepository = Database.newInstance();
        this.plugin = plugin;
        this.reward = plugin.getConfig().getDouble("reward");
        this.prefix = ChatColor.GRAY + "[" + ChatColor.GOLD + plugin.getDescription().getPrefix() + ChatColor.GRAY + "] ";
        this.logger = plugin.getLogger();
    }
    //</editor-fold>

    //<editor-fold desc="Getters/Setters">
    public double getReward() {
        return reward;
    }

    private void setReward(double newReward) {
        this.reward = newReward;
    }
    //</editor-fold>


    //<editor-fold desc="Handlers">

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        boolean valid = false;
        try {
            if (args.length == 0) {
                onHelp(commandSender);
            } else {
                switch (args[0]) {
                    case "help": {
                        onHelp(commandSender);
                        break;
                    }

                    case "vote": {
                        onVote(commandSender);
                        break;
                    }

                    case "check": {
                        String username = args[1];
                        checkUserVote(commandSender, username);
                        break;
                    }

                    case "list": {
                        onList(commandSender);
                        break;
                    }

                    case "reward": {
                        String mode = args[1];
                        if (mode.equals("set")) {
                            double rewardMoney = Double.parseDouble(args[2]);
                            doSetReward(rewardMoney);
                            sendMessageToSender(commandSender, ChatColor.RED + "The reward has been set to: " + ChatColor.GREEN + getReward());
                        } else if (mode.isEmpty() || mode.equals("get")) {
                            sendMessageToSender(commandSender, ChatColor.RED + "The reward that is given for a vote: " + ChatColor.GREEN + getReward());
                        } else {
                            throw new IllegalArgumentException("unknown mode provided");
                        }

                        break;
                    }

                    default: {
                        throw new IllegalArgumentException("unknown argument: " + args[0]);
                    }
                }
            }

            valid = true;
        } catch (IndexOutOfBoundsException ex) {
            sendMessageToSender(commandSender, ChatColor.RED + "Error: Too few arguments provided!");
        } catch (NumberFormatException ex) {
            sendMessageToSender(commandSender, ChatColor.RED + "Error: Please type in a valid number!");
        } catch (IllegalArgumentException | VoteException | PermissionException ex) {
            sendMessageToSender(commandSender, ChatColor.RED + "Error: " + ex.getMessage());
        } catch (SQLException ex) {
            sendMessageToSender(commandSender, ChatColor.RED + "SQL Error: " + ex.getMessage());
            logger.log(Level.WARNING, "There was an error while connecting to the database!", ex);
        } catch (HTTPException ex) {
            sendMessageToSender(commandSender, ChatColor.RED + "There was an error while connecting to NameMC!");
            logger.log(Level.WARNING, "There was an error while connecting to NameMC!", ex);
        } catch (Exception ex) {
            sendMessageToSender(commandSender, ChatColor.RED + "Severe error! Please contact an administrator!");
            logger.log(Level.SEVERE, "Severe Error!", ex);
        }


        return valid;
    }

    private void onHelp(CommandSender commandSender) {
        List<String> msgs = new ArrayList<>();

        msgs.add(ChatColor.GREEN + "/namemcvote help " + ChatColor.GRAY + "- to get a help page for this plugin");
        msgs.add(ChatColor.GREEN + "/namemcvote vote " + ChatColor.GRAY + "- If the user has not voted for the server, it will send a link, else the user is given a monetary reward, if it hasn't been given (10.000 Bucks). ");
        if (commandSender.hasPermission("namemcvote.admin")) {
            msgs.add(ChatColor.RED + "/namemcvote check <Name> " + ChatColor.GRAY + "- To verify whether someone voted for a server.");
            msgs.add(ChatColor.RED + "/namemcvote list " + ChatColor.GRAY + "- to get a list of all users who voted for a server on NameMC");
            msgs.add(ChatColor.RED + "/namemcvote reward set <Amount> " + ChatColor.GRAY + "- to set the amount to be received.");
        }
        commandSender.sendMessage(msgs.toArray(new String[0]));
    }

    private void onVote(CommandSender commandSender) throws SQLException, IOException, VoteException, at.warix.exceptions.HTTPException {
        if (!(commandSender instanceof Player)) {
            throw new IllegalArgumentException("This command can be only executed by a player!");
        }

        Player player = (Player) commandSender;
        sendMessageToSender(commandSender, "Your vote is being verified...");
        if (NameMcAccessController.getInstance().verifyVote(player.getUniqueId())) {
            doRegisterVote(player);
            sendMessageToSender(commandSender, "Your vote has been verified! You will earn " + reward + "...");
        } else {
            sendMessageToSender(commandSender, String.format("Go to %s and vote for the server!", NameMcAccessController.getInstance().getServerToVoteFor()));
            sendMessageToSender(commandSender, "After that, type in /vote again!");
        }
    }

    private void checkUserVote(CommandSender commandSender, String username) throws SQLException, PermissionException {
        if (!commandSender.hasPermission("namemcvote.admin")) {
            throw new PermissionException("You don't have the permission to execute this command!");
        }

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("No username has been provided");
        }

        sendMessageToSender(commandSender, "The vote of " + username + " is being checked...");

        Vote vote = voteRepository.getVoteByPlayerName(username);

        if (vote == null) {
            sendMessageToSender(commandSender, username + " has not voted for the server yet (or hasn't got the reward for doing so)");
        } else {
            sendMessageToSender(commandSender, username + " has voted for the server and received the reward on " + vote.getVotedOn());
        }

    }

    private void onList(CommandSender commandSender) throws SQLException, PermissionException {
        if (!commandSender.hasPermission("namemcvote.admin")) {
            throw new PermissionException("You don't have the permission to execute this command!");
        }
        sendMessageToSender(commandSender, "The list of users is being evaluated...");

        List<Vote> votes = voteRepository.getVotes();
        votes.forEach(item -> sendMessageToSender(commandSender, item.getVotedOn().format(DateTimeFormatter.ISO_DATE_TIME) + ": " + item.getPlayerName()));
    }
    //</editor-fold>


    //<editor-fold desc="Data Access">

    /**
     * @param player The player to be registered
     * @throws VoteException If the vote has been already registered.
     * @throws SQLException  If there was any other error registering the vote.
     */
    private void doRegisterVote(Player player) throws SQLException, VoteException {
        Vote v = new Vote(player.getUniqueId(), player.getName());
        voteRepository.addVote(v);
    }

    private void doSetReward(double newReward) {
        plugin.getConfig().set("reward", newReward);
        plugin.saveConfig();
        setReward(newReward);
    }
    //</editor-fold>

    //<editor-fold desc="Messages">
    private void sendMessageToSender(CommandSender sender, String message) {
        sender.sendMessage(prefix + message);
    }
    //</editor-fold>

}

package at.warix.controllers.commands;

import at.warix.data.Database;
import at.warix.data.NameMcAccessController;
import at.warix.data.entities.Vote;
import at.warix.data.repositories.VoteRepository;
import at.warix.exceptions.VoteException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class CommandNameMcVote implements CommandExecutor {

    //<editor-fold desc="Field Variables">
    private final VoteRepository voteRepository;
    private double reward = 10000;
    //</editor-fold>

    //<editor-fold desc="Initialization">
    public CommandNameMcVote() throws SQLException {
        voteRepository = Database.newInstance();
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
                            setReward(rewardMoney);
                            commandSender.sendMessage(ChatColor.RED + "The reward has been set to: " + ChatColor.GREEN + getReward());
                        } else if (mode.isEmpty() || mode.equals("get")) {
                            commandSender.sendMessage(ChatColor.RED + "The reward that is given for a vote: " + ChatColor.GREEN + getReward());
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
            commandSender.sendMessage(ChatColor.RED + "Error: Too few arguments provided!");
        } catch (NumberFormatException ex) {
            commandSender.sendMessage(ChatColor.RED + "Error: Please type in a valid number!");
        } catch (IllegalArgumentException ex) {
            commandSender.sendMessage(ChatColor.RED + "Error: " + ex.getMessage());
        } catch (Exception ex) {
            commandSender.sendMessage(ChatColor.RED + "Severe error! Please contact an administrator!");
            ex.printStackTrace();
        }


        return valid;
    }

    private void onHelp(CommandSender commandSender) {
        List<String> msgs = new ArrayList<>();

        msgs.add("``/namemcvote help`` to get a help page for this plugin");
        msgs.add("``/namemcvote vote`` If the user has not voted for the server, it will send a link, else the user is given a monetary reward, if it hasn't been given (10.000 Bucks). ");
        msgs.add("``/namemcvote check <Name>`` To verify whether someone voted for a server.");
        msgs.add("``/namemcvote list`` to get a list of all users who voted for a server on NameMC [Admin]");
        msgs.add("``/namemcvote reward set <Amount>`` to set the amount to be received. [Admin]");

        commandSender.sendMessage(msgs.toArray(new String[0]));
    }

    private void onVote(CommandSender commandSender) throws SQLException, IOException, VoteException {
        if (!(commandSender instanceof Player)) {
            throw new IllegalArgumentException("This command can be only executed by a player!");
        }
        Player player = (Player) commandSender;
        if (NameMcAccessController.getInstance().verifyVote(player.getUniqueId())) {
            doRegisterVote(player);
            commandSender.sendMessage("your vote has been verified! You will earn " + reward + "...");
        } else {
            commandSender.sendMessage("You have not voted for the server!");
        }
    }

    private void checkUserVote(CommandSender commandSender, String username) throws SQLException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("No username has been provided");
        }

        commandSender.sendMessage("the vote of " + username + " is being checked...");

        Vote vote = voteRepository.getVoteByPlayerName(username);

        if (vote == null) {
            commandSender.sendMessage(username + " has not voted for the server yet (or hasn't got the reward for doing so)");
        } else {
            commandSender.sendMessage(username + " has voted for the server and received the reward on " + vote.getVotedOn());
        }

    }

    private void onList(CommandSender commandSender) throws SQLException {
        commandSender.sendMessage("the list of users is being evaluated...");

        List<Vote> votes = voteRepository.getVotes();
        votes.forEach(item -> commandSender.sendMessage(item.getVotedOn().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)) + ": " + item.getPlayerName()));
        commandSender.sendMessage("the list of users is being evaluated...");
    }
    //</editor-fold>


    //<editor-fold desc="Data Access">

    /**
     * @param player The player to be registered
     * @throws SQLException If there was an error registering the vote. Expected, if the user already redeemed his reward for his vote)
     */
    private void doRegisterVote(Player player) throws SQLException {
        Vote v = new Vote(player.getUniqueId(), player.getName());
        voteRepository.addVote(v);
    }
    //</editor-fold>


}

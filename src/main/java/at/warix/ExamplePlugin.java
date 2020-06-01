package at.warix;

import at.warix.controllers.commands.CommandNameMcVote;
import at.warix.data.Database;
import at.warix.data.DatabaseConnectionDetails;
import at.warix.data.NameMcAccessController;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamplePlugin extends JavaPlugin {

    private Logger logger;

    //<editor-fold desc="Event Hooks">

    @Override
    public void onEnable() {
        try {
            initializePlugin();
            initializeConnections();
            initializeCommands();
            logger.log(Level.FINE, "ExamplePlugin loaded");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "There was an error while establishing a connection to the database!", ex);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Severe error!", ex);
        }

    }

    @Override
    public void onDisable() {
        logger.log(Level.FINE, "ExamplePlugin stopped");
    }

    //</editor-fold>

    //<editor-fold desc="Initialization">
    private void initializePlugin() {
        logger = getLogger();
        initializeConfigurations();
    }

    private void initializeConfigurations() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    private void initializeCommands() throws SQLException {
        Objects.requireNonNull(this.getCommand("namemcvote"), "The command 'namemcvote' was not declared in the plugin.yml. Please hang the dev!").setExecutor(new CommandNameMcVote(this));
        logger.log(Level.FINE, "commands loaded");
    }

    private void initializeConnections() throws SQLException {
        // Creates Database and Tables, if none
        // throws an error, if it was unsuccessful

        String username = (getConfig().getString("mysql.username"));
        String password = (getConfig().getString("mysql.password"));
        String host = (getConfig().getString("mysql.host"));
        String port = (getConfig().getString("mysql.port"));
        String database = (getConfig().getString("mysql.database"));

        DatabaseConnectionDetails.writeConnectionDetails(host, port, database, username, password);

        Database.newInstance();
        NameMcAccessController.getInstance().setServerToVoteFor(getConfig().getString("servername"));
    }

    //</editor-fold>


}

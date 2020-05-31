package at.warix;

import at.warix.controllers.commands.CommandNameMcVote;
import at.warix.data.Database;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamplePlugin extends JavaPlugin {

    Logger logger;

    //<editor-fold desc="Event Hooks">

    @Override
    public void onEnable() {
        try {
            initializePlugin();
            initializeCommands();
            initializeDatabaseConnection();
            logger.log(Level.FINE, "ExamplePlugin loaded");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "There was an error while establishing a connection to the database!", ex);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Severe error!", ex);
        }

    }

    private void initializeDatabaseConnection() throws SQLException {
        // Creates Database and Tables, if none
        // throws an error, if it was unsuccessful
        Database.newInstance();
    }

    private void initializeCommands() throws SQLException {
        Objects.requireNonNull(this.getCommand("namemcvote"), "The command 'namemcvote' were not declared in the plugin.yml. Please beat up the dev!").setExecutor(new CommandNameMcVote());
        logger.log(Level.FINE, "commands loaded");
    }

    @Override
    public void onDisable() {
        logger.log(Level.FINE, "ExamplePlugin stopped");
    }

    //</editor-fold>

    //<editor-fold desc="Initialization">
    private void initializePlugin() {
        logger = getLogger();
    }

    //</editor-fold>


}

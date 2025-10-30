package fun.sunrisemc.template.scheduled_command;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class CommandExecutable {

    private final CommandType type;

    private final String command;

    CommandExecutable(CommandType type, String command) {
        this.type = type;
        this.command = command;
    }

    public void execute() {
        Server server = Bukkit.getServer();
        if (type == CommandType.CONSOLE) {
            server.dispatchCommand(Bukkit.getConsoleSender(), command);
        } 
        else if (type == CommandType.CONSOLE_FOR_EACH_PLAYER) {
            Collection<? extends Player> players = server.getOnlinePlayers();
            for (Player player : players) {
                String parsedCommand = command.replace("{player}", player.getName());
                server.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
            }
        }
        else if (type == CommandType.FOR_EACH_PLAYER) {
            Collection<? extends Player> players = server.getOnlinePlayers();
            for (Player player : players) {
                String parsedCommand = command.replace("{player}", player.getName());
                player.performCommand(parsedCommand);
            }
        }
    }
}
package fun.sunrisemc.command_schedular.scheduled_command;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class CommandExecutable {

    private final @NotNull CommandType type;

    private final @NotNull String command;

    CommandExecutable(@NotNull CommandType type, @NotNull String command) {
        this.type = type;
        this.command = command.trim();
    }

    @NotNull
    public CommandType getType() {
        return type;
    }

    @NotNull
    public String getCommand() {
        return command;
    }

    public void execute(@NotNull Collection<? extends Player> playersWhoMeetConditions) {
        Server server = Bukkit.getServer();
        if (type == CommandType.CONSOLE) {
            server.dispatchCommand(Bukkit.getConsoleSender(), command);
        } 
        else if (type == CommandType.CONSOLE_FOR_EACH_PLAYER) {
            for (Player player : playersWhoMeetConditions) {
                if (player == null) {
                    continue;
                }
                String parsedCommand = command.replace("{player}", player.getName());
                server.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
            }
        }
        else if (type == CommandType.FOR_EACH_PLAYER) {
            for (Player player : playersWhoMeetConditions) {
                if (player == null) {
                    continue;
                }
                String parsedCommand = command.replace("{player}", player.getName());
                player.performCommand(parsedCommand);
            }
        }
        else if (type == CommandType.BROADCAST) {
            String message = ChatColor.translateAlternateColorCodes('&', command);
            server.broadcastMessage(message);
        }
        else if (type == CommandType.MESSAGE) {
            for (Player player : playersWhoMeetConditions) {
                if (player == null) {
                    continue;
                }
                String message = command.replace("{player}", player.getName());
                message = ChatColor.translateAlternateColorCodes('&', message);
                player.sendMessage(message);
            }
        }
    }
}
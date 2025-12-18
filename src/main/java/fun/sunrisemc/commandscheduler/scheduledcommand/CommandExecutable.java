package fun.sunrisemc.commandscheduler.scheduledcommand;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

import org.jetbrains.annotations.NotNull;

public class CommandExecutable {

    private final @NotNull CommandType type;

    private final @NotNull String command;

    protected CommandExecutable(@NotNull CommandType type, @NotNull String command) {
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
        else if (type == CommandType.OP_FOR_EACH_PLAYER) {
            for (Player player : playersWhoMeetConditions) {
                if (player == null) {
                    continue;
                }

                String parsedCommand = command.replace("{player}", player.getName());

                boolean wasOp = player.isOp();
                try {
                    if (!wasOp) {
                        player.setOp(true);
                    }
                    player.performCommand(parsedCommand);
                } 
                finally {
                    if (!wasOp) {
                        player.setOp(false);
                    }
                }
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
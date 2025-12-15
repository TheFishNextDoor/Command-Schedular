package fun.sunrisemc.commandscheduler.utils;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.scheduledcommand.CommandType;
import fun.sunrisemc.commandscheduler.scheduledcommand.ExecuteOn;

public class Names {

    @NotNull
    public static ArrayList<String> getCommandTypeNames() {
        ArrayList<String> names = new ArrayList<>();
        for (CommandType commandType : CommandType.values()) {
            String formattedName = StringUtils.formatName(commandType.name());
            names.add(formattedName);
        }
        return names;
    }

    @NotNull
    public static ArrayList<String> getExecuteOnNames() {
        ArrayList<String> names = new ArrayList<>();
        for (ExecuteOn executeOn : ExecuteOn.values()) {
            String formattedName = StringUtils.formatName(executeOn.name());
            names.add(formattedName);
        }
        return names;
    }

    @NotNull
    public static ArrayList<String> getEnvironmentNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Environment environment : Environment.values()) {
            String formattedName = StringUtils.formatName(environment.name());
            names.add(formattedName);
        }
        return names;
    }

    @NotNull
    public static ArrayList<String> getBiomeNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            String formattedName = StringUtils.formatName(biome.name());
            names.add(formattedName);
        }
        return names;
    }

    @NotNull
    public static ArrayList<String> getGameModeNames() {
        ArrayList<String> names = new ArrayList<>();
        for (GameMode gameMode : GameMode.values()) {
            String formattedName = StringUtils.formatName(gameMode.name());
            names.add(formattedName);
        }
        return names;
    }
}
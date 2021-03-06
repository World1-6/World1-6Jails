package com.andrew121410.mc.world16jails.commands;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16jails.objects.JailObject;
import com.andrew121410.mc.world16utils.utils.TabUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JailTab implements TabCompleter {

    private Map<String, JailObject> jailsMap;

    private World16Jails plugin;

    public JailTab(World16Jails plugin) {
        this.plugin = plugin;
        this.jailsMap = this.plugin.getSetListMap().getJailsMap();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alies, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("world16.jail")) {
            return null;
        }
        List<String> listOfJails = new ArrayList<>(this.jailsMap.keySet());
        List<String> listOfPlayer = this.plugin.getServer().getOnlinePlayers().stream().map(Player::getDisplayName).collect(Collectors.toList());

        if (args.length == 1) {
            return TabUtils.getContainsString(args[0], Arrays.asList("create", "cell", "delete", "incarcerate", "tp", "release", "version"));
        } else if (args[0].equalsIgnoreCase("cell")) {
            if (args.length == 2) {
                return TabUtils.getContainsString(args[1], Arrays.asList("create", "delete", "setdoor", "setspawn"));
            } else if (args.length == 3) {
                return TabUtils.getContainsString(args[2], listOfJails);
            } else if (args.length == 4 && !args[1].equalsIgnoreCase("create")) {
                return this.jailsMap.containsKey(args[2]) ? this.jailsMap.get(args[2]).getJailCells().keySet().stream().map(String::valueOf).collect(Collectors.toList()) : null;
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length == 2) {
                return TabUtils.getContainsString(args[1], listOfJails);
            }
            return null;
        } else if (args[0].equalsIgnoreCase("incarcerate")) {
            if (args.length == 2) {
                return TabUtils.getContainsString(args[1], listOfPlayer);
            } else if (args.length == 3) {
                return TabUtils.getContainsString(args[2], listOfJails);
            }
            return null;
        } else if (args[0].equalsIgnoreCase("tp")) {
            if (args.length == 2) {
                return TabUtils.getContainsString(args[1], listOfJails);
            }
            return null;
        } else if (args[0].equalsIgnoreCase("release")) {
            if (args.length == 2) {
                return TabUtils.getContainsString(args[1], listOfPlayer);
            }
            return null;
        }
        return null;
    }
}
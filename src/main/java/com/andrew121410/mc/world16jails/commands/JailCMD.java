package com.andrew121410.mc.world16jails.commands;

import com.andrew121410.mc.world16jails.Jail;
import com.andrew121410.mc.world16jails.JailCell;
import com.andrew121410.mc.world16jails.JailedPlayer;
import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16jails.managers.JailManager;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.config.UnlinkedWorldLocation;
import com.andrew121410.mc.world16utils.player.PlayerUtils;
import com.andrew121410.mc.world16utils.utils.TabUtils;
import com.andrew121410.mc.world16utils.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class JailCMD implements CommandExecutor, TabCompleter {

    private final Map<String, Jail> jailsMap;
    private final Map<UUID, JailedPlayer> jailPlayerMap;

    private final World16Jails plugin;
    private final JailManager jailManager;

    public JailCMD(World16Jails plugin) {
        this.plugin = plugin;
        this.jailManager = this.plugin.getJailManager();

        this.jailsMap = this.plugin.getJailsMap();
        this.jailPlayerMap = this.plugin.getJailedPlayerMap();

        this.plugin.getCommand("jail").setExecutor(this);
        this.plugin.getCommand("jail").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only Players Can Use This Command.");
            return true;
        }

        if (!player.hasPermission("world16.jail")) {
            player.sendMessage(Translate.miniMessage("<darK_red>You don't have permission to use this command."));
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            if (!player.hasPermission("world16.jail.create")) {
                player.sendMessage(Translate.color("&4You don't have permission -> world16.create"));
                return true;
            }
            String jailName = args[1];

            if (this.jailsMap.containsKey(jailName)) {
                player.sendMessage("&6There's a jail already with that name.");
                return true;
            }

            Jail jail = new Jail(jailName, player.getLocation().clone());
            this.jailsMap.put(jailName, jail);
            player.sendMessage(Translate.color("&aJail: " + jailName + " has been created!"));
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("cell")) {
            if (!player.hasPermission("world16.jail.cell")) {
                player.sendMessage(Translate.color("&4You don't have permission -> world16.jail.cell"));
                return true;
            }
            if (args.length == 1) {
                player.sendMessage(Translate.color("&6/jail cell create <Jail> <Number>"));
                player.sendMessage(Translate.color("&6/jail cell delete <Jail> <Number>"));
                player.sendMessage(Translate.color("&6/jail cell setdoor <Jail> <Number> &r-Make sure you look at the block under the door."));
                player.sendMessage(Translate.color("&6/jail cell setspawn <Jail> <Number>"));
                return true;
            } else if (args.length == 4) { //
                Jail jail = this.jailsMap.get(args[2]);
                Integer integer = Utils.asIntegerOrElse(args[3], Integer.MIN_VALUE);

                // Check if jail exists
                if (jail == null) {
                    player.sendMessage(Translate.color("&cJail doesn't exist"));
                    return true;
                }

                // Check if number is valid
                if (integer == Integer.MIN_VALUE) {
                    player.sendMessage(Translate.miniMessage("<dark_red>Invalid number."));
                    return true;
                }

                // Only check if jail cell exists if we are not creating it.
                JailCell jailCell = jail.getJailCells().get(integer);
                if (jailCell == null && !args[1].equalsIgnoreCase("create")) {
                    player.sendMessage(Translate.color("&cJail cell doesn't exist."));
                    return true;
                }

                switch (args[1].toLowerCase()) {
                    case "create" -> {
                        JailCell newJailCell = new JailCell(integer, new UnlinkedWorldLocation(player.getLocation().clone()), null, null);
                        jail.getJailCells().putIfAbsent(integer, newJailCell);
                        player.sendMessage(Translate.color("&aJail cell has been created."));
                    }
                    case "delete" -> {
                        jail.getJailCells().remove(integer);
                        player.sendMessage(Translate.color("&4Jail cell has been deleted"));
                    }
                    case "setdoor" -> {
                        jailCell.setDoorLocation(PlayerUtils.getBlockPlayerIsLookingAt(player).getLocation());
                        player.sendMessage(Translate.color("&aDoor has been set for the cell."));
                    }
                    case "setspawn" -> {
                        jailCell.setSpawnLocation(player.getLocation());
                        player.sendMessage(Translate.color("&aThe setspawn for that cell has been changed!"));
                    }
                }
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            if (!player.hasPermission("world16.jail.delete")) {
                player.sendMessage(Translate.color("&4You don't have permission -> world16.jail.incarcerate"));
                return true;
            }
            Jail jail = this.jailsMap.get(args[1]);
            if (jail == null) {
                player.sendMessage("&cCould not find jail.");
                return true;
            }
            this.jailManager.deleteJail(args[1]);
            this.jailsMap.remove(args[1]);
            player.sendMessage(Translate.color("&4Jail has been deleted."));
            return true;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("incarcerate")) {
            if (!player.hasPermission("world16.jail.incarcerate")) {
                player.sendMessage(Translate.color("&4You don't have permission -> world16.jail.incarcerate"));
                return true;
            }
            String playerName = args[1];
            String jailName = args[2];
            Integer seconds = Utils.asIntegerOrElse(args[3], Integer.MIN_VALUE);

            Player player1 = this.plugin.getServer().getPlayer(playerName);
            if (player1 == null) {
                player.sendMessage(Translate.color("&cCould not find player."));
                return true;
            }
            Jail jail = this.jailsMap.get(jailName);
            if (jail == null) {
                player.sendMessage(Translate.color("&cJail not found."));
                return true;
            }
            if (seconds == Integer.MIN_VALUE) {
                player.sendMessage(Translate.color("&cNot a int."));
                return true;
            }
            this.jailManager.jailPlayer(player, player1, jailName, null, seconds);
            player.sendMessage(Translate.color("&aPlayer has been jailed."));
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
            String jailName = args[1];
            Jail jail = this.jailsMap.get(jailName);

            if (jail == null) {
                player.sendMessage(Translate.color("&cNot a jail."));
                return true;
            }

            Location jailLocation = jail.getJailLocation();
            if (!jailLocation.isWorldLoaded()) {
                player.sendMessage(Translate.colorc("&cWorld is not loaded."));
                return true;
            }

            player.teleport(jailLocation);
            player.sendMessage(Translate.color("&6Teleporting..."));
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("release")) {
            if (!player.hasPermission("world16.jail.release")) {
                player.sendMessage(Translate.color("&4You don't have permission -> world16.jail.incarcerate"));
                return true;
            }
            String playerName = args[1];
            Player player1 = this.plugin.getServer().getPlayer(playerName);

            if (player1 == null) {
                player.sendMessage(Translate.color("&cCould not find the player?"));
                return true;
            }

            if (!this.jailPlayerMap.containsKey(player1.getUniqueId())) {
                player.sendMessage(Translate.color("&cThat player isn't in jail."));
                return true;
            }

            this.jailManager.releasePlayer(this.jailPlayerMap.get(player1.getUniqueId()));
            player.sendMessage(Translate.color("&aThe player " + player1.getDisplayName() + " has been released from jail"));
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
            player.sendMessage(Translate.color("&6Jail version: " + World16Jails.VERSION + " made by Andrew121410."));
            return true;
        } else {
            player.sendMessage(Translate.color("&6/jail create <Name>"));
            player.sendMessage(Translate.color("&6/jail cell &r- Show's help for cell stuff."));
            player.sendMessage(Translate.color("&6/jail delete <Name>"));
            player.sendMessage(Translate.color("&6/jail incarcerate <Player> <Jail> <Seconds>"));
            player.sendMessage(Translate.color("&6/jail tp <JailName>"));
            player.sendMessage(Translate.color("&6/jail release <User>"));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alies, String[] args) {
        if (!(sender instanceof Player player)) return null;
        if (!player.hasPermission("world16.jail")) return null;

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

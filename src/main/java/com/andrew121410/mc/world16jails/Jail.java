package com.andrew121410.mc.world16jails;

import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.config.UnlinkedWorldLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SerializableAs("Jail")
public class Jail implements ConfigurationSerializable {

    private final String name;
    private final UnlinkedWorldLocation jailLocation;
    private final Map<Integer, JailCell> jailCells;

    public Jail(String name, UnlinkedWorldLocation jailLocation, Map<Integer, JailCell> jailCellObjects) {
        this.name = name;
        this.jailLocation = jailLocation;
        this.jailCells = jailCellObjects;
    }

    public Jail(String name, Location jailLocation, Map<Integer, JailCell> jailCellObjects) {
        this(name, new UnlinkedWorldLocation(jailLocation), jailCellObjects);
    }

    public Jail(String name, Location jailLocation) {
        this(name, jailLocation, new HashMap<>());
    }

    public void jailPlayer(CommandSender sender, Player target, Integer number, int seconds) {
        JailCell jailCell = null;
        if (number == null) { // Have to find an empty jail cell.
            Optional<JailCell> optionalJailCellObject = this.jailCells.values().stream().filter(cellObject -> cellObject.getJailedPlayer() == null).findAny();
            if (optionalJailCellObject.isPresent()) {
                jailCell = optionalJailCellObject.get();
            }
        } else { // Use the number that was given.
            jailCell = this.jailCells.get(number);
        }

        // Check if jail cell is null.
        if (jailCell == null) {
            sender.sendMessage(Translate.miniMessage("<red>Could not find jail cell."));
            return;
        }

        // Check if the world is loaded.
        Location cellLocation = jailCell.getSpawnLocation().toLocation();
        if (!cellLocation.isWorldLoaded()) {
            sender.sendMessage(Translate.miniMessage("<red>World is not loaded."));
            return;
        }

        // Check if the door is still there.
        if (jailCell.getDoorLocation() != null && jailCell.getDoorLocation().toLocation().isWorldLoaded()) {
            if (!IfDoorThenDoIfNotThenFalse(jailCell.getDoorLocation().toLocation().getBlock(), false)) {
                jailCell.setDoorLocation(null);
            }
        }

        // Create JailedPlayer
        JailedPlayer jailedPlayer = new JailedPlayer(target.getUniqueId(), seconds, this.name, jailCell.getNumber());
        jailCell.setJailedPlayer(jailedPlayer);

        // Teleport player to cell.
        target.teleport(jailCell.getSpawnLocation().toLocation());
        target.sendMessage(Translate.miniMessage("<dark_red>You have been jailed for " + seconds + " seconds."));

        jailedPlayer.getCountdownTimer().scheduleTimer();
        World16Jails.getPlugin().getJailedPlayerMap().put(target.getUniqueId(), jailedPlayer);
    }

    public void releasePlayer(JailedPlayer jailedPlayer) {
        JailCell jailCell = this.jailCells.get(jailedPlayer.getCellNumber());
        if (jailCell == null) {
            throw new NullPointerException("Class: " + this.getClass() + " Function: releasePlayer -> jailCellObject == null");
        }

        jailCell.setJailedPlayer(null);
        Player player = jailedPlayer.getPlayer();
        jailedPlayer.getCountdownTimer().cancelTimer();
        World16Jails.getPlugin().getJailManager().deletePlayer(jailedPlayer.getUuid());
        World16Jails.getPlugin().getJailedPlayerMap().remove(jailedPlayer.getUuid());

        UnlinkedWorldLocation unlinkedDoorLocation = jailCell.getDoorLocation();
        if (unlinkedDoorLocation != null) {
            Location doorLocation = unlinkedDoorLocation.toLocation();
            if (doorLocation != null && doorLocation.isWorldLoaded()) {
                Block jailDoorBlock = doorLocation.getBlock();
                if (IfDoorThenDoIfNotThenFalse(jailDoorBlock, true)) {
                    player.sendMessage(Translate.color("&6You have been released!"));
                } else {
                    jailedPlayer.getPlayer().sendMessage(Translate.color("&cYou have been released from jail."));
                    player.teleport(player.getWorld().getSpawnLocation());
                }
            }
        }

        Bukkit.getServer().broadcastMessage(Translate.color("&9[Jail]&r&6 " + player.getDisplayName() + " has been released from jail."));
    }

    public static Door isDoor(Location location) {
        Door door = null;
        if (location.getBlock().getType() == Material.IRON_DOOR) {
            door = (Door) location.getBlock().getBlockData();
        }
        return door;
    }

    public boolean IfDoorThenDoIfNotThenFalse(Block block, boolean b) {
        Door door = isDoor(block.getLocation());
        if (door == null) return false;
        door.setOpen(b);
        block.setBlockData(door);
        return true;
    }

    public String getName() {
        return name;
    }

    public UnlinkedWorldLocation getJailLocation() {
        return jailLocation;
    }

    public Map<Integer, JailCell> getJailCells() {
        return jailCells;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("Name", this.name);
        map.put("JailLocation", this.jailLocation);
        map.put("JailCells", this.jailCells);
        return map;
    }

    public static Jail deserialize(Map<String, Object> map) {
        UnlinkedWorldLocation unlinkedWorldLocation = (UnlinkedWorldLocation) map.get("JailLocation");
        return new Jail((String) map.get("Name"), unlinkedWorldLocation, (Map<Integer, JailCell>) map.get("JailCells"));
    }
}

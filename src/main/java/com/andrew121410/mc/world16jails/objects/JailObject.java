package com.andrew121410.mc.world16jails.objects;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SerializableAs("JailObject")
public class JailObject implements ConfigurationSerializable {

    private String name;
    private Location jailLocation;
    private Map<Integer, JailCellObject> jailCells;

    public JailObject(String name, Location jailLocation, Map<Integer, JailCellObject> jailCellObjects) {
        this.name = name;
        this.jailLocation = jailLocation;
        this.jailCells = jailCellObjects;
    }

    public JailObject(String name, Location jailLocation) {
        this(name, jailLocation, new HashMap<>());
    }

    public void jailPlayer(Player player, Integer number, int seconds) {
        JailCellObject jailCellObject;
        if (number == null) {
            //Have to find an empty jail cell.
            Optional<JailCellObject> optionalJailCellObject = this.jailCells.values().stream().filter(cellObject -> cellObject.getJailPlayerObject() == null).findAny();
            if (!optionalJailCellObject.isPresent()) {
                Bukkit.getServer().broadcastMessage("Could not find empty jail cell.");
                return;
            }
            jailCellObject = optionalJailCellObject.get();
        } else jailCellObject = this.jailCells.get(number);
        if (jailCellObject == null) return;

        //Check if there is a door if some how the door was removed then set door location back to null.
        if (jailCellObject.getDoorLocation() != null) {
            if (!IfDoorThenDoIfNotThenFalse(jailCellObject.getDoorLocation().getBlock(), false)) {
                jailCellObject.setDoorLocation(null);
            }
        }

        //Create JailPlayerObject
        JailPlayerObject jailPlayerObject = new JailPlayerObject(player.getUniqueId(), seconds, this.name, jailCellObject.getNumber());
        jailCellObject.setJailPlayerObject(jailPlayerObject);
        //Teleport player to cell.
        player.teleport(jailCellObject.getSpawnLocation());
        player.sendMessage(Translate.color("&c&lYou have been jailed for " + seconds + " seconds."));

        jailPlayerObject.getCountdownTimer().scheduleTimer();
        World16Jails.getPlugin().getSetListMap().getJailPlayersMap().put(player.getUniqueId(), jailPlayerObject);
    }

    public void releasePlayer(JailPlayerObject jailPlayerObject) {
        JailCellObject jailCellObject = this.jailCells.get(jailPlayerObject.getCellNumber());
        if (jailCellObject == null) {
            throw new NullPointerException("Class: " + this.getClass() + " Function: releasePlayer -> jailCellObject == null");
        }
        Location doorLocation = jailCellObject.getDoorLocation();
        jailCellObject.setJailPlayerObject(null);
        Player player = jailPlayerObject.getPlayer();
        World16Jails.getPlugin().getJailManager().deletePlayer(jailPlayerObject.getUuid());
        World16Jails.getPlugin().getSetListMap().getJailPlayersMap().remove(jailPlayerObject.getUuid());
        if (doorLocation != null) {
            Block jailDoorBlock = doorLocation.getBlock();
            if (IfDoorThenDoIfNotThenFalse(jailDoorBlock, true)) {
                player.sendMessage(Translate.color("&6You have been released!"));
            } else {
                jailPlayerObject.getPlayer().sendMessage(Translate.color("&cYou have been released from jail."));
                player.teleport(player.getWorld().getSpawnLocation());
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

    public Location getJailLocation() {
        return jailLocation;
    }

    public Map<Integer, JailCellObject> getJailCells() {
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

    public static JailObject deserialize(Map<String, Object> map) {
        return new JailObject((String) map.get("Name"), (Location) map.get("JailLocation"), (Map<Integer, JailCellObject>) map.get("JailCells"));
    }
}

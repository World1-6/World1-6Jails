package com.andrew121410.mc.world16jails.objects;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
    private Map<Integer, JailCellObject> jailCells;

    public JailObject(String name, Map<Integer, JailCellObject> jailCellObjects) {
        this.name = name;
        this.jailCells = jailCellObjects;
    }

    public JailObject(String name) {
        this(name, new HashMap<>());
    }

    public String getName() {
        return name;
    }

    public Map<Integer, JailCellObject> getJailCells() {
        return jailCells;
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
        JailPlayerObject jailPlayerObject = new JailPlayerObject(player.getUniqueId(), seconds, this.name, jailCellObject.getNumber());
        jailCellObject.setJailPlayerObject(jailPlayerObject);
        player.teleport(jailCellObject.getSpawnLocation());
        player.sendMessage(Translate.color("&cYou have been placed in jail for: " + seconds));
        jailPlayerObject.getCountdownTimer().scheduleTimer();
        World16Jails.getPlugin().getSetListMap().getJailPlayersMap().put(player.getUniqueId(), jailPlayerObject);
    }

    public void releasePlayer(JailPlayerObject jailPlayerObject) {
        JailCellObject jailCellObject = this.jailCells.get(jailPlayerObject.getCellNumber());
        if (jailCellObject == null) {
            throw new NullPointerException("Class: " + this.getClass() + " Function: releasePlayer -> jailCellObject == null");
        }
        Location doorLocation = jailCellObject.getDoorLocation();
        if (doorLocation != null) {
            Block jailDoorBlock = doorLocation.getBlock().getRelative(BlockFace.UP);
            if (IfDoorThenDoIfNotThenFalse(jailDoorBlock, true)) {
                jailPlayerObject.getPlayer().sendMessage(Translate.color("&6You have been released!"));
            } else {
                jailPlayerObject.getPlayer().sendMessage(Translate.color("&cYou have been released from jail."));
                jailPlayerObject.getPlayer().teleport(jailPlayerObject.getPlayer().getWorld().getSpawnLocation());
            }
        }
        jailCellObject.setJailPlayerObject(null);
        World16Jails.getPlugin().getJailManager().deletePlayer(jailPlayerObject.getUuid());
        World16Jails.getPlugin().getSetListMap().getJailPlayersMap().remove(jailPlayerObject.getUuid());
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

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("Name", this.name);
        map.put("JailCells", this.jailCells);
        return map;
    }

    public static JailObject deserialize(Map<String, Object> map) {
        return new JailObject((String) map.get("Name"), (Map<Integer, JailCellObject>) map.get("JailCells"));
    }
}

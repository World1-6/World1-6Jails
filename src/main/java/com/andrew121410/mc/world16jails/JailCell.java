package com.andrew121410.mc.world16jails;

import com.andrew121410.mc.world16utils.config.UnlinkedWorldLocation;
import org.bukkit.Location;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("JailCell")
public class JailCell implements ConfigurationSerializable {

    private final int number;
    private UnlinkedWorldLocation spawnLocation;
    private UnlinkedWorldLocation doorLocation;
    private JailedPlayer jailedPlayer;

    public JailCell(int number, UnlinkedWorldLocation spawnLocation, UnlinkedWorldLocation doorLocation, JailedPlayer jailedPlayer) {
        this.number = number;
        this.spawnLocation = spawnLocation;
        this.doorLocation = doorLocation;
        this.jailedPlayer = jailedPlayer;
    }

    public int getNumber() {
        return number;
    }

    public JailedPlayer getJailedPlayer() {
        return jailedPlayer;
    }

    public void setJailedPlayer(JailedPlayer jailedPlayer) {
        this.jailedPlayer = jailedPlayer;
    }

    public UnlinkedWorldLocation getDoorLocation() {
        return doorLocation;
    }

    public void setDoorLocation(Location doorLocation) {
        if (doorLocation == null) {
            this.doorLocation = null;
            return;
        }
        Location newDoorLocation;
        Door door = Jail.isDoor(doorLocation);
        if (door != null) {
            if (door.getHalf().toString().equals("TOP")) {
                newDoorLocation = doorLocation.subtract(0, 1, 0);
            } else newDoorLocation = doorLocation;
        } else newDoorLocation = doorLocation.add(0, 1, 0);
        if (Jail.isDoor(newDoorLocation) == null) this.doorLocation = null;
        door = (Door) newDoorLocation.getBlock().getBlockData();
        door.setOpen(true);
        doorLocation.getBlock().setBlockData(door);
        this.doorLocation = new UnlinkedWorldLocation(newDoorLocation);
    }

    public UnlinkedWorldLocation getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = new UnlinkedWorldLocation(spawnLocation);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("Number", this.number);
        map.put("SpawnLocation", this.spawnLocation);
        map.put("DoorLocation", this.doorLocation);
        map.put("JailPlayer", this.jailedPlayer);
        return map;
    }

    public static JailCell deserialize(Map<String, Object> map) {
        UnlinkedWorldLocation unlinkedSpawnLocation = (UnlinkedWorldLocation) map.get("SpawnLocation");
        UnlinkedWorldLocation unlinkedDoorLocation = (UnlinkedWorldLocation) map.get("DoorLocation");
        return new JailCell((int) map.get("Number"), unlinkedSpawnLocation, unlinkedDoorLocation, (JailedPlayer) map.get("JailPlayer"));
    }
}

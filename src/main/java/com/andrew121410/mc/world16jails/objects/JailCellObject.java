package com.andrew121410.mc.world16jails.objects;

import org.bukkit.Location;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("JailCellObject")
public class JailCellObject implements ConfigurationSerializable {

    private int number;
    private Location spawnLocation;
    private Location doorLocation;
    private JailPlayerObject jailPlayerObject;

    public JailCellObject(int number, Location spawnLocation, Location doorLocation, JailPlayerObject jailPlayerObject) {
        this.number = number;
        this.spawnLocation = spawnLocation;
        this.doorLocation = doorLocation;
        this.jailPlayerObject = jailPlayerObject;
    }

    public int getNumber() {
        return number;
    }

    public JailPlayerObject getJailPlayerObject() {
        return jailPlayerObject;
    }

    public void setJailPlayerObject(JailPlayerObject jailPlayerObject) {
        this.jailPlayerObject = jailPlayerObject;
    }

    public Location getDoorLocation() {
        return doorLocation;
    }

    public void setDoorLocation(Location doorLocation) {
        if (doorLocation == null) {
            this.doorLocation = null;
            return;
        }
        Location newDoorLocation;
        Door door = JailObject.isDoor(doorLocation);
        if (door != null) {
            if (door.getHalf().toString().equals("TOP")) {
                newDoorLocation = doorLocation.subtract(0, 1, 0);
            } else newDoorLocation = doorLocation;
        } else newDoorLocation = doorLocation.add(0, 1, 0);
        if (JailObject.isDoor(newDoorLocation) == null) this.doorLocation = null;
        door = (Door) newDoorLocation.getBlock().getBlockData();
        door.setOpen(true);
        doorLocation.getBlock().setBlockData(door);
        this.doorLocation = newDoorLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("Number", this.number);
        map.put("SpawnLocation", this.spawnLocation);
        map.put("DoorLocation", this.doorLocation);
        map.put("JailPlayer", this.jailPlayerObject);
        return map;
    }

    public static JailCellObject deserialize(Map<String, Object> map) {
        return new JailCellObject((int) map.get("Number"), (Location) map.get("SpawnLocation"), (Location) map.get("DoorLocation"), (JailPlayerObject) map.get("JailPlayer"));
    }
}

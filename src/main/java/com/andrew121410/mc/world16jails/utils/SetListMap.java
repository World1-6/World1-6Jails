package com.andrew121410.mc.world16jails.utils;

import com.andrew121410.mc.world16jails.objects.JailObject;
import com.andrew121410.mc.world16jails.objects.JailPlayerObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetListMap {

    //Clear when the server shuts down.
    private final Map<String, JailObject> jailsMap;

    //Remove player when player leaves.
    private final Map<UUID, JailPlayerObject> jailPlayersMap;

    public SetListMap() {
        this.jailsMap = new HashMap<>();
        this.jailPlayersMap = new HashMap<>();
    }

    public Map<String, JailObject> getJailsMap() {
        return jailsMap;
    }

    public Map<UUID, JailPlayerObject> getJailPlayersMap() {
        return jailPlayersMap;
    }
}

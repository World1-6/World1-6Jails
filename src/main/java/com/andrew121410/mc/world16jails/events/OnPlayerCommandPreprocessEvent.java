package com.andrew121410.mc.world16jails.events;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16jails.objects.JailPlayerObject;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;
import java.util.UUID;

public class OnPlayerCommandPreprocessEvent implements Listener {

    private Map<UUID, JailPlayerObject> jailPlayerObjectMap;

    private World16Jails plugin;

    public OnPlayerCommandPreprocessEvent(World16Jails plugin) {
        this.plugin = plugin;
        this.jailPlayerObjectMap = this.plugin.getSetListMap().getJailPlayersMap();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onPreCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if (jailPlayerObjectMap.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(Translate.color("&4You are unable to use commands while in jail."));
            event.setCancelled(true);
        }
    }
}

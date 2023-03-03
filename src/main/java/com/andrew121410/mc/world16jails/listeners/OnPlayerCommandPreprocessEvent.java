package com.andrew121410.mc.world16jails.listeners;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16jails.JailedPlayer;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;
import java.util.UUID;

public class OnPlayerCommandPreprocessEvent implements Listener {

    private final Map<UUID, JailedPlayer> jailedPlayerMap;

    private final World16Jails plugin;

    public OnPlayerCommandPreprocessEvent(World16Jails plugin) {
        this.plugin = plugin;
        this.jailedPlayerMap = this.plugin.getJailedPlayerMap();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onPreCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if (this.plugin.isPlayerJailed(event.getPlayer().getUniqueId())) {
            if (event.getPlayer().hasPermission("world16.jail.release") && event.getMessage().equalsIgnoreCase("/jail release")) {
                this.plugin.getJailManager().releasePlayer(jailedPlayerMap.get(event.getPlayer().getUniqueId()));
                event.setCancelled(true);
                return;
            }
            event.getPlayer().sendMessage(Translate.colorc("&4You are unable to use commands while in jail."));
            event.setCancelled(true);
        }
    }
}

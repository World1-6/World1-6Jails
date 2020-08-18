package com.andrew121410.mc.world16jails.events;

import com.andrew121410.mc.world16jails.World16Jails;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitEvent implements Listener {

    private World16Jails plugin;

    public OnPlayerQuitEvent(World16Jails plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getJailManager().unloadPlayer(event.getPlayer().getUniqueId());
    }
}

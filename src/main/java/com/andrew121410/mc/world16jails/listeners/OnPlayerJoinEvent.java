package com.andrew121410.mc.world16jails.listeners;

import com.andrew121410.mc.world16jails.World16Jails;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoinEvent implements Listener {

    private final World16Jails plugin;

    public OnPlayerJoinEvent(World16Jails plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.plugin.getJailManager().loadPlayer(event.getPlayer());
    }
}

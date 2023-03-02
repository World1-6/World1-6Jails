package com.andrew121410.mc.world16jails.listeners;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OnPlayerTeleportEvent implements Listener {

    private final World16Jails plugin;

    public OnPlayerTeleportEvent(World16Jails plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (this.plugin.isPlayerJailed(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(Translate.color("&4You are unable to teleport while in jail."));
            event.setCancelled(true);
        }
    }
}
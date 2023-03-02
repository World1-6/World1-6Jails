package com.andrew121410.mc.world16jails.listeners;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16jails.objects.JailPlayerObject;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;

public class OnPlayerInteractEvent implements Listener {

    private final Map<UUID, JailPlayerObject> jailPlayerObjectMap;

    private final World16Jails plugin;

    public OnPlayerInteractEvent(World16Jails plugin) {
        this.plugin = plugin;
        this.jailPlayerObjectMap = this.plugin.getSetListMap().getJailPlayersMap();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (this.plugin.isPlayerJailed(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(Translate.color("&4You are unable to interact with the world while in jail."));
            event.setCancelled(true);
        }
    }
}

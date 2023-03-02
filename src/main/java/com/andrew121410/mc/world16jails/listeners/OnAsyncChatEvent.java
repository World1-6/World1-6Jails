package com.andrew121410.mc.world16jails.listeners;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16utils.chat.Translate;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnAsyncChatEvent implements Listener {

    private final World16Jails plugin;

    public OnAsyncChatEvent(World16Jails plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        if (this.plugin.isPlayerJailed(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(Translate.colorc("&4You are unable to talk while in jail."));
            event.setCancelled(true);
        }
    }
}
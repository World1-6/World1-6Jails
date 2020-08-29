package com.andrew121410.mc.world16jails.utils;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16utils.World16Utils;
import org.bukkit.plugin.Plugin;

public class OtherPlugins {

    private World16Jails plugin;

    //Plugins
    private World16Utils world16Utils;

    public OtherPlugins(World16Jails plugin) {
        this.plugin = plugin;
        setupWorld16Utils();
    }

    private void setupWorld16Utils() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("World1-6Utils");

        if (plugin instanceof World16Utils) {
            this.world16Utils = (World16Utils) plugin;
        }
    }

    public World16Utils getWorld16Utils() {
        return world16Utils;
    }
}

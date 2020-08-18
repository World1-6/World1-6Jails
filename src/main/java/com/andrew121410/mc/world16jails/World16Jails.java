package com.andrew121410.mc.world16jails;

import com.andrew121410.mc.world16jails.managers.JailManager;
import com.andrew121410.mc.world16jails.utils.SetListMap;
import org.bukkit.plugin.java.JavaPlugin;

public class World16Jails extends JavaPlugin {

    private static World16Jails plugin;
    private SetListMap setListMap;

    private JailManager jailManager;

    @Override
    public void onEnable() {
        plugin = this;
        this.setListMap = new SetListMap();

        this.jailManager = new JailManager(this);
    }

    @Override
    public void onDisable() {
    }

    private void regEvents() {

    }

    private void regCommands() {

    }

    public SetListMap getSetListMap() {
        return setListMap;
    }

    public JailManager getJailManager() {
        return jailManager;
    }

    public static World16Jails getPlugin() {
        return plugin;
    }
}

package com.andrew121410.mc.world16jails;

import com.andrew121410.mc.world16jails.commands.JailCMD;
import com.andrew121410.mc.world16jails.events.*;
import com.andrew121410.mc.world16jails.managers.JailManager;
import com.andrew121410.mc.world16jails.objects.JailCellObject;
import com.andrew121410.mc.world16jails.objects.JailObject;
import com.andrew121410.mc.world16jails.objects.JailPlayerObject;
import com.andrew121410.mc.world16jails.utils.SetListMap;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class World16Jails extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(JailPlayerObject.class, "JailPlayerObject");
        ConfigurationSerialization.registerClass(JailCellObject.class, "JailCellObject");
        ConfigurationSerialization.registerClass(JailObject.class, "JailObject");
    }

    public static final String VERSION = "1.0";

    private static World16Jails plugin;
    private SetListMap setListMap;

    private JailManager jailManager;

    @Override
    public void onEnable() {
        plugin = this;
        this.setListMap = new SetListMap();
        this.jailManager = new JailManager(this);
        regEvents();
        regCommands();
    }

    @Override
    public void onDisable() {
        this.jailManager.saveAllJails();
        this.jailManager.saveAllJailedPlayers();
    }

    private void regEvents() {
        new OnAsyncPlayerChatEvent(this);
        new OnPlayerCommandPreprocessEvent(this);
        new OnPlayerInteractEvent(this);
        new OnPlayerJoinEvent(this);
        new OnPlayerQuitEvent(this);
        new OnPlayerTeleportEvent(this);
    }

    private void regCommands() {
        new JailCMD(this);
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

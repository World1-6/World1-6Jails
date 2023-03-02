package com.andrew121410.mc.world16jails;

import com.andrew121410.mc.world16jails.commands.JailCMD;
import com.andrew121410.mc.world16jails.listeners.*;
import com.andrew121410.mc.world16jails.managers.JailManager;
import com.andrew121410.mc.world16jails.objects.JailCellObject;
import com.andrew121410.mc.world16jails.objects.JailObject;
import com.andrew121410.mc.world16jails.objects.JailPlayerObject;
import com.andrew121410.mc.world16jails.utils.OtherPlugins;
import com.andrew121410.mc.world16jails.utils.SetListMap;
import com.andrew121410.mc.world16utils.updater.UpdateManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class World16Jails extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(JailPlayerObject.class, "JailPlayerObject");
        ConfigurationSerialization.registerClass(JailCellObject.class, "JailCellObject");
        ConfigurationSerialization.registerClass(JailObject.class, "JailObject");
    }

    public static final String VERSION = "1.3";
    private static World16Jails plugin;

    private SetListMap setListMap;
    private OtherPlugins otherPlugins;

    private JailManager jailManager;

    @Override
    public void onEnable() {
        plugin = this;
        this.setListMap = new SetListMap();
        this.otherPlugins = new OtherPlugins(this);
        this.jailManager = new JailManager(this);
        this.jailManager.loadAllJails();
        registerListeners();
        registerCommands();

        // Register updater
        UpdateManager.registerUpdater(this, new com.andrew121410.mc.world16jails.Updater(this));
    }

    @Override
    public void onDisable() {
        this.jailManager.saveAllJails();
        this.jailManager.saveAllJailedPlayers();
    }

    private void registerListeners() {
        new OnAsyncChatEvent(this);
        new OnPlayerCommandPreprocessEvent(this);
        new OnPlayerInteractEvent(this);
        new OnPlayerJoinEvent(this);
        new OnPlayerQuitEvent(this);
        new OnPlayerTeleportEvent(this);
    }

    private void registerCommands() {
        new JailCMD(this);
    }

    public boolean isPlayerJailed(UUID uuid) {
        return this.setListMap.getJailPlayersMap().containsKey(uuid);
    }

    public SetListMap getSetListMap() {
        return setListMap;
    }

    public JailManager getJailManager() {
        return jailManager;
    }

    public OtherPlugins getOtherPlugins() {
        return otherPlugins;
    }

    public static World16Jails getPlugin() {
        return plugin;
    }
}

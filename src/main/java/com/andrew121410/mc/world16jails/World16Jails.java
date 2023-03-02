package com.andrew121410.mc.world16jails;

import com.andrew121410.mc.world16jails.commands.JailCMD;
import com.andrew121410.mc.world16jails.listeners.*;
import com.andrew121410.mc.world16jails.managers.JailManager;
import com.andrew121410.mc.world16jails.utils.OtherPlugins;
import com.andrew121410.mc.world16utils.updater.UpdateManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class World16Jails extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(JailedPlayer.class, "JailedPlayer");
        ConfigurationSerialization.registerClass(JailCell.class, "JailCell");
        ConfigurationSerialization.registerClass(Jail.class, "Jail");
    }

    public static final String VERSION = "1.3";
    private static World16Jails plugin;

    private Map<String, Jail> jailsMap;
    private Map<UUID, JailedPlayer> jailedPlayerMap;

    private OtherPlugins otherPlugins;

    private JailManager jailManager;

    @Override
    public void onEnable() {
        plugin = this;

        this.jailsMap = new HashMap<>();
        this.jailedPlayerMap = new HashMap<>();

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
        return this.jailedPlayerMap.containsKey(uuid);
    }

    public JailManager getJailManager() {
        return jailManager;
    }

    public OtherPlugins getOtherPlugins() {
        return otherPlugins;
    }

    public Map<String, Jail> getJailsMap() {
        return jailsMap;
    }

    public Map<UUID, JailedPlayer> getJailedPlayerMap() {
        return jailedPlayerMap;
    }

    public static World16Jails getPlugin() {
        return plugin;
    }
}

package com.andrew121410.mc.world16jails.managers;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16jails.objects.JailObject;
import com.andrew121410.mc.world16jails.objects.JailPlayerObject;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.config.CustomYmlManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class JailManager {

    private Map<String, JailObject> jailsMap;
    private Map<UUID, JailPlayerObject> jailPlayerMap;

    private World16Jails plugin;

    private CustomYmlManager jailsYml;

    public JailManager(World16Jails plugin) {
        this.plugin = plugin;

        this.jailsMap = this.plugin.getSetListMap().getJailsMap();
        this.jailPlayerMap = this.plugin.getSetListMap().getJailPlayersMap();

        //jails.yml
        this.jailsYml = new CustomYmlManager(this.plugin, false);
        this.jailsYml.setup("jails.yml");
        this.jailsYml.saveConfig();
        this.jailsYml.reloadConfig();
        //...

        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        if (jailsSection == null) {
            this.jailsYml.getConfig().createSection("Jails");
            this.jailsYml.getConfig().createSection("JailPlayers");
        }
    }

    private JailObject loadJail(String name) {
        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        return (JailObject) jailsSection.get(name);
    }

    private void saveJail(JailObject jailObject) {
        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        jailsSection.set(jailObject.getName(), jailObject);
    }

    public void loadAllJails() {
        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        for (String key : jailsSection.getKeys(false)) {
            this.jailsMap.put(key, loadJail(key));
        }
    }

    public void saveAllJails() {
        this.jailsMap.forEach((name, jail) -> saveJail(jail));
    }

    private JailPlayerObject loadPlayer(UUID uuid) {
        ConfigurationSection jailPlayersSection = this.jailsYml.getConfig().getConfigurationSection("JailPlayers");
        return (JailPlayerObject) jailPlayersSection.get(uuid.toString());
    }

    private void savePlayer(JailPlayerObject jailPlayerObject) {
        ConfigurationSection jailPlayersSection = this.jailsYml.getConfig().getConfigurationSection("JailPlayers");
        jailPlayersSection.set(jailPlayerObject.getUuid().toString(), jailPlayerObject);
    }

    public void loadPlayer(Player player) {
        JailPlayerObject jailPlayerObject = loadPlayer(player.getUniqueId());
        if (jailPlayerObject == null) return; //Player isn't in jail so return.
        this.jailPlayerMap.put(player.getUniqueId(), jailPlayerObject);
    }

    public void saveAllJailedPlayers() {
        this.jailPlayerMap.forEach(((uuid, jailPlayerObject) -> {
            jailPlayerObject.getCountdownTimer().cancelTimer();
            savePlayer(jailPlayerObject);
        }));
    }

    public void unloadPlayer(UUID uuid) {
        JailPlayerObject jailPlayerObject = this.jailPlayerMap.get(uuid);
        if (jailPlayerObject == null) return;
        jailPlayerObject.getCountdownTimer().cancelTimer();
        savePlayer(jailPlayerObject);
        this.jailPlayerMap.remove(uuid);
    }

    public void deletePlayer(UUID uuid) {
        ConfigurationSection jailPlayersSection = this.jailsYml.getConfig().getConfigurationSection("JailPlayers");
        jailPlayersSection.set(uuid.toString(), null);
    }

    public void deleteJail(String name) {
        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        jailsSection.set(name, null);
    }

    public void releasePlayer(JailPlayerObject jailPlayerObject) {
        JailObject jailObject = this.jailsMap.get(jailPlayerObject.getJailName());
        //So this runs when the player was put in jail but somebody deleted the jail.
        if (jailObject == null) {
            jailPlayerObject.getPlayer().sendMessage(Translate.color("&6The jail was deleted but you were in in but you have been released."));
            this.jailPlayerMap.remove(jailPlayerObject.getUuid());
            return;
        }
        jailObject.releasePlayer(jailPlayerObject);
    }

    public void jailPlayer(Player player, String jailName, Integer cellNumber, int seconds) {
        JailObject jailObject = this.jailsMap.get(jailName);
        if (jailObject == null) return;
        jailObject.jailPlayer(player, cellNumber, seconds);
    }
}
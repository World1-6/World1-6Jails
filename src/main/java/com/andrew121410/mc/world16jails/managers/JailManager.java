package com.andrew121410.mc.world16jails.managers;

import com.andrew121410.mc.world16jails.Jail;
import com.andrew121410.mc.world16jails.JailedPlayer;
import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.config.CustomYmlManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class JailManager {

    private final Map<String, Jail> jailMap;
    private final Map<UUID, JailedPlayer> jailedPlayerMap;

    private final World16Jails plugin;

    private final CustomYmlManager jailsYml;

    public JailManager(World16Jails plugin) {
        this.plugin = plugin;

        this.jailMap = this.plugin.getJailsMap();
        this.jailedPlayerMap = this.plugin.getJailedPlayerMap();

        // jails.yml
        this.jailsYml = new CustomYmlManager(this.plugin, false);
        this.jailsYml.setup("jails.yml");
        this.jailsYml.saveConfig();
        this.jailsYml.reloadConfig();
        //...

        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        if (jailsSection == null) {
            this.jailsYml.getConfig().createSection("Jails");
            this.jailsYml.getConfig().createSection("JailPlayers");
            this.jailsYml.saveConfig();
        }
    }

    private Jail loadJail(String name) {
        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        return (Jail) jailsSection.get(name);
    }

    private void saveJail(Jail jail) {
        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        jailsSection.set(jail.getName(), jail);
        this.jailsYml.saveConfig();
    }

    public void loadAllJails() {
        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        for (String key : jailsSection.getKeys(false)) {
            this.jailMap.put(key, loadJail(key));
        }
    }

    public void saveAllJails() {
        this.jailMap.forEach((name, jail) -> saveJail(jail));
    }

    private JailedPlayer loadPlayer(UUID uuid) {
        ConfigurationSection jailPlayersSection = this.jailsYml.getConfig().getConfigurationSection("JailPlayers");
        return (JailedPlayer) jailPlayersSection.get(uuid.toString());
    }

    private void savePlayer(JailedPlayer jailedPlayer) {
        ConfigurationSection jailPlayersSection = this.jailsYml.getConfig().getConfigurationSection("JailPlayers");
        jailPlayersSection.set(jailedPlayer.getUuid().toString(), jailedPlayer);
        this.jailsYml.saveConfig();
    }

    public void loadPlayer(Player player) {
        JailedPlayer jailedPlayer = loadPlayer(player.getUniqueId());
        if (jailedPlayer == null) return; //Player isn't in jail so return.
        jailedPlayer.getCountdownTimer().scheduleTimer();
        this.jailedPlayerMap.put(player.getUniqueId(), jailedPlayer);
    }

    public void saveAllJailedPlayers() {
        this.jailedPlayerMap.forEach(((uuid, jailPlayerObject) -> {
            jailPlayerObject.getCountdownTimer().cancelTimer();
            savePlayer(jailPlayerObject);
        }));
    }

    public void unloadPlayer(UUID uuid) {
        JailedPlayer jailedPlayer = this.jailedPlayerMap.get(uuid);
        if (jailedPlayer == null) return;
        jailedPlayer.getCountdownTimer().cancelTimer();
        savePlayer(jailedPlayer);
        this.jailedPlayerMap.remove(uuid);
    }

    public void deletePlayer(UUID uuid) {
        ConfigurationSection jailPlayersSection = this.jailsYml.getConfig().getConfigurationSection("JailPlayers");
        jailPlayersSection.set(uuid.toString(), null);
        this.jailsYml.saveConfig();
    }

    public void deleteJail(String name) {
        ConfigurationSection jailsSection = this.jailsYml.getConfig().getConfigurationSection("Jails");
        jailsSection.set(name, null);
        this.jailsYml.saveConfig();
    }

    public void releasePlayer(JailedPlayer jailedPlayer) {
        Jail jail = this.jailMap.get(jailedPlayer.getJailName());
        //So this runs when the player was put in jail but somebody deleted the jail.
        if (jail == null) {
            jailedPlayer.getPlayer().sendMessage(Translate.color("&6The jail was deleted but you were in in but you have been released."));
            jailedPlayer.getCountdownTimer().cancelTimer();
            this.jailedPlayerMap.remove(jailedPlayer.getUuid());
            return;
        }
        jail.releasePlayer(jailedPlayer);
    }

    public void jailPlayer(CommandSender sender, Player target, String jailName, Integer cellNumber, int seconds) {
        Jail jail = this.jailMap.get(jailName);
        if (jail == null) return;
        jail.jailPlayer(sender, target, cellNumber, seconds);
    }
}
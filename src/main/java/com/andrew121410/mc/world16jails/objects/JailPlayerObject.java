package com.andrew121410.mc.world16jails.objects;

import com.andrew121410.mc.world16jails.World16Jails;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.time.CountdownTimer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("JailPlayerObject")
public class JailPlayerObject implements ConfigurationSerializable {

    private final UUID uuid;
    private final CountdownTimer countdownTimer;
    private final String jailName;
    private final int cellNumber;

    public JailPlayerObject(UUID uuid, int secondsLeft, String jailName, int cellNumber) {
        this.uuid = uuid;
        this.countdownTimer = new CountdownTimer(World16Jails.getPlugin(), secondsLeft + 10, secondsLeft, () -> World16Jails.getPlugin().getJailManager().releasePlayer(get()), (countdownTimer1 -> {
            Player player1 = getPlayer();
            if (player1 == null || !player1.isOnline()) return;
            getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Translate.color("&6You have " + countdownTimer1.getFancyTimeLeft(true) + " left in jail.")));
        }));
        this.jailName = jailName;
        this.cellNumber = cellNumber;
    }

    public JailPlayerObject get() {
        return this;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public CountdownTimer getCountdownTimer() {
        return countdownTimer;
    }

    public String getJailName() {
        return jailName;
    }

    public int getCellNumber() {
        return cellNumber;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("UUID", this.uuid);
        map.put("SecondsLeft", this.countdownTimer.getSecondsLeft());
        map.put("JailName", this.jailName);
        map.put("CellNumber", this.cellNumber);
        return map;
    }

    public static JailPlayerObject deserialize(Map<String, Object> map) {
        return new JailPlayerObject((UUID) map.get("UUID"), (int) map.get("SecondsLeft"), (String) map.get("JailName"), (int) map.get("CellNumber"));
    }
}
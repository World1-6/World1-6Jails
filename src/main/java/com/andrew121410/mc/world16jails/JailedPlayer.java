package com.andrew121410.mc.world16jails;

import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.time.CountdownTimer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("JailedPlayer")
public class JailedPlayer implements ConfigurationSerializable {

    private final UUID uuid;
    private final CountdownTimer countdownTimer;
    private final String jailName;
    private final int cellNumber;

    public JailedPlayer(UUID uuid, int secondsLeft, String jailName, int cellNumber) {
        this.uuid = uuid;
        this.countdownTimer = new CountdownTimer(World16Jails.getPlugin(), secondsLeft + 10, secondsLeft, () -> World16Jails.getPlugin().getJailManager().releasePlayer(get()), (countdownTimer1 -> {
            Player player1 = getPlayer();
            if (player1 == null || !player1.isOnline()) return;
            getPlayer().sendActionBar(Translate.miniMessage("<gold>You have <yellow>" + countdownTimer1.getFancyTimeLeft(false) + " <gold>left in jail."));
        }));
        this.jailName = jailName;
        this.cellNumber = cellNumber;
    }

    public JailedPlayer get() {
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

    public static JailedPlayer deserialize(Map<String, Object> map) {
        return new JailedPlayer((UUID) map.get("UUID"), (int) map.get("SecondsLeft"), (String) map.get("JailName"), (int) map.get("CellNumber"));
    }
}
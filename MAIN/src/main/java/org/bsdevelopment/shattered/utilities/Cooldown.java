package org.bsdevelopment.shattered.utilities;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.Consumer;

public class Cooldown {
    private final HashMap<String, Long> MAP = new HashMap<>();
    private final int WAIT_TIME;

    public Cooldown(int waitTime) {
        this.WAIT_TIME = waitTime;
    }

    public boolean hasCooldown(Player player, Consumer<Long> consumer) {
        return hasCooldown(player.getName(), consumer);
    }
    public boolean hasCooldown(Class<?> clazz, Consumer<Long> consumer) {
        return hasCooldown(clazz.getSimpleName(), consumer);
    }

    public boolean hasCooldown(String key, Consumer<Long> consumer) {
        if (!MAP.containsKey(key)) return false;

        long secondsLeft = MAP.get(key) / 1000L + WAIT_TIME - System.currentTimeMillis() / 1000L;

        if (secondsLeft > 0L) {
            consumer.accept(secondsLeft);
            return true;
        }

        MAP.remove(key);
        return false;
    }

    public void activateCooldown(String key) {
        MAP.put(key, System.currentTimeMillis());
    }
    public void activateCooldown(Player player) {
        activateCooldown(player.getName());
    }
    public void activateCooldown(Class<?> clazz) {
        activateCooldown(clazz.getSimpleName());
    }
}

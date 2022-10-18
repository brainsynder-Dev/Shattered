package org.bsdevelopment.shattered.utilities;

import lib.brainsynder.nms.TitleMessage;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.events.core.GameCountdownEvent;
import org.bsdevelopment.shattered.files.options.ConfigOption;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.managers.Management;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCountdownTask extends BukkitRunnable {
    private final Shattered PLUGIN;
    private final ShatteredGameMode GAMEMODE;

    private int time = 41;
    private TitleMessage message;

    public GameCountdownTask(Shattered plugin, ShatteredGameMode gamemode) {
        this.PLUGIN = plugin;
        GAMEMODE = gamemode;
        time = ConfigOption.INSTANCE.COUNTDOWN_TIME.getValue() +1;

        if (ConfigOption.INSTANCE.TESTING_MODE.getValue()) time = 11;
        message = new TitleMessage().setFadeIn(0).setStay(11);
    }

    @Override
    public void run() {
        time--;
        ShatteredUtilities.fireShatteredEvent(new GameCountdownEvent(time, GAMEMODE));

        switch (time) {
            case 80, 70, 60, 50, 40, 30, 20, 10, 5, 4, 3, 2, 1 -> {
                PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Remaining Time: "+time);
                message = message.setHeader(ChatColor.GRAY + "" + time);
                Management.GAME_MANAGER.getCurrentPlayers().forEach(shatteredPlayer -> {
                    shatteredPlayer.fetchPlayer(player -> {
                        message.sendMessage(player);
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, 1f, 1f);
                    });
                });
            }
            case 0 -> {
                if ((Management.GAME_MANAGER.getCurrentPlayers().size() == 1) && !ConfigOption.INSTANCE.TESTING_MODE.getValue()) {
                    PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Not enough players... cleanup the mess...");
                    Management.GAME_MANAGER.setState(GameState.CLEANUP);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        PLUGIN.sendPrefixedMessage(player, MessageType.ERROR, "Not enough players to play.");
                    }
                    cancel();
                    return;
                }
                PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Countdown finished... start the game...");
                message.setHeader(" ");
                Management.GAME_MANAGER.getCurrentPlayers().forEach(shatteredPlayer -> {
                    shatteredPlayer.fetchPlayer(player -> {
                        message.sendMessage(player);
                    });
                });
                Management.GAME_MANAGER.setState(GameState.IN_GAME);
                cancel();
            }
        }
    }
}

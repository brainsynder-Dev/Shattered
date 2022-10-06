package org.bsdevelopment.shattered.listeners;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.managers.list.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinLeaveListener implements Listener {
    private final Shattered PLUGIN;

    public JoinLeaveListener(Shattered plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onJoin (PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ShatteredPlayer shatteredPlayer = Management.PLAYER_MANAGER.getShatteredPlayer(event.getPlayer());
                Management.LOBBY_MANAGER.joinLobby(shatteredPlayer);
            }
        }.runTaskLater(PLUGIN, 20);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        ShatteredPlayer shatteredPlayer = Management.PLAYER_MANAGER.getShatteredPlayer(event.getPlayer());
        Management.GAME_MANAGER.leaveGame(shatteredPlayer, GameManager.Reason.LOGIN_OR_OUT);
    }

    @EventHandler
    private void onKick(PlayerKickEvent event) {
        ShatteredPlayer shatteredPlayer = Management.PLAYER_MANAGER.getShatteredPlayer(event.getPlayer());
        Management.GAME_MANAGER.leaveGame(shatteredPlayer, GameManager.Reason.LOGIN_OR_OUT);
    }
}

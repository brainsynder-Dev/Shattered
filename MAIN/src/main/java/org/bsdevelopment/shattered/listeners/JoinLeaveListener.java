package org.bsdevelopment.shattered.listeners;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.managers.list.GameManager;
import org.bsdevelopment.shattered.managers.list.lobby.ReadyCube;
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
        handleLeave(shatteredPlayer);
    }

    @EventHandler
    private void onKick(PlayerKickEvent event) {
        ShatteredPlayer shatteredPlayer = Management.PLAYER_MANAGER.getShatteredPlayer(event.getPlayer());
        handleLeave(shatteredPlayer);
    }

    private void handleLeave (ShatteredPlayer shatteredPlayer) {
        Management.GAME_MANAGER.leaveGame(shatteredPlayer, GameManager.Reason.LOGIN_OR_OUT);

        if (Management.GAME_MANAGER.getState() != GameState.WAITING) return;

        ReadyCube readyCube1 = Management.LOBBY_MANAGER.getReadyCube1();
        if (readyCube1.isReady() && readyCube1.getCubePlayers().isEmpty())
            readyCube1.toggleCube(false);

        ReadyCube readyCube2 = Management.LOBBY_MANAGER.getReadyCube2();
        if (readyCube2.isReady() && readyCube2.getCubePlayers().isEmpty())
            readyCube2.toggleCube(false);
    }
}

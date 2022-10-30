package org.bsdevelopment.shattered.listeners;

import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.Management;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class OtherEventListener implements Listener {
    private final Shattered PLUGIN;

    public OtherEventListener(Shattered plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onSpawn (CreatureSpawnEvent event) {
        Cuboid region = PLUGIN.getSchematics().getCurrentRegion();
        if (region == null) return;
        if (!region.contains(event.getLocation().getBlock())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (Management.PLAYER_MANAGER.getShatteredPlayer(player).getState().getMasterState() != ShatteredPlayer.PlayerState.LOBBY)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onHealthRegenChange(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (Management.PLAYER_MANAGER.getShatteredPlayer(player).getState().getMasterState() != ShatteredPlayer.PlayerState.LOBBY) return;
        if (Management.GAME_OPTIONS_MANAGER.NO_REGENERATION.getValue()) {
            event.setAmount(0);
            event.setCancelled(true);
            return;
        }
        event.setAmount(0.2);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (Management.PLAYER_MANAGER.getShatteredPlayer(event.getPlayer()).getState().getMasterState() != ShatteredPlayer.PlayerState.LOBBY)
            event.setCancelled(true);
    }
}

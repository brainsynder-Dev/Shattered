package org.bsdevelopment.shattered.listeners;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.BowInfoPersistentData;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListeners implements Listener {
    private final Shattered PLUGIN;

    public PlayerDamageListeners(Shattered plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onDamageEvent(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) return;
        if (!(event.getEntity() instanceof Player player)) return;
        ShatteredPlayer shatteredPlayer = Management.PLAYER_MANAGER.getShatteredPlayer(player);

        // Checking if the player is in the lobby, and if they are, it cancels the event.
        if (shatteredPlayer.getState() == ShatteredPlayer.PlayerState.LOBBY) {
            event.setCancelled(true);
            return;
        }

        // Checking if the player is in the game, and if they are not, it returns.
        if (shatteredPlayer.getState() != ShatteredPlayer.PlayerState.IN_GAME) return;


        // Checking if the option to disable fall damage is enabled, and if it is, it cancels the event.
        if (Management.GAME_OPTIONS_MANAGER.DISABLE_FALL_DAMAGE.getValue() && (event.getCause() == EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
            return;
        }

        ShatteredGameMode.DeathReasons reason = ShatteredGameMode.DeathReasons.UNKNOWN;

        // Checking if the player is in the void, and if they are, it sets the reason to `VOID`.
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) reason = ShatteredGameMode.DeathReasons.VOID;
        // Checking if the player is taking fall damage, and if they are, it sets the reason to `FALL_DAMAGE`.
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) reason = ShatteredGameMode.DeathReasons.FALL_DAMAGE;

        ShatteredGameMode gameMode = Management.GAME_MANAGER.getCurrentGamemode();
        // Checking if the game mode is null, and if it is, it cancels the event and returns.
        if (gameMode == null) {
            event.setCancelled(true);
            return;
        }

        // Checking if the player is going to die from the damage, and if they are, it cancels the event and calls the `onDeath` method.
        if ((player.getHealth() - event.getDamage()) < 1) {
            event.setCancelled(true);
            gameMode.onDeath(shatteredPlayer, reason, true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ShatteredPlayer victim = Management.PLAYER_MANAGER.getShatteredPlayer(player);

        // Checking if the victim is in the lobby, and if they are not, it returns.
        if (victim.getState().getMasterState() != ShatteredPlayer.PlayerState.LOBBY) return;

        // Checking if the damager is a player, and if it is, it cancels the event and returns.
        if (event.getDamager() instanceof Player) {
            event.setCancelled(true);
            return;
        }

        ShatteredPlayer attacker = null;

        // Checking if the damager is an arrow, and if it is, it checks if the arrow has the `BowInfo` persistent data, and
        // if it does, it gets the `BowInfo` and sets the attacker to the shooter.
        if (event.getDamager() instanceof AbstractArrow arrow) {
            if (!arrow.getPersistentDataContainer().has(Management.KEY_MANAGER.BOW_INFO_KEY, BowInfoPersistentData.INSTANCE)) {
                event.setCancelled(true);
                return;
            }

            BowInfo info = arrow.getPersistentDataContainer().get(Management.KEY_MANAGER.BOW_INFO_KEY, BowInfoPersistentData.INSTANCE);
            if (info.getShooter() != null) attacker = Management.PLAYER_MANAGER.getShatteredPlayer(info.getShooter());
        }

        // Checking if the damager is a firework, and if it is, it checks if the shooter is a player, and if it is, it sets
        // the attacker to the shooter.
        if (event.getDamager() instanceof Firework firework) {
            if (firework.getShooter() instanceof Player shooter)
                attacker = Management.PLAYER_MANAGER.getShatteredPlayer(shooter);
        }

        // Checking if the attacker is null, and if it is, it cancels the event and returns.
        if (attacker == null) {
            event.setCancelled(true);
            return;
        }

        // Checking if the attacker is in the game, playing, and in the current players list. If they are not, it cancels
        // the event and plays a sound and spawns particles.
        if ((attacker.getState() != ShatteredPlayer.PlayerState.IN_GAME)
                || (!attacker.isPlaying())
                || (!Management.GAME_MANAGER.getCurrentPlayers().contains(attacker))) {
            event.setCancelled(true);
            attacker.fetchPlayer(player1 -> {
                player1.playSound(player1, Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                player1.spawnParticle(Particle.ASH, player1.getEyeLocation(), 10, 0.5, 0.5, 0.5);
            });
            return;
        }

        ShatteredGameMode gameMode = Management.GAME_MANAGER.getCurrentGamemode();
        // Checking if the game mode is null, and if it is, it cancels the event and plays a sound and spawns particles.
        if (gameMode == null) {
            event.setCancelled(true);
            attacker.fetchPlayer(player1 -> {
                player1.playSound(player1, Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                player1.spawnParticle(Particle.ASH, player1.getEyeLocation(), 10, 0.5, 0.5, 0.5);
            });
            return;
        }

        // This is checking if the attacker can damage the victim, and if they can not, it cancels the event and plays a
        // sound and spawns particles.
        if (!gameMode.canDamagePlayer(victim, attacker)) {
            event.setCancelled(true);
            attacker.fetchPlayer(player1 -> {
                player1.playSound(player1, Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                player1.spawnParticle(Particle.ASH, player1.getEyeLocation(), 10, 0.5, 0.5, 0.5);
            });
            return;
        }

        // Checking if the victim is the attacker, and if they are, it cancels the event and returns.
        if (victim.getUuid().equals(attacker.getUuid())) {
            event.setCancelled(true);
            return;
        }

        // This is checking if the victim is going to die from the damage, and if they are, it cancels the event and calls
        // the `onDeathByPlayer` method.
        if ((((player.getHealth() - 0.5) - event.getDamage()) < 1) || Management.GAME_OPTIONS_MANAGER.GOLDEN_BOW.getValue()) {
            event.setDamage(0);
            event.setCancelled(true);
            gameMode.onDeathByPlayer(victim, attacker, true);
        }
    }
}

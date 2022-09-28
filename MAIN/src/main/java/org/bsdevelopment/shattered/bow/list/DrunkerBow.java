package org.bsdevelopment.shattered.bow.list;

import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.BowData;
import org.bsdevelopment.shattered.bow.data.BowForce;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.bow.data.BowType;
import org.bsdevelopment.shattered.bow.tasks.HitPlayerTask;
import org.bsdevelopment.shattered.bow.tasks.HitTask;
import org.bsdevelopment.shattered.managers.Management;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@BowData(
        name = "Drunker",
        description = {"Intoxicates players near were it lands (nausea)"},
        type = {BowType.UTILITY, BowType.SUPPORT}
)
public class DrunkerBow extends ShatteredBow implements HitTask, HitPlayerTask {

    @Override
    public void cleanup() {}

    @Override
    public void onHit(Arrow arrow, BowInfo info, Player player) {
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (player.hasPotionEffect(PotionEffectType.CONFUSION)) return;

        Management.GAME_STATS_MANAGER.PLAYERS_INTOXICATED.increase();

        player.spawnParticle(org.bukkit.Particle.ITEM_CRACK, player.getEyeLocation(),
                10, 0.5, 0.5, 0.5, 0, new ItemStack(Material.EGG));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 40, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 40, 2));

    }

    @Override
    public void onHit(Arrow arrow, BowInfo info) {
        double radius = 1.5;
        int time = 5;
        if (info.getForce() == BowForce.MEDIUM) {
            radius = 3;
            time = 15;
        }
        if (info.getForce() == BowForce.MAX) {
            radius = 5.5;
            time = 20;
        }
        int finalTime = time;

        Location location = info.getEndLocation();
        AtomicBoolean targetHit = new AtomicBoolean(false);

        Collection<Entity> collection = location.getWorld().getNearbyEntities(location, radius, radius, radius);
        if (collection.isEmpty()) return;

        collection.forEach(entity -> {
            if (entity == null) return;
            if (!(entity instanceof Player player)) return;

            player.spawnParticle(org.bukkit.Particle.ITEM_CRACK, location,
                    120, 0.5, 1, 0.5, 0, new ItemStack(Material.EGG));

            if (player.getGameMode() == GameMode.SPECTATOR) return;
            if (player.hasPotionEffect(PotionEffectType.CONFUSION)) return;

            Management.GAME_STATS_MANAGER.PLAYERS_INTOXICATED.increase();

            if (!targetHit.get()) {
                Objects.requireNonNull(info.getShooter()).playSound(info.getShooter(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                targetHit.set(true);
            }

            location.getWorld().playSound(location, Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.0f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * finalTime, 5));
        });
    }
}

package org.bsdevelopment.shattered.version.wrappers;

import fr.skytasul.glowingentities.GlowingEntities;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import org.bsdevelopment.shattered.version.VersionWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Wrapper_v1_19_2 extends VersionWrapper {
    private final GlowingEntities GLOWING_ENTITIES;

    public Wrapper_v1_19_2(Plugin plugin) {
        super(plugin);
        GLOWING_ENTITIES = new GlowingEntities(plugin);
    }

    @Override
    public void highlightBlock(ChatColor color, Location location, int lifeTime, Player... viewers) {
        MagmaCube magmaCube = new MagmaCube(EntityType.MAGMA_CUBE, ((CraftWorld) location.getWorld()).getHandle());
        magmaCube.absMoveTo(location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5, 0, 0);

        // Creating a new packet to spawn the magma cube.
        ClientboundAddEntityPacket spawnPacket = new ClientboundAddEntityPacket(magmaCube);
        for (Player player : viewers) {
            ((CraftPlayer) player).getHandle().connection.send(spawnPacket);

            try {
                // Using the GlowingEntities API to make the magma cube glow.
                GLOWING_ENTITIES.setGlowing(magmaCube.getId(), magmaCube.getUUID().toString(), player, color);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }

        magmaCube.setSize(2, false);
        magmaCube.setSharedFlag(6, true);
        magmaCube.setInvisible(true);

        ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(magmaCube.getId(), magmaCube.getEntityData(), true);
        for (Player player : viewers) ((CraftPlayer) player).getHandle().connection.send(dataPacket);


        // Deleting the entity after the specified time.
        ClientboundRemoveEntitiesPacket deletePacket = new ClientboundRemoveEntitiesPacket(magmaCube.getId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            for (Player player : viewers) {
                ((CraftPlayer) player).getHandle().connection.send(deletePacket);

                try {
                    GLOWING_ENTITIES.unsetGlowing(magmaCube.getId(), player);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }, lifeTime);
    }
}

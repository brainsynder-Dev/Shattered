package org.bsdevelopment.shattered.bow.list;

import lib.brainsynder.utils.Cuboid;
import lib.brainsynder.utils.DirectionUtils;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.BowData;
import org.bsdevelopment.shattered.bow.annotations.UnbreakableGlass;
import org.bsdevelopment.shattered.bow.data.BowForce;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.bow.data.BowType;
import org.bsdevelopment.shattered.bow.tasks.HitTask;
import org.bsdevelopment.shattered.bow.tasks.LaunchTask;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@BowData(
        name = "Waller",
        description = {"Builds a Wall of glass"},
        type = {BowType.DEFENCIVE, BowType.SUPPORT}
)
@UnbreakableGlass
public class WallerBow extends ShatteredBow implements LaunchTask, HitTask {
    private final Map<UUID, DirectionUtils.Direction> DIRECTION_MAP;

    public WallerBow() {
        DIRECTION_MAP = new HashMap<>();
    }

    @Override
    public void cleanup() {
        DIRECTION_MAP.clear();
    }

    @Override
    public void onLaunch(Arrow arrow, BowInfo info) {
        DIRECTION_MAP.put(arrow.getUniqueId(), ShatteredUtilities.getCardinalDirection(Objects.requireNonNull(info.getShooter())));
    }

    @Override
    public void onHit(Arrow arrow, BowInfo info) {
        if (!DIRECTION_MAP.containsKey(arrow.getUniqueId())) return;

        int width = 0, height = 1;
        if (info.getForce() == BowForce.MEDIUM) {
            width = 1;
            height = 2;
        }
        if (info.getForce() == BowForce.MAX) {
            width = 2;
            height = 3;
        }
        Management.GAME_STATS_MANAGER.WALLS_BUILT.increase();

        BlockFace face = BlockFace.UP;
        if (info.getFace() != null) face = info.getFace();

        Block target = Objects.requireNonNull(info.getEndLocation()).getBlock().getRelative(face);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_TURTLE_EGG_CRACK, 1.0f, 1.0f);

        Cuboid cuboid = new Cuboid(target.getLocation(), target.getLocation()).expand(Cuboid.CuboidDirection.Up, height);

        switch (DIRECTION_MAP.get(arrow.getUniqueId())) {
            case EAST:
            case WEST:
                if (width != 0) cuboid = cuboid.expand(Cuboid.CuboidDirection.West, width).expand(Cuboid.CuboidDirection.East, width);
                break;
            default:
                if (width != 0) cuboid = cuboid.expand(Cuboid.CuboidDirection.North, width).expand(Cuboid.CuboidDirection.South, width);
                break;
        }


        cuboid.getBlocks().forEach(block -> {
            if (Shattered.INSTANCE.getSchematics().getCurrentRegion().contains(block)) {
                if ((!block.getType().name().contains("GLASS"))
                        && (!block.getType().name().contains("QUARTZ"))) {
                    if (!Management.GLASS_MANAGER.isSaved(block)) Management.GLASS_MANAGER.saveBlock(block);

                    block.setType(Material.LIGHT_GRAY_STAINED_GLASS);
                }
            }
        });
    }
}

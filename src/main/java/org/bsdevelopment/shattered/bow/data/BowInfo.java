package org.bsdevelopment.shattered.bow.data;

import lib.brainsynder.nbt.JsonToNBT;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.other.NBTException;
import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.managers.Management;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class BowInfo {
    private ShatteredBow bow;

    private final BowForce force;
    private final Player shooter;
    private final Location startLocation;
    private Location endLocation;
    private BlockFace face;

    public BowInfo (String rawData) {
        try {
            StorageTagCompound compound = JsonToNBT.getTagFromJson(rawData);

            force = compound.getEnum("bow-force", BowForce.class);
            startLocation = compound.getLocation("start-location");
            shooter = Bukkit.getPlayer(compound.getUniqueId("shooter"));

            endLocation = compound.getLocation("end-location", null);
            face = compound.getEnum("hit-block-face", BlockFace.class, null);

            if (compound.hasKey("bow"))
                bow = Management.BOW_MANAGER.getBow(compound.getString("bow"));
        } catch (NBTException e) {
            throw new IllegalArgumentException("The rawData supplied could not be parsed: "+e.getMessage());
        }
    }

    public StorageTagCompound toCompound () {
        StorageTagCompound compound = new StorageTagCompound ();
        compound.setUniqueId("shooter", shooter.getUniqueId());
        compound.setEnum("bow-force", force);
        compound.setLocation("start-location", startLocation);

        if (endLocation != null) compound.setLocation("end-location", endLocation);
        if (face != null) compound.setEnum("hit-block-face", face);

        if (bow != null) compound.setString("bow", bow.fetchBowData().name());
        return compound;
    }

    public BowInfo(Player shooter, Location startLocation) {
        this (shooter, startLocation, BowForce.MAX);
    }

    public BowInfo(Player shooter, Location startLocation, BowForce force) {
        this.force = force;
        this.shooter = shooter;
        this.startLocation = startLocation;
    }

    public BowInfo setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
        return this;
    }

    public BowInfo setBlockFace(BlockFace face) {
        this.face = face;
        return this;
    }

    public BowInfo setBow(ShatteredBow bow) {
        this.bow = bow;
        return this;
    }

    @Nullable
    public ShatteredBow getBow() {
        return bow;
    }

    @Nullable
    public BlockFace getFace() {
        return face;
    }

    public BowForce getForce() {
        return force;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    @Nullable
    public Location getEndLocation() {
        return endLocation;
    }

    @Nullable
    public Player getShooter() {
        return shooter;
    }
}

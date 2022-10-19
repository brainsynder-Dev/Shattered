package org.bsdevelopment.shattered.game;

import fr.mrmicky.fastboard.FastBoard;
import lib.brainsynder.nbt.StorageTagCompound;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.managers.Management;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class ShatteredPlayer {
    private final UUID UUID;
    private final String NAME;

    private FastBoard fastBoard;
    private PlayerState state = PlayerState.UNKNOWN;
    private boolean spectating = false;
    private boolean playing = false;


    // It's creating a new ShatteredPlayer object.
    public ShatteredPlayer (Player player) {
        UUID = player.getUniqueId();
        NAME = player.getName();


        fastBoard = new FastBoard(player);
    }

    // It's loading the data from the StorageTagCompound.
    public ShatteredPlayer (StorageTagCompound compound) {
        UUID = compound.getUniqueId("uuid");
        NAME = compound.getString("name");
    }

    /**
     * "This function returns a StorageTagCompound that contains the name and UUID of the player."
     *
     * Now, let's look at the function that loads the data from the StorageTagCompound
     *
     * @return A StorageTagCompound
     */
    public StorageTagCompound toCompound () {
        StorageTagCompound compound = new StorageTagCompound ();
        compound.setString("name", NAME);
        compound.setUniqueId("uuid", UUID);
        return compound;
    }

    /**
     * If the player is spectating, set their gamemode to spectator, clear their inventory, and teleport them to the center
     * of the current region
     *
     * @param spectating Whether or not the player is spectating
     */
    public void setSpectating(boolean spectating) {
        if ((Management.GAME_MANAGER.getState() != GameState.IN_GAME)
                || (Management.GAME_MANAGER.getCurrentGamemode() == null)
                || (Shattered.INSTANCE.getSchematics().getCurrentRegion() == null))
            spectating = false;

        this.spectating = spectating;

        fetchPlayer(player -> {
            player.setGameMode(this.spectating ? GameMode.SPECTATOR : GameMode.ADVENTURE);
            player.getInventory().clear();
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

            if (this.spectating) {
                setState(PlayerState.SPECTATING_GAME);
                player.teleport(Shattered.INSTANCE.getSchematics().getCurrentRegion().getCenter());
            }
        });
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public PlayerState getState() {
        return state;
    }

    /**
     * This function returns the name of the player.
     *
     * @return The name of the class.
     */
    public String getName() {
        return NAME;
    }

    /**
     * This function returns the UUID of the player.
     *
     * @return The UUID of the user.
     */
    public UUID getUuid() {
        return UUID;
    }

    public boolean isSpectating() {
        return spectating;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /**
     * If the player is online, run the consumer with the player as the parameter.
     *
     * @param consumer The consumer that will be called when the player is online.
     */
    public void fetchPlayer (Consumer<Player> consumer) {
        Player player = Bukkit.getPlayer(UUID);
        if (player == null) return;
        if (!player.isOnline()) return;
        consumer.accept(player);
    }

    /**
     * "Get the player, and if the player exists, get the board, and if the board exists, return it, otherwise create a new
     * board and return it."
     *
     * @param boardConsumer A consumer that takes a FastBoard as a parameter.
     */
    public void getOrCreateBoard (Consumer<FastBoard> boardConsumer) {
        fetchPlayer(player -> {
            if (fastBoard != null) {
                boardConsumer.accept(fastBoard);
                return;
            }

            boardConsumer.accept(fastBoard = new FastBoard(player));
        });
    }

    /**
     * "If the board exists, delete it."
     */
    public void removeBoard () {
        getOrCreateBoard(FastBoard::delete);

        fastBoard = null;
    }

    public enum PlayerState {
        // If the player is not in the lobby or a game it will be this state
        UNKNOWN,

        // If the player is in the lobby of Shattered it will be this state
        LOBBY,

        // If the player is currently in a Shattered game it will be this state
        IN_GAME (LOBBY),

        // If the player is currently spectating a Shattered game it will be this state
        SPECTATING_GAME (LOBBY);

        private final PlayerState masterState;

        PlayerState () {
            this.masterState = this;
        }
        PlayerState (PlayerState masterState) {
            this.masterState = masterState;
        }

        public PlayerState getMasterState() {
            return masterState;
        }
    }

    @Override
    public String toString() {
        return toCompound().toString();
    }
}

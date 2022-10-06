package org.bsdevelopment.shattered.game.modes.list;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.game.GameModeData;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.option.Option;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@GameModeData(
        name = "FFA",
        description = "Be the last player alive in this Free-For-All"
)
public class FFAGameMode extends ShatteredGameMode {
    private final LinkedHashMap<ShatteredPlayer, Integer> LIFE_MAP;

    private Option<Integer> LIVES;

    public FFAGameMode(Shattered plugin) {
        super(plugin);

        LIFE_MAP = new LinkedHashMap<>();
    }

    @Override
    public void initiate() {
        Management.GAME_OPTIONS_MANAGER.register(getClass(), LIVES = new Option<>("FFA Lives", 4));
    }

    @Override
    public void cleanup() {
        LIFE_MAP.clear();
    }

    @Override
    public void disqualifyPlayer(ShatteredPlayer shatteredPlayer) {
        LIFE_MAP.remove(shatteredPlayer);

        broadcastMessage(getColor(shatteredPlayer)+shatteredPlayer.getName() + MessageType.SHATTERED_GRAY +" was disqualified from the game");

        checkForWin();
    }

    @Override
    public void start() {
        super.start();

        // Adding all the players in the game to the map with 4 lives.
        Management.GAME_MANAGER.getCurrentPlayers().forEach(shatteredPlayer -> {
            LIFE_MAP.put(shatteredPlayer, LIVES.getValue());
        });
    }

    @Override
    public void onEnd() {
        LIFE_MAP.clear();
    }

    @Override
    public void tick() {
    }

    @Override
    public void checkForWin() {
        if (LIFE_MAP.size() != 1) return;

        ShatteredPlayer winner = (ShatteredPlayer) LIFE_MAP.keySet().toArray()[0];

        broadcastMessage(getColor(winner)+winner.getName() + MessageType.SHATTERED_GRAY +" has just won the FFA game!");
        Management.GAME_MANAGER.setState(GameState.CLEANUP);
    }

    @Override
    public void onDeath(ShatteredPlayer shatteredPlayer, DeathReasons reasons, boolean respawn) {
        // Getting the amount of lives the player has, subtracting 1 from it, and then putting it back into the map.
        int lives = (LIFE_MAP.get(shatteredPlayer) - 1);
        LIFE_MAP.put(shatteredPlayer, lives);

        // Checking if the player has any lives left. If they don't, it sets the player to spectate and broadcasts a
        // message to the server.
        if (lives <= 0) {
            LIFE_MAP.remove(shatteredPlayer);

            respawn = false;
            shatteredPlayer.setSpectating(true);
        }
        super.onDeath(shatteredPlayer, reasons, respawn);

        // Checking if the player has 1 life left. If they do, it broadcasts a message to the server.
        if (lives == 1) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    broadcastMessage(getColor(shatteredPlayer)+shatteredPlayer.getName() + MessageType.SHATTERED_GRAY +" is on their last life!");
                }
            }.runTaskLater(Shattered.INSTANCE, 1);
        }

        // Checking if the player should respawn. If they shouldn't, it broadcasts a message to the server
        if (!respawn) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    broadcastMessage(getColor(shatteredPlayer)+shatteredPlayer.getName() + MessageType.SHATTERED_GRAY +" was eliminated from the game");
                    checkForWin();
                }
            }.runTaskLater(Shattered.INSTANCE, 1);
        }
    }

    @Override
    public List<String> getScoreboardLines() {
        List<String> scoreboardLines = super.getScoreboardLines();

        int lines = 1;
        for (Map.Entry<ShatteredPlayer, Integer> entry : LIFE_MAP.entrySet()) {
            // The scoreboard can only have 16 lines. This is checking if the scoreboard has reached the limit.
            if (lines > 16) break;

            int lives = entry.getValue();
            // Checking if the player has any lives left. If they don't, it skips them.
            if (lives <= 0) continue;

            scoreboardLines.add(ChatColor.GRAY + entry.getKey().getName() + ": " + ChatColor.GREEN + lives);
            lines++;
        }
        return scoreboardLines;
    }
}

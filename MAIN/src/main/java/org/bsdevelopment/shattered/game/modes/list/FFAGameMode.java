package org.bsdevelopment.shattered.game.modes.list;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.events.player.ShatteredPlayerEliminatedEvent;
import org.bsdevelopment.shattered.files.options.ConfigOption;
import org.bsdevelopment.shattered.game.GameModeData;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.option.IntegerOption;
import org.bsdevelopment.shattered.option.Option;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@GameModeData(
        name = "FFA",
        description = "Be the last player alive in this Free-For-All"
)
public class FFAGameMode extends ShatteredGameMode {
    private final LinkedHashMap<ShatteredPlayer, Integer> LIFE_MAP;

    private Option<Integer> LIVES;
    private String SCORE_LINE_FORMAT = "";

    public FFAGameMode(Shattered plugin) {
        super(plugin);

        LIFE_MAP = new LinkedHashMap<>();
    }

    @Override
    public void initiate() {
        Management.GAME_OPTIONS_MANAGER.register(getClass(), LIVES = new Option<>("ffa_lives", ConfigOption.INSTANCE.FFA_OPTION_NAME.getValue(), ConfigOption.INSTANCE.FFA_DEFAULT_LIVES.getValue())
                .setDescription(ConfigOption.INSTANCE.FFA_OPTION_DESCRIPTION.getValue()));

        if (!ConfigOption.INSTANCE.FFA_LIFE_LIST.getValue().isEmpty()) {
            LIVES.setValueList(ConfigOption.INSTANCE.FFA_LIFE_LIST.getValue());
        }else{
            int start = ConfigOption.INSTANCE.FFA_RANGE_START.getValue();
            int end = ConfigOption.INSTANCE.FFA_RANGE_END.getValue();
            int increment = ConfigOption.INSTANCE.FFA_RANGE_INCREMENT.getValue();

            if (start > end) {
                getShattered().sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.ERROR, "[FFA] Your start value is bigger than your end value... we will invert them for you (please update in your config file)");
                LIVES.setValueList(IntegerOption.range(end, start, increment));
            }else{
                LIVES.setValueList(IntegerOption.range(start, end, increment));
            }
        }
    }

    @Override
    public void cleanup() {
        LIFE_MAP.clear();
    }

    @Override
    public void disqualifyPlayer(ShatteredPlayer shatteredPlayer) {
        if (!LIFE_MAP.containsKey(shatteredPlayer)) return;

        LIFE_MAP.remove(shatteredPlayer);

        broadcastMessage(getColor(shatteredPlayer)+shatteredPlayer.getName() + MessageType.SHATTERED_GRAY +" was disqualified from the game");

        checkForWin();
    }

    @Override
    public void start() {
        super.start();
        SCORE_LINE_FORMAT = ConfigOption.INSTANCE.FFA_SCOREBOARD_LINE.getValue();

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
    public boolean checkForWin() {
        if (LIFE_MAP.size() != 1) return false;

        ShatteredPlayer winner = (ShatteredPlayer) LIFE_MAP.keySet().toArray()[0];

        broadcastMessage(getColor(winner)+winner.getName() + MessageType.SHATTERED_GRAY +" has just won the FFA game!");
        Management.GAME_MANAGER.setState(GameState.CLEANUP);
        return true;
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
        }
        super.onDeath(shatteredPlayer, reasons, true);

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
                    ShatteredUtilities.fireShatteredEvent(new ShatteredPlayerEliminatedEvent(shatteredPlayer, FFAGameMode.this));
                    broadcastMessage(getColor(shatteredPlayer)+shatteredPlayer.getName() + MessageType.SHATTERED_GRAY +" was eliminated from the game");
                    if (!checkForWin()) shatteredPlayer.setSpectating(true);
                }
            }.runTaskLater(Shattered.INSTANCE, 1);
        }
    }

    @Override
    public LinkedList<String> getScoreboardLines() {
        LinkedList<String> scoreboardLines = super.getScoreboardLines();

        int lines = 1;
        for (Map.Entry<ShatteredPlayer, Integer> entry : LIFE_MAP.entrySet()) {
            // The scoreboard can only have 16 lines. This is checking if the scoreboard has reached the limit.
            if (lines > 16) break;

            int lives = entry.getValue();
            // Checking if the player has any lives left. If they don't, it skips them.
            if (lives <= 0) continue;

            scoreboardLines.add(ChatColor.translateAlternateColorCodes('&', SCORE_LINE_FORMAT
                    .replace("{name}", entry.getKey().getName())
                    .replace("{lives}", String.valueOf(lives))
            ));
            lines++;
        }
        return scoreboardLines;
    }
}

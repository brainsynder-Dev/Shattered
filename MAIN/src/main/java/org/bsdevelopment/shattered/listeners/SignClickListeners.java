package org.bsdevelopment.shattered.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import lib.brainsynder.utils.Colorize;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.events.lobby.ShatteredGameOptionChangeEvent;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.managers.list.lobby.ReadyCube;
import org.bsdevelopment.shattered.option.GameModeOption;
import org.bsdevelopment.shattered.option.Option;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SignClickListeners implements Listener {
    private final Shattered PLUGIN;

    public SignClickListeners(Shattered plugin) {
        PLUGIN = plugin;
    }

    @EventHandler (ignoreCancelled = true)
    private void onOptionClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();

        if (!block.getType().name().contains("SIGN")) return;
        Sign sign = (Sign) block.getState();

        if (!sign.getPersistentDataContainer().has(Management.KEY_MANAGER.OPTION_SIGN_KEY, DataType.STRING)) return;
        ShatteredPlayer shatteredPlayer = Management.PLAYER_MANAGER.getShatteredPlayer(event.getPlayer());
        if (shatteredPlayer.getState() != ShatteredPlayer.PlayerState.LOBBY) return;

        if (Management.GAME_MANAGER.getState() != GameState.WAITING) {
            PLUGIN.sendPrefixedMessage(event.getPlayer(), MessageType.ERROR, "Unable to modify game options while a game is in-progress");
            return;
        }

        String optionName = sign.getPersistentDataContainer().get(Management.KEY_MANAGER.OPTION_SIGN_KEY, DataType.STRING);

        Option<?> option = Management.GAME_OPTIONS_MANAGER.getOptionFromName(optionName, true);

        if (ShatteredUtilities.fireShatteredCancelEvent(new ShatteredGameOptionChangeEvent(shatteredPlayer, option))) return;

        if (event.getPlayer().isSneaking()) {
            option.previous();
        }else{
            option.next();
        }

        if (option instanceof GameModeOption gameModeOption) {
            ShatteredGameMode gameMode = gameModeOption.getValue();
            Management.GAME_MANAGER.setCurrentGamemode(gameMode);
        }

        if (option == Management.GAME_OPTIONS_MANAGER.LIGHTING) {
            Management.LOBBY_MANAGER.getLobbyPlayers().forEach(shatteredPlayer1 -> Management.LOBBY_MANAGER.updateLighting(shatteredPlayer1));
        }

        Management.GAME_OPTIONS_MANAGER.updateSign(option);
    }


    @EventHandler
    private void onClickReady(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();

        if (!block.getType().name().contains("SIGN")) return;
        Sign sign = (Sign) block.getState();

        if (!sign.getPersistentDataContainer().has(Management.KEY_MANAGER.READY_SIGN_KEY, DataType.BOOLEAN)) return;
        ShatteredPlayer shatteredPlayer = Management.PLAYER_MANAGER.getShatteredPlayer(event.getPlayer());
        if (shatteredPlayer.getState() != ShatteredPlayer.PlayerState.LOBBY) return;
        boolean value = sign.getPersistentDataContainer().get(Management.KEY_MANAGER.READY_SIGN_KEY, DataType.BOOLEAN);

        ReadyCube readyCube1 = Management.LOBBY_MANAGER.getReadyCube1();
        ReadyCube readyCube2 = Management.LOBBY_MANAGER.getReadyCube2();

        if (sign.getLocation().equals(readyCube1.getReadySign().getLocation())) {
            readyCube1.toggleCube(value = !value);
        }else if (sign.getLocation().equals(readyCube2.getReadySign().getLocation())) {
            readyCube2.toggleCube(value = !value);
        }

        boolean bypassCubeCount = false;

        if (value) {
            int totalPlayers = Management.LOBBY_MANAGER.getLobbyPlayers().size();
            if (totalPlayers > 2) {

                if (readyCube1.getCubePlayers().size() == totalPlayers) {
                    readyCube2.toggleCube(true);
                    bypassCubeCount = true;
                }
                if (readyCube2.getCubePlayers().size() == totalPlayers) {
                    readyCube1.toggleCube(true);
                    bypassCubeCount = true;
                }
            }
        }


        if (readyCube1.isReady() && readyCube2.isReady()) {
            Management.GAME_MANAGER.setState(GameState.READY_UP);
            // TODO: This is from the old original code implement something similar
//            lobby.forEachPlayer(player -> {
//                message.setHeader(" ").sendMessage(player);
//                if (!lobby.isLeftPlayer(player) && !lobby.isRightPlayer(player)) {
//                    Tellraw raw = Tellraw.getInstance("[").color(Utilities.BLUE).then("Shattered").color(Utilities.LIGHT_CYAN).then("]").color(Utilities.BLUE)
//                            .then(" Want to join the game? Then ").color(Utilities.GRAY).then("[CLICK HERE]").color(ChatColor.GREEN).style(new ChatColor[]{ChatColor.BOLD, ChatColor.UNDERLINE}).command(command.getCommand("shattered_forceJoin", player1 -> {
//                                if (gameManager.getState() != GameState.WAITING) {
//                                    Utilities.errorMessage(player1, "The game has already started.");
//                                    return;
//                                }
//                                lobby.addLeftPlayer(player1);
//                                Utilities.nmsTP(player1, getRandomLocation(lobby.getLeft()));
//                            })).tooltip("&a&lCLICK HERE TO JOIN THE GAME");
//                    raw.send(player);
//                }
//            });

            boolean finalBypassCubeCount = bypassCubeCount;
            new BukkitRunnable() {
                int time = 5;

                @Override
                public void run() {
                    if (Management.GAME_MANAGER.getState() == GameState.COUNTDOWN) {
                        cancel();
                        Management.LOBBY_MANAGER.getLobbyPlayers().forEach(shatteredPlayer -> {
                            shatteredPlayer.fetchPlayer(player -> {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+" "));
                            });
                        });
                        return;
                    }

                    if (time <= 0) {
                        if (readyCube1.isReady() && readyCube2.isReady()) {
                            if ((!finalBypassCubeCount) && (readyCube1.getCubePlayers().isEmpty() || readyCube2.getCubePlayers().isEmpty())) {
                                cancel();
                                Management.GAME_MANAGER.setState(GameState.CLEANUP);
                                Management.LOBBY_MANAGER.getLobbyPlayers().forEach(shatteredPlayer -> {
                                    shatteredPlayer.fetchPlayer(player -> PLUGIN.sendPrefixedMessage(player, MessageType.ERROR, "One of the readycubes does not contain any players"));
                                });
                                return;
                            }
                            PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Starting countdown...");
                            Management.GAME_MANAGER.setState(GameState.COUNTDOWN);
                        }
                        return;
                    }

                    Management.LOBBY_MANAGER.getLobbyPlayers().forEach(shatteredPlayer -> {
                        shatteredPlayer.fetchPlayer(player -> {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Colorize.translateBungeeHex("&c&lCOUNTDOWN STARTING IN " + time + "s")));
                        });
                    });
                    time--;
                }
            }.runTaskTimer(PLUGIN, 0, 15);
        }
    }
}

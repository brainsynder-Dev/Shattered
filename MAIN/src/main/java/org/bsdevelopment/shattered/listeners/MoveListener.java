package org.bsdevelopment.shattered.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.option.Option;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    @EventHandler
    public void onSignLook (PlayerMoveEvent event) {
        ShatteredPlayer shatteredPlayer = Management.PLAYER_MANAGER.getShatteredPlayer(event.getPlayer());
        if (shatteredPlayer.getState() != ShatteredPlayer.PlayerState.LOBBY) return;

        Block block = event.getPlayer().getTargetBlockExact(10, FluidCollisionMode.NEVER);
        if ((block == null) || (!(block.getState() instanceof Sign sign))) return;

        if (!sign.getPersistentDataContainer().has(Management.KEY_MANAGER.OPTION_SIGN_KEY, DataType.STRING)) return;
        String optionName = sign.getPersistentDataContainer().get(Management.KEY_MANAGER.OPTION_SIGN_KEY, DataType.STRING);

        Option<?> option = Management.GAME_OPTIONS_MANAGER.getOptionFromName(optionName, true);
        if (option.getDescription() == null) return;

        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD+option.getDescription()));
    }
}

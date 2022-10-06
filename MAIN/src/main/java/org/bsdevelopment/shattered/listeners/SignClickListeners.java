package org.bsdevelopment.shattered.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bsdevelopment.shattered.managers.Management;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignClickListeners implements Listener {

    @EventHandler
    private void onClickReady(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();

        if (!block.getType().name().contains("SIGN")) return;
        Sign sign = (Sign) block.getState();

        if (!sign.getPersistentDataContainer().has(Management.KEY_MANAGER.READY_SIGN_KEY, DataType.BOOLEAN)) return;
        boolean value = sign.getPersistentDataContainer().get(Management.KEY_MANAGER.READY_SIGN_KEY, DataType.BOOLEAN);

        if (sign.getLocation().equals(Management.LOBBY_MANAGER.getReadySign1().getLocation())) {
            Management.LOBBY_MANAGER.toggleSign(sign, value = !value);
            Management.LOBBY_MANAGER.toggleDoor(Management.LOBBY_MANAGER.getReadyDoor1(), value);
            return;
        }

        if (sign.getLocation().equals(Management.LOBBY_MANAGER.getReadySign2().getLocation())) {
            Management.LOBBY_MANAGER.toggleSign(sign, value = !value);
            Management.LOBBY_MANAGER.toggleDoor(Management.LOBBY_MANAGER.getReadyDoor2(), value);
            return;
        }
    }
}

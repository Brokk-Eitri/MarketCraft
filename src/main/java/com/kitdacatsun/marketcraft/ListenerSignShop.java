package com.kitdacatsun.marketcraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Objects;

public class ListenerSignShop implements Listener {

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (!(Objects.requireNonNull(event.getClickedBlock()).getBlockData() instanceof WallSign)) {
            return;
        }

        WallSign sign = (WallSign) event.getClickedBlock();
        String[] lines = sign.getLines();

        if (!lines[0].toUpperCase().contains("SHOP")) {
            return;
        }

        int price = Integer.parseInt(lines[1].replaceAll("[^0-9]", ""));
        int quantity = Integer.parseInt(lines[2].replaceAll("[^0-9]", ""));
        Player seller = Bukkit.getServer().getPlayer(lines[3]);


        Vector vector = null;

        switch (sign.getFacing()) {
            case NORTH:
                vector = new Vector(0, 0, 1);
                return;

            case SOUTH:
                vector = new Vector(0, 0, -1);
                return;

            case EAST:
                vector = new Vector(-1, 0, 1);
                return;

            case WEST:
                vector = new Vector(1, 0, 1);
                return;

            default:
        }

        Vector a = vector;
        Location chestLocation = ((Block) sign).getLocation().add(vector);
        Chest chest;


        CommandPay.pay(event.getPlayer(), seller, price);
    }
}

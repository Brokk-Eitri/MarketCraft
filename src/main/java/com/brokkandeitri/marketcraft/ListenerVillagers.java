package com.brokkandeitri.marketcraft;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Objects;

public class ListenerVillagers implements Listener {
    @EventHandler
    private void entityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!(event.getRightClicked() instanceof Villager)){
            return;
        }

        Villager villager = (Villager) event.getRightClicked();
        String name = Objects.requireNonNull(villager.getCustomName());

        if(name.equals("Shop") && villager.isInvulnerable()) {
            new CommandShopMenu().doMenu("root", player, "Shop menu");
        } else if (name.equals("Auction - Sell") && villager.isInvulnerable()){
            CommandPlayerShop.addPLayerShop(player, null);
        } else if (name.equals("Auction - Buy") && villager.isInvulnerable()) {
            CommandPlayerShop.openPlayerShop(player, 0);
        } else if (name.equals("Price History") && villager.isInvulnerable()){
            CommandPrice.openPriceHistory(player, null);
        } else if (name.equals("Rankings") && villager.isInvulnerable()) {
            CommandRanking.sendRankings(player);
        }
    }

}


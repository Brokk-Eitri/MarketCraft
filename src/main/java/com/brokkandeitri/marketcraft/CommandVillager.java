package com.brokkandeitri.marketcraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class CommandVillager implements CommandExecutor{


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        String name = String.join(" ", args);
        World world = player.getWorld();
        Location location = player.getLocation();
        SummonVillager(location, world, name);

        return true;
    }

    public void SummonVillager(Location location, World world, String name){
        Villager villager = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
        villager.setProfession(Villager.Profession.CLERIC);
        villager.setVillagerType(Villager.Type.JUNGLE);
        villager.setAI(false);
        villager.setCanPickupItems(false);
        villager.setInvulnerable(true);
        villager.setCustomName(name);
        villager.setRecipes(new ArrayList<>());
        villager.setSilent(true);

        List<String> villagers = MarketCraft.files.config.getStringList("VILLAGERS");
        if (!villagers.contains(name)){
            villagers.add(name);
            MarketCraft.files.config.set("VILLAGERS", villagers);
        }
        MarketCraft.files.config.set(name, villager.getUniqueId().toString());
    }


    public static void openShop(Player player, ItemStack item, String title) {
        GUIBuilder shop = new GUIBuilder();

        List<GUIItem> items = new ArrayList<>();

        // Row 1
        items.add(new GUIItem(4));
        items.add(new GUIItem("Back", Material.RED_DYE, 1, "Return to Previous Menu", 1));
        items.add(new GUIItem(4));

        // Row 2
        items.add(new GUIItem("Sell 64", Material.RED_STAINED_GLASS_PANE, 64, "Sell", 1));
        items.add(new GUIItem("Sell 16", Material.RED_STAINED_GLASS_PANE, 16, "Sell", 1));
        items.add(new GUIItem("Sell 1", Material.RED_STAINED_GLASS_PANE, 1, "Sell", 1));

        items.add(new GUIItem(1));
        items.add(new GUIItem(item, 1));
        items.add(new GUIItem(1));


        items.add(new GUIItem("Buy 1", Material.GREEN_STAINED_GLASS_PANE, 1, "Buy", 1));
        items.add(new GUIItem("Buy 16", Material.GREEN_STAINED_GLASS_PANE, 16, "Buy", 1));
        items.add(new GUIItem("Buy 64", Material.GREEN_STAINED_GLASS_PANE, 64, "Buy", 1));

        // Row 3
        items.add(new GUIItem(4));
        items.add(new GUIItem("Select an option", Material.GRAY_DYE, 1, "Confirm", 1));
        items.add(new GUIItem(4));

        shop.makeGUI("Shop | " + title, items);
        shop.showGUI(player);
    }

    public static void openSellShop(Player player, ItemStack item, String title) {
        GUIBuilder shop = new GUIBuilder();

        List<GUIItem> items = new ArrayList<>();

        // Row 1
        items.add(new GUIItem(4));
        items.add(new GUIItem("Back", Material.RED_DYE, 1, "Return to Previous Menu", 1));
        items.add(new GUIItem(4));

        // Row 2
        items.add(new GUIItem("Sell 64", Material.RED_STAINED_GLASS_PANE, 64, "Sell", 1));
        items.add(new GUIItem("Sell 16", Material.RED_STAINED_GLASS_PANE, 16, "Sell", 1));
        items.add(new GUIItem("Sell 1", Material.RED_STAINED_GLASS_PANE, 1, "Sell", 1));

        items.add(new GUIItem(1));
        items.add(new GUIItem(item, 1));
        items.add(new GUIItem(1));


        items.add(new GUIItem("N/A", Material.BARRIER, 1, "Cannot Buy Selected item here", 1));
        items.add(new GUIItem("N/A", Material.BARRIER, 16, "Cannot Buy Selected item here", 1));
        items.add(new GUIItem("N/A", Material.BARRIER, 64, "Cannot Buy Selected item here", 1));

        // Row 3
        items.add(new GUIItem(4));
        items.add(new GUIItem("Select an option", Material.GRAY_DYE, 1, "Confirm", 1));
        items.add(new GUIItem(4));

        shop.makeGUI("Shop | " + title, items);
        shop.showGUI(player);
    }
}
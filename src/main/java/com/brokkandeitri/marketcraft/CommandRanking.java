package com.brokkandeitri.marketcraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandRanking implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        sendRankings(player);
        return true;
    }

    public static Map<String, Integer> returnTopRanking() {
        String[] keys = MarketCraft.files.balances.getKeys(true).toArray(new String[0]);

        Map<String, Integer> rankedMap = new HashMap<>();

        for (int i = 2; i < keys.length; i += 2) {
            String name = Objects.requireNonNull(MarketCraft.server.getOfflinePlayer(UUID.fromString(keys[i].substring(8, 44)))).getName();
            int playerBalance = MarketCraft.files.balances.getInt(keys[i]);

            rankedMap.put(name, playerBalance);
        }

        Comparator<String> compareBalance = (String a, String b) -> {
            if (rankedMap.get(a) > rankedMap.get(b)) {
                return -1;
            } else if (rankedMap.get(a) < rankedMap.get(b)) {
                return 1;
            }
            return 0;
        };
        List<String> names = new ArrayList<>(rankedMap.keySet());
        names.sort(compareBalance);

        return rankedMap;
    }

    public static void sendRankings(Player player) {
        Map<String, Integer> rankedMap = returnTopRanking();
        String[] names = rankedMap.keySet().toArray(new String[0]);
        for (int i = 0; i < Math.min(names.length, 10); i++) {
            ChatColor chatColor = ChatColor.GOLD;
            if (player.getName().equals(names[i])) {
                chatColor = ChatColor.LIGHT_PURPLE;
            }
            player.sendMessage( chatColor + String.valueOf(i + 1) + ": " + names[i] + ": " + rankedMap.get(names[i]));
        }
        player.sendMessage(ChatColor.GOLD + "You are in position " + (getIndexOf(names, player.getName()) + 1) + " With a balance of " + rankedMap.get(player.getName()));
    }

    private static int getIndexOf(String[] array, String value) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                index = i;
            }
        }

        return index;
    }
}

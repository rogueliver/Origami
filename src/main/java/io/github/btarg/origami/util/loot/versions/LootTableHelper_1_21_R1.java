package io.github.btarg.origami.util.loot.versions;

import io.github.btarg.origami.util.loot.LootTableHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class LootTableHelper_1_21_R1 implements LootTableHelper {
    @Override
    public List<ItemStack> getBlockDrops(String dropLootTable, Location loc, ItemStack minedWith) {
        Bukkit.getLogger().warning("LootTableHelper not fully implemented for 1.21 - returning empty drops for: " + dropLootTable);
        return Collections.emptyList();
    }
}
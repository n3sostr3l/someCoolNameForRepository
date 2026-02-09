package ru.joutak.template;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;

public class CreakingSpawnListener implements Listener{
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event){
        Wave activeWave = Fight.lastWave();
        if (event.getEntityType() != EntityType.CREAKING) return;

        if (activeWave == null) return;

        Location loc = event.getLocation();
        World world = loc.getWorld();


        int radius = 20;
        boolean hasHeart = false;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = world.getBlockAt(
                            loc.getBlockX() + x,
                            loc.getBlockY() + y,
                            loc.getBlockZ() + z
                    );
                    if (b.getType() == Material.CREAKING_HEART) {
                        hasHeart = true;
                        break;
                    }
                }
            }
        }

        if (!hasHeart) return;

        activeWave.addMob(event.getEntity());
    }
}

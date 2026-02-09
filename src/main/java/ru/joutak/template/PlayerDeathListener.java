package ru.joutak.template;

import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener{

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt){
        Player player = evt.getPlayer();

        if(!Fight.isFightActive()){
            Fight.lastWave().bossBar.removePlayer(player);
            Fight.lastWave().clear(player.getWorld());
            Fight.clear();
        }
    }
}

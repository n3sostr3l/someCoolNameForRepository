package ru.joutak.someCoolPlugin;

import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDeathListener implements Listener{

    @EventHandler
    public void onMobDeath(EntityDeathEvent evt){
        Entity e = (Entity)evt.getEntity();
        Fight.lastWave().bossBar.setProgress(1.0 - (Fight.lastWave().mobCap - Fight.lastWave().aliveMobs.size()) / Fight.lastWave().mobCap);
        if(Fight.isCurrentWaveMob(e)){
            Fight.removeMobFromCurrentWave(e);
        }
        if(Fight.isCurrentWaveFinished()){
            Fight.endWave();
        }
    }
}

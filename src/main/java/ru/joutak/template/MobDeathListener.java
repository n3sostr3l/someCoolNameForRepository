package ru.joutak.template;

import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDeathListener implements Listener{

    @EventHandler
    public void onMobDeath(EntityDeathEvent evt){
        Entity e = (Entity) evt.getEntity();

        if(Fight.isCurrentWaveMob(e)){
            Fight.removeMobFromCurrentWave(e);
        }
        if(Fight.isCurrentWaveFinished()){
            Fight.endWave();
        }
    }
}

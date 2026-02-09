package ru.joutak.template;
import org.bukkit.entity.*;

import java.util.*;

public class Fight{
    private static ArrayList<Wave> waves = new ArrayList<>();
    private static Wave currentWave;
    

    public void addWave(Wave w){
        waves.add(w);
    }
    
    public void rmLast(Wave w){
        if(!waves.isEmpty()) waves.remove(waves.getLast());
    }

    public static Wave lastWave(){
        if(!waves.isEmpty()) return waves.getFirst();
        Wave o = null;
        return o;
    }

    public static void removeMobFromCurrentWave(Entity e){
        if(!waves.isEmpty()) {
            currentWave = waves.getFirst();
            currentWave.removeMob(e);
        }
    }

    public static boolean isCurrentWaveMob(Entity e){
        if(!waves.isEmpty()) {
            currentWave = waves.getFirst();
            return currentWave.isWaveMob(e);
        }
        return false;
    }

    public static boolean isCurrentWaveFinished() {
        if (!waves.isEmpty()) {
            currentWave = waves.getFirst();
            return currentWave.isWaveFinished();
        }
        return false;
    }

    public static void endWave(){
        if(!waves.isEmpty()) {
            currentWave = waves.getFirst();
            currentWave.bossBar.removePlayer(currentWave.linkedTo);
            currentWave.endCallback.perform();
            waves.remove(waves.getFirst());
            currentWave = waves.getFirst();
            currentWave.perform();
        }
    }

    public static boolean isFightActive(){
        return ((!waves.isEmpty())&&(waves.getFirst().isWaveActive()));
    }

    public static void clear(){
        for(Wave w: waves){
            w.clear(w.linkedTo.getWorld());
            waves.remove(w);
        }
    }
}

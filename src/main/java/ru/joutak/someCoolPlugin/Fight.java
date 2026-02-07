package ru.joutak.someCoolPlugin;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.*;

public class Fight{
    private static ArrayList<Wave> waves = new ArrayList<>();
    private static Wave currentWave;
    

    public void addWave(Wave w){
        waves.add(w);
    }
    
    public void rmLast(Wave w){
        waves.remove(waves.getLast());
    }

    public static Wave lastWave(){
        return waves.getLast();
    }

    public static void removeMobFromCurrentWave(Entity e){
        currentWave = waves.getLast();
        currentWave.removeMob(e);
    }

    public static boolean isCurrentWaveMob(Entity e){
        currentWave = waves.getLast();
        return currentWave.isWaveMob(e);
    }

    public static boolean isCurrentWaveFinished(){
        currentWave = waves.getLast();
        return currentWave.isWaveFinished();
    }

    public static void endWave(){
        currentWave = waves.getLast();
        currentWave.endCallback.perform();
    }

    public static boolean isFightActive(){
        return ((!waves.isEmpty())&&(waves.getLast().isWaveActive())); 
    }

    public static void clear(){
        waves = new ArrayList<>();
    }
}

package ru.joutak.someCoolPlugin;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import org.bukkit.block.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarStyle;

public class Wave{
    protected  final Set<UUID> aliveMobs = new HashSet<>();
    protected  int mobCap;
    protected  Player linkedTo;
    protected  WaveEnding endCallback;
    protected  BossBar bossBar;
    
    public Wave(Player linkedTo, WaveEnding endCallback, int mobCap){
        this.linkedTo = linkedTo;
        this.endCallback = endCallback;
        this.mobCap = mobCap;
        this.bossBar = Bukkit.createBossBar(String.format("Yet Another Wave"), BarColor.GREEN, BarStyle.SEGMENTED_10);
    }

    public void addMob(Entity e){
        aliveMobs.add(e.getUniqueId());
    }

    public void removeMob(Entity e){
        aliveMobs.remove(e.getUniqueId());
    }

    public int getAliveNumber(Entity e){
        return aliveMobs.size();
    }

    public boolean isWaveFinished(){
        return aliveMobs.isEmpty();
    }

    public boolean isWaveActive(){
        return !aliveMobs.isEmpty();
    }

    public boolean isWaveMob(Entity e){
        return aliveMobs.contains(e.getUniqueId());
    }

    public void clear(World world){
        for(UUID uuid: aliveMobs){
            Entity e = world.getEntity(uuid);
            if (e != null && !e.isDead()) {
                e.remove();
            }
        }
    }

}

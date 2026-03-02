package ru.joutak.template;
import org.bukkit.Sound;
import org.bukkit.entity.*;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarStyle;

public class Wave{
    protected  final Set<UUID> aliveMobs = new HashSet<>();
    protected  int mobCap;
    protected  Player linkedTo;
    protected  WaveEnding endCallback;
    protected  BossBar bossBar;

    public Wave(Player linkedTo, WaveEnding endCallback, int mobCap, BarStyle seg){
        this.linkedTo = linkedTo;
        this.endCallback = endCallback;
        this.mobCap = mobCap;
        this.bossBar = Bukkit.createBossBar(String.format("Yet Another Wave"), BarColor.GREEN, seg);
    }

    public Wave(Player linkedTo, WaveEnding endCallback, int mobCap, BarStyle seg, BarColor barColor){
        this.linkedTo = linkedTo;
        this.endCallback = endCallback;
        this.mobCap = mobCap;
        this.bossBar = Bukkit.createBossBar(String.format("Yet Another Wave"), barColor, seg);
    }

    public void addMob(Entity e){
        aliveMobs.add(e.getUniqueId());
        this.bossBar.setProgress(aliveMobs.size() / (float) this.mobCap);
    }

    public void removeMob(Entity e){
        aliveMobs.remove(e.getUniqueId());
        this.bossBar.setProgress(aliveMobs.size() / (float) this.mobCap);
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

    public void perform(){}

    public void clear(World world){
        for(UUID uuid: aliveMobs){
            Entity e = world.getEntity(uuid);
            if (e != null && !e.isDead()) {
                world.playSound(e.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK,1.0f, ThreadLocalRandom.current().nextFloat(0.0f,1.0f));
                e.remove();
            }
        }
        this.bossBar.removePlayer(linkedTo);
    }

    public void begin(){
        this.bossBar.addPlayer(linkedTo);
    }

}

package ru.joutak.someCoolPlugin;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.Location;
import org.bukkit.World;

// import Wave;


public final class SomeCoolPlugin extends JavaPlugin {
    @Getter
    private static SomeCoolPlugin instance;

    private boolean running = false;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info(
                String.format("Плагин %s версии %s включен!", getPluginMeta().getName(), getPluginMeta().getVersion())
        );
        getServer().getPluginManager().registerEvents(
            new MobDeathListener(),
            this
    );
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(command.getName().equalsIgnoreCase("coolplugin")){
            if(sender instanceof Player player){
                String toggler = args[0];
                switch (toggler.toLowerCase()) {
                    case "start" -> this.start(player, command, label, args);
                    case "stop" -> this.stop(player, command, label, args);
                    case "help" -> this.help(player, command, label, args);
                    default -> player.sendMessage("Неверный аргумент! Используйте /coolplugin <start|stop|help>.");
                }
            }
            return true;
        }
        return false;
    }

    public void start(Player player, Command command, String label, String[] args){
        int radius = Integer.getInteger(args[1]);
        String level = args[2];
        World world = player.getWorld();
        Location center = player.getLocation();
        final int heightBoundary = 10;
        final int attempts = 10;
        if(!running){
            running = !running;

            switch(level){
                case "1": { 
                    // 2 волны, 1 волна -- зомби (80%) + пауки (20%), 2 волна -- пауки(33%) + скелеты (33%) + зомби (34%), награда за волну - случайный эффект 
                    // из ['регенерация','огнестойкость','поглощение'] 
                    // + ['instantHealth','HealthBoost'] и случайное оружие

                    class FirstWaveCallback implements WaveEnding{
                        public void perform(){

                        }
                    }

                    int waveMobCount = 10;
                    Wave first = new Wave(player, new FirstWaveCallback(), waveMobCount);

                    for(int i = 0; i < waveMobCount; i++){
                        Location loc = this.findSpawnBlock(center, radius, heightBoundary, attempts);
                        double prob = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
                        if(prob<0.8){
                            Entity e = world.spawnEntity(loc, EntityType.ZOMBIE);
                            first.addMob(e);
                        }else{
                            Entity e = world.spawnEntity(loc, EntityType.SPIDER);
                            first.addMob(e);
                        }
                        
                    }
                    
                };
                default: player.sendMessage("Такого уровня сложности нет:( Есть только 1-3 уровни.");
            }

        }

    }

    public void stop(Player player, Command command, String label, String[] args){

    }

    public void help(Player player, Command command, String label, String[] args){

    }

    private Location findSpawnBlock(
        Location center,
        int radius,
        int heightBoundary,
        int attempts
    ){
        World world = center.getWorld();
        for(int i =0; i < attempts; i++){
            int x = center.getBlockX()+ThreadLocalRandom.current().nextInt(-radius,radius + 1);
            int z = center.getBlockZ()+ThreadLocalRandom.current().nextInt(-radius,radius + 1);
            int y = center.getBlockY()+Math.clamp(
                ThreadLocalRandom.current().nextInt(-radius,radius + 1),
                -heightBoundary,
                heightBoundary);
            
            Block ground = world.getBlockAt(x,y,z);
            Block head = world.getBlockAt(x,y+1,z);
            Block feet = world.getBlockAt(x,y+2,z);

            if(!ground.getType().isSolid()){continue;}
            if(!head.isPassable()){continue;}
            if(!feet.isPassable()){continue;}

            return new Location(world, x, y+1, z);
        }
        return null;
    }
}

package ru.joutak.template;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

// import Wave;


public final class SomeCoolPlugin extends JavaPlugin {
    @Getter
    private static SomeCoolPlugin instance;

    public static SomeCoolPlugin getInstance() {
        return instance;
    }
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
        getServer().getPluginManager().registerEvents(
                new CreakingSpawnListener(),
                this
        );
        getServer().getPluginManager().registerEvents(
                new PlayerDeathListener(),
                this
        );
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(command.getName().equalsIgnoreCase("coolplugin")){
            if(args.length < 3) {
                sender.sendMessage("Использование: /coolplugin start <radius> <level>");
                return false;
            }

            int radius;
            try {
                radius = Integer.parseInt(args[1]);
            } catch(NumberFormatException e) {
                sender.sendMessage("Радиус должен быть числом!");
                return false;
            }
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
        int radius = Integer.parseInt(args[1]);
        String level = args[2];
        World world = player.getWorld();
        Location center = player.getLocation();
        final int heightBoundary = 10;
        final int attempts = 10;
            switch(level){
                case "1": { 
                    // 2 волны, 1 волна -- зомби (80%) + пауки (20%), 2 волна -- пауки(33%) + скелеты (33%) + зомби (34%), награда за волну - случайный эффект 
                    // из ['регенерация','огнестойкость','поглощение'] 
                    // + ['instantHealth','HealthBoost'] и случайное оружие

                    PotionEffect[] potionEffects = {
                            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 30, 2),
                            new PotionEffect(PotionEffectType.ABSORPTION, 20*30, 2),
                            new PotionEffect(PotionEffectType.REGENERATION, 20*10, 2)};
                    PotionEffect[] modifiers = {new PotionEffect(PotionEffectType.INSTANT_HEALTH, 40,1), new PotionEffect(PotionEffectType.HEALTH_BOOST, 30, 20*20)};
                    Material[] weapons = {
                            Material.CROSSBOW,
                            Material.BOW,
                            Material.STONE_AXE,
                            Material.IRON_SWORD,
                            Material.IRON_AXE,
                            Material.STONE_SWORD
                    };
                    Material weapon = weapons[ThreadLocalRandom.current().nextInt(weapons.length)];
                    class FirstWaveCallback implements WaveEnding{
                        public void perform(){
                            player.addPotionEffect(potionEffects[ThreadLocalRandom.current().nextInt(potionEffects.length)]);
                            player.addPotionEffect(modifiers[ThreadLocalRandom.current().nextInt(modifiers.length)]);
                            if(weapon == Material.CROSSBOW|| weapon == Material.BOW){
                                player.getInventory().addItem(new ItemStack(Material.ARROW, 8 + ThreadLocalRandom.current().nextInt(24)));
                            }
                            player.getInventory().addItem(new ItemStack(weapon, 1));
                        }
                    }

                    class SecondWaveCallback implements WaveEnding{
                        public void perform(){
                            player.giveExpLevels(5);
                            player.sendMessage("Yet Another Way Completed!");
                            player.getWorld().playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                        }
                    }

                    final int waveMobCount = 10;
                    Fight fight = new Fight();

                    Wave first = new Wave(player, new FirstWaveCallback(), waveMobCount, 10){
                        @Override
                        public void perform() {
                            begin();
                            long delay = 0L;
                            for(int i = 0; i < waveMobCount; i++){
                                Location loc = findSpawnBlock(center, radius, heightBoundary);
                                double prob = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
                                if(prob<0.8){
                                    spawnMob(loc, EntityType.ZOMBIE, world, Sound.ENTITY_WITHER_SPAWN, this, delay);
                                }else{
                                    spawnMob(loc, EntityType.SPIDER, world, Sound.ENTITY_BLAZE_AMBIENT, this, delay);
                                }
                                delay += 5L+ThreadLocalRandom.current().nextInt(1,10);
                            }
                        }
                    };
                    fight.addWave(first);

                    final int secondWaveCount = waveMobCount*2;
                    Wave second = new Wave(player, new SecondWaveCallback(), secondWaveCount, 20, BarColor.BLUE){
                        @Override
                        public void perform() {
                            begin();
                            long delay = 7*20L;
                            for (int i = 0; i < secondWaveCount; i++) {
                                Location loc = findSpawnBlock(center, radius, heightBoundary);
                                double prob = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
                                if (prob < 0.33) {
                                    spawnMob(loc, EntityType.ZOMBIE, world, Sound.ENTITY_WITHER_SPAWN, this, delay);
                                } else if (prob < 0.66) {
                                    spawnMob(loc, EntityType.SPIDER, world, Sound.ENTITY_BLAZE_AMBIENT, this, delay);
                                } else {
                                    spawnMob(loc, EntityType.SKELETON, world, Sound.ENTITY_BLAZE_AMBIENT, this, delay);
                                }
                                delay += 5L + ThreadLocalRandom.current().nextInt(1, 10);
                            }
                        }
                    };

                    fight.addWave(second);

                    first.perform();

                }
                break;
                case "2":{
                    // 3 волны, 1 волна -- скелеты (80%) + бризы (20%), 2 волна -- блейзы (50%) + скрипуны (50%), награда за волну - случайный эффект
                    // из ['регенерация','огнестойкость','поглощение']
                    // + ['instantHealth','HealthBoost'] и случайное оружие

                    PotionEffect[] potionEffects = {
                            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60, 2),
                            new PotionEffect(PotionEffectType.ABSORPTION, 20*60, 2),
                            new PotionEffect(PotionEffectType.REGENERATION, 20*20, 2)};
                    PotionEffect[] modifiers = {new PotionEffect(PotionEffectType.INSTANT_HEALTH, 40,1), new PotionEffect(PotionEffectType.HEALTH_BOOST, 60, 20*20)};
                    Material[] weapons = {
                            Material.CROSSBOW,
                            Material.BOW,
                            Material.IRON_SWORD,
                            Material.IRON_AXE,
                            Material.DIAMOND_SWORD,
                            Material.DIAMOND_AXE
                    };
                    Material weapon = weapons[ThreadLocalRandom.current().nextInt(weapons.length)];
                    class FirstWaveCallback implements WaveEnding{
                        public void perform(){
                            player.addPotionEffect(potionEffects[ThreadLocalRandom.current().nextInt(potionEffects.length)]);
                            player.addPotionEffect(modifiers[ThreadLocalRandom.current().nextInt(modifiers.length)]);
                            if(weapon == Material.CROSSBOW|| weapon == Material.BOW){
                                player.getInventory().addItem(new ItemStack(Material.ARROW, 16 + ThreadLocalRandom.current().nextInt(48)));
                            }
                            player.getInventory().addItem(new ItemStack(weapon, 1));
                        }
                    }

                    class SecondWaveCallback implements WaveEnding{
                        public void perform(){
                            player.addPotionEffect(potionEffects[ThreadLocalRandom.current().nextInt(potionEffects.length)]);
                            player.addPotionEffect(modifiers[ThreadLocalRandom.current().nextInt(modifiers.length)]);
                            if(weapon == Material.CROSSBOW|| weapon == Material.BOW){
                                player.getInventory().addItem(new ItemStack(Material.ARROW, 16 + ThreadLocalRandom.current().nextInt(48)));
                            }
                            player.getInventory().addItem(new ItemStack(weapon, 1));
                            player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 4+ThreadLocalRandom.current().nextInt(4)));

                        }
                    }
                    class ThirdWaweCallback implements WaveEnding{
                        public void perform(){
                            player.giveExpLevels(15);
                            player.sendMessage("Yet Another Way Completed!");
                            player.getWorld().playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                        }
                    }

                    final int waveMobCount = 2*radius;
                    Fight fight = new Fight();

                    Wave first = new Wave(player, new FirstWaveCallback(), waveMobCount, 10){
                        @Override
                        public void perform() {
                            begin();
                            long delay = 0L;
                            for(int i = 0; i < waveMobCount; i++){
                                Location loc = findSpawnBlock(center, radius, heightBoundary);
                                double prob = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
                                if(prob<0.8){
                                    spawnMob(loc, EntityType.SKELETON, world, Sound.ENTITY_ARROW_SHOOT, this, delay);
                                }else{
                                    spawnMob(loc, EntityType.BREEZE, world, Sound.ENTITY_BREEZE_CHARGE, this, delay);
                                }
                                delay += 5L+ThreadLocalRandom.current().nextInt(1,10);
                            }
                        }
                    };
                    fight.addWave(first);

                    final int secondWaveCount = (int) (waveMobCount*1.2);
                    Wave second = new Wave(player, new SecondWaveCallback(), secondWaveCount, 20, BarColor.BLUE){
                        @Override
                        public void perform() {
                            begin();
                            long delay = 10*20L;
                            world.setTime(13000);
                            for (int i = 0; i < secondWaveCount; i++) {
                                Location loc = findSpawnBlock(center, radius, heightBoundary);
                                double prob = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
                                if (prob < 0.5) {
                                    spawnMob(loc, EntityType.BLAZE, world, Sound.ENTITY_BLAZE_HURT, this, delay);
                                } else{
                                    spawnRandomCreakingColumn(player, 1, (int)(radius*2.5));
                                }
                                delay += 5L + ThreadLocalRandom.current().nextInt(1, 10);
                            }
                        }
                    };

                    Wave third = new Wave(player, new ThirdWaweCallback(), secondWaveCount, 20, BarColor.YELLOW){
                        @Override
                        public void perform(){
                            begin();
                            long delay = 15*20L;
                            Location loc = findSpawnBlock(center, radius, heightBoundary);
                            for (int i = 0; i < secondWaveCount; i++) {
                                spawnMob(loc, EntityType.PHANTOM, world, Sound.ENTITY_WITHER_SPAWN, this, delay);
                            }
                        }
                    };

                    fight.addWave(second);
                    fight.addWave(third);

                    first.perform();
                }
                break;
                case "3": {

                    final int wave1Count = 12 + radius;
                    final int wave2Count = 16 + radius;
                    final int wave3Count = 8 + radius / 2;
                    final int wave4Count = 18 + radius;
                    final int wave5Support = 4 + radius / 2;

                    Fight fight = new Fight();

                    //
                    class FirstWaveCallback implements WaveEnding {
                        @Override
                        public void perform() {

                            ThreadLocalRandom r = ThreadLocalRandom.current();
                            double roll = r.nextDouble();

                            if (roll < 0.4) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.REGENERATION,
                                        20 * 30,
                                        1
                                ));
                            } else if (roll < 0.7) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.RESISTANCE,
                                        20 * 35,
                                        1
                                ));
                            } else if (roll < 0.9) {
                                player.getInventory().addItem(
                                        new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1)
                                );
                            } else {
                                player.getInventory().addItem(
                                        new ItemStack(Material.NETHERITE_SCRAP, 1)
                                );
                            }
                        }
                    }

                    Wave first = new Wave(player, new FirstWaveCallback(), wave1Count, 15, BarColor.GREEN) {
                        @Override
                        public void perform() {
                            begin();
                            long delay = 0L;

                            for (int i = 0; i < wave1Count; i++) {
                                Location loc = findSpawnBlock(center, radius, heightBoundary);

                                if (ThreadLocalRandom.current().nextDouble() < 0.7) {
                                    spawnMob(loc, EntityType.ZOMBIE,
                                            world, Sound.ENTITY_ZOMBIE_AMBIENT, this, delay);
                                } else {
                                    spawnMob(loc, EntityType.CAVE_SPIDER,
                                            world, Sound.ENTITY_SPIDER_AMBIENT, this, delay);
                                }

                                delay += 5L;
                            }
                        }
                    };
                    fight.addWave(first);

                    //
                    class SecondWaveCallback implements WaveEnding {
                        @Override
                        public void perform() {

                            ThreadLocalRandom r = ThreadLocalRandom.current();
                            double roll = r.nextDouble();

                            if (roll < 0.35) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.FIRE_RESISTANCE,
                                        20 * 60,
                                        2
                                ));
                            } else if (roll < 0.7) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.STRENGTH,
                                        20 * 30,
                                        1
                                ));
                            } else if (roll < 0.9) {
                                player.getInventory().addItem(
                                        new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2)
                                );
                            } else {
                                player.getInventory().addItem(
                                        new ItemStack(Material.TOTEM_OF_UNDYING, 1)
                                );
                            }
                        }
                    }

                    Wave second = new Wave(player, new SecondWaveCallback(), wave2Count, 20, BarColor.RED) {
                        @Override
                        public void perform() {
                            begin();
                            long delay = 5 * 20L;

                            for (int i = 0; i < wave2Count; i++) {
                                Location loc = findSpawnBlock(center, radius, heightBoundary);

                                spawnMob(loc, EntityType.BLAZE,
                                        world, Sound.ENTITY_BLAZE_AMBIENT, this, delay);

                                if (ThreadLocalRandom.current().nextDouble() < 0.15) {
                                    spawnMob(loc, EntityType.GHAST,
                                            world, Sound.ENTITY_GHAST_SCREAM, this, delay + 5L);
                                }

                                delay += 7L;
                            }
                        }
                    };
                    fight.addWave(second);

                    //
                    class ThirdWaveCallback implements WaveEnding {
                        @Override
                        public void perform() {

                            ThreadLocalRandom r = ThreadLocalRandom.current();
                            double roll = r.nextDouble();

                            if (roll < 0.4) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.RESISTANCE,
                                        20 * 45,
                                        1
                                ));
                            } else if (roll < 0.7) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.REGENERATION,
                                        20 * 35,
                                        2
                                ));
                            } else if (roll < 0.9) {
                                player.getInventory().addItem(
                                        new ItemStack(Material.DIAMOND_AXE, 1)
                                );
                            } else {
                                player.getInventory().addItem(
                                        new ItemStack(Material.TOTEM_OF_UNDYING, 1)
                                );
                            }
                        }
                    }

                    Wave third = new Wave(player, new ThirdWaveCallback(), wave3Count, 25, BarColor.YELLOW) {
                        @Override
                        public void perform() {
                            begin();
                            long delay = 10 * 20L;

                            for (int i = 0; i < wave3Count; i++) {

                                spawnRandomCreakingColumn(player, 1, radius);

                                if (ThreadLocalRandom.current().nextDouble() < 0.1) {
                                    Location loc = findSpawnBlock(center, radius, heightBoundary);
                                    spawnMob(loc, EntityType.PHANTOM,
                                            world, Sound.ENTITY_PHANTOM_AMBIENT, this, delay);
                                }

                                delay += 20L;
                            }
                        }
                    };
                    fight.addWave(third);

                    //
                    class FourthWaveCallback implements WaveEnding {
                        @Override
                        public void perform() {

                            ThreadLocalRandom r = ThreadLocalRandom.current();
                            double roll = r.nextDouble();

                            if (roll < 0.3) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.REGENERATION,
                                        20 * 40,
                                        2
                                ));
                            } else if (roll < 0.6) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.STRENGTH,
                                        20 * 35,
                                        2
                                ));
                            } else if (roll < 0.85) {
                                player.getInventory().addItem(
                                        new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2)
                                );
                            } else {
                                player.getInventory().addItem(
                                        new ItemStack(Material.TOTEM_OF_UNDYING, 1)
                                );
                            }
                        }
                    }

                    Wave fourth = new Wave(player, new FourthWaveCallback(), wave4Count, 30, BarColor.PURPLE) {
                        @Override
                        public void perform() {
                            begin();
                            long delay = 15 * 20L;

                            for (int i = 0; i < wave4Count; i++) {
                                Location loc = findSpawnBlock(center, radius, heightBoundary);

                                if (ThreadLocalRandom.current().nextDouble() < 0.6) {
                                    spawnMob(loc, EntityType.PIGLIN_BRUTE,
                                            world, Sound.ENTITY_PIGLIN_BRUTE_ANGRY, this, delay);
                                } else {
                                    spawnMob(loc, EntityType.WITHER_SKELETON,
                                            world, Sound.ENTITY_WITHER_SPAWN, this, delay);
                                }

                                delay += 8L;
                            }
                        }
                    };
                    fight.addWave(fourth);

                    //
                    class FifthWaveCallback implements WaveEnding {
                        @Override
                        public void perform() {
                            player.giveExpLevels(30);
                            player.getWorld().playSound(
                                    player.getLocation(),
                                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                                    1.0f,
                                    1.0f
                            );
                        }
                    }

                    Wave fifth = new Wave(player, new FifthWaveCallback(), 1, 40, BarColor.BLUE) {
                        @Override
                        public void perform() {
                            begin();
                            long delay = 20 * 20L;

                            Location bossLoc = findSpawnBlock(center, radius, heightBoundary);
                            spawnMob(bossLoc, EntityType.WARDEN,
                                    world, Sound.ENTITY_WARDEN_EMERGE, this, delay);

                            for (int i = 0; i < wave5Support; i++) {
                                Location loc = findSpawnBlock(center, radius, heightBoundary);
                                spawnMob(loc, EntityType.PHANTOM,
                                        world, Sound.ENTITY_PHANTOM_AMBIENT, this, delay + i * 15L);
                            }
                        }
                    };
                    fight.addWave(fifth);
                    first.perform();

                }
                break;

                default: player.sendMessage("Такого уровня сложности нет:( Есть только 1-3 уровни.");
            }



    }

    public void stop(Player player, Command command, String label, String[] args){
        Fight.clear();
        player.sendMessage("Yet Another Wave Cancelled.");
    }

    public void help(Player player, Command command, String label, String[] args){

    }

    private Location findSpawnBlock(
        Location center,
        int radius,
        int heightBoundary
    ){
        Location loc = null;
        World world = center.getWorld();
        int dy = 0;
        while(loc == null){
            int x = center.getBlockX()+ThreadLocalRandom.current().nextInt(-radius,radius + 1);
            int z = center.getBlockZ()+ThreadLocalRandom.current().nextInt(-radius,radius + 1);
            int y = center.getBlockY()+Math.clamp(
                ThreadLocalRandom.current().nextInt(-radius,radius + 1),
                -heightBoundary,
                heightBoundary)
                    +dy;

            Block ground = world.getBlockAt(x,y,z);
            Block feet = world.getBlockAt(x,y+1,z);
            Block head = world.getBlockAt(x,y+2,z);

//            if(!ground.getType().isSolid()){ dy -=1; continue;}
            if(!head.isPassable()){dy+=2;continue;}
            if(!feet.isPassable()){dy+=1;continue;}

            loc = new Location(world, x, y+1+0.7, z);
        }
        return loc;
    }

    public void spawnMob(Location loc, EntityType e, World world, Sound s, Wave w, long spawnDelay){
        SomeCoolPlugin.getInstance().getServer().getScheduler().runTaskLater(SomeCoolPlugin.getInstance(),new Runnable(){
            public void run(){
                Entity ent = world.spawnEntity(loc, e);
                world.spawnParticle(
                        Particle.FLAME,
                        loc.getX(),
                        loc.getY() + 1,
                        loc.getZ(),
                        30,
                        0.3, 0.5, 0.3,
                        0.1
                );
                world.playSound(loc, s, 1.0f, ThreadLocalRandom.current().nextFloat(0.5f,1.0f));
                w.addMob(ent);
            }
        }, spawnDelay);
    }

    public void spawnRandomCreakingColumn(Player p, int amount, int radius) {
        World world = p.getWorld();
        world.playSound(
                p.getLocation(),
                Sound.ENTITY_WARDEN_HEARTBEAT,
                1.0f,
                1.0f
        );
        for (int i = 0; i < amount; i++) {

            Location base = findSpawnBlock(p.getLocation(), radius, 10);
            if (base == null) continue;


            Location ground = base.clone();
            while (ground.getY() > world.getMinHeight()
                    && !ground.getBlock().getType().isSolid()) {
                ground.subtract(0, 1, 0);
            }

            if (!ground.getBlock().getType().isSolid()) continue;

            int height = ThreadLocalRandom.current().nextInt(1, 7);
            long delay = i * 20L;

            for (int y = 1; y <= height; y++) {
                Location place = ground.clone().add(0, y, 0);
                long blockDelay = delay + y * 3L;

                Bukkit.getScheduler().runTaskLater(
                        SomeCoolPlugin.getInstance(),
                        () -> {
                            place.getBlock().setType(Material.PALE_OAK_LOG);
                            world.playSound(
                                    place,
                                    Sound.BLOCK_WOOD_PLACE,
                                    1.0f,
                                    ThreadLocalRandom.current().nextFloat(0.6f, 1.0f)
                            );
                        },
                        blockDelay
                );
            }
            Location heart = ground.clone().add(0, height + 1, 0);
            Location hat = ground.clone().add(0, height + 2, 0);
            Bukkit.getScheduler().runTaskLater(
                    SomeCoolPlugin.getInstance(),
                    () -> {
                        heart.getBlock().setType(Material.CREAKING_HEART);
                        world.playSound(
                                heart,
                                Sound.BLOCK_CREAKING_HEART_PLACE,
                                1.0f,
                                1.0f
                        );

                    },
                    delay + (height + 1) * 3L
            );
            Bukkit.getScheduler().runTaskLater(
                    SomeCoolPlugin.getInstance(),
                    () -> {
                        hat.getBlock().setType(Material.PALE_OAK_LOG);
                        world.playSound(
                                hat,
                                Sound.BLOCK_WOOD_PLACE,
                                1.0f,
                                1.0f
                        );
                    },
                    delay + (height + 2) * 3L
            );
        }
    }

}

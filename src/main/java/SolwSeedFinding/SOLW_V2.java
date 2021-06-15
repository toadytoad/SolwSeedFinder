package SolwSeedFinding;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.BuriedTreasure;
import kaptainwutax.featureutils.structure.DesertPyramid;
import kaptainwutax.featureutils.structure.Mansion;
import kaptainwutax.featureutils.structure.SwampHut;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import kaptainwutax.mcutils.util.data.SeedIterator;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.mcutils.util.data.SeedIterator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SOLW_V2 {
    static int structureX;
    static int structureZ;
    public static void main(String[] args) {
        Random rand = new Random();
        long structureSeed;
        structureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710657L);

        //biome counters for biome checks (zero = biomes where sheep can spawn, one = desert biomes for cactus, two = beach, three = warm ocean, four = anything else)
        int zeroBiomeCount = 0;
        int oneBiomeCount = 0;
        int twoBiomeCount = 0;
        int threeBiomeCount = 0;
        int fourBiomeCount = 0;
        //sets of biome ids for checks
        Set<Integer> zeroBiomes = new HashSet<>(Arrays.asList(1, 3, 4, 34, 35, 129, 131));
        Set<Integer> oneBiomes = new HashSet<>(Arrays.asList(17, 2));
        Set<Integer> threeBiomes = new HashSet<>(Arrays.asList(44, 47));
        int tempBiomeName;
        long seedsCounted = 0;
        long worldSeed;
        while(true) {
            structureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710657L);
            System.out.println("Structure seed: "+structureSeed);
            //searches for a viable sea pickle route
            if (btCheck(structureSeed)==true) {
                SeedIterator worldSeeds = WorldSeed.getSisterSeeds(structureSeed);
                System.out.println("Pickle seed");
                while (worldSeeds.hasNext()) {
                    worldSeed = worldSeeds.next();
                    zeroBiomeCount = 0;
                    oneBiomeCount = 0;
                    twoBiomeCount = 0;
                    threeBiomeCount = 0;
                    fourBiomeCount = 0;
                    OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, worldSeed);
                    BPos spawnPos = biomeSource.getSpawnPoint();
                    Biome biome;
                    int playerX = (int) Math.ceil(spawnPos.getX() / 16) * 16;
                    int playerZ = (int) Math.floor(spawnPos.getZ() / 16) * 16;
                    if (Math.abs(playerX - structureX) < 32 && Math.abs(playerZ - structureZ) < 32) {
                        if (biomeSource.getBiome(structureX, 0, structureZ).getId() == 16) {
                            for (int x = 0; x < 6; x++) {
                                for (int z = 0; z < 6; z++) {
                                    biome = biomeSource.getBiome(x * 16 - 48 + playerX, 0, z * 16 - 48 + playerZ);
                                    tempBiomeName = biome.getId();
                                    if (zeroBiomes.contains(tempBiomeName)) {
                                        zeroBiomeCount++;
                                    } else if (tempBiomeName == 16) {
                                        twoBiomeCount++;
                                    } else if (threeBiomes.contains(tempBiomeName)) {
                                        threeBiomeCount++;
                                    } else {
                                        fourBiomeCount++;
                                    }
                                }
                            }
                        }
                        if (zeroBiomeCount >= 16) {
                            if (threeBiomeCount >= 12 && twoBiomeCount >= 1) {
                                System.out.println("Found: " + worldSeed + ", " + "Pickle Route");
                            } else {
                                seedsCounted++;
                            }
                        } else {
                            seedsCounted++;
                        }
                    }
                }
            }
            //searches for a viable desert temple route
            if (templeCheck(structureSeed)==true) {
                System.out.println("Desert seed");
                SeedIterator worldSeeds = WorldSeed.getSisterSeeds(structureSeed);
                while (worldSeeds.hasNext()) {
                    worldSeed = worldSeeds.next();
                    zeroBiomeCount = 0;
                    oneBiomeCount = 0;
                    twoBiomeCount = 0;
                    threeBiomeCount = 0;
                    fourBiomeCount = 0;
                    OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, worldSeed);
                    BPos spawnPos = biomeSource.getSpawnPoint();
                    Biome biome;
                    int playerX = (int) Math.ceil(spawnPos.getX() / 16) * 16;
                    int playerZ = (int) Math.floor(spawnPos.getZ() / 16) * 16;
                    if (Math.abs(playerX - structureX) < 24 && Math.abs(playerZ - structureZ) < 24) {
                        if (oneBiomes.contains(biomeSource.getBiome(structureX, 0, structureZ).getId())) {
                            for (int x = 0; x < 6; x++) {
                                for (int z = 0; z < 6; z++) {
                                    biome = biomeSource.getBiome(x * 16 - 48 + playerX, 0, z * 16 - 48 + playerZ);
                                    tempBiomeName = biome.getId();
                                    if (zeroBiomes.contains(tempBiomeName)) {
                                        zeroBiomeCount++;
                                    } else if (oneBiomes.contains(tempBiomeName)) {
                                        oneBiomeCount++;
                                    } else {
                                        fourBiomeCount++;
                                    }
                                }
                            }
                        }
                    }
                    if (zeroBiomeCount >= 16) {
                        if (oneBiomeCount >= 8) {
                            System.out.println("Found: " + worldSeed + ", " + "Desert Route");

                        } else {
                            seedsCounted++;
                        }
                    } else {
                        seedsCounted++;
                    }
                }
            }
        }
    }
    public static boolean btCheck(long structureSeed){
        ChunkRand rand = new ChunkRand();
        BuriedTreasure buriedTreasure = new BuriedTreasure(MCVersion.v1_16);
        RPos minBound = new BPos(-1, 0, -1).toRegionPos(buriedTreasure.getSpacing() * 16);
        RPos maxBound = new BPos(1, 0, 1).toRegionPos(buriedTreasure.getSpacing() * 16);
        for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
            for (int regZ = minBound.getZ() + 1; regZ <= maxBound.getZ(); regZ++) {
                CPos pos = buriedTreasure.getInRegion(structureSeed, regX, regZ, rand);
                if (pos!=null) {
                    BPos bPos = pos.toBlockPos().add(9, 0, 9);
                    if (bPos.getX() <= 256 && bPos.getX() >= -256 && bPos.getZ() <= 256 && bPos.getZ() >= -256) {
                        structureX = bPos.getX();
                        structureZ = bPos.getZ();
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean templeCheck(long structureSeed){
        ChunkRand rand = new ChunkRand();
        DesertPyramid desertPyramid = new DesertPyramid(MCVersion.v1_16);
        RPos minBound = new BPos(-1, 0, -1).toRegionPos(desertPyramid.getSpacing() * 16);
        RPos maxBound = new BPos(1, 0, 1).toRegionPos(desertPyramid.getSpacing() * 16);
        for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
            for (int regZ = minBound.getZ() + 1; regZ <= maxBound.getZ(); regZ++) {
                CPos pos = desertPyramid.getInRegion(structureSeed, regX, regZ, rand);
                BPos bPos = pos.toBlockPos().add(9, 0, 9);
                if (bPos.getX() <= 256 && bPos.getX() >= -256 && bPos.getZ() <= 256 && bPos.getZ() >= -256) {
                    structureX = bPos.getX();
                    structureZ = bPos.getZ();
                    return true;
                }
            }
        }
        return false;
    }

}

/*public static void main(String[] args) {
        Random rand = new Random();
        long worldSeed;
        OverworldBiomeSource biomeSource;
        BPos pos;
        int tempBiomeName;
        int playerX;
        int playerZ;
        Set<Integer> zeroBiomes = new HashSet<>(Arrays.asList(1, 3, 4, 34, 35, 129, 131));
        Set<Integer> oneBiomes = new HashSet<>(Arrays.asList(17, 2));
        Set<Integer> threeBiomes = new HashSet<>(Arrays.asList(44, 47));
        int zeroBiomeCount = 0;
        int oneBiomeCount = 0;
        int twoBiomeCount = 0;
        int threeBiomeCount = 0;
        int fourBiomeCount = 0;
        int seedsCounted = 0;
        int[][] biomeMap = new int[6][6];
        long templeSeed = 0;
        boolean noTemple = false;
        while (true) {
            System.out.println("searching for templeSeed");
            templeSeed = findTemple();
            System.out.println("Found templeSeed: " + templeSeed);
            biomeSource = new OverworldBiomeSource(MCVersion.v1_16_5, templeSeed);
            pos = biomeSource.getSpawnPoint();
            playerX = (int) Math.ceil(pos.getX() / 16) * 16;
            playerZ = (int) Math.floor(pos.getZ() / 16) * 16;
            zeroBiomeCount = 0;
            oneBiomeCount = 0;
            twoBiomeCount = 0;
            threeBiomeCount = 0;
            fourBiomeCount = 0;
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    Biome biome = biomeSource.getBiome(x * 16 - 48+playerX, 0, z * 16 - 48+playerZ);
                    tempBiomeName = biome.getId();
                    if (zeroBiomes.contains(tempBiomeName)) {
                        zeroBiomeCount++;
                        biomeMap[x][z] = 0;
                    } else if (oneBiomes.contains(tempBiomeName)) {
                        oneBiomeCount++;
                        biomeMap[x][z] = 1;
                    } else if (tempBiomeName == 16) {
                        twoBiomeCount++;
                        biomeMap[x][z] = 2;
                    } else if (threeBiomes.contains(tempBiomeName)) {
                        threeBiomeCount++;
                        biomeMap[x][z] = 3;
                    } else {
                        fourBiomeCount++;
                        biomeMap[x][z] = 4;
                    }
                    //0 biomes: 1 3 4 34 35 129 131 \
                    //1 biomes: 17 2
                    //2 biomes: 16
                    //3 biomes: 44 47
                }
            }
            if (zeroBiomeCount >= 16) {
                if (threeBiomeCount >= 12 && twoBiomeCount >= 1) {
                    System.out.println("    " + templeSeed + ", " + "Pickle Route");
                } else if (oneBiomeCount >= 8){
                    System.out.println("    " + templeSeed + ", " + "Desert Route");

                }
                else {
                    seedsCounted++;
                }
            }
            else {
                seedsCounted++;
            }
            System.out.println(seedsCounted);
        }
        /*for (String[] row : biomeMap) {
            for (String el : row) {
                System.out.print(el + " ");
            }
            System.out.println();
        }*/


    /*
public static long findTemple() {
        long templeSeed = 0;
        Random random = new Random();
        while (templeSeed == 0) {
        long worldSeed = random.nextLong();
        ChunkRand rand = new ChunkRand();
        DesertPyramid desertPyramid = new DesertPyramid(MCVersion.v1_16_1);
        RPos minBound = new BPos(-1, 0, -1).toRegionPos(desertPyramid.getSpacing() * 16);
        RPos maxBound = new BPos(1, 0, 1).toRegionPos(desertPyramid.getSpacing() * 16);
        OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, worldSeed);
        BPos spawnPos = biomeSource.getSpawnPoint();
        int playerX = (int) Math.ceil(spawnPos.getX() / 16) * 16;
        int playerZ = (int) Math.floor(spawnPos.getZ() / 16) * 16;
        for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
        for (int regZ = minBound.getZ() + 1; regZ <= maxBound.getZ(); regZ++) {
        CPos pos = desertPyramid.getInRegion(worldSeed % 281474976710656L, regX, regZ, rand);
        BPos bPos = pos.toBlockPos().add(9, 0, 9);
        if (bPos.getX() <= playerX + 32 && bPos.getX() >= playerX - 32 && bPos.getZ() <= playerZ + 32 && bPos.getZ() >= playerZ - 32) {
        Biome templeBiome = biomeSource.getBiome(bPos);
        if (templeBiome.getId() == 17 || templeBiome.getId() == 2) {
        templeSeed = worldSeed;
        }
        }
        }
        }
        }
        return templeSeed;
        }*/
/*public static void ifbt(long structureSeed) {
        System.out.println("check 1");
        ChunkRand rand = new ChunkRand();
        OverworldBiomeSource source=new OverworldBiomeSource(MCVersion.latest(),);
        BuriedTreasure buriedTreasure = new BuriedTreasure(MCVersion.v1_16);
        BPos buriedPos = null;
        RPos minBound = new BPos(-1, 0, -1).toRegionPos(buriedTreasure.getSpacing() * 16);
        RPos maxBound = new BPos(0, 0, 0).toRegionPos(buriedTreasure.getSpacing() * 16);
        System.out.println("check 1");
        for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
            for (int regZ = minBound.getZ() + 1; regZ < maxBound.getZ(); regZ++) {
                CPos pos = buriedTreasure.getInRegion(structureSeed, regX, regZ, rand);
                buriedPos = pos.toBlockPos().add(9, 0, 9);
                System.out.println(buriedPos);
            }
        }

        BPos spawnPos= source.getSpawnPoint();
        System.out.println("check 2");
        System.out.println(buriedPos + ", " + structureSeed);
        /*if (Math.abs(spawnPos.getX()-buriedPos.getX())<32 && Math.abs(spawnPos.getY()-buriedPos.getY())<32) {

            System.out.println(structureSeed);
        }
    }*/
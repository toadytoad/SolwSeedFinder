package SOLW_V5_Outpost;
import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import kaptainwutax.mcutils.util.data.SeedIterator;
import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.mcutils.util.data.SeedIterator;
import kaptainwutax.seedutils.rand.Rand;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


//seed finder made by toadytop (__toad_)

//logic:
//array with buried treasure locations
//array with outpost locations
//check if there are any structures right beside eachother
//search for a warm ocean
public class SOLW_V5 {
    static BPos[] buriedTreasures = new BPos[4];
    static BPos[] pillagerOutposts = new BPos[4];
    public static void main(String[] args){
        long structureSeed;
        Random random = new Random();
        structureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710657L);
        long worldSeed;
        BPos buriedTreasure = null;
        BPos pillagerOutpost = null;
        boolean closeStructures = false;
        int seedsFound = 0;
        while (true){
            seedsFound = 0;
            System.out.println("Searching structure seed: " + structureSeed + "\n-\n-\n-");
            findBuriedTreasures(structureSeed);
            findPillagerOutposts(structureSeed);
            int POI = 0;
            int BTI = 0;
            closeStructures = false;
            while (buriedTreasures[BTI]!=null){
                while (pillagerOutposts[POI]!=null){
                    if (getDistance2D(buriedTreasures[BTI],pillagerOutposts[POI])<33){
                        buriedTreasure = buriedTreasures[BTI];
                        pillagerOutpost = pillagerOutposts[POI];
                        buriedTreasures = new BPos[4];
                        pillagerOutposts = new BPos[4];
                        closeStructures = true;
                    }
                    POI++;
                }
                BTI++;
            }
            if (closeStructures==true) {
                SeedIterator worldSeeds = WorldSeed.getSisterSeeds(structureSeed);
                while (worldSeeds.hasNext()) {
                    worldSeed = worldSeeds.next();
                    if (areStructuresPossible(buriedTreasure, pillagerOutpost, worldSeed)) {
                        if (isWarmOcean(worldSeed, buriedTreasure)) {
                            System.out.println(worldSeed);
                            seedsFound++;
                        }
                    }
                }
            }
            structureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710657L);
        }
    }

    private static boolean isWarmOcean(long worldSeed, BPos buriedTreasure) {
        OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_17, worldSeed);
        int tempBiome;
        for (int x = buriedTreasure.getX()-32; x<buriedTreasure.getX()+33; x+=16){
            for (int z = buriedTreasure.getZ()-32; x<buriedTreasure.getZ()+33; z+=16){
                tempBiome = biomeSource.getBiome(x, 0 , z).getId();
                if (tempBiome == 44 || tempBiome == 47) {
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean areStructuresPossible(BPos buriedTreasure, BPos pillagerOutpost, long worldSeed){
        OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_17, worldSeed);
        int POBiome;
        int BTBiome;
        POBiome = biomeSource.getBiome(pillagerOutpost).getId();
        BTBiome = biomeSource.getBiome(buriedTreasure).getId();
        Set<Integer> POBiomes = new HashSet<>(Arrays.asList(1, 37, 5, 19, 12, 2, 17));
        if (POBiomes.contains(POBiome) && BTBiome == 16){
            return true;
        }
        else {
            return false;
        }
    }
    public static double getDistance2D(BPos pos1, BPos pos2) {
        return DistanceMetric.EUCLIDEAN.getDistance(
                pos1.getX() - pos2.getX(),
                pos1.getY() - pos2.getY(),
                pos1.getZ() - pos2.getZ()
        );
    }
    private static void findPillagerOutposts(long structureSeed) {
        ChunkRand rand = new ChunkRand();
        PillagerOutpost pillagerOutpost = new PillagerOutpost(MCVersion.v1_17);
        RPos minBound = new BPos(-1, 0, -1).toRegionPos(pillagerOutpost.getSpacing() * 16);
        RPos maxBound = new BPos(0, 0, 0).toRegionPos(pillagerOutpost.getSpacing() * 16);
        int arrayIndex = 0;
        for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
            for (int regZ = minBound.getZ() + 1; regZ <= maxBound.getZ(); regZ++) {
                CPos pos = pillagerOutpost.getInRegion(structureSeed, regX, regZ, rand);
                if (pos != null) {
                    BPos bPos = pos.toBlockPos().add(9, 0, 9);
                    if (bPos.getX() <= 256 && bPos.getX() >= -256 && bPos.getZ() <= 256 && bPos.getZ() >= -256) {
                        pillagerOutposts[arrayIndex] = bPos;
                        arrayIndex++;
                    }
                }
            }
        }
    }

    private static void findBuriedTreasures(long structureSeed) {
        ChunkRand rand = new ChunkRand();
        BuriedTreasure buriedTreasure = new BuriedTreasure(MCVersion.v1_17);
        RPos minBound = new BPos(-1, 0, -1).toRegionPos(buriedTreasure.getSpacing() * 16);
        RPos maxBound = new BPos(0, 0, 0).toRegionPos(buriedTreasure.getSpacing() * 16);
        int arrayIndex = 0;
        for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
            for (int regZ = minBound.getZ() + 1; regZ <= maxBound.getZ(); regZ++) {
                CPos pos = buriedTreasure.getInRegion(structureSeed, regX, regZ, rand);
                if (pos != null) {
                    BPos bPos = pos.toBlockPos().add(9, 0, 9);
                    if (bPos.getX() <= 256 && bPos.getX() >= -256 && bPos.getZ() <= 256 && bPos.getZ() >= -256) {
                        buriedTreasures[arrayIndex] = bPos;
                        arrayIndex++;
                    }
                }
            }
        }
    }
}

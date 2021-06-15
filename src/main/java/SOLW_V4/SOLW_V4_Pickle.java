package SOLW_V4;
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
import kaptainwutax.seedutils.rand.Rand;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
public class SOLW_V4_Pickle {
    /* SOLW Pickle route seed finder logic
    1. Create an array with buried treasure coordinates in each region within -256 to 256 region
    2. iterate the sister seeds
    3. check if any buried treasures can spawn
    4. search for at least 1 warm ocean biome and at least 6 sheep spawning biomes
     */
    static BPos[] structures = new BPos[4];
    public static  void main(String[] args){
        /*findStructures(58671615592672L);
        for (int i = 0; i<4; i++) {
            if (structures[i]!=null) {
                System.out.println(isStructurePossible(-240602433472018208L, structures[i]));
            }
        }*/
        long structureSeed;
        Random random = new Random();
        structureSeed = 58671615592672L;
        long worldSeed;
        boolean posibleStructure = false;
        int warmOcean = 0;
        int sheepBiomes = 0;
        int seedsFound = 0;
        while (true){
            seedsFound = 0;
            posibleStructure = false;
            structures = new BPos[4];
            System.out.println("Check 1");
            findStructures(structureSeed);
            //System.out.println("Check 2");
            int index = 0;
            SeedIterator worldSeeds = WorldSeed.getSisterSeeds(structureSeed);
            //System.out.println("Searching structure seed: " + structureSeed);
            while (worldSeeds.hasNext()){

                worldSeed = worldSeeds.next();
                index = 0;
                posibleStructure = false;
                while (structures[index]!=null){

                    if (isStructurePossible(worldSeed, structures[index])){
                        //System.out.println("Check 3");
                        posibleStructure = true;
                    }
                    index++;
                }
                if (posibleStructure==true){
                    //System.out.println("Check 4 with: " + worldSeed);
                    if (findBiomes(worldSeed)){
                        System.out.println(worldSeed);
                        seedsFound++;
                    }
                }
            }
            System.out.println("Found " + seedsFound + " seeds.");
            structureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710657L);
        }
    }

    private static boolean findBiomes(long worldSeed) {
        OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, worldSeed);
        int biome;
        Set<Integer> zeroBiomes = new HashSet<>(Arrays.asList(1, 3, 4, 34, 35, 129, 131));
        Set<Integer> threeBiomes = new HashSet<>(Arrays.asList(44, 47));
        boolean warmOcean = false;
        boolean sheepBiome = false;
        for (int x = -256; x<257; x+=32){
            for (int z = -256; z<257; z+=32){
                biome = biomeSource.getBiome(x, 0, z).getId();
                if (threeBiomes.contains(biome)){
                    warmOcean = true;
                    //System.out.println("Found warm ocean");
                }
                if (zeroBiomes.contains(biome)){
                    //System.out.println("Found sheep biome");
                    sheepBiome = true;
                }
                if (sheepBiome==true && warmOcean==true){
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isStructurePossible(long worldSeed, BPos structure) {
        OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, worldSeed);
        int biome;
        biome = biomeSource.getBiome(structure).getId();
        if (biome==16){
            return true;
        }
        else {
            return false;
        }
    }

    public static void findStructures(long structureSeed){
        ChunkRand rand = new ChunkRand();
        BuriedTreasure buriedTreasure = new BuriedTreasure(MCVersion.v1_16);
        RPos minBound = new BPos(-1, 0, -1).toRegionPos(buriedTreasure.getSpacing() * 16);
        RPos maxBound = new BPos(0, 0, 0).toRegionPos(buriedTreasure.getSpacing() * 16);
        int arrayIndex = 0;
        for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
            for (int regZ = minBound.getZ() + 1; regZ <= maxBound.getZ(); regZ++) {
                CPos pos = buriedTreasure.getInRegion(structureSeed, regX, regZ, rand);
                if (pos != null) {
                    BPos bPos = pos.toBlockPos().add(9, 0, 9);
                    if (bPos.getX() <= 256 && bPos.getX() >= -256 && bPos.getZ() <= 256 && bPos.getZ() >= -256) {
                        structures[arrayIndex] = bPos;
                        arrayIndex++;
                    }
                }
            }
        }
    }
}

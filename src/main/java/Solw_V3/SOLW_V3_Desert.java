package Solw_V3;
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
public class SOLW_V3_Desert {
    //structure arrays
    static BPos[] structures = new BPos[4];
    public static void main(String[] args){
        //random seed
        Random random = new Random();
        long structureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710657L);
        //biome counters
        int desertCount = 0;
        int sheepBiomeCount = 0;
        int uselessBiomCount = 0;
        //biome categorization

        //biome id variable
        int biomeID;
        //final worldSeed to spit back
        long worldSeed;

        while (true){
            findStructures(structureSeed);
            SeedIterator worldSeeds = WorldSeed.getSisterSeeds(structureSeed);
            //while loop to iterate through all sister seeds
            while (worldSeeds.hasNext()){
                worldSeed = worldSeeds.next();
                //set a seed and cycles to the next sister seed
                OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, worldSeed);
                BPos spawnPos = biomeSource.getSpawnPoint();
                //gets the spawnpoint
                int index = 0;
                //index for searching for multiple temples
                double distance;
                //while check to prevent looking for desert temples which are not in range. (structures[] will usually have some nulls at the end
                while (structures[index]!=null){
                    //calculates the distance of the current temple to world spawn
                    distance = Math.sqrt((structures[index].getX()-spawnPos.getX()*structures[index].getX()-spawnPos.getX())+(structures[index].getZ()-spawnPos.getZ()*structures[index].getZ()-spawnPos.getZ()));
                    //checks the distance
                    if (distance<16) {
                        //runs the biomeCheck function which searches for optimal biome composition, as well as whether or not the temple can spawn
                        if(biomeCheck(worldSeed, structures[index])==true){
                            System.out.println("Found a seed: " + worldSeed);
                        }
                    }
                    index++;
                }
            }
            //sets a new structure seed
            structureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710657L);
        }



        //while loop to loop through structure seeds


        //find all desert temples in 512x512 area
        //create 2d arrays which store these coordinates
        //iterate all sister strings
        //find which structures are possible
        //check if any structures are within reasonable distance of the player
        //check the biome composition
    }
    public static void findStructures(long structureSeed){
        ChunkRand rand = new ChunkRand();
        DesertPyramid desertPyramid = new DesertPyramid(MCVersion.v1_16);
        RPos minBound = new BPos(-1, 0, -1).toRegionPos(desertPyramid.getSpacing() * 16);
        RPos maxBound = new BPos(0, 0, 0).toRegionPos(desertPyramid.getSpacing() * 16);
        int arrayIndex = 0;
        for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
            for (int regZ = minBound.getZ() + 1; regZ <= maxBound.getZ(); regZ++) {
                CPos pos = desertPyramid.getInRegion(structureSeed, regX, regZ, rand);
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
    public static boolean biomeCheck(long worldSeed, BPos templePos){
        OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, worldSeed);
        int biome;
        biome = biomeSource.getBiome(templePos).getId();
        Set<Integer> zeroBiomes = new HashSet<>(Arrays.asList(1, 3, 4, 34, 35, 129, 131));
        Set<Integer> oneBiomes = new HashSet<>(Arrays.asList(17, 2));
        int desertBiomeCount = 0;
        int sheepBiomeCount = 0;
        int uselessBiomeCount = 0;
        int tempBiomeId;
        if (oneBiomes.contains(biome)){
            for (int x = -48; x<49; x+=16){
                for (int z = -48; z<49; z+=16){
                    tempBiomeId = biomeSource.getBiome(x,0,z).getId();
                    if (zeroBiomes.contains(tempBiomeId)){
                        sheepBiomeCount++;
                    }
                    else if (oneBiomes.contains((tempBiomeId))){
                        desertBiomeCount++;
                    }
                    else {
                        uselessBiomeCount++;
                    }
                }
            }
            if (sheepBiomeCount>=30 && desertBiomeCount >= 3){
                return true;
            }
        }
        return false;
    }
}

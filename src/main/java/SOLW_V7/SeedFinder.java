package SOLW_V7;


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

import static java.lang.Math.floor;

public class SeedFinder {
    public static void main(String[] args){
        BPos[] buriedTreasures = new BPos[256];
        BPos[] pillagerOutposts = new BPos[4];
        NearStructs[] nearStructs = new NearStructs[256];
        long structureSeed;
        Random random = new Random();
        ChunkRand cr = new ChunkRand();
        structureSeed = 177169479421511L;
        PillagerOutpost pillagerOutpost = new PillagerOutpost(MCVersion.v1_17);
        BuriedTreasure buriedTreasure = new BuriedTreasure(MCVersion.v1_17);
        int buriedTreasureIndex = 0;
        int pillagerOutpostIndex = 0;
        int closeStructuresIndex = 0;
        while (true) {
            System.out.print(",");
            for (int x = -16; x < 16; x++) {
                for (int z = -16; z < 16; z++) {
                    CPos pos = buriedTreasure.getInRegion(structureSeed, x, z, cr);
                    if (pos != null) {
                        buriedTreasures[buriedTreasureIndex] = pos.toBlockPos();
                        //System.out.print(buriedTreasures[buriedTreasureIndex] + ", ");
                        buriedTreasureIndex++;
                    }
                }
            }
            try {
                int x = -1;
                int z = 0;
                CPos pos = pillagerOutpost.getInRegion(structureSeed, x, z, cr);
                if (pos != null) {
                    if (pos.toBlockPos().getX() < 257 && pos.toBlockPos().getX() > -256 && pos.toBlockPos().getZ() < 257 && pos.toBlockPos().getZ() > -256) {
                        pillagerOutposts[pillagerOutpostIndex] = pos.toBlockPos();
                        //System.out.print(pillagerOutposts[pillagerOutpostIndex] + ", ");
                        pillagerOutpostIndex++;
                    }
                }
                x = 0;
                z = -1;
                pos = pillagerOutpost.getInRegion(structureSeed, x, z, cr);
                if (pos != null) {
                    if (pos.toBlockPos().getX() < 257 && pos.toBlockPos().getX() > -256 && pos.toBlockPos().getZ() < 257 && pos.toBlockPos().getZ() > -256) {
                        pillagerOutposts[pillagerOutpostIndex] = pos.toBlockPos();
                        //System.out.print(pillagerOutposts[pillagerOutpostIndex] + ", ");
                        pillagerOutpostIndex++;
                    }
                }
                x = 0;
                z = 0;
                pos = pillagerOutpost.getInRegion(structureSeed, x, z, cr);
                if (pos != null) {
                    if (pos.toBlockPos().getX() < 257 && pos.toBlockPos().getX() > -256 && pos.toBlockPos().getZ() < 257 && pos.toBlockPos().getZ() > -256) {
                        pillagerOutposts[pillagerOutpostIndex] = pos.toBlockPos();
                        //System.out.print(pillagerOutposts[pillagerOutpostIndex] + ", ");
                        pillagerOutpostIndex++;
                    }
                }
            } catch(Exception e){

            }
            for (int i = 0; buriedTreasures[i] != null; i++) {
                for (int j = 0; pillagerOutposts[j] != null; j++) {
                    long dist = Math.round(Math.sqrt((pillagerOutposts[j].getX() - buriedTreasures[i].getX()) * (pillagerOutposts[j].getX() - buriedTreasures[i].getX()) + (pillagerOutposts[j].getZ() - buriedTreasures[i].getZ()) * (pillagerOutposts[j].getZ() - buriedTreasures[i].getZ())));
                    if (dist<33) {
                        nearStructs[closeStructuresIndex] = new NearStructs(pillagerOutposts[j], buriedTreasures[i], dist);
                        closeStructuresIndex++;
                    }
                }
            }
            SeedIterator seedIterator = WorldSeed.getSisterSeeds(structureSeed);
            int seen = 0;
            boolean addNoted = false;
            while (seedIterator.hasNext()&&seen<10) {
                long worldSeed = seedIterator.next();
                int index = 0;
                int tempBiome;
                OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_17, worldSeed);
                addNoted = false;
                while (nearStructs[index].getOutpost() != null) {
                    if (buriedTreasure.canSpawn(nearStructs[index].getTreasure().toChunkPos(), biomeSource) && pillagerOutpost.canSpawn(nearStructs[index].getOutpost().toChunkPos(), biomeSource)) {
                        //System.out.println("Found world with existing structures: " + worldSeed);
                        for (int x = nearStructs[index].getTreasure().getX(); x < nearStructs[index].getTreasure().getX() + 49; x += 16) {
                            for (int z = nearStructs[index].getTreasure().getZ() - 48; z < nearStructs[index].getTreasure().getZ()+49; z += 16) {
                                tempBiome = biomeSource.getBiome(x, 0, z).getId();
                                if (tempBiome == 44 || tempBiome == 47) {
                                    System.out.println("Found seed: " + worldSeed);
                                    addNoted = true;
                                }
                            }
                        }
                        if (addNoted==false){
                            seen++;
                            System.out.print("@");
                        }
                    }
                    index++;
                }
            }
            structureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710657L);
            buriedTreasures = new BPos[256];
            pillagerOutposts = new BPos[4];
            nearStructs = new NearStructs[256];
            buriedTreasureIndex = 0;
            pillagerOutpostIndex = 0;
            closeStructuresIndex = 0;
        }


    }
}

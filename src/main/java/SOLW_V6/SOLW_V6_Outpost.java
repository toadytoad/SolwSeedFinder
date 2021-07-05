package SOLW_V6;
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

//logic:

//new structure seed--V
//find all buried treasures/pillager outposts in -256->256 area -- V
//find a pair of nearby structures -- V
//iterate sister seeds --
//if .canSpawn --
//if there is a warm ocean in a 7x7 area around buried treasure --
//if all is met return seed --
public class SOLW_V6_Outpost {
    static BPos[] buriedTreasures = new BPos[256];
    static BPos[] pillagerOutposts = new BPos[4];
    static BPos[][] closeStructures = new BPos[2][32];
    public static void main(String[] args){
        long structureSeed;
        Random random = new Random();
        ChunkRand cr = new ChunkRand();
        structureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710657L);
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
            //System.out.println("");
            for (int x = -1; x < 1; x++) {
                for (int z = -1; z < 1; z++) {
                    CPos pos = pillagerOutpost.getInRegion(structureSeed, x, z, cr);
                    if (pos != null) {
                        if (pos.toBlockPos().getX()<257&&pos.toBlockPos().getX()>-256&&pos.toBlockPos().getZ()<257&&pos.toBlockPos().getZ()>-256) {
                            pillagerOutposts[pillagerOutpostIndex] = pos.toBlockPos();
                            //System.out.print(pillagerOutposts[pillagerOutpostIndex] + ", ");
                            pillagerOutpostIndex++;
                        }
                    }
                }
            }
            //System.out.println("");
            for (int i = 0; buriedTreasures[i] != null; i++) {
                for (int j = 0; pillagerOutposts[j] != null; j++) {
                    if (Math.sqrt((pillagerOutposts[j].getX() - buriedTreasures[i].getX()) * (pillagerOutposts[j].getX() - buriedTreasures[i].getX()) + (pillagerOutposts[j].getZ() - buriedTreasures[i].getZ()) * (pillagerOutposts[j].getZ() - buriedTreasures[i].getZ())) < 33) {
                        closeStructures[0][closeStructuresIndex] = pillagerOutposts[j];
                        closeStructures[1][closeStructuresIndex] = buriedTreasures[i];
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
                while (closeStructures[0][index] != null) {
                    if (buriedTreasure.canSpawn(closeStructures[1][index].toChunkPos(), biomeSource) && pillagerOutpost.canSpawn(closeStructures[0][index].toChunkPos(), biomeSource)) {
                        //System.out.println("Found world with existing structures: " + worldSeed);
                        for (int x = closeStructures[1][index].getX() - 48; x < closeStructures[1][index].getX() + 49; x += 16) {
                            for (int z = closeStructures[1][index].getZ() - 48; z < closeStructures[1][index].getZ() + 49; z += 16) {
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
            closeStructures = new BPos[2][32];
            buriedTreasureIndex = 0;
            pillagerOutpostIndex = 0;
            closeStructuresIndex = 0;
        }
    }
}

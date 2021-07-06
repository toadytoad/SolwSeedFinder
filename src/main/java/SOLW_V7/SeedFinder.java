package SOLW_V7;


import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.loot.ChestContent;
import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.featureutils.structure.generator.Generator;
import kaptainwutax.featureutils.structure.generator.structure.BuriedTreasureGenerator;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.data.SeedIterator;
import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.mcutils.util.data.SeedIterator;
import kaptainwutax.seedutils.rand.Rand;
import kaptainwutax.terrainutils.TerrainGenerator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.floor;
import static kaptainwutax.featureutils.loot.item.Items.TNT;
import static kaptainwutax.featureutils.loot.item.Items.IRON_INGOT;
import static kaptainwutax.featureutils.loot.item.Items.GOLD_INGOT;

public class SeedFinder implements Runnable{
    long structureSeed;

    public SeedFinder(long structureSeed) {
        this.structureSeed = structureSeed;
    }

    @Override
    public void run() {
        CPos[] buriedTreasures = new CPos[256]; //stores the values of the buried treasures in each of the 256 chunks of a structure seed
        BPos[] pillagerOutposts = new BPos[4]; //stores the values of the pillager outposts in 3 regions
        NearStructs[] nearStructs = new NearStructs[256]; //NearStructs class which stores the coordinates of the outpost, buried treasure, and distance
        ChunkRand cr = new ChunkRand(); //initializes a chunk randomizer for wutax lib
        Generator generator = new Generator(MCVersion.v1_16_1) {
            @Override
            public boolean generate(TerrainGenerator generator, int chunkX, int chunkZ, ChunkRand rand) {
                return false;
            }

            @Override
            public List<Pair<ILootType, BPos>> getChestsPos() {
                return null;
            }

            @Override
            public List<Pair<ILootType, BPos>> getLootPos() {
                return null;
            }

            @Override
            public ILootType[] getLootTypes() {
                return new ILootType[0];
            }
        };
        PillagerOutpost pillagerOutpost = new PillagerOutpost(MCVersion.v1_17); //wutax outpost object
        BuriedTreasure buriedTreasure = new BuriedTreasure(MCVersion.v1_17); //wutax buried treasure object
        int buriedTreasureIndex = 0; //indices for arrays
        int pillagerOutpostIndex = 0;
        int closeStructuresIndex = 0;

        System.out.print(","); //notifies that the program has started a new structure seed
        for (int x = -16; x < 16; x++) {
            for (int z = -16; z < 16; z++) { //initial iterators for buried treasures
                CPos pos = buriedTreasure.getInRegion(structureSeed, x, z, cr); //gets the buriedTreasure in the region
                if (pos != null) { //checks if the buriedTreasure might spawn
                    try {
                        if (lootCheck(MCVersion.v1_17, structureSeed, pos)) {
                            buriedTreasures[buriedTreasureIndex] = pos; //appends the coordinates to the index
                            //System.out.print(buriedTreasures[buriedTreasureIndex] + ", ");
                            buriedTreasureIndex++; //increases the index
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
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
            System.out.println("Caught an error");
        }
        for (int i = 0; buriedTreasures[i] != null; i++) { //sets an index for treasure
            for (int j = 0; pillagerOutposts[j] != null; j++) { //sets and index for outposts
                long dist = Math.round(Math.sqrt((pillagerOutposts[j].getX() - (buriedTreasures[i].getX()*16+9)) * (pillagerOutposts[j].getX() - (buriedTreasures[i].getX()*16+9)) + (pillagerOutposts[j].getZ() - (buriedTreasures[i].getZ()*16+9)) * (pillagerOutposts[j].getZ() - (buriedTreasures[i].getZ()*16+9))));
                if (dist<48) { //checks for distance between structures
                    nearStructs[closeStructuresIndex] = new NearStructs(pillagerOutposts[j], buriedTreasures[i], dist); //appends a new class to nearStructs
                    closeStructuresIndex++;
                    System.out.println(structureSeed+" "+pillagerOutposts[j]+" "+buriedTreasures[i]+"!");
                }
            }
        }
        int indexMax = 0;
        for (int i = 0; nearStructs[i]!=null; i++){
            if (!lootCheck(MCVersion.v1_17, structureSeed, nearStructs[i].getTreasure())){
                nearStructs[i]=null;
            }
        }
        SeedIterator seedIterator = WorldSeed.getSisterSeeds(structureSeed);
        while (seedIterator.hasNext()) {
            long worldSeed = seedIterator.next();
            int tempBiome;
            OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_17, worldSeed);
            boolean foundBiome = false;
            int index = 0;
            while(nearStructs[index]!=null){
                if (pillagerOutpost.canSpawn(nearStructs[index].getOutpost().toChunkPos(), biomeSource)&&buriedTreasure.canSpawn(nearStructs[index].getTreasure(), biomeSource)){
                    int treasureX = nearStructs[index].getTreasure().getX();
                    int treasureZ = nearStructs[index].getTreasure().getZ();
                    for (int x = treasureX-48; x<treasureX+49; x+=16){
                        for(int z = treasureZ-48; z<treasureZ+49; z+=16){
                            tempBiome = biomeSource.getBiome(x,0,z).getId();
                            if (tempBiome==44||tempBiome==47){
                                foundBiome=true;
                                System.out.println("Found seed: " + worldSeed);
                            }
                            if (foundBiome){
                                break;
                            }
                        }
                        if (foundBiome){
                            break;
                        }
                    }
                }
                index++;
            }
        }
        Main.StartNext();
    }
    public boolean lootCheck(MCVersion mcVersion, Long structureSeed, CPos pos){
        ChunkRand rand = new ChunkRand();
        BuriedTreasure buriedTreasure = new BuriedTreasure(mcVersion);
        BuriedTreasureGenerator generator=new BuriedTreasureGenerator(mcVersion);
        generator.generate(null, pos, new ChunkRand());
        List<ChestContent> chestContents=buriedTreasure.getLoot(structureSeed, generator, new ChunkRand(), false);
        if (chestContents.get(0).containsAtLeast(TNT, 4)){
            if(chestContents.get(0).containsAtLeast(IRON_INGOT, 2)){
                if(chestContents.get(0).containsAtLeast(IRON_INGOT, 4)||chestContents.get(0).containsAtLeast(GOLD_INGOT, 2)){
                    return true;
                }
            }
        }
        return false;
    }

}

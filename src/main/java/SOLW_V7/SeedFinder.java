package SOLW_V7;


import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.loot.ChestContent;
import kaptainwutax.featureutils.structure.BuriedTreasure;
import kaptainwutax.featureutils.structure.PillagerOutpost;
import kaptainwutax.featureutils.structure.generator.Generator;
import kaptainwutax.featureutils.structure.generator.structure.BuriedTreasureGenerator;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.data.SeedIterator;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.terrainutils.TerrainGenerator;
import net.minecraft.world.chunk.Chunk;

import static SOLW_V7.Main.cr;
import static SOLW_V7.Main.buriedTreasure;
import static SOLW_V7.Main.pillagerOutpost;
import static kaptainwutax.featureutils.loot.item.Items.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
        int buriedTreasureIndex = 0; //indices for arrays
        int pillagerOutpostIndex = 0;
        int closeStructuresIndex = 0;

        //System.out.print(","); //notifies that the program has started a new structure seed
        for (int x = -16; x < 16; x++) {
            for (int z = -16; z < 16; z++) { //initial iterators for buried treasures
                CPos pos = buriedTreasure.getInRegion(structureSeed, x, z, cr); //gets the buriedTreasure in the region
                if (pos != null) { //checks if the buriedTreasure might spawn
                    try {
                        if (lootCheck(MCVersion.v1_17, structureSeed, pos, cr)) {
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
        for (int x = -1; x<1;x++) {
            for (int z = -1; x<1; x++) {
                CPos pos = pillagerOutpost.getInRegion(structureSeed, x, z, cr);
                if (pos != null) {
                    if (pos.toBlockPos().getX() < 257 && pos.toBlockPos().getX() > -256 && pos.toBlockPos().getZ() < 257 && pos.toBlockPos().getZ() > -256) {
                        pillagerOutposts[pillagerOutpostIndex] = pos.toBlockPos();
                        //System.out.print(pillagerOutposts[pillagerOutpostIndex] + ", ");
                        pillagerOutpostIndex++;
                    }
                }
            }
        }
        for (int i = 0; buriedTreasures[i] != null; i++) { //sets an index for treasure
            for (int j = 0; pillagerOutposts[j] != null; j++) { //sets and index for outposts
                long dist = Math.round(Math.sqrt((pillagerOutposts[j].getX() - (buriedTreasures[i].getX()*16+9)) * (pillagerOutposts[j].getX() - (buriedTreasures[i].getX()*16+9)) + (pillagerOutposts[j].getZ() - (buriedTreasures[i].getZ()*16+9)) * (pillagerOutposts[j].getZ() - (buriedTreasures[i].getZ()*16+9))));
                if (dist<48) { //checks for distance between structures
                    nearStructs[closeStructuresIndex] = new NearStructs(pillagerOutposts[j], buriedTreasures[i], dist); //appends a new class to nearStructs
                    closeStructuresIndex++;
                    //System.out.println("!");
                }
            }
        }
        int indexMax = 0;
        FileWriter csv;
        if (nearStructs[0]!=null) {
            try {
                csv = new FileWriter(".\\seeds.csv", true);
                SeedIterator seedIterator = WorldSeed.getSisterSeeds(structureSeed);
                while (seedIterator.hasNext()) {
                    long worldSeed = seedIterator.next();
                    int tempBiome;
                    OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_17, worldSeed);
                    boolean foundBiome = false;
                    int index = 0;
                    while (nearStructs[index] != null) {
                        if (pillagerOutpost.canSpawn(nearStructs[index].getOutpost().toChunkPos(), biomeSource) && buriedTreasure.canSpawn(nearStructs[index].getTreasure(), biomeSource)) {
                            int treasureX = nearStructs[index].getTreasure().toBlockPos().getX()+9;
                            int treasureZ = nearStructs[index].getTreasure().toBlockPos().getZ()+9;
                            for (int x = treasureX - 48; x < treasureX + 49; x += 16) {
                                for (int z = treasureZ - 48; z < treasureZ + 49; z += 16) {
                                    tempBiome = biomeSource.getBiome(x, 0, z).getId();
                                    if (tempBiome == 44 || tempBiome == 47) {
                                        foundBiome = true;

                                        csv.append(worldSeed + "," + nearStructs[index].getOutpost().getX() + "," + nearStructs[index].getOutpost().getZ() + "," + treasureX + "," + treasureZ + "\n");
                                        //System.out.println("Found seed: " + worldSeed);
                                    }
                                    if (foundBiome) {
                                        break;
                                    }
                                }
                                if (foundBiome) {
                                    break;
                                }
                            }
                        }
                        index++;
                    }
                }

                //System.out.println("reached flushing");
                csv.flush();
                csv.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Main.StartNext();
    }
    public boolean lootCheck(MCVersion mcVersion, Long structureSeed, CPos pos, ChunkRand rand){
        BuriedTreasure buriedTreasure = new BuriedTreasure(mcVersion);
        BuriedTreasureGenerator generator=new BuriedTreasureGenerator(mcVersion);
        generator.generate(null, pos, rand);
        List<ChestContent> chestContents=buriedTreasure.getLoot(structureSeed, generator, rand, false);
        if (chestContents.get(0).containsAtLeast(TNT, 8)){
            if(chestContents.get(0).containsAtLeast(IRON_INGOT, 2)){
                return chestContents.get(0).containsAtLeast(IRON_INGOT, 4) || chestContents.get(0).containsAtLeast(GOLD_INGOT, 2);
            }
        }
        return false;
    }

}

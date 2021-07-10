package SOLW_V7;


import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.biome.Biomes;
import kaptainwutax.biomeutils.layer.water.OceanTemperatureLayer;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.loot.ChestContent;
import kaptainwutax.featureutils.structure.BuriedTreasure;
import kaptainwutax.featureutils.structure.generator.structure.BuriedTreasureGenerator;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import kaptainwutax.mcutils.util.data.SeedIterator;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Box;
import nl.jellejurre.seedchecker.SeedChecker;
import nl.jellejurre.seedchecker.SeedCheckerDimension;

import java.io.FileWriter;
import java.util.List;
import java.util.function.Predicate;

import static SOLW_V7.Main.*;
import static kaptainwutax.featureutils.loot.item.Items.*;

public class SeedFinder implements Runnable{
    long structureSeed;
    public SeedFinder(long structureSeed) {
        this.structureSeed = structureSeed;
    }

    @Override
    public void run() {
        CPos[] buriedTreasures = new CPos[256]; //stores the values of the buried treasures in each of the 256 chunks of a structure seed
        CPos[] pillagerOutposts = new CPos[4]; //stores the values of the pillager outposts in 3 regions
        NearStructs[] nearStructs = new NearStructs[100]; //NearStructs class which stores the coordinates of the outpost, buried treasure, and distance
        int buriedTreasureIndex = 0; //indices for arrays
        int pillagerOutpostIndex = 0;
        int closeStructuresIndex = 0;

        //System.out.print(","); //notifies that the program has started a new structure seed
        for (int x = -1; x<1;x++) {
            for (int z = -1; x<1; x++) {
                CPos pos = pillagerOutpost.getInRegion(structureSeed, x, z, cr);
                if (pos != null) {
                    if (pos.toBlockPos().getX() < 257 && pos.toBlockPos().getX() > -256 && pos.toBlockPos().getZ() < 257 && pos.toBlockPos().getZ() > -256) {
                        pillagerOutposts[pillagerOutpostIndex] = pos;
                        //System.out.print(pillagerOutposts[pillagerOutpostIndex] + ", ");
                        pillagerOutpostIndex++;
                    }
                }
            }
        }
        for (int index = 0; pillagerOutposts[index]!=null; index++){
            for (int x = pillagerOutposts[index].getX()-2; x<pillagerOutposts[index].getX()+3; x++){
                for (int z = pillagerOutposts[index].getZ()-2; z<pillagerOutposts[index].getZ()+3; z++){
                    CPos pos = buriedTreasure.getInRegion(structureSeed,x,z,cr);
                    if (pos!=null){
                        if(lootCheck(MCVersion.v1_17,structureSeed,pos,cr)){
                            if(isCorrectTemp(structureSeed,pos.getX()*16+9,pos.getZ()*16+9, (n)->n==44||n==47)) {
                                nearStructs[closeStructuresIndex] = new NearStructs(pillagerOutposts[index].toBlockPos(), pos);
                                closeStructuresIndex++;
                            }
                        }
                    }
                }
            }
        }
        FileWriter csv;
        if (nearStructs[0]!=null) {
            try {
                csv = new FileWriter(".\\seeds.csv", true);
                SeedIterator seedIterator = WorldSeed.getSisterSeeds(structureSeed);
                while (seedIterator.hasNext()) {
                    long worldSeed = seedIterator.next();
                    OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_17, worldSeed);
                    int index = 0;
                    while (nearStructs[index] != null) {
                        //System.out.println("Initing spawnability checks");
                        if (pillagerOutpost.canSpawn(nearStructs[index].getOutpost().toChunkPos(), biomeSource) && buriedTreasure.canSpawn(nearStructs[index].getTreasure(), biomeSource)) {
                            //System.out.println("Found spawnability");
                            int treasureZ = nearStructs[index].getTreasure().toBlockPos().getZ()+9;
                            int treasureX = nearStructs[index].getTreasure().toBlockPos().getX()+9;
                            int outpostX = nearStructs[index].getOutpost().getX();
                            int outpostZ = nearStructs[index].getOutpost().getZ();
                            SeedChecker checker = new SeedChecker(worldSeed, 8, SeedCheckerDimension.OVERWORLD);
                            Box pickleBox = new Box(treasureX-72, 36, treasureZ-72,treasureX+72, 63, treasureZ+72);
                            Box woolBox = new Box(outpostX-48, 63, outpostZ-48,outpostX+48, 100, outpostZ+48);
                            BPos spawn = biomeSource.getSpawnPoint();
                            //System.out.println("Initing lvl 8 checks");
                            int pickleCount = checker.getBlockCountInBox(Blocks.SEA_PICKLE, pickleBox);
                            if (pickleCount>=64) {
                                System.out.println("Pickles: " + pickleCount);
                                if (checker.getBlockCountInBox(Blocks.WHITE_WOOL,woolBox)>=60) {
                                    //System.out.println("Found wool");
                                    if(spawn.getX()<treasureX+48&&spawn.getX()>treasureX-48&&spawn.getZ()<treasureZ+48&&spawn.getZ()>treasureZ-48) {
                                        System.out.println("Seed: "+worldSeed);
                                        csv.append(worldSeed + "," + nearStructs[index].getOutpost().getX() + "," + nearStructs[index].getOutpost().getZ() + "," + nearStructs[index].getTreasure().toBlockPos().getX() + 9 + "," + nearStructs[index].getTreasure().toBlockPos().getZ() + 9 + "\n");
                                        csv.flush();
                                    }
                                    //System.out.println("Didn't find spawn");
                                }
                                //System.out.println("Didn't find wool");
                            }
                            //System.out.println("Didn't find pickles");
                        }
                        index++;
                    }
                }

                System.out.println("reached flushing");
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
    //special thanks to Neil for making this function for me!!! Full credit goes to him. (edited by me to use ids)
    public boolean isCorrectTemp(long structureSeed,int x, int z, Predicate<Integer> biomePredicate){
        int lowerX=x<0?256-x&0xff:x&0xff;
        int lowerZ=z<0?256-z&0xff:z&0xff;
        OceanTemperatureLayer layer=new OceanTemperatureLayer(MCVersion.v1_17,structureSeed,2);
        boolean upX=lowerX>228;
        boolean upZ=lowerZ>228;
        boolean lwX=lowerX<32;
        boolean lwZ=lowerZ<32;
        boolean res;
        if (lwX){
            // square -1,0
            if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256 -1 ,0,z/256 )).getId())){
                return true;
            }
            if (lwZ){
                // square -1,-1
                // square 0,-1
                if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256 -1 ,0,z/256 -1)).getId())){
                    return true;
                }
                if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256  ,0,z/256-1 )).getId())){
                    return true;
                }
            }else if (upZ){
                // square -1, 1
                // square 0, +1
                if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256 -1 ,0,z/256 +1)).getId())){
                    return true;
                }
                if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256  ,0,z/256+1 )).getId())){
                    return true;
                }
            }
        }else if (upX){
            // square 1,0
            if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256 +1 ,0,z/256 )).getId())){
                return true;
            }
            if (lwZ){
                // square 1,-1
                // square 0,-1
                if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256 +1 ,0,z/256 -1)).getId())){
                    return true;
                }
                if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256  ,0,z/256-1 )).getId())){
                    return true;
                }
            }else if (upZ){
                // square 1, 1
                // square 0, +1
                if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256 +1 ,0,z/256 +1)).getId())){
                    return true;
                }
                if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256  ,0,z/256+1 )).getId())){
                    return true;
                }
            }
        }else if (lwZ){
            // square 0,-1
            return biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x / 256, 0, z / 256 - 1)).getId());
        }else if (upZ){
            // square 0,1
            if (biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x/256  ,0,z/256 +1)).getId())){
                return true;
            }
        }
        return  biomePredicate.test(Biomes.REGISTRY.get(layer.sample(x / 256, 0, z / 256 )).getId());
    }

}

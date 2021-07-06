package Sisterseeds;
import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
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
public class SisterSeedChecker {
    public static void main(String[] args){
        long structureSeed = 185884575282376L;
        //Pos{x=-240, y=0, z=0} Pos{x=-247, y=0, z=-39}
        BPos pos = new BPos(137, 0, -39);
        SeedIterator seedIterator = WorldSeed.getSisterSeeds(structureSeed);
        PillagerOutpost pillagerOutpost = new PillagerOutpost(MCVersion.v1_17);
        BuriedTreasure buriedTreasure = new BuriedTreasure(MCVersion.v1_17);
        while (seedIterator.hasNext()){
            long worldSeed = seedIterator.next();
            OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_17, worldSeed);
            if (pillagerOutpost.canSpawn(-15, 0, biomeSource)&&biomeSource.getBiome(-247,0,-39).getId()==16){
                System.out.println("Found possible treasure at seed: " + worldSeed);
            }
        }

    }
}

package SOLW_V7;

import kaptainwutax.featureutils.structure.BuriedTreasure;
import kaptainwutax.featureutils.structure.PillagerOutpost;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.version.MCVersion;
import nl.jellejurre.seedchecker.SeedCheckerSettings;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
public class Main {
    static long StructureSeed = 0;
    static ThreadPoolExecutor pool;
    static Object lock = new Object();
    static ChunkRand cr = new ChunkRand();
    static PillagerOutpost pillagerOutpost = new PillagerOutpost(MCVersion.v1_17);
    static BuriedTreasure buriedTreasure = new BuriedTreasure(MCVersion.v1_17);
    //All basic multi threading.
    public static void main(String[] args) {
        SeedCheckerSettings.initialise();
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        for (int i = 0; i < 100; i++) {
            StartNext();
        }
    }

    public static void StartNext(){
        synchronized (lock) {
            StructureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710656L);
            pool.execute(new SeedFinder(StructureSeed));
        }
    }
}

package SOLW_V7;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
public class Main {
    static long StructureSeed = 194736563634066L;
    static ThreadPoolExecutor pool;
    static Object lock = new Object();

    public static void main(String[] args) {
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

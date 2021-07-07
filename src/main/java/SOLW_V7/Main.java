package SOLW_V7;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
public class Main {
    static long StructureSeed = 194736563634066L;
    static ThreadPoolExecutor pool;
    static Object lock = new Object();
    static long structuresChecked = 0;
    public static void main(String[] args) {
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);

        for (int i = 0; i < 100; i++) {

            StartNext();
        }
    }

    public static void StartNext(){
        synchronized (lock) {
            if (structuresChecked%65536==0){
                System.out.println("Checked: " + structuresChecked + "seeds");
            }
            structuresChecked+=1;
            StructureSeed = ThreadLocalRandom.current().nextLong(-281474976710656L, 281474976710656L);
            pool.execute(new SeedFinder(StructureSeed));
        }
    }
}

package SOLW_V7;

import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;

public class NearStructs {
    BPos outpost;
    CPos treasure;
    long dist;
    public NearStructs(BPos a, CPos b, long c){
        this.outpost = a;
        this.treasure = b;
        this.dist = c;
    }
    public BPos getOutpost(){
        return outpost;
    }
    public CPos getTreasure(){return treasure;}
    public long getDist(){return dist;}
}

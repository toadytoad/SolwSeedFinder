package SOLW_V7;

import kaptainwutax.mcutils.util.pos.BPos;

public class NearStructs {
    BPos outpost;
    BPos treasure;
    int dist;
    public NearStructs(BPos a, BPos b, int c){
        this.outpost = a;
        this.treasure = b;
        this.dist = c;
    }
    public BPos getOutpost(){
        return outpost;
    }
    public BPos getTreasure(){return treasure;}
    public int getDist(){return dist;}
}

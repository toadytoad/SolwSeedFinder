package SOLW_V7;

import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;

public class NearStructs {
    BPos outpost;
    CPos treasure;
    public NearStructs(BPos a, CPos b){
        this.outpost = a;
        this.treasure = b;
    }
    public BPos getOutpost(){
        return outpost;
    }
    public CPos getTreasure(){return treasure;}
}

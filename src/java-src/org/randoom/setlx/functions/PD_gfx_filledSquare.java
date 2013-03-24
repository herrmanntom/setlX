package org.randoom.setlx.functions;


import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_filledSquare extends GfxXYRFunction {
    public final static PreDefinedFunction DEFINITION = new PD_gfx_filledSquare();
    
    public PD_gfx_filledSquare(){
        super("gfx_filledSquare");
    }
    
    
    protected void executeStdDrawFunction(Double x, Double y, Double r){
        StdDraw.filledSquare( x, y, r );    
    }
}

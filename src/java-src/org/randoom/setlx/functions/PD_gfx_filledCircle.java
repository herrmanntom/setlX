package org.randoom.setlx.functions;


import org.randoom.setlx.utilities.StdDraw;

//square(NumberValue,NumberValue,NumberValue) : 
//
public class PD_gfx_filledCircle extends GfxXYRFunction{
    public final static PreDefinedFunction DEFINITION = new PD_gfx_filledCircle();

    private PD_gfx_filledCircle() {
        super("gfx_filledCircle");
    }
    
    protected void executeStdDrawFunction(Double x, Double y, Double r){
        StdDraw.filledCircle( x, y, r );    
    }
    
}

package org.randoom.setlx.functions;


import org.randoom.setlx.utilities.StdDraw;

//square(NumberValue,NumberValue,NumberValue) : 
//
public class PD_gfx_circle extends GfxXYRFunction{
    public final static PreDefinedFunction DEFINITION = new PD_gfx_circle();

    private PD_gfx_circle() {
        super("circle");
    }
    
    protected void executeStdDrawFunction(Double x, Double y, Double r){
        StdDraw.circle( x, y, r );    
    }
    
}

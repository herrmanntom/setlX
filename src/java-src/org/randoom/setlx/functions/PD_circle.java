package org.randoom.setlx.functions;


import org.randoom.setlx.utilities.StdDraw;

//square(NumberValue,NumberValue,NumberValue) : 
//
public class PD_circle extends StdDrawXYRFunction{
    public final static PreDefinedFunction DEFINITION = new PD_circle();

    private PD_circle() {
        super("circle");
    }
    
    protected void executeStdDrawFunction(Double x, Double y, Double r){
        StdDraw.circle( x, y, r );    
    }
    
}

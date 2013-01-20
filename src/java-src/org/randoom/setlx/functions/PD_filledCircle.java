package org.randoom.setlx.functions;


import org.randoom.setlx.utilities.StdDraw;

//square(NumberValue,NumberValue,NumberValue) : 
//
public class PD_filledCircle extends StdDrawXYRFunction{
    public final static PreDefinedFunction DEFINITION = new PD_filledCircle();

    private PD_filledCircle() {
        super("filledCircle");
    }
    
    protected void executeStdDrawFunction(Double x, Double y, Double r){
        StdDraw.filledCircle( x, y, r );    
    }
    
}

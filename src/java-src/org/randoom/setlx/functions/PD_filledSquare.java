package org.randoom.setlx.functions;


import org.randoom.setlx.utilities.StdDraw;

public class PD_filledSquare extends StdDrawXYRFunction {
    public final static PreDefinedFunction DEFINITION = new PD_filledSquare();
    
    public PD_filledSquare(){
        super("filledSquare");
    }
    
    
    protected void executeStdDrawFunction(Double x, Double y, Double r){
        StdDraw.filledSquare( x, y, r );    
    }
}

package org.randoom.setlx.functions;


import org.randoom.setlx.utilities.StdDraw;

//square(NumberValue,NumberValue,NumberValue) : 
//
public class PD_square extends StdDrawXYRFunction{
	public final static PreDefinedFunction DEFINITION = new PD_square();

	private PD_square() {
	    super("square");
	}
	
	protected void executeStdDrawFunction(Double x, Double y, Double r){
	    StdDraw.square( x, y, r );    
	}
	
}

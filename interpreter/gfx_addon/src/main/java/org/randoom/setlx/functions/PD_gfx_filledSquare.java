package org.randoom.setlx.functions;

import org.randoom.setlx.gfx.utilities.StdDraw;

public class PD_gfx_filledSquare extends GfxXYRFunction {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_filledSquare();

    public PD_gfx_filledSquare(){
        super();
    }

    @Override
    protected void executeStdDrawFunction(final Double x, final Double y, final Double r){
        StdDraw.filledSquare( x, y, r );
    }
}

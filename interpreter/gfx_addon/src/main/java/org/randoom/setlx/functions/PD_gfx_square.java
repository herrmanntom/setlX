package org.randoom.setlx.functions;

import org.randoom.setlx.gfx.utilities.StdDraw;

public class PD_gfx_square extends GfxXYRFunction{
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_square();

    private PD_gfx_square() {
        super();
    }

    @Override
    protected void executeStdDrawFunction(final Double x, final Double y, final Double r){
        StdDraw.square( x, y, r );
    }

}

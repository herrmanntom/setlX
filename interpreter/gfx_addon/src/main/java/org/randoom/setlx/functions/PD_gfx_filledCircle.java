package org.randoom.setlx.functions;

import org.randoom.setlx.gfx.utilities.StdDraw;

public class PD_gfx_filledCircle extends GfxXYRFunction{
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_filledCircle();

    private PD_gfx_filledCircle() {
        super();
    }

    @Override
    protected void executeStdDrawFunction(final Double x, final Double y, final Double r){
        StdDraw.filledCircle( x, y, r );
    }

}

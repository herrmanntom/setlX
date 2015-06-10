package org.randoom.setlx.utilities;

/**
 * Created by arne on 10.06.15.
 */
public class FrameWrapper {

    public AbstractFrame getFrame() {
        return frame;
    }

    public void setFrame(AbstractFrame frame) {
        this.frame = frame;
    }

    private AbstractFrame frame;


    public static final int DRAW_FRAME = 1;
    public static final int BAR_FRAME = 2;
    public static final int PIE_FRAME = 3;
    public static final int BOX_FRAME = 4;
    public static final int VIRGIN_FRAME = 0;

    public int getFrameType() {
        return this.frameType;
    }

    public void setFrameType(int frameType) {
        this.frameType = frameType;
    }

    private int frameType;

    public int hashCode(){
        return frame.hashCode();
    }

}

package org.randoom.setlx.plot.utilities;

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



    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
    private double height = 460;

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    private double width = 640;

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
        return frame.hashCode() + 32* ((Double) height).intValue() + 32*((Double) width).intValue() ;
    }

}

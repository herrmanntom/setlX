package org.randoom.setlx.utilities;


import org.randoom.setlx.types.Value;

import javax.swing.*;


public class Canvas extends Value {
    public void setFrame(AbstractFrame frame) {
        this.frame = frame;
    }

    private AbstractFrame frame;
    public static final int DRAW_FRAME = 1;
    public static final int BAR_FRAME = 2;
    public static final int PIE_FRAME = 3;
    public static final int BOX_FRAME = 4;
    public static final int VIRGIN_FRAME = 0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;


    public int getFrameType() {
        return this.frameType;
    }

    public void setFrameType(int frameType) {
        this.frameType = frameType;
    }

    private int frameType = VIRGIN_FRAME;

    public AbstractFrame getFrame(){
        return this.frame;
    }


    @Override
    public Value clone() {
        Canvas result = new Canvas();
        result.setFrameType(this.frameType);
        result.setTitle(this.title);
        result.setFrame(this.frame);
        return result;
    }

    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {
    }

    @Override
    public int compareTo(CodeFragment other) {
        return 0;
    }

    @Override
    public long compareToOrdering() {
        return 0;
    }

    @Override
    public int hashCode() {
        int result = (title != null ? title.hashCode() : 0);
        result = 36 * result + (frameType);
        result = 36 * result + (frame != null ? frame.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equalTo(Object o) {
        if (this == o) return true;
        if (!(o instanceof Graph)) return false;

        Canvas c = (Canvas) o;

        if(this.title != c.getTitle()){ return false;}
        if(this.frame != c.getFrame()){return false;}
        return (this.frameType == c.getFrameType());

    }
}

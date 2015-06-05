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

    public Canvas(AbstractFrame frame){
        this.frame = frame;
    }


    public AbstractFrame getFrame(){
        return this.frame;
    }


    @Override
    public Value clone() {
        return new Canvas(this.frame);
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
        if(frame != null) {
            return 37*frame.hashCode();
        }else{
            return 0;
        }
    }

    @Override
    public boolean equalTo(Object other) {
        //equal defined as the same class
        return other.getClass() == this.getClass();
    }
}

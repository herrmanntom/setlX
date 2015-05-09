package org.randoom.setlx.utilities;


import org.randoom.setlx.types.Value;

import javax.swing.*;

public class Canvas extends Value {
    private DrawFrame frame;

    public Canvas(DrawFrame frame){
        this.frame = frame;
    }


    public DrawFrame getFrame(){
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
        return 0;
    }

    @Override
    public boolean equalTo(Object other) {
        return other.getClass() == this.getClass();
    }
}

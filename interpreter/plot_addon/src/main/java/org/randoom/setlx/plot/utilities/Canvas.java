package org.randoom.setlx.plot.utilities;


import org.randoom.setlx.plot.types.Graph;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.Value;


public class Canvas extends Value {

    private FrameWrapper frame;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Canvas(FrameWrapper frame){
        this.frame = frame;
    }
    private String title;


    public FrameWrapper getFrame(){
        return this.frame;
    }


    @Override
    public Value clone() {
        Canvas result = new Canvas(this.frame);
        result.setTitle(this.title);
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
        result = 36 * result + (frame != null ? frame.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equalTo(Object o) {
        if (this == o) return true;
        if (!(o instanceof Graph)) return false;

        Canvas c = (Canvas) o;

        if(this.title != c.getTitle()){ return false;}
        return (this.frame == c.getFrame());

    }
}

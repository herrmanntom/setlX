package org.randoom.setlx.plot.types;

import org.randoom.setlx.plot.utilities.FrameWrapper;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.Objects;

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
        if (other.getClass() == Canvas.class) {
            return title.compareTo(((Canvas) other).title);
        }  else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Canvas.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Canvas canvas = (Canvas) o;
        return Objects.equals(frame, canvas.frame) &&
               Objects.equals(title, canvas.title);
    }

    @Override
    public int hashCode() {
        int result = (title != null ? title.hashCode() : 0);
        result = 36 * result + (frame != null ? frame.hashCode() : 0);
        return result;
    }
}

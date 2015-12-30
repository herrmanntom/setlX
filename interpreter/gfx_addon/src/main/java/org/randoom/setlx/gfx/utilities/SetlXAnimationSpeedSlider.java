package org.randoom.setlx.gfx.utilities;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SetlXAnimationSpeedSlider extends JSlider implements ChangeListener {

    private static final long serialVersionUID = 129749679538437133L;

    private Double animationSpeedMultiplier;

    private Double stepNumber;

    public SetlXAnimationSpeedSlider(){
        this(-2,4,0,2.0);
    }

    private SetlXAnimationSpeedSlider( final int from, final int to, final int value, final Double steps ){
        super(from,to);
        setValue(value);
        setMajorTickSpacing(1);
        setPaintTicks(true);
        setPaintLabels(true);
        setVisible(true);
        addChangeListener(this);
        setStepNumber(steps);
        setSpeedMultiplier();
        setLabelTable(getLabelDictionary());
    }

    private  Dictionary<Integer,JLabel> getLabelDictionary(){
        final HashMap<Integer,JLabel> d = new HashMap<Integer,JLabel>();
        for( int i = getMinimum(); i <= getMaximum(); i++ ){
            d.put(i, new JLabel( getSpeedMultiplierLabel( i )));
        }
        return new Hashtable<Integer,JLabel>(d);
    }


    public Double getSpeedMultiplier(){
        return animationSpeedMultiplier;
    }

    public void setStepNumber( final Double steps){
        stepNumber = steps;
    }


    private void setSpeedMultiplier(){
        animationSpeedMultiplier = 1 / Math.pow(stepNumber, getValue());
    }

    private String getSpeedMultiplierLabel( final int step ){
        Double result = Math.pow(stepNumber, step);
        result        = Math.round(result*1000.0) / 1000.0;
        return result + "x";
    }

    /**
     * Added for setlX
     */
    @Override
    public void stateChanged(final ChangeEvent arg0) {
        setSpeedMultiplier();
        StdDraw.interruptWaitingThread();
    }
}

package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * logo() : prints the setlX logo
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *                             CHEATING IS BAD!
 *                             CHEATING IS BAD!
 *                             CHEATING IS BAD!
 *                             CHEATING IS BAD!
 *
 *
 *
 *
 *
 *
 *
 *                  Don't look at the source, execute it!
 *
 *
 *
 *
 *
 *
 *
 *
 *                             CHEATING IS BAD!
 *                             CHEATING IS BAD!
 *                             CHEATING IS BAD!
 *                             CHEATING IS BAD!
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class PD_logo extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `logo'. */
    public  final static PreDefinedProcedure DEFINITION  = new PD_logo();

    private       static int    count    = 0;
    private final static String LOGO_BIG = "\n" +
    "                                                                    " + "\n" +
    "        .mO0C0O0M0P0U0T0E0R0S000M0A0K0E000V0E0R0Y000F0A0S0T0Om.     " + "\n" +
    "       .OOOOOOOOOOOOOOOOOOOOOO0S0E0T0L0X0OOOOOOOOOOOOOOOOOOOOOO.    " + "\n" +
    "       OOO'                                                 'OOO    " + "\n" +
    "       OOO                                                   OOO    " + "\n" +
    "       OOO                      mW.       Wm                 OOO    " + "\n" +
    "       OOO                       YWw.     'WW.               OOO    " + "\n" +
    "       OOO                        YWWi     .WWw              OOO    " + "\n" +
    "       OOO         _____           YWWw.   W 'W              OOO    " + "\n" +
    "       OOO      .wWWWWWWWwa.      iW 'WWw, W                 OOO    " + "\n" +
    "       OOO     .W.      'WWWi    [WW   'WWwW      .aaaa.     OOO    " + "\n" +
    "     .OOO'    .WWW.      'WWW    [WW     'WWw    .W    W.    'OOO.  " + "\n" +
    "   .aOOOY     'WWW'       WWWi    YWw.     'Ww.  WW    WW     YOOOa." + "\n" +
    "   OOOO{                 iWWW      WWW       Ww  WW    WW      }OOOO" + "\n" +
    "   'YOOOo               .WWW'   -wWW'         Y  'W    W'     oOOOY'" + "\n" +
    "     'OOO.             sWWW                       'aaaa'     .OOO'  " + "\n" +
    "       OOO           .yWW/                                   OOO    " + "\n" +
    "       OOO         .wWW/                                     OOO    " + "\n" +
    "       OOO        sWW/                                       OOO    " + "\n" +
    "       OOO      .dW/      _.M                                OOO    " + "\n" +
    "       OOO    .wWWWWWWWWWWWWM                                OOO    " + "\n" +
    "       OOO    ```````````````                                OOO    " + "\n" +
    "       OOO                                                   OOO    " + "\n" +
    "       OOO.                                                 .OOO    " + "\n" +
    "       'OOOOOOOOOOOOOOOOOOOOOO0S0E0T0L0X0OOOOOOOOOOOOOOOOOOOOOO'    " + "\n" +
    "        'YOOO0V0E0R0Y000A0C0C0U0R0A0T0E000M0I0S0T0A0K0E0S0OOOY'     " + "\n" +
    "                                                                    " + "\n";

    private final static String             LOGO_SMALL  = "\n" +
    "                                                   " + "\n" +
    "     .oooooooooooooooooooooooooooooooooooooooo.    " + "\n" +
    "     OOOOOOOOOOOOOOO0S0E0T0L0X0OOOOOOOOOOOOOOOO    " + "\n" +
    "    |OO'                                    'OO|   " + "\n" +
    "    |OO                 mm     m.            OO|   " + "\n" +
    "    |OO                 'Ww    'Ww.          OO|   " + "\n" +
    "    |OO                  -Wwc   'Ww.         OO|   " + "\n" +
    "    |OO     .wWWWWWw     .W Ww. W 'Y         OO|   " + "\n" +
    "   .OOO    wW     'Ww.   WW  YW.d    .wWw,   OOO.  " + "\n" +
    " .oOOY'   wWWw     WWw   WW,  'Yw.   W' 'W   'YOOo." + "\n" +
    " 'YOOo.   'WW'     WWY    WWm  'Yw   W   W   .oOOY'" + "\n" +
    "   'OOO           iWY   -mY'     W   W. .W   OOO'  " + "\n" +
    "    |OO         .wW'                  YWY    OO|   " + "\n" +
    "    |OO       .wW'                           OO|   " + "\n" +
    "    |OO     .wW'    .M                       OO|   " + "\n" +
    "    |OO    mWWWWWWWWWM                       OO|   " + "\n" +
    "    |OO                                      OO|   " + "\n" +
    "     OOOooooooooooooooooooooooooooooooooooooOOO    " + "\n" +
    "     'OOOOOOOOOOOOOO0S0E0T0L0X0OOOOOOOOOOOOOOO'    " + "\n" +
    "                                                   " + "\n";

    private final static String[]           COINS       = {"Penny", "Nickel", "Dime", "Quarter"};

    private PD_logo() {
        super();
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws StopExecutionException {
        String  logo;
        int     payUp = 0;
        while(payUp <= 0 && payUp > -3 && count < 3) {
            try {
                payUp--;
                state.prompt("Insert USD-Coin: ");
                String input = state.inReadLine();
                if (input != null) {
                    input = input.trim();
                } else {
                    continue;
                }
                for (int i = 0; i < COINS.length; ++i) {
                    if (input.equalsIgnoreCase(COINS[i])) {
                        payUp = i + 1;
                        break;
                    }
                }
            } catch (final JVMIOException ioe) { /* who cares? */}
        }
        if (payUp <= 0) {
            if (++count == 3) {
                state.outWriteLn("You got 2 pennies already and won't let one go? You are an ass.");
            } else if (count > 3) {
                state.outWriteLn("You are an ass.");
            } else if (count > 1) {
                state.outWriteLn("Too bad... here's another `penny' for your thoughts.");
            } else {
                state.outWriteLn("Too bad... here's a `penny' for your thoughts.");
            }
            return Rational.ZERO;
        } else if (payUp == 1) {
            state.outWriteLn("  ...cheap bastard...  ");
            logo = LOGO_SMALL;
        } else if (payUp == 2) {
            state.outWriteLn("  ...well... never mind...  ");
            logo = LOGO_SMALL;
        }  else if (payUp == 3) {
            state.outWriteLn("  ...you can do one better...  ");
            logo = LOGO_BIG;
        } else {
            state.outWriteLn("Thank you!");
            logo = LOGO_BIG;
        }

        count = 0;
        int timeSum = 0;
        for (int i = 0; i < logo.length(); i++) {
            state.outWrite("" + logo.charAt(i));
            try {
                int time = (i / (10 * payUp)) +1;
                if (time > 125) {
                    time = 125;
                }
                time = state.getRandomInt(time);
                timeSum += time;
                Thread.sleep(time);
            } catch (final InterruptedException ie) {
                throw new StopExecutionException();
            }
        }

        state.outWriteLn("Please come again.");

        return Rational.valueOf(timeSum);
    }
}


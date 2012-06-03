package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// logo()                        : prints the setlX logo



/*                            CHEATING IS BAD!                                */
/*                            CHEATING IS BAD!                                */
/*                            CHEATING IS BAD!                                */
/*                            CHEATING IS BAD!                                */
/*                            CHEATING IS BAD!                                */
/*                            CHEATING IS BAD!                                */
/*                            CHEATING IS BAD!                                */
/*                            CHEATING IS BAD!                                */
/*                            CHEATING IS BAD!                                */



public class PD_logo extends PreDefinedFunction {
    public  final static PreDefinedFunction DEFINITION  = new PD_logo();
    private final static String[]           COINS       = {"Penny", "Nickel", "Dime", "Quarter"};
    private final static String             LOGO_BIG    = "\n" +
    "                                                                    " + "\n" +
    "        .mO0C0O0M0P0U0T0E0R0S000M0A0K0E000V0E0R0Y000F0A0S0T0om.     " + "\n" +
    "       .OOOOOOOOOOOOOOOOOOOOOO0S0E0T0L0X0OOOOOOOOOOOOOOOOOOOOOO.    " + "\n" +
    "       OOO'                                                 'OOO    " + "\n" +
    "       OOO                                                   OOO    " + "\n" +
    "       OOO                      =W,       dg.                OOO    " + "\n" +
    "       OOO                       4Wc.     'WWc               OOO    " + "\n" +
    "       OOO                        ?$mi     .WW6              OOO    " + "\n" +
    "       OOO         _____           jWWg,   W 'W              OOO    " + "\n" +
    "       OOO      .wWWWWWWWwa.      jW -WWw, W                 OOO    " + "\n" +
    "       OOO     jW       -WWWi    [Wf   -WWwm      .aaaa.     OOO    " + "\n" +
    "     .OOO'    .WWw.      )WWW    [Wk     'WWc    ,W    W,    'OOO.  " + "\n" +
    "   .aOOOP     'WWW'      .WWW;    ?Wg,     'Wm,  WW    WW     $OOOa." + "\n" +
    "   OOOO{        *        jWWW      WWW       WW  WW    WW      }OOOO" + "\n" +
    "   'YOOO,               _WWW`   -'??'`        ^  '@    @'     $OOOY'" + "\n" +
    "     'OOO.             sWWW                       'aaaa'     .OOO'  " + "\n" +
    "       OOO           _yWW/                                   OOO    " + "\n" +
    "       OOO         .wWW/                                     OOO    " + "\n" +
    "       OOO        sWW/                                       OOO    " + "\n" +
    "       OOO      _dW/      _.M                                OOO    " + "\n" +
    "       OOO    _wWWWWWWWWWWWWM                                OOO    " + "\n" +
    "       OOO    ''''''''''''''`                                OOO    " + "\n" +
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
    "    |OO                  -Wwc   'WWw         OO|   " + "\n" +
    "    |OO     .wWWWWWw     .W Ww. W 'Y         OO|   " + "\n" +
    "    |OO    wW     'Ww.   WW  YW.d    .wWw,   OO|   " + "\n" +
    "   .OOO   wWWw     WWw   WW,  'Yw.   W' 'W   OOO.  " + "\n" +
    " .oOOY'   'WWW     WWY    WWm  'Yw   W   W   'YOOo." + "\n" +
    " 'YOOo.           iWY   -mY'     W   W. .W   .oOOY'" + "\n" +
    "   'OOO         .wW'                  YWY    OOO'  " + "\n" +
    "    |OO       .wW'                           OO|   " + "\n" +
    "    |OO     .wW'    .M                       OO|   " + "\n" +
    "    |OO    mWWWWWWWWWM                       OO|   " + "\n" +
    "    |OO   ''''''''''''                       OO|   " + "\n" +
    "    |OO                                      OO|   " + "\n" +
    "     OOOooooooooooooooooooooooooooooooooooooOOO    " + "\n" +
    "     'OOOOOOOOOOOOOO0S0E0T0L0X0OOOOOOOOOOOOOOO'    " + "\n" +
    "                                                   " + "\n";

    private PD_logo() {
        super("logo");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        String  logo  = LOGO_SMALL;
        int     payUp = 0;
        while(payUp <= 0 && payUp > -3) {
            try {
                payUp--;
                Environment.prompt("Insert USD-Coin: ");
                String input = Environment.inReadLine();
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
            } catch (JVMIOException ioe) { /* who cares? */}
        }
        if (payUp <= 0) {
            Environment.outWriteLn("Too bad... here a `penny' for your thoughts.");
            return new Rational(0);
        } else if (payUp == 1) {
            Environment.outWriteLn("  ...cheap bastard...  ");
            logo = LOGO_SMALL;
        } else if (payUp == 2) {
            Environment.outWriteLn("  ...well... never mind...  ");
            logo = LOGO_SMALL;
        }  else if (payUp == 3) {
            Environment.outWriteLn("  ...you can to one better...  ");
            logo = LOGO_BIG;
        } else {
            Environment.outWriteLn("Thank You!");
            logo = LOGO_BIG;
        }

        int timeSum = 0;
        for (int i = 0; i < logo.length(); i++) {
            Environment.outWrite("" + logo.charAt(i));
            try {
                int time = (i / (10 * payUp)) +1;
                if (time > 125) {
                    time = 125;
                }
                time = Environment.getRandomInt(time);
                timeSum += time;
                Thread.sleep(time);
            } catch (InterruptedException ie) { /* who cares? */}
        }

        Environment.outWriteLn("Please come again.");

        return new Rational(timeSum);
    }
}


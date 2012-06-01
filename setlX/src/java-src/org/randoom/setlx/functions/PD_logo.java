package org.randoom.setlx.functions;

import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// logo()                        : prints the setlX logo

public class PD_logo extends PreDefinedFunction {
    public  final static PreDefinedFunction DEFINITION  = new PD_logo();
    private final static String             DATA        = "\n" +
    "                                                                                " + "\n" +
    "          _aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,.         " + "\n" +
    "        sQQQWWQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQWQQa        " + "\n" +
    "       ]WQQQQWBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBWWQQQQc       " + "\n" +
    "       mQQW!                                                        'QQQQ.      " + "\n" +
    "      :QQQF                                                          ]QQQ;      " + "\n" +
    "      )WQQ[                                                          )QQQ[      " + "\n" +
    "      )WQQ(                                                          .QQQ[      " + "\n" +
    "      )WQQ(                          m,         mc                   .QQQ[      " + "\n" +
    "      )WQQ(                          QQ/        $Qw.                 .QQQ[      " + "\n" +
    "      )WQQ(                          -4Qm,       ?QQw                .QQQ[      " + "\n" +
    "      )WQQ(                            -$Qg,      Q?W[               .QQQ[      " + "\n" +
    "      )WQQ(         _aaawaa,           vW'$Qw,   ]F '`               .QQQ[      " + "\n" +
    "      ]WQQ;      .w2?^---'?QQg/       <Q[  'WQw. j(                   QQQf      " + "\n" +
    "      jQQQ      _Q^        -$QQw      QQ`    ?$QaQ       _a~~<a       QQQh      " + "\n" +
    "     _QQQD      mmwa        )WQQL    .QQ/     -?QQc     ]@    )m      4QQQ/     " + "\n" +
    "  _saQQQQ'      QQQQ(        QQQm     )WQa      -?Qm,   Qf    :Q[     +QQQQw,,  " + "\n" +
    "_mQWWWQP'       -??^        _QQQF      +QQm       -4Q/  Qf    :Qf      '9QQWQQm/" + "\n" +
    ")QQQQQma,                   yQQ@`    ssuBV?         H'  $k    :Q(       ayQQQQQ[" + "\n" +
    " -!?9QQQQ,                .yQQ?                         -9c. _J!      .mQQQD??` " + "\n" +
    "     )QQWk               _mQT'                            -''^.       jQQQf     " + "\n" +
    "      4QQQ             _jWT`                                          QQQ@      " + "\n" +
    "      ]QQQ;          .a@!       .                                     QQQf      " + "\n" +
    "      )QQQ;         sZ!        ]k                                    .QQQ[      " + "\n" +
    "      )WQQ(       _Z!        ..y[                                    .QQQ[      " + "\n" +
    "      )WQQ(     _yQQQWQQQQQQQQQQ                                     .QQQ[      " + "\n" +
    "      )WQQ(     TTTTTTTTTTTTTTTY                                     .QQQ[      " + "\n" +
    "      )WQQ(                                                          .QQQ[      " + "\n" +
    "      )WQQ(                                                          .QQQ[      " + "\n" +
    "      )WQQ[                                                          :QQQ[      " + "\n" +
    "      :QQQL                                                          ]QQQ(      " + "\n" +
    "       QQQQ,                                                        _mQQQ`      " + "\n" +
    "       ]QQQQmywwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwywwwymQQQQF       " + "\n" +
    "        4QQQQQWQQQQQQQWQQWQQWQQWQQWQQQQQWQWQWQWWWWQQQQQQQQQQQQQQQQWQQQQP`       " + "\n" +
    "         ~??VHW#W#WBBBW#W#W#WBBW#W#W#W#W#W#WBBW#WBBW#WBBW#W#W#WBW#BVT!^         " + "\n" +
    "                                                                                " + "\n";

    private PD_logo() {
        super("logo");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        for (int i = 0; i < DATA.length(); i++) {
            Environment.outWrite("" + DATA.charAt(i));
            try {
                Thread.sleep(2);
            } catch (InterruptedException ie) { /* who cares? */}
        }
        return new Rational(42);
    }
}


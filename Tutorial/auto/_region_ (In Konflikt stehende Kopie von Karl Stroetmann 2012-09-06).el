(TeX-add-style-hook "_region_"
 (lambda ()
    (LaTeX-add-environments
     "Definition"
     "Notation"
     "Korollar"
     "Lemma"
     "Satz"
     "Theorem")
    (LaTeX-add-labels
     "fig:banner"
     "fig:sqrt.stlx"
     "fig:primes-slim.stlx"
     "fig:lambda.stlx"
     "fig:toBin.stlx"
     "fig:sort3.stlx"
     "fig:sort3switch.stlx"
     "fig:ulam.stlx"
     "fig:break-and-continue.stlx")
    (TeX-add-symbols
     '("cmd" 1)
     '("quoted" 1)
     '("squoted" 1)
     "chess"
     "bigchess"
     "chf"
     "club"
     "spade"
     "heart"
     "diamo"
     "example"
     "remark"
     "exercise"
     "qed"
     "exend"
     "setlx"
     "pair")
    (TeX-run-style-hooks
     "alltt"
     "fancyvrb"
     "wasysym"
     "amssymb"
     "a4wide"
     "inputenc"
     "latin1"
     "epsfig"
     "fleqn"
     "latex2e"
     "rep10"
     "report")))


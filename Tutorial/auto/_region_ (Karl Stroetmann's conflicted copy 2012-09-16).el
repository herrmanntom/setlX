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
     "fig:primes-slim.stlx"
     "fig:lambda.stlx"
     "fig:fibonacci.stlx"
     "fig:fibonacci-trace.stlx"
     "fig:fibonacci.trace"
     "fig:fibonacci-cached.stlx"
     "fig:toBin.stlx"
     "fig:sort3.stlx"
     "fig:sort3switch.stlx"
     "fig:reverse.stlx"
     "fig:reverse-long.stlx"
     "fig:reverse-pairs.stlx"
     "fig:set-sort.stlx"
     "fig:binary-tree.stlx"
     "fig:diff.stlx"
     "fig:ulam.stlx"
     "fig:break-and-continue.stlx"
     "fig:multiplication-table.stlx"
     "fig:multiplication-table"
     "fig:relational-product-for.stlx"
     "fig:relational-product.stlx"
     "fig:scope-for-loop.stlx")
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


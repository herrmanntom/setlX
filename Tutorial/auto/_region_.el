(TeX-add-style-hook "_region_"
 (lambda ()
    (LaTeX-add-environments
     "Definition"
     "Notation"
     "Korollar"
     "Lemma"
     "Satz"
     "Theorem")
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
     "hypcap"
     "all"
     "hyperref"
     "alltt"
     "fancyhdr"
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
     "report"
     "twoside")))


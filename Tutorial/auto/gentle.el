(TeX-add-style-hook "gentle"
 (lambda ()
    (LaTeX-add-bibliographies
     "/Users/karldrstroetmann/Dropbox/Kurse/cs")
    (LaTeX-add-environments
     "Definition"
     "Notation"
     "Korollar"
     "Lemma"
     "Satz"
     "Theorem")
    (TeX-add-symbols
     '("quoted" 1)
     "chess"
     "bigchess"
     "chf"
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
     "art10"
     "article"
     "introduction")))


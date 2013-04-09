pdf: tutorial.pdf

%.pdf: %.ps
	ps2pdf $<

%.ps:  %.dvi
	dvips -t a4 -o $@ $<

%.dvi: tutorial.tex backtracking.tex basics.tex closures.tex introduction.tex \
       oo.tex predefined-functions.tex regular-expressions.tex statements.tex \
       tutorial.tex types.tex
	latex $<
	if [ -f $@ ]; then latex $< ; fi
	if [ -f $@ ]; then latex $< ; fi

clean:
	rm -f *.?00pk *.aux *.blg *.dvi *.lof *.log *.out *.lof *.toc

dist-clean: clean
	rm -f *.pdf

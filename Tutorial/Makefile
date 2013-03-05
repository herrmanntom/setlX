pdf: tutorial.pdf

%.pdf: %.ps
	ps2pdf $<

%.ps:  %.dvi
	dvips -t a4 -o $@ $<

%.dvi: %.tex
	latex $<
	if [ -f $@ ]; then latex $< ; fi
	if [ -f $@ ]; then latex $< ; fi

clean:
	rm -f *.?00pk *.aux *.blg *.dvi *.lof *.log *.out *.lof *.toc

dist-clean: clean
	rm -f *.pdf

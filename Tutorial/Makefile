pdf: tutorial.pdf

%.pdf: %.ps
	ps2pdf $<

%.ps:  %.dvi
	dvips -o $@ $<

%.dvi: %.tex
	latex $<

clean:
	rm -f *.aux *.dvi *.lof *.log *.out *.lof *.toc

dist-clean: clean
	rm -f *.pdf

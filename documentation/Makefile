pdf: developmentGuide.pdf manual.pdf

%.pdf: %.dvi
	dvipdf $<

%.dvi: %.tex
	latex $<
	if [ -f $@ ]; then latex $< ; fi

clean:
	rm -f   *.aux   *.dvi   *.lof   *.log   *.out   *.lof   *.toc
	rm -f */*.aux */*.dvi */*.lof */*.log */*.out */*.lof */*.toc

dist-clean: clean
	rm -f *.pdf


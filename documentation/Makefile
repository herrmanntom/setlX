pdf: developmentGuide.pdf manual.pdf

developmentGuide.pdf: developmentGuide.tex version.tex
	latex developmentGuide.tex
	if [ -f developmentGuide.dvi ]; then latex developmentGuide.tex; fi
	dvipdf developmentGuide.dvi

manual.pdf: manual.tex version.tex
	latex manual.tex
	if [ -f manual.dvi ]; then latex manual.tex; fi
	dvipdf manual.dvi

clean:
	rm -f   *.aux   *.dvi   *.lof   *.log   *.out   *.lof   *.toc
	rm -f */*.aux */*.dvi */*.lof */*.log */*.out */*.lof */*.toc

dist-clean: clean
	rm -f *.pdf


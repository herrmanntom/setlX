pdf: developmentGuide.pdf manual.pdf

developmentGuide.pdf: developmentGuide.tex version.tex
	latex developmentGuide.tex
	latex developmentGuide.tex 1>/dev/null 2>&1
	dvipdf developmentGuide.dvi

manual.pdf: manual.tex version.tex
	latex manual.tex
	latex manual.tex 1>/dev/null 2>&1
	dvipdf manual.dvi

clean:
	rm -f   *.aux   *.dvi   *.lof   *.log   *.out   *.lof   *.toc
	rm -f */*.aux */*.dvi */*.lof */*.log */*.out */*.lof */*.toc

dist-clean: clean
	rm -f *.pdf


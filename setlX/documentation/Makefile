pdf: developmentGuide.pdf manual.pdf

developmentGuide.pdf: developmentGuide.tex version.tex
	latex developmentGuide.tex 1>/dev/null 2>&1
	latex developmentGuide.tex
	dvipdf developmentGuide.dvi

manual.pdf: manual.tex version.tex
	latex manual.tex 1>/dev/null 2>&1
	latex manual.tex
	dvipdf manual.dvi

clean:
	rm -f   *.aux   *.dvi   *.lof   *.log   *.out   *.lof   *.toc
	rm -f */*.aux */*.dvi */*.lof */*.log */*.out */*.lof */*.toc

dist-clean: clean
	rm -f *.pdf


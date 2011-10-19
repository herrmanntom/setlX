pdf: developmentGuide.pdf manual.pdf

developmentGuide.pdf: developmentGuide.tex version.tex
	pdflatex developmentGuide.tex 1>/dev/null 2>&1
	pdflatex developmentGuide.tex

manual.pdf: manual.tex version.tex
	pdflatex manual.tex 1>/dev/null 2>&1
	pdflatex manual.tex

clean:
	rm -f   *.aux   *.lof   *.log   *.out   *.lof   *.toc
	rm -f */*.aux */*.lof */*.log */*.out */*.lof */*.toc

dist-clean: clean
	rm -f *.pdf


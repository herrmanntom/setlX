To add SetlX syntax highlighting in gedit or any other application using GtkSourceView,
copy the setlx.lang file into the same folder where the file "def.lang" is located on
on your system:

$ locate def.lang

usually the path is something like:

$ su
$ cp setlx.lang /usr/share/gtksourceview-3.0/language-specs/
$ chmod 644 /usr/share/gtksourceview-3.0/language-specs/setlx.lang



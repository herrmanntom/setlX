To adapt emacs c-mode in a way that it accepts SetlX programs, we have to set the
variable 

           c-recognize-colon-labels to nil.  

This is a buffer local variable.  One way to achieve this is to load the provided
file "c-mode-hook" into your emacs session.  Of course, when doing so you have
to edit the path to the file "cc-mode.el" that is used in the call to "load-file".

The easiest way to load c-mode-hook into your emacs seesion is by adding the line

(load-file "c-mode-hook.el")

to the  ".emacs" file that is in your home directory.  In this case, the file "c-mode-hook.el"
has to be copied to your home directory.  Of course, you can copy this file to any place you
like, but then you have to provide the full path to this file in the line above.

Another way to achive the same effect is to copy the content of the file c-mode-hook into your
".emacs" file.

;;; EDIT NEXT LINE!!!
(load-file "/sw/lib/xemacs/xemacs-packages/lisp/cc-mode/cc-mode.el") ; path has to be adapted
;;; EDIT PREVIOUS LINE!!!

(setq c-mode-hook
      '(lambda ()
            (c-set-style "bsd")
            (set-variable 'c-basic-offset 4)
            (set-variable 'indent-tabs-mode nil)
            (set-variable 'c-cleanup-list '(scope-operator empty-defun-braces 
                                                           defun-close-semi list-close-comma))
            (setq tab-width 4)
            (set-variable 'c-echo-syntactic-information-p t)
            (c-set-offset 'inline-open 0)
            (c-set-offset 'innamespace 0)
	    (set-variable 'c-recognize-colon-labels nil) ; ignore colons so SetlX programs are indented nicely
       )
)

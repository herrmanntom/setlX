DISTRIBUTION-README - SomeSortOfSetl2 Interpreter                        v1.0.0
================================================================================

Three different 'distributions' can be automatically created:

a)  A binary only distribution, which should work on all Unix-like OS and Windows
    without the need of the full Java JDK.
    Any Java Runtime (JRE) installation which is newer or the same major version
    as the JDK which created the distribution should work.
    This distribution does not include any Setl2 code.

b)  A source distribution, which must be rebuild by the user, which is automatically
    done on first launch. A Java JDK has to be installed for this to work.
    The version of the users JDK can be lower or even incompatible to the JDK
    which created the distribution.
    This distribution also does not include Setl2 code, except a simple program
    to test the interpreter (see 'make test' in Readme.txt).

c)  The development kit, which includes everything used in the development of
    the interpreter itself.
    This distribution includes various Setl2 example programs and the grammar of the
    interpreter in both 'EBNF' and 'pure' form.

Build distributable zip-files (only on Unix-like OS):
    The automated distribution creator script can be launched by executing
        ./createDistributions

    Note that this process will 'clean' the source (e.g. remove all automatically
    created files), rebuild the interpreter and clean the source again.


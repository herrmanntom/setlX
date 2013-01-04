@ECHO off

IF EXIST java-src (
    echo "Building the setlX interpreter ..."
    IF EXIST bin (
        rmdir /S /Q bin
    )
    IF EXIST java-src\org\randoom\setlx\grammar\*.* (
        del /F /Q java-src\org\randoom\setlx\grammar\*.*
    )
    IF EXIST setlX.jar (
        del /F /Q setlX.jar
    )
    java  -cp antlr\antlr-*.jar org.antlr.Tool -fo java-src\org\randoom\setlx\grammar grammar\SetlXgrammar.g
    mkdir bin
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlx\exceptions\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlx\types\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlx\utilities\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlx\expressionUtilities\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlx\boolExpressions\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlx\expressions\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlx\statements\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlx\grammar\SetlXgrammar*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlx\functions\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\org\randoom\setlxUI\pc\SetlX.java
    cd bin
    jar xf ..\antlr\antlr-*.jar org/antlr/runtime
    cd ..
    jar cmf java-src\MANIFEST.MF setlX.jar -C bin\ .
    rmdir /S /Q bin
    del /F /Q java-src\org\randoom\setlx\grammar\*.*
    echo "The setlX interpreter was (hopefully correctly) build."
) ELSE (
    echo "No source available -- the setlX interpreter can not be build."
)

echo.
pause

@ECHO on

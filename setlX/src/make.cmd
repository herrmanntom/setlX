@ECHO off

IF EXIST java-src (
    echo "Building the setlX interpreter ..."
    IF EXIST bin (
        rmdir /S /Q bin
    )
    IF EXIST java-src\grammar\*.java (
        del /F /Q java-src\grammar\*.java
        del /F /Q java-src\grammar\*.tokens
    )
    IF EXIST setlX.jar (
        del /F /Q setlX.jar
    )
    java  -cp antlr\antlr-*.jar org.antlr.Tool -fo java-src\grammar java-src\grammar\SetlXgrammar.g
    mkdir bin
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\comparableSet\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\interpreter\exceptions\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\interpreter\types\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\interpreter\utilities\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\interpreter\functions\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\interpreter\boolExpressions\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\interpreter\expressions\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\interpreter\statements\*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\grammar\SetlXgrammar*.java
    javac -cp antlr\antlr-*.jar -d bin -sourcepath java-src java-src\SetlX.java
    cd bin
    jar xf ..\antlr\antlr-*.jar org/antlr/runtime
    cd ..
    jar cmf java-src\MANIFEST.MF setlX.jar -C bin\ .
    rmdir /S /Q bin
    del /F /Q java-src\grammar\*.java
    del /F /Q java-src\grammar\*.tokens
    echo "The setlX interpreter was (hopefully correctly) build."
) ELSE (
    echo "No source available -- the setlX interpreter can not be build."
)

echo.
pause

@ECHO on

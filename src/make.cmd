@ECHO off

set antlrJarLocation=antlr\antlr-4.0-complete.jar
set CLASSPATH="%antlrJarLocation%;%CLASSPATH%"

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
    java  -cp "%CLASSPATH%" org.antlr.v4.Tool -no-listener -Werror -o java-src\org\randoom\setlx -package org.randoom.setlx.grammar grammar\SetlXgrammar.g
    mkdir bin
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\exceptions\*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\types\*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\utilities\*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\expressionUtilities\*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\boolExpressions\*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\expressions\*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\statements\*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\grammar\SetlXgrammar*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\functions\*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlxUI\pc\SetlX.java
    cd bin
    jar xf "..\%antlrJarLocation%" org/antlr/v4/runtime
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

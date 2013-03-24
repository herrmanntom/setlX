@ECHO off
SetLocal

set setlXJarLocation=..\src\setlX.jar
set CLASSPATH="%setlXJarLocation%;%CLASSPATH%"

IF EXIST java-src (
    echo "Building the setlX graphics library ..."
    IF EXIST bin (
        rmdir /S /Q bin
    )
    IF EXIST setlX-gfx.jar (
        del /F /Q setlX-gfx.jar
    )
    mkdir bin
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\utilities\*.java
    javac -cp "%CLASSPATH%" -d bin -sourcepath java-src java-src\org\randoom\setlx\functions\*.java
    jar cmf java-src\MANIFEST.MF setlX-gfx.jar -C bin\ .
    copy setlX-gfx.jar ..\src\setlX-gfx.jar
    rmdir /S /Q bin
    echo "The setlX graphics library was (hopefully correctly) build."
) ELSE (
    echo "No source available -- the setlX graphics library can not be build."
)

echo.
pause

EndLocal
@ECHO on


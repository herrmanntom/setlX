@ECHO off

REM insert full path to the setlX.jar file here
set setlXJarLocation=setlX.jar

REM ########################## additional options ##############################
set javaParameters=

REM execute with increased stack size
set javaParameters=%javaParameters% -Xss16m

REM uncomment the next line by removing 'REM' to execute with increased maximum memory size (6GB) (>2GB needs 64 Bit mode!)
REM set javaParameters=%javaParameters% -Xmx6g"

REM ############################################################################

IF EXIST %setlXJarLocation% (
    java -cp "%setlXJarLocation%;%CLASSPATH%" %javaParameters% SetlX %*
) ELSE (
    echo "The setlX.jar file can not be found!"
)

echo.
pause

@ECHO on

@ECHO off
REM
REM
REM launcher script for the setlX interpreter on Microsoft Windows
REM
REM

REM insert full path to the setlX.jar file here
set setlXJarLocation=setlX.jar

REM ########################## additional options ##############################
set javaParameters=

REM uncomment by removing 'REM' to force execution in 64 bit mode (only recent Java versions for Windows support this)
REM set javaParameters=%javaParameters% -d64

REM uncomment by removing 'REM' to execute with increased memory size (6GB) (>2GB needs 64 Bit mode!)
REM set javaParameters=%javaParameters% -Xmx6144m

REM ############################################################################

IF EXIST %setlXJarLocation% (
    java -cp "%setlXJarLocation%;%CLASSPATH%" %javaParameters% org.randoom.setlxUI.pc.SetlX %*
) ELSE (
    echo "The setlX.jar file can not be found!"
)

echo.
pause

@ECHO on

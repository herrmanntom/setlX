@ECHO off
SetLocal enabledelayedexpansion
REM
REM
REM launcher script for the setlX interpreter on Microsoft Windows
REM
REM

REM insert path to the folder where you copied the jar files here
set setlXJarDirectory=.

REM insert full path to library location here
set SETLX_LIBRARY_PATH=%HOMEDRIVE%%HOMEPATH%\setlXlibrary\

REM ########################## additional options ##############################
set javaParameters=

REM uncomment by removing 'REM' to force execution in 64 bit mode (only recent Java versions for Windows support this)
REM set javaParameters=%javaParameters% -d64

REM uncomment by removing 'REM' to execute with increased memory size (6GB) (>2GB needs 64 Bit mode!)
REM set javaParameters=%javaParameters% -Xmx6144m

REM ############################################################################

set class_path=%CLASSPATH%
pushd "%setlXJarDirectory%"
for %%g in (*.jar) do set class_path=%setlXJarDirectory%\%%g;!class_path!;
popd

IF NOT "a%class_path%"=="a%CLASSPATH%" (
    java -cp "%class_path%" %javaParameters% org.randoom.setlx.pc.ui.SetlX %*
) ELSE (
    echo "The setlX jar files cannot be found!"
)

EndLocal
@ECHO on

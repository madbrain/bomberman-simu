
@echo off

setlocal
set DIRNAME=%~dp0%
set PROGNAME=%~nx0%
set ARGS=%*

java -jar "%DIRNAME%\bomberman-simu-${project.version}.jar" %ARGS%

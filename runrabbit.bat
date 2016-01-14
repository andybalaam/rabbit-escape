@echo off

setlocal
setlocal enableextensions

set jar=

:: Find jar.

for /F "usebackq" %%a in (`dir *.jar /b ^| findstr rabbit-escape-[0-9\.]*jar`) do set jar=%%a 2>nul

echo Looking for jar in current dir: %jar%

if not "%jar%" == "" goto start
	
cd dist
for /F "usebackq" %%a in (`dir *.jar /b ^| findstr rabbit-escape-[0-9\.]*jar`) do set jar=.\dist\%%a
echo Looking for jar in .\dist: %jar%
cd ..

if not "%jar%" == "" goto start

echo Cannot find rabbit-escape-<version>.jar. Exit.
goto:eof

:start

if  "%1" == "swing" (
    :: Kludgey, but using shift does not get rid of "swing" if it is the only arg
    java -cp %jar% rabbitescape.ui.swing.SwingMain %2 %3 %4 %5 %6 %7 %8 %9
	goto:eof
)
:: Default to text ui and utils with all args
java -cp %jar% rabbitescape.ui.text.TextMain %*

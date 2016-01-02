@echo off
:: Find jar. Leave only one in the same directory
for %%a in (*.jar) do set jar=%%a
echo %jar%
if  "%1" == "" (
    java -cp %jar% rabbitescape.ui.swing.SwingMain
	goto:eof
)
if  "%1" == "swing" (
    :: Kludgey, but using shift does not get rid of "swing" if it is the only arg
    java -cp %jar% rabbitescape.ui.swing.SwingMain %2 %3 %4 %5 %6 %7 %8 %9
	goto:eof
)
if "%1" == "text" (
    java -cp %jar% rabbitescape.ui.text.TextMain %2 %3 %4 %5 %6 %7 %8 %9
	goto:eof
)
:: Default to help message
java -cp %jar% rabbitescape.ui.text.TextMain -h

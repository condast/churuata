@echo off
rem START or STOP Services
rem ----------------------------------
rem Check if argument is STOP or START

if not ""%1"" == ""START"" goto stop


"H:\Project\churuata\XAMPP\mysql\bin\mysqld" --defaults-file="H:\Project\churuata\XAMPP\mysql\bin\my.ini" --standalone
if errorlevel 1 goto error
goto finish

:stop
cmd.exe /C start "" /MIN call "H:\Project\churuata\XAMPP\killprocess.bat" "mysqld.exe"

if not exist "H:\Project\churuata\XAMPP\mysql\data\%computername%.pid" goto finish
echo Delete %computername%.pid ...
del "H:\Project\churuata\XAMPP\mysql\data\%computername%.pid"
goto finish


:error
echo MySQL could not be started

:finish
exit

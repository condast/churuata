@echo off

if not ""%1"" == ""START"" goto stop

cmd.exe /C start /B /MIN "" "H:\Project\churuata\XAMPP\apache\bin\httpd.exe"

if errorlevel 255 goto finish
if errorlevel 1 goto error
goto finish

:stop
cmd.exe /C start "" /MIN call "H:\Project\churuata\XAMPP\killprocess.bat" "httpd.exe"

if not exist "H:\Project\churuata\XAMPP\apache\logs\httpd.pid" GOTO finish
del "H:\Project\churuata\XAMPP\apache\logs\httpd.pid"
goto finish

:error
echo Error starting Apache

:finish
exit

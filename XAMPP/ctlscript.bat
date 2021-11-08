@echo off
rem START or STOP Services
rem ----------------------------------
rem Check if argument is STOP or START

if not ""%1"" == ""START"" goto stop

if exist H:\Project\churuata\XAMPP\hypersonic\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\server\hsql-sample-database\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\ingres\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\ingres\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\mysql\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\mysql\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\postgresql\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\postgresql\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\apache\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\apache\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\openoffice\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\openoffice\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\apache-tomcat\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\apache-tomcat\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\resin\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\resin\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\jetty\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\jetty\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\subversion\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\subversion\scripts\ctl.bat START)
rem RUBY_APPLICATION_START
if exist H:\Project\churuata\XAMPP\lucene\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\lucene\scripts\ctl.bat START)
if exist H:\Project\churuata\XAMPP\third_application\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\third_application\scripts\ctl.bat START)
goto end

:stop
echo "Stopping services ..."
if exist H:\Project\churuata\XAMPP\third_application\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\third_application\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\lucene\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\lucene\scripts\ctl.bat STOP)
rem RUBY_APPLICATION_STOP
if exist H:\Project\churuata\XAMPP\subversion\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\subversion\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\jetty\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\jetty\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\hypersonic\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\server\hsql-sample-database\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\resin\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\resin\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\apache-tomcat\scripts\ctl.bat (start /MIN /B /WAIT H:\Project\churuata\XAMPP\apache-tomcat\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\openoffice\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\openoffice\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\apache\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\apache\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\ingres\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\ingres\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\mysql\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\mysql\scripts\ctl.bat STOP)
if exist H:\Project\churuata\XAMPP\postgresql\scripts\ctl.bat (start /MIN /B H:\Project\churuata\XAMPP\postgresql\scripts\ctl.bat STOP)

:end


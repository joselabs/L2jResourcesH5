set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_301
set Path=%JAVA_HOME%\bin;%Path%

@echo off
title Eternity-World Login Server
:start
echo Starting Login Server.
echo.
java -Xms128m -Xmx128m  -cp ./../libs/* l2e.loginserver.LoginServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Server Restart ...
echo.
goto start
:error
echo.
echo Server terminated abnormally
echo.
:end
echo.
echo server terminated
echo.
pause

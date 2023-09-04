@echo off
title L2jPrivate Developers Team  - Gameserver
:start
echo L2jPrivate Developers Team  - Gameserver starting
echo.
REM -------------------------------------
REM Default parameters for a basic server.
java -Djava.util.logging.manager=com.l2jserver.util.L2LogManager -Xms2048m -Xmx25048m -cp ./../libs/*;server.jar com.l2jserver.gameserver.GameServer
REM
REM If you have a big server and lots of memory, you could experiment for example with
REM java -server -Xmx1536m -Xms1024m -Xmn512m -XX:PermSize=256m -XX:SurvivorRatio=8 -Xnoclassgc -XX:+AggressiveOpts
REM -------------------------------------
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restart ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly
echo.
:end
echo.
echo server terminated
echo.
pause

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_301
set Path=%JAVA_HOME%\bin;%Path%

@echo off
title Eternity-World Game Server
:start
echo Starting Game Server.
echo.
REM -------------------------------------
REM If you have a big server and lots of memory, you could experiment for example with
java -Dfile.encoding=UTF-8 -Djava.util.logging.manager=l2e.commons.util.L2LogManager -server -Xmx6G -XX:+UseTLAB -Xnoclassgc -XX:+AggressiveOpts -XX:TargetSurvivorRatio=90 -XX:SurvivorRatio=16 -XX:MaxTenuringThreshold=12 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:UseSSE=3 -XX:+UseFastAccessorMethods -Duser.timezone=GMT+3 -cp ./../libs/* l2e.gameserver.GameServer
REM -------------------------------------
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

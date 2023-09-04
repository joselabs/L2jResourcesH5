#!/bin/sh

# ======== JVM settings =======
# Set heap min/max to same size for consistent results
# одинаковый размер памяти для Xms и Xmx, JVM пытается удержать размер heap'а минимальным, и если его нужно меньше, чем в Xmx - гоняет GC понапрасну
# -Xmn ставим в 5-6 рас меньше чем -Xmx.
javaopts=" -Xms8G"
javaopts="$javaopts -Xmx8G"

# Альтернативные настройки самой JVM."
java_settings="-XX:+DoEscapeAnalysis"
# Циклы заполнения/копирования массивов заменяются на прямые машинные инструкции для ускорения работы.
java_settings="$java_settings -XX:+OptimizeFill"
# Опция, устраняет лишние блокировки путем их объединения.
java_settings="$java_settings -XX:+EliminateLocks"
# Позволяет расширить диапазон кешируемых значений для целых типов при старте виртуальной машины.
java_settings="$java_settings -XX:AutoBoxCacheMax=65536"
# TODO: �?зучить данный конфиг, как он будет вести себя у нас в сборке. 4000 - по умолчанию, 500 - при включенной AggressiveOpts
# java_settings="$java_settings -XX:BiasedLockingStartupDelay=4000"
# TODO: �?зучить профит у нас в сборке, от данной опции.
java_settings="$java_settings -XX:+OptimizeStringConcat"
# java_settings="$java_settings -XX:+UseCompressedStrings"
# TODO: Не совсем понятный параметр.
# java_settings="$java_settings XX:+EliminateAutoBox"
# TODO: Провести тесты, на сколько сильно, это нам секономит потребление ОЗУ.
#java_settings="$java_settings -XX:+UseCompressedOops"
# TODO: Есть ли нам профит от данной опции?
#java_settings="$java_settings -XX:+UseStringCache"
# Включает опции которые выше + использует альтернативную библиотеку...
# java_settings="$java_settings -XX:+AggressiveOpts"

# Настройки сборщиков мусора.
# ###################################################################################
#java_settings="$java_settings -Xincgc"
#java_settings="$java_settings -Xnoclassgc"
# -----------------------------------------------------------
java_settings="$java_settings -XX:+UseParNewGC"
# java_settings="$java_settings -XX:+UseParallelOldGC"
# Параметр равен, количеству потоков ЦП.
java_settings="$java_settings -XX:ParallelGCThreads=8"

# -----------------------------------------------------------
# Хуеватый вариант, по предварительной оценке:)
# java_settings="$java_settings -XX:+UseG1GC"
# java_settings="$java_settings -XX:MaxGCPauseMillis=50"
# java_settings="$java_settings -XX:GCPauseIntervalMillis=60000"
# -----------------------------------------------------------
# -----------------------------------------------------------
# Дает очень частые задержки, но в замен держит ОЗУ в чистоте и порядке.
java_settings="$java_settings -XX:+UseConcMarkSweepGC"
# 
# Параметр равен, количеству потоков ЦП.
java_settings="$java_settings -XX:ParallelCMSThreads=8"
#java_settings="$java_settings -XX:+ExplicitGCInvokesConcurrent"
#java_settings="$java_settings -XX:+CMSParallelRemarkEnabled"
#java_settings="$java_settings -XX:+CMSScavengeBeforeRemark"

java_settings="$java_settings -XX:+CMSClassUnloadingEnabled"
java_settings="$java_settings -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses"
# java_settings="$java_settings -XX:+UseCMSInitiatingOccupancyOnly"
# java_settings="$java_settings -XX:CMSInitiatingOccupancyFraction=60"
# -----------------------------------------------------------
# ###################################################################################
# Логирование, использовать только для отладки.
# md .\gc_log\
# java_settings="$java_settings -verbose:gc"
# java_settings="$java_settings -XX:+PrintHeapAtGC"
# java_settings="$java_settings -XX:+PrintGCDetails"
# java_settings="$java_settings -XX:+PrintGCDateStamps"
# java_settings="$java_settings -XX:+PrintGCApplicationStoppedTime"
# java_settings="$java_settings -XX:+PrintGC"
# java_settings="$java_settings -Xloggc:.\gc_log\garbage_collector%DATE%-%ctime%.log"
# java_settings="$java_settings -XX:+PrintGCTimeStamps"
# java_settings="$java_settings -XX:+PrintTenuringDistribution"

java_settings="$java_settings -Dfile.encoding=UTF-8"
java_settings="$java_settings -Djava.net.preferIPv4Stack=true"
java_settings="$java_settings -Xbootclasspath/p:./lib/jsr167_8.jar"

# ==========================================

# exit codes of GameServer:
#  0 normal shutdown
#  2 reboot attempt

while :; do
	cp log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	cp log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
	cp log/chat.log "log/`date +%Y-%m-%d_%H:%M:%S`-chat.log"
	nice -n -2 java -server $java_settings $javaopts -cp ./lib/ccpGuard.jar:./lib/tools.jar:./lib/l2openserver.jar:./lib/*: l2open.gameserver.GameStart > log/stdout.log 2>&1
	[ $? -ne 2 ] && break
	sleep 10;
done

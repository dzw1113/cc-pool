@echo off
@echo "start....."
cd %~dp0
cd ../lib
start /b java -jar ./ccpool.jar
@echo "start success！"
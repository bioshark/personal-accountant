@echo off
set DIR=%~dp0..
cd /d "%DIR%"
java -Djava.rmi.server.hostname=localhost -jar "%DIR%\personal-accountant-1.0.0.jar"

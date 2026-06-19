@echo off
set DIR=%~dp0..
cd /d "%DIR%"
java -Djava.rmi.server.hostname=localhost -jar "%DIR%\personal-accountant-0.0.1-SNAPSHOT.jar"

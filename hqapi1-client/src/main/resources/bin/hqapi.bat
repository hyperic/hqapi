@echo off

setLocal EnableDelayedExpansion
set CLASSPATH="
for /R ..\ %%a in (*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%a
)
set CLASSPATH=!CLASSPATH!"

set _JAVACMD=%JAVACMD%

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe

set HQAPILOGDIR=../logs

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe

"%_JAVACMD%" -Dhqapi.logDir=%HQAPILOGDIR% -cp %CLASSPATH% org.hyperic.hq.hqapi1.tools.Shell %*

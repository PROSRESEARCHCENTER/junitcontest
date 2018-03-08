@echo off
set JAIDA_HOME=%~dp0..
set LOCALCLASSPATH=%CLASSPATH%
for %%i in ("%JAIDA_HOME%\lib\*.jar") do call "%JAIDA_HOME%\bin\lcp.bat" %%i
set CLASSPATH=%LOCALCLASSPATH%
set LOCALCLASSPATH=
set PATH=%PATH%;%JAIDA_HOME%\lib\x86-Windows-msvc

@echo off
chcp 65001 > nul

cd /d "%~dp0.."

call "%~dp0..\run.bat" --vfs vfs\missing.json
echo.

echo { "name": "/" > vfs\invalid.json
call "%~dp0..\run.bat" --vfs vfs\invalid.json
del vfs\invalid.json
echo.

call "%~dp0..\run.bat" --script tests\missing.txt

echo.
pause

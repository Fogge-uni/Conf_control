@echo off
cd /d "%~dp0.."

call run.bat --vfs vfs\missing.json

echo.
echo { "name": "/" > vfs\invalid.json
call run.bat --vfs vfs\invalid.json
del vfs\invalid.json

echo.
call run.bat --script tests\missing.txt

echo.
call run.bat --script tests\commands.txt

echo.
pause


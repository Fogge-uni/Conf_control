@echo off
cd /d "%~dp0.."

call run.bat --vfs vfs\nested.json --script tests\commands.txt
echo.
pause
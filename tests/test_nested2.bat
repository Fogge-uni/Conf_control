
@echo off
cd /d "%~dp0.."

call run.bat --vfs vfs\nested2.json --script tests\nested2_commands.txt
echo.
pause

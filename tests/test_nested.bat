@echo off

cd /d "%~dp0.."
call "%~dp0..\run.bat" --vfs vfs\nested.json --script tests\nested_commands.txt
echo.
pause
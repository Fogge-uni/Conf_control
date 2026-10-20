@echo off
cd /d "%~dp0.."

call "%~dp0..\run.bat" --vfs vfs\nested.json --script tests\test_one.txt
echo.
pause

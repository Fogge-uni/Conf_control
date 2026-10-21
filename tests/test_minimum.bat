
@echo off
chcp 65001 > nul

cd /d "%~dp0.."

call "%~dp0..\run.bat" --vfs vfs\minimum.json --script tests\test_one.txt
pause

#!bin/sh

mkdir -p out
javac -d out scr/*.java
java -cp out Main
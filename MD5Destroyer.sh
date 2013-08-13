#!/bin/bash
javac -cp javax.mail.jar *.java
java -classpath .:javax.mail.jar ServidorMD5Destroyer  >> "log.txt"

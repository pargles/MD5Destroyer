#!/bin/bash
javac -cp javax.mail.jar *.java
for i in {1..60}#cada PC pode executar ate 60 vezes
do
  java -classpath .:javax.mail.jar ClienteMD5Destroyer >> "log.txt"
done

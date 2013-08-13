#!/bin/bash
javac -cp javax.mail.jar *.java
#cada PC pode executar ate 60 vezes
max=50
for i in `seq 1 $max`
do
    echo "$i"
    java ClienteMD5Destroyer >> "logCli.txt"
done

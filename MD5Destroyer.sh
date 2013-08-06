#!/bin/bash
rmiregistry&
javac -cp javax.mail.jar *.java
java ServidorMD5Destroyer

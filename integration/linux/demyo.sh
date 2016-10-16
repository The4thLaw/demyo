#!/bin/bash
cd $(dirname $0)
java $JAVA_OPTIONS -cp 'lib/*' org.demyo.desktop.Start

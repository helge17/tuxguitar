#!/bin/bash
##SCRIPT DIR
TG_HOME=`dirname "$0"`
TG_HOME=`cd "$TG_HOME"; pwd`

cd ${TG_HOME}

export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${TG_HOME}/lib

${TG_HOME}/bin/tuxguitar.bin -Dtuxguitar.home.path="${TG_HOME}" "$1" "$2"

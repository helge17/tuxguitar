#!/bin/bash
##SCRIPT DIR
TG_HOME=`dirname "$0"`
TG_HOME=`cd "$TG_HOME"; pwd`

export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${TG_HOME}/lib

cd ${TG_HOME}

${TG_HOME}/bin/tuxguitar.bin

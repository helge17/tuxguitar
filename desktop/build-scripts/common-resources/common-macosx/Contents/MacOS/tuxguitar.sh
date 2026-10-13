#!/bin/sh

##SCRIPT DIR
TG_DIR=`dirname "$0"`
TG_DIR=`cd "$TG_DIR"; pwd`

##JAVA
JAVA="${TG_DIR}/jre/bin/java"
##LIBRARY_PATH
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${TG_DIR}/lib/
##CLASSPATH
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/*
CLASSPATH=${CLASSPATH}:${TG_DIR}/share/
CLASSPATH=${CLASSPATH}:${TG_DIR}/dist/
##MAINCLASS
MAINCLASS=app.tuxguitar.app.TGMainSingleton
##SWT ARGUMENTS
ls "${TG_DIR}"/lib/*swt*.jar > /dev/null 2>&1 && SWT_ARGS="-XstartOnFirstThread"
##EXPORT VARS
export CLASSPATH
export LD_LIBRARY_PATH
##LAUNCH
"${JAVA}" ${SWT_ARGS} -cp ":${CLASSPATH}" -Dtuxguitar.home.path="${TG_DIR}" -Dtuxguitar.share.path="share" -Djava.library.path="${LD_LIBRARY_PATH}" ${MAINCLASS} "$@"

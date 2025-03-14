#!/bin/sh
##SCRIPT DIR
TG_DIR=`dirname $(realpath "$0")`
##JAVA
JAVA=`which java`
##LIBRARY_PATH
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${TG_DIR}/lib/
##CLASSPATH
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/*
CLASSPATH=${CLASSPATH}:${TG_DIR}/share/
CLASSPATH=${CLASSPATH}:${TG_DIR}/dist/
##MAINCLASS
MAINCLASS=app.tuxguitar.app.TGMainSingleton
##JVM ARGUMENTS
VM_ARGS="-Xmx512m"
##EXPORT VARS
export CLASSPATH
export LD_LIBRARY_PATH
##Avoid problems with Accelerated Compositing mode in SWT/WebKitGTK
export WEBKIT_DISABLE_COMPOSITING_MODE=1
##LAUNCH
${JAVA} ${VM_ARGS} -cp :${CLASSPATH} -Dtuxguitar.home.path="${TG_DIR}" -Dtuxguitar.share.path="share/" -Djava.library.path="${LD_LIBRARY_PATH}" ${MAINCLASS} "$@"

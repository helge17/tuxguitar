#!/bin/sh
##SCRIPT DIR
TG_DIR=`dirname $(realpath "$0")`
##JAVA
JAVA=`which java`
##LIBRARY_PATH
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${TG_DIR}/lib/
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib/jni
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/local/lib
ls $TG_DIR/lib/*jfx*.jar > /dev/null 2>&1 && LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/local/openjfx14/lib
##CLASSPATH
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/*
ls $TG_DIR/lib/*jfx*.jar > /dev/null 2>&1 && CLASSPATH=${CLASSPATH}:/usr/local/openjfx14/lib/*
CLASSPATH=${CLASSPATH}:${TG_DIR}/share/
CLASSPATH=${CLASSPATH}:${TG_DIR}/dist/
##MAINCLASS
MAINCLASS=org.herac.tuxguitar.app.TGMainSingleton
##JVM ARGUMENTS
VM_ARGS="-Xmx512m"
##EXPORT VARS
export CLASSPATH
export LD_LIBRARY_PATH
##LAUNCH
${JAVA} ${VM_ARGS} -cp :${CLASSPATH} -Dtuxguitar.home.path="${TG_DIR}" -Dtuxguitar.share.path="share/" -Djava.library.path="${LD_LIBRARY_PATH}" ${MAINCLASS} "$@"

#!/bin/sh

##SCRIPT DIR
DIR_NAME=`dirname "$0"`
DIR_NAME=`cd "$DIR_NAME"; pwd`
cd "${DIR_NAME}"

##JAVA
if [ -z $JAVA ]; then
	JAVA=${JAVA_HOME}/bin/java
	[ ! -f ${JAVA} ] && JAVA=/usr/bin/java
	[ ! -f ${JAVA} ] && JAVA=java
fi

##PLATFORM_HOME
PLATFORM_OS=./platform-linux-x86_64
PLATFORM_ALL=./platform-all

##LIBRARY_PATH
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${PLATFORM_OS}/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib/jni
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/local/lib

##CLASSPATH
CLASSPATH=${CLASSPATH}:${PLATFORM_ALL}/tuxguitar.jar
CLASSPATH=${CLASSPATH}:${PLATFORM_ALL}/share
CLASSPATH=${CLASSPATH}:${PLATFORM_OS}/lib/tuxguitar-dist.jar
CLASSPATH=${CLASSPATH}:${PLATFORM_OS}/lib/swt.jar
CLASSPATH=${CLASSPATH}:${PLATFORM_OS}/share

##MAINCLASS
MAINCLASS=org.herac.tuxguitar.app.TGMain

##JVM ARGUMENTS
VM_ARGS="-Xmx512m"

##EXPORT VARS
export CLASSPATH
export LD_LIBRARY_PATH
export MOZILLA_FIVE_HOME

##LAUNCH
${JAVA} ${VM_ARGS} -cp :${CLASSPATH} -Djava.library.path="${LD_LIBRARY_PATH}" ${MAINCLASS} "$1" "$2"

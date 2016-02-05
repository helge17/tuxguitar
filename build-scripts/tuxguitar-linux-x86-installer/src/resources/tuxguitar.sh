#!/bin/sh
##SCRIPT DIR
DIR_NAME=`dirname "$0"`
DIR_NAME=`cd "$DIR_NAME"; pwd`
cd "${DIR_NAME}"
##JAVA
JAVA="${DIR_NAME}/jre/bin/java"
if [ -z $JAVA ]; then
	JAVA=${JAVA_HOME}/bin/java
	[ ! -f ${JAVA} ] && JAVA=/usr/bin/java
	[ ! -f ${JAVA} ] && JAVA=java
fi
##MOZILLA_FIVE_HOME
if [ -z $MOZILLA_FIVE_HOME ]; then
	MOZILLA_FIVE_HOME=/usr/lib/firefox
	[ ! -d ${MOZILLA_FIVE_HOME} ] && MOZILLA_FIVE_HOME=/usr/lib/mozilla
	[ ! -d ${MOZILLA_FIVE_HOME} ] && MOZILLA_FIVE_HOME=/usr/lib/iceweasel
fi
##LIBRARY_PATH
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:lib/
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib/jni
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/local/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${MOZILLA_FIVE_HOME}
##CLASSPATH
CLASSPATH=${CLASSPATH}:./lib/tuxguitar.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-lib.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-editor-utils.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-gm-utils.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-awt-graphics.jar
CLASSPATH=${CLASSPATH}:./lib/swt.jar
CLASSPATH=${CLASSPATH}:./lib/gervill.jar
CLASSPATH=${CLASSPATH}:./lib/itext-pdf.jar
CLASSPATH=${CLASSPATH}:./lib/itext-xmlworker.jar
CLASSPATH=${CLASSPATH}:./share/
CLASSPATH=${CLASSPATH}:./dist/
##MAINCLASS
MAINCLASS=org.herac.tuxguitar.app.TGMainSingleton
##JVM ARGUMENTS
VM_ARGS="-Xmx512m"
##EXPORT VARS
export CLASSPATH
export LD_LIBRARY_PATH
export MOZILLA_FIVE_HOME
##LAUNCH
${JAVA} ${VM_ARGS} -cp :${CLASSPATH} -Dtuxguitar.home.path="${DIR_NAME}" -Dtuxguitar.share.path="share/" -Djava.library.path="${LD_LIBRARY_PATH}" ${MAINCLASS} "$1" "$2"
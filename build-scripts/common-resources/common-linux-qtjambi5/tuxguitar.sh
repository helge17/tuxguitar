#!/bin/sh
##SCRIPT DIR
DIR_NAME=`dirname "$0"`
DIR_NAME=`cd "$DIR_NAME"; pwd`
cd "${DIR_NAME}"
##JAVA
JAVA_BUNDLED="./jre/bin/java"
if [ -f "${JAVA_BUNDLED}" ]; then
	JAVA="${JAVA_BUNDLED}"
fi
if [ -z ${JAVA} ]; then
	[ -z ${JAVA_HOME} ] && JAVA_HOME="/usr"
	[ ! -f "${JAVA}" ] && JAVA="${JAVA_HOME}/bin/java"
	[ ! -f "${JAVA}" ] && JAVA="java"
fi
##LIBRARY_PATH
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:lib/
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib/jni
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/local/lib
##CLASSPATH
CLASSPATH=${CLASSPATH}:./lib/tuxguitar.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-ui-toolkit.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-ui-toolkit-qt5.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-lib.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-editor-utils.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-gm-utils.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-awt-graphics.jar
CLASSPATH=${CLASSPATH}:./lib/qtjambi.jar
CLASSPATH=${CLASSPATH}:./lib/qtjambi-native.jar
CLASSPATH=${CLASSPATH}:./lib/gervill.jar
CLASSPATH=${CLASSPATH}:./lib/itext-pdf.jar
CLASSPATH=${CLASSPATH}:./lib/itext-xmlworker.jar
CLASSPATH=${CLASSPATH}:./lib/commons-compress.jar
CLASSPATH=${CLASSPATH}:./lib/icedtea-sound.jar
CLASSPATH=${CLASSPATH}:./share/
CLASSPATH=${CLASSPATH}:./dist/
##MAINCLASS
MAINCLASS=org.herac.tuxguitar.app.TGMainSingleton
##JVM ARGUMENTS
VM_ARGS="-Xmx512m"
##EXPORT VARS
export CLASSPATH
export LD_LIBRARY_PATH
##LAUNCH
${JAVA} ${VM_ARGS} -cp :${CLASSPATH} -Dtuxguitar.home.path="${DIR_NAME}" -Dtuxguitar.share.path="share/" -Djava.library.path="${LD_LIBRARY_PATH}" -Dorg.herac.tuxguitar.ui.qt.style=Fusion ${MAINCLASS} "$1" "$2"

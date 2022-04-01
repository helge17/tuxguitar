#!/bin/bash
##SCRIPT DIR
DIR_NAME=`dirname "$0"`
DIR_NAME=`cd "$DIR_NAME"; pwd`
cd "${DIR_NAME}"
##JAVA
if [ -z "$JAVA" ]; then
	JAVA="${JAVA_HOME}/bin/java"
	[ ! -f "${JAVA}" ] && JAVA="/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/bin/java"
	[ ! -f "${JAVA}" ] && JAVA="/usr/bin/java"
	[ ! -f "${JAVA}" ] && JAVA="java"
fi
##CLASSPATH
CLASSPATH=${CLASSPATH}:./lib/tuxguitar.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-ui-toolkit.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-ui-toolkit-qt4.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-lib.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-editor-utils.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-gm-utils.jar
CLASSPATH=${CLASSPATH}:./lib/tuxguitar-awt-graphics.jar
CLASSPATH=${CLASSPATH}:./lib/qtjambi.jar
CLASSPATH=${CLASSPATH}:./lib/qtjambi-native.jar
CLASSPATH=${CLASSPATH}:./lib/gervill.jar
CLASSPATH=${CLASSPATH}:./lib/itext-pdf.jar
CLASSPATH=${CLASSPATH}:./lib/itext-xmlworker.jar
CLASSPATH=${CLASSPATH}:./share/
CLASSPATH=${CLASSPATH}:./dist/
##LIBRARY_PATH
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:lib/
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib/jni
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/local/lib
##MAINCLASS
MAINCLASS=org.herac.tuxguitar.app.TGMainSingleton
##JVM ARGUMENTS
VM_ARGS="-Xmx512m"
##SWT ARGUMENTS
SWT_ARGS="-XstartOnFirstThread"
##LAUNCH
exec "${JAVA}" ${VM_ARGS} ${SWT_ARGS} -cp :${CLASSPATH} -Dtuxguitar.home.path="${DIR_NAME}" -Djava.library.path="${LD_LIBRARY_PATH}" -Dorg.herac.tuxguitar.ui.qt.style="Plastique" ${MAINCLASS} "$1" "$2"

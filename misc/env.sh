#! /bin/sh
#@ident "$Id:  $"
#@Author: www.philippe.coval.online.fr -- revision: $Author: rzr $
#@Licence: LGPL
#@Description: Env script for starting java application tuxguitar
###############################################################################
PACKAGE="tuxguitar"

# overide if needed


#{ java : JAVA_HOME JAVA

t="/usr/lib/jvm/java-6-openjdk/jre/bin/../../"

[ -d "${t}" ] && JAVA_HOME="$t" && export JAVA_HOME
t=${JAVA:=${JAVA_HOME}/jre/bin/java}
[ -x "${t}" ] && JAVA="$t" && export JAVA

#} java


#{ swt : SWT_DIR

t=/usr/lib/java/swt.jar

[ -r "${t}" ] && SWT_DIR="$t" && export SWT_DIR

#} swt : not used, for running not building


#{ mozilla : MOZILLA_FIVE_HOME

t="/usr/lib/xulrunner-1.9.1"

if [ -d "$t" ] ; then
	MOZILLA_FIVE_HOME="$t"
	export MOZILLA_FIVE_HOME
 	export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$MOZILLA_FIVE_HOME"
fi

#} mozilla

#eof "$Id:$"

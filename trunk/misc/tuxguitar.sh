#! /bin/sh
#@ident "$Id: tuxguitar.sh,v 1.3 2008/04/16 17:04:50 rzr Exp $"
#@Author: www.philippe.coval.online.fr -- revision: $Author: rzr $
#@Licence: LGPL
#@Description: Wrapper script for starting java application tuxguitar
###############################################################################
PACKAGE="tuxguitar"
[ ! -z $DEBUG ] && set -e
[ ! -z $DEBUG ] && set -x

#/// guess JAVA_HOME if undefined
java_guess_()
{
    d="/opt/java/"
    [ -d "$t" ] && d="$t"
    d="/usr/local/opt/java/"
    [ -d "$t" ] && d="$t"
# windows
    t="C:\\Program\ Files\\Java\\jre1.6.0_03/"
    [ -d "$t" ] && d="$t"
    t="\\Program\ Files\\Java\\jre1.6.0_03/"
    [ -d "$t" ] && d="$t"
    t="/Program\ Files/Java/jre1.6.0_03/"
    [ -d "$t" ] && d="$t"
# macosx
    t="/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home/"
    [ -d "$t" ] && d="$t"
# opensuse
    t="/etc/profile.d/alljava.sh"
    [ -r $t ] && . $t && echo ${JAVA_HOME} && return
    t="/usr/lib/jvm/java"
    [ -d "$t" ] && d="$t"
    t="/usr/lib64/jvm/java"
    [ -d "$t" ] && d="$t"
# mandriva
    t="/usr/lib/jvm/jre-1.6.0-sun/bin/../"
    [ -d "$t" ] && d="$t"
# gentoo
    t="/opt/sun-jdk-1.5.0.14/"
    [ -d "$t" ] && d="$t"
    t="/opt/sun-jdk-1.6.0.04/"
    [ -d "$t" ] && d="$t"
# debian
    if [ -r /etc/debian_version  ]; then
        t="/usr/lib/jvm/java-gcj/jre/bin/../../"
        [ -d "$t" ] && d="$t"
        t="/usr/lib/jvm/java-1.5.0-sun/jre/bin/../../"
        [ -d "$t" ] && d="$t"
        t="/usr/lib/jvm/java-6-sun/jre/bin/../../"
        [ -d "$t" ] && d="$t"
        t="/usr/lib/jvm/java-6-openjdk/jre/bin/../../"
        [ -d "$t" ] && d="$t"
    fi
# results
    [ -d "$d" ] && echo "$d"
}

swt_guess_()
{
    t="/usr/lib/eclipse/plugins/org.eclipse.swt.gtk.linux.*.jar"
    [ -r "$t" ] && f="$t"
    t="/etc/alternatives/swt.jar"
    [ -r "$t" ] && f="$t"
    t="/usr/share/java/swt.jar"
    [ -r "$t" ] && f="$t"
    t="/usr/lib/java/swt.jar"
    [ -r "$t" ] && f="$t"
    t="/usr/lib/java/swt-gtk-3.5.1.jar"
    [ -r "$t" ] && f="$t"
    t="/usr/share/java/swt-gtk-3.4.jar"
    [ -r "$t" ] && f="$t"
    t="/usr/lib/java/swt3.2-gtk.jar"
    [ -r "$t" ] && f="$t"
    file -L "$f" >/dev/null 2>&1 || f=""
    echo "$f"
}

#/// org.eclipse.swt.SWTError: No more handles
#/// [Unknown Mozilla path (MOZILLA_FIVE_HOME not set)]
mozilla_guess_()
{
    t="/usr/lib/mozilla"
    test -r "$t/libxpcom.so" && d="$t"
    t="/usr/lib/iceape/"
    test -r "$t/libxpcom.so" && d="$t"
    t="/usr/lib/icedove/"
    test -r "$t/libxpcom.so" && d="$t"
    t="/usr/lib/firefox/"
    test -r "$t/libxpcom.so" && d="$t"
    t="/usr/lib/iceweasel"
    test -r "$t/libxpcom.so" && d="$t"
    t="/usr/lib/xulrunner"
    test -r "$t/libxpcom.so" && d="$t"
    t="/usr/lib/xulrunner-1.9"
    test -r "$t/libxpcom.so" && d="$t"
    t="/usr/lib/xulrunner-`xulrunner-1.9.1 --gre-version`"
    test -r "$t/libxpcom.so" && d="$t"
    t="/usr/lib/xulrunner-1.9.1"
    test -r "$t/libxpcom.so" && d="$t"
    t="/usr/lib/xulrunner-1.9.2"
    test -r "$t/libxpcom.so" && d="$t"
    echo "$d"
}


#
env_()
{
    t="/etc/default/${PACKAGE}/env.sh"
    [ -r "$t" ] && . "$t"

    t="/etc/${PACKAGE}/env.sh"
    [ -r "$t" ] && . "$t"

# java
    [ -z ${JAVA_HOME} ] && t=$(java_guess_) && [ -d "$t" ] && JAVA_HOME="$t"
    if [ -d "${JAVA_HOME}" ] ; then
        export JAVA_HOME
        JAVA="${JAVA:=${JAVA_HOME}/jre/bin/java}"
        [ -x ${JAVA} ] && export JAVA
    else
        JAVA=${JAVA:=java}
        export JAVA
    fi

# mozilla
    [ -z ${MOZILLA_FIVE_HOME} ] \
	&& t=$(mozilla_guess_) && [ -d "$t" ] && MOZILLA_FIVE_HOME="$t"
    if [ -d "$MOZILLA_FIVE_HOME" ] ; then
        export MOZILLA_FIVE_HOME
        export LD_LIBRARY_PATH="$MOZILLA_FIVE_HOME"
    else
        echo '$MOZILLA_FIVE_HOME not valid : check doc shipped w/ tuxguitar'
    fi

    [ ! -z ${DEBUG} ] && echo "# MOZILLA_FIVE_HOME=${MOZILLA_FIVE_HOME}"
    [ ! -z ${DEBUG} ] && echo "# JAVA_HOME=${JAVA_HOME}"}
}


tuxguitar_()
{
    local PACKAGE=${PACKAGE:=tuxguitar}
    local PACKAGE_HOME=${PACKAGE_HOME:=/usr/share/${PACKAGE}/}
#   local PACKAGE_MAIN=${PACKAGE_MAIN:=org.herac.tuxguitar.gui.TGMain}
    local PACKAGE_MAIN=${PACKAGE_MAIN:=org.herac.tuxguitar.app.TGMain}
    local PACKAGE_LIB=${PACKAGE_LIB:=/usr/lib/jni}
# java env
    JAVA=${JAVA:=java}
    CLASSPATH=${CLASSPATH}
    t="${PACKAGE_HOME}"
    [ -d "$t" ] && CLASSPATH=${CLASSPATH}:$t
    t="${PACKAGE_HOME}/${PACKAGE}.jar"
    [ -r "$t" ] && CLASSPATH=${CLASSPATH}:$t
    t="/usr/share/java/itext.jar"
    [ -r "$t" ] && CLASSPATH=${CLASSPATH}:$t
    t=$(swt_guess_)
    [ -r "$t" ] && CLASSPATH=${CLASSPATH}:$t

    JAVA_FLAGS=${JAVA_FLAGS:="-Xms128m -Xmx128m"}
    JAVA_FLAGS=" ${JAVA_FLAGS} \
        -Djava.library.path=${PACKAGE_LIB} \
        -D${PACKAGE}.share.path=/usr/share/${PACKAGE} \
        -cp ${CLASSPATH}:${PACKAGE_CLASSPATH} \
        "
    local arg=""
    [ -z "$1" ] && arg="/usr/share/tuxguitar/tuxguitar.tg"
# run java
    [ ! -z ${DEBUG} ] && echo "LD_LIBRARY_PATH=${LD_LIBRARY_PATH}"
    [ ! -z ${DEBUG} ] && ${JAVA} -version
    [ ! -z ${DEBUG} ] && ${JAVA} ${JAVA_FLAGS} ${PACKAGE_MAIN} --version

    ${JAVA} ${JAVA_FLAGS} ${PACKAGE_MAIN} $arg "$@"
}

# main

env_
tuxguitar_ "$@"

#eof

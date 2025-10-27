#!/bin/sh
## try to read light/dark theme from dbus
getTheme() {
    tuxguitarTheme="none"
    # does not work with all desktops, limited to KDE
    if [ $XDG_CURRENT_DESKTOP = 'KDE' ]; then
        dbusTheme=$(dbus-send --session --print-reply=literal --dest=org.freedesktop.portal.Desktop /org/freedesktop/portal/desktop org.freedesktop.portal.Settings.Read string:org.freedesktop.appearance string:color-scheme 2>/dev/null)
        [ -n "$( echo $dbusTheme | sed -n '/uint32 2/p')" ] && tuxguitarTheme="light"
        [ -n "$( echo $dbusTheme | sed -n '/uint32 1/p')" ] && tuxguitarTheme="dark"
    fi
    echo $tuxguitarTheme
}

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
##EXPORT VARS
export CLASSPATH
export LD_LIBRARY_PATH
##Avoid problems with Accelerated Compositing mode in SWT/WebKitGTK
export WEBKIT_DISABLE_COMPOSITING_MODE=1
##LAUNCH
${JAVA} -cp :${CLASSPATH} -Dtuxguitar.home.path="${TG_DIR}" -Dtuxguitar.share.path="share/" -Djava.library.path="${LD_LIBRARY_PATH}" -Dtuxguitar.theme=$(getTheme) ${MAINCLASS} "$@"

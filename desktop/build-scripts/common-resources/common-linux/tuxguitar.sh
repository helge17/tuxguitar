#!/bin/sh

## GNOME detects dark mode only if GTK_THEME=Adwaita:dark is set
# uint32 0: no preference (defaults to light)
# uint32 1: dark
# uint32 2: light
dbusTheme=$(dbus-send --reply-timeout=1000 --session --print-reply=literal --dest=org.freedesktop.portal.Desktop /org/freedesktop/portal/desktop org.freedesktop.portal.Settings.Read string:org.freedesktop.appearance string:color-scheme 2>/dev/null)
[ -n "$( echo $dbusTheme | sed -n '/uint32 1/p' )" ] && export GTK_THEME=Adwaita:dark

##SCRIPT DIR
TG_DIR=`dirname "$(realpath "$0")"`

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
${JAVA} -cp ":${CLASSPATH}" -Dtuxguitar.home.path="${TG_DIR}" -Dtuxguitar.share.path="share" -Djava.library.path="${LD_LIBRARY_PATH}" ${MAINCLASS} "$@"

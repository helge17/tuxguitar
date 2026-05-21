#!/bin/sh
## try to read light/dark theme from dbus
getTheme() {
    tuxguitarTheme="none"
    # does not work with all desktops, limited to KDE
    if [ "$XDG_CURRENT_DESKTOP" = 'KDE' ]; then
        dbusTheme=$(dbus-send --session --print-reply=literal --dest=org.freedesktop.portal.Desktop /org/freedesktop/portal/desktop org.freedesktop.portal.Settings.Read string:org.freedesktop.appearance string:color-scheme 2>/dev/null)
        [ -n "$( echo $dbusTheme | sed -n '/uint32 2/p')" ] && tuxguitarTheme="light"
        [ -n "$( echo $dbusTheme | sed -n '/uint32 1/p')" ] && tuxguitarTheme="dark"
    fi
    echo $tuxguitarTheme
}

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
## Announce this script's PID to the NSM session manager so SIGTERM is sent
## here instead of to the JVM.  The trap below converts it to SIGUSR2, which
## Java handles cleanly without JACK/SWT sigaction interference.
export NSM_LAUNCHER_PID=$$
##LAUNCH
${JAVA} -cp ":${CLASSPATH}" -Dtuxguitar.home.path="${TG_DIR}" -Dtuxguitar.share.path="share/" -Djava.library.path="${LD_LIBRARY_PATH}" -Dtuxguitar.theme=$(getTheme) ${MAINCLASS} "$@" &
_tg_pid=$!
_tg_sigterm() { kill -USR2 "$_tg_pid" 2>/dev/null; wait "$_tg_pid" 2>/dev/null; exit 0; }
trap _tg_sigterm TERM
wait $_tg_pid

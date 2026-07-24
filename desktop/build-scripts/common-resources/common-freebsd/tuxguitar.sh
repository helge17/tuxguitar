#!/bin/sh

##SCRIPT DIR
TG_DIR=`dirname "$(realpath "$0")"`

##CHECK for missing packages and set lib path depending on GUI toolkit
needed_pkgs="openjdk11 alsa-plugins fluidsynth lilv suil"
missing_pkgs=""
if [ -f ${TG_DIR}/lib/tuxguitar-ui-toolkit-swt.jar ]; then
    needed_pkgs="swt webkit2-gtk_40 $needed_pkgs"
    CLASSPATH=${CLASSPATH}:/usr/local/share/java/classes/swt.jar
elif [ -f ${TG_DIR}/lib/tuxguitar-ui-toolkit-jfx.jar ]; then
    needed_pkgs="openjfx14 gcc $needed_pkgs"
    LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/local/openjfx14/lib/
    CLASSPATH=${CLASSPATH}:/usr/local/openjfx14/lib/*
else
    echo "TuxGuitar package is broken!"
    exit 1
fi
for pkg in $needed_pkgs; do
    pkg info -q "$pkg" || missing_pkgs="$missing_pkgs $pkg"
done
if [ -n "$missing_pkgs" ]; then
    missing_pkgs=$(echo "$missing_pkgs" | xargs)
    echo "TuxGitar needs the following packages to work properly: $missing_pkgs"
    echo "Please install them with:"
    echo "  doas pkg install $missing_pkgs"
    exit 1
fi

##JAVA
JAVA=`which java`
##LIBRARY_PATH
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${TG_DIR}/lib/
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib/jni
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/local/lib
##CLASSPATH
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/*
CLASSPATH=${CLASSPATH}:${TG_DIR}/share/
CLASSPATH=${CLASSPATH}:${TG_DIR}/dist/
##MAINCLASS
MAINCLASS=app.tuxguitar.app.TGMainSingleton
##EXPORT VARS
export CLASSPATH
export LD_LIBRARY_PATH
##LAUNCH
${JAVA} -cp ":${CLASSPATH}" -Dtuxguitar.home.path="${TG_DIR}" -Dtuxguitar.share.path="share" -Djava.library.path="${LD_LIBRARY_PATH}" ${MAINCLASS} "$@"

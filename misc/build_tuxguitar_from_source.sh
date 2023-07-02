#!/usr/bin/env -S bash -l

# Exit on error
#set -e
# Print commands
#set -x

COMMAND=`basename $0`" $@"

if [ -z $1 ] || [ $1 != "GO" ]; then
  echo
  echo "# USE AT YOUR OWN RISK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo "#"
  echo "# TuxGuitar build script for https://github.com/helge17/tuxguitar"
  echo "#"
  echo "# I use this script to build TuxGuitar for Linux and Windows on a Debian Bullseye system and on"
  echo "# remote FreeBSD and MacOS systems."
  echo "# The script heavily depends on my build environment, so examine it carefully and modify it to"
  echo "# your needs before starting it on your computer!"
  echo "# The script contains ugly hacks and modifies many source files, so only start it on a copy of"
  echo "# your sources!"
  echo "#"
  echo "# To start building type:"
  echo "#   $0 GO"
  echo "#"
  echo "# USE AT YOUR OWN RISK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo
  exit 1
fi

# Check if we are in the TuxGuitar source directory
if [ ! -e pom.xml ] || [ ! -d TuxGuitar ] || [ ! -d build-scripts ]; then
  echo
  echo "$COMMAND must be started in the TuxGuitar source directory!"
  echo
  exit 1
fi

# Build version
TGVERSION=`date +%Y`-`date +%m`-`date +%d`"-snapshot"
#TGVERSION=`grep 'CURRENT = new TGVersion' TuxGuitar-lib/src/org/herac/tuxguitar/util/TGVersion.java | awk -F '[(,)]' '{ print $2"."$3"."$4 }'`

# Software needed to build TuxGuitar is located in this directory 
SW_DIR=~/Software/TuxGuitar/tuxguitar_build_dependencies

# Binary packages are placed into this directory
DIST_DIR=`pwd`/00-Binary_Packages

mkdir -p $SW_DIR

echo
echo "### Host: "`hostname -s`" #################################################"
echo "### Building TuxGuitar from source. All Builds will be placed into the dirctory"
echo "### $DIST_DIR"
echo "### Host: "`hostname -s`" #################################################"
mkdir -p $DIST_DIR

function prepare_source {

echo -e "\n### Host: "`hostname -s`" ########### Hacks ...\n"

if [ ! -e $SW_DIR/VST_SDK/V2/VST_SDK_2.4 ]; then
  echo -e "\n# Download the Steinberg SDK (VST_SDK_2.4) ..."
  mkdir -p $SW_DIR/VST_SDK/V2
  ( cd $SW_DIR/VST_SDK/V2 && git clone https://github.com/R-Tur/VST_SDK_2.4.git )
  echo "# OK."
fi

echo -e "\n# Copy header files of the Steinberg SDK (VST_SDK_2.4) in place ..."
  cp -a $SW_DIR/VST_SDK/V2/VST_SDK_2.4/pluginterfaces/vst2.x/ build-scripts/native-modules/tuxguitar-synth-vst-linux-x86_64/include/
  cp -a $SW_DIR/VST_SDK/V2/VST_SDK_2.4/pluginterfaces/vst2.x/ build-scripts/native-modules/tuxguitar-synth-vst-windows-x86/include/
echo "# OK."

echo -e "\n# Change version from SNAPSHOT to $TGVERSION in config files..."
  find . \( -name "*.xml" -or -name "*.gradle"  -or -name "*.properties" -or -name control -or -name Info.plist \) -and -type f -exec sed -i "s/SNAPSHOT/$TGVERSION/" '{}' \;
  # Also set the version in the "Help - About" dialog
  sed -i "s/static final String RELEASE_NAME =.*/static final String RELEASE_NAME = (TGApplication.NAME + \" $TGVERSION\");/" TuxGuitar/src/org/herac/tuxguitar/app/view/dialog/about/TGAboutDialog.java
echo "# OK."

echo -e "\n# Remove some files and directories to find out if they are needed to build TuxGuitar..."
  rm -r TuxGuitar-android-gdrive-gdaa
  rm -r TuxGuitar-android-midimaster*
  #rm -r TuxGuitar-AudioUnit
  rm -r TuxGuitar-abc
  rm -r TuxGuitar-CoreAudio
  rm -r TuxGuitar-midi-input
  rm -r TuxGuitar-ui-toolkit-qt5
  rm -r TuxGuitar-viewer
echo "# OK."

echo -e "\n### Host: "`hostname -s`" ########### Hacks done."

}

function install_eclipse_swt {

# The swt-repo https://maven-eclipse.github.io/maven in pom.xml is outdated.
# I could not find any other repo for current swt versions, so swt must be installed manually.
# See https://github.com/pcarmona79/tuxguitar/issues/1

SWT_FILE=swt-$SWT_VERSION-$SWT_PLATFORM-$BUILD_ARCH
SWT_LINK=https://archive.eclipse.org/eclipse/downloads/drops4/R-$SWT_VERSION-$SWT_DATE/$SWT_FILE.zip
SWT_DEST=~/.m2/repository/org/eclipse/swt/org.eclipse.swt.${SWT_PLATFORM//-/.}.$BUILD_ARCH/$SWT_VERSION/org.eclipse.swt.${SWT_PLATFORM//-/.}.$BUILD_ARCH-$SWT_VERSION.jar
if [ -e $SWT_DEST ]; then
  echo -e "\n# $SWT_DEST is already installed."
else
  echo -e "\n# Installing $SWT_DEST from $SWT_LINK ..."
  if [ -e $SW_DIR/$SWT_FILE/swt.jar ]; then
    cd $SW_DIR/$SWT_FILE
  else
    [ ! -e $SW_DIR/$SWT_FILE.zip ] && wget $SWT_LINK -O $SW_DIR/$SWT_FILE.zip
    rm -rf $SW_DIR/$SWT_FILE && mkdir $SW_DIR/$SWT_FILE && cd $SW_DIR/$SWT_FILE && unzip ../$SWT_FILE.zip
  fi
  mvn install:install-file -Dfile=swt.jar -DgroupId=org.eclipse.swt -DartifactId=org.eclipse.swt.${SWT_PLATFORM//-/.}.$BUILD_ARCH -Dpackaging=jar -Dversion=$SWT_VERSION
  cd - > /dev/null
  echo "# OK."
fi

}

function get_java_win {

if [ -e $SW_DIR/$PA_JAVA ]; then
  echo -e "\n# Using Java for Windows in $SW_DIR/$PA_JAVA.\n"
else
  echo -e "\n# Getting Java for Windows $PA_JAVA.exe from $PA_LINK ...\n"
  [ ! -e $SW_DIR/$PA_JAVA.exe ] && wget $PA_LINK -O $SW_DIR/$PA_JAVA.exe
  rm -rf $SW_DIR/$PA_JAVA && mkdir $SW_DIR/$PA_JAVA && ( cd $SW_DIR/$PA_JAVA && 7z x -xr'!_DO NOT store your files here or in subfolders.txt' -xr'!$PLUGINSDIR' ../$PA_JAVA.exe )
  echo -e "\n# OK."
fi

}

function build_tg_for_linux {

install_eclipse_swt

# --batch-mode:      Disables output color to avoid problems when you redirect the output to a file
# -e clean verify:   Avoids problems when trying to build for Linux after having built for Windows
# -P native-modules: Build with native modules

for GUI_TK in swt jfx; do
  echo -e "\n### Host: "`hostname -s`" ########### Building Linux $GUI_TK $BUILD_ARCH TAR.GZ & DEB & RPM package ..."
  cd build-scripts/tuxguitar-linux-$GUI_TK-$BUILD_ARCH-deb
  mvn --batch-mode -e clean verify -P native-modules
  cp -a target/tuxguitar-$TGVERSION-linux-$GUI_TK-$BUILD_ARCH.deb $DIST_DIR
  cd - > /dev/null
  # Create RPM from DEB
  cd $DIST_DIR
  fakeroot alien --verbose --keep-version --scripts --to-rpm tuxguitar-$TGVERSION-linux-$GUI_TK-$BUILD_ARCH.deb
  cd - > /dev/null
  tar --owner=root --group=root --directory=build-scripts/tuxguitar-linux-$GUI_TK-$BUILD_ARCH/target -czf $DIST_DIR/tuxguitar-$TGVERSION-linux-$GUI_TK-$BUILD_ARCH.tar.gz tuxguitar-$TGVERSION-linux-$GUI_TK-$BUILD_ARCH
  echo -e "\n### Host: "`hostname -s`" ########### Building Linux $GUI_TK $BUILD_ARCH TAR.GZ & DEB & RPM package done.\n"
done

}

function build_tg_for_windows {

# To build the installer package you must install the VMware InstallBuilder for Linux from https://installbuilder.com/ and link the binary /opt/installbuilder-<version>/bin/builder to /usr/local/bin/builder
# E.g. download installbuilder-enterprise-21.9.0-linux-x64-installer.run and link with "sudo ln -s /opt/installbuilder-21.9.0/bin/builder /usr/local/bin/"

install_eclipse_swt
get_java_win

# Copy Java to get it integrated in the ZIP & INSTALL packages
rm -rf build-scripts/common-resources/common-windows/jre
cp -ai $SW_DIR/$PA_JAVA build-scripts/common-resources/common-windows/jre

for GUI_TK in swt jfx; do
  echo -e "\n### Host: "`hostname -s`" ########### Building Windows $GUI_TK $BUILD_ARCH ZIP & INSTALL (including Java) ..."
  cd build-scripts/tuxguitar-windows-$GUI_TK-$BUILD_ARCH-installer
  mvn --batch-mode -e clean verify -P native-modules
  cp -a target/tuxguitar-$TGVERSION-windows-$GUI_TK-$BUILD_ARCH-installer.exe $DIST_DIR
  cd - > /dev/null
  (
    cd build-scripts/tuxguitar-windows-$GUI_TK-$BUILD_ARCH/target
    zip -r $DIST_DIR/tuxguitar-$TGVERSION-windows-$GUI_TK-$BUILD_ARCH.zip tuxguitar-$TGVERSION-windows-$GUI_TK-$BUILD_ARCH
  )
  echo -e "\n### Host: "`hostname -s`" ########### Building Windows $GUI_TK $BUILD_ARCH ZIP & INSTALL done.\n"
done

}

function start_remote_bsd_build {

BUILD_HOST=$USER@172.16.208.131
echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for BSD $BUILD_ARCH TAR.GZ on $BUILD_HOST ..."
SRC_PATH=/home/$USER/tg-1.x-build-bsd
BIN_PATH=/home/$USER/bin
echo -e "\n# Copy sources to $BUILD_HOST:$SRC_PATH/ ..."
ssh $BUILD_HOST mkdir -p $SRC_PATH
rsync --verbose --archive --delete `pwd`/ $BUILD_HOST:$SRC_PATH/
echo "# OK."
echo -e "\n# Copy this script to $BUILD_HOST:$BIN_PATH/ ..."
ssh $BUILD_HOST mkdir -p $BIN_PATH
scp -p $0 $BUILD_HOST:$BIN_PATH/
echo "# OK."
echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for BSD $BUILD_ARCH TAR.GZ done."

ssh $BUILD_HOST "cd $SRC_PATH && $BIN_PATH/$COMMAND"
scp -p $BUILD_HOST:$SRC_PATH/00-Binary_Packages/tuxguitar-$TGVERSION-freebsd-swt-$BUILD_ARCH.tar.gz $DIST_DIR

}

function build_tg_for_bsd {

install_eclipse_swt

#for GUI_TK in swt jfx; do
for GUI_TK in swt; do
  echo -e "\n### Host: "`hostname -s`" ########### Building BSD $GUI_TK $BUILD_ARCH TAR.GZ ..."
  cd build-scripts/tuxguitar-freebsd-$GUI_TK-$BUILD_ARCH
  mvn --batch-mode -e clean verify -P native-modules
  cd - > /dev/null
  tar --uname=root --gname=root --directory=build-scripts/tuxguitar-freebsd-$GUI_TK-$BUILD_ARCH/target -czf $DIST_DIR/tuxguitar-$TGVERSION-freebsd-$GUI_TK-$BUILD_ARCH.tar.gz tuxguitar-$TGVERSION-freebsd-$GUI_TK-$BUILD_ARCH
  echo -e "\n### Host: "`hostname -s`" ########### Building BSD $GUI_TK $BUILD_ARCH TAR.GZ done.\n"
done

}

function start_remote_macos_build {

BUILD_HOST=$USER@172.16.208.132
echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for MacOS $BUILD_ARCH APP on $BUILD_HOST ..."
SRC_PATH=/Users/$USER/tg-1.x-build-macos
BIN_PATH=/Users/$USER/bin
echo -e "\n# Copy sources to $BUILD_HOST:$SRC_PATH/ ..."
ssh $BUILD_HOST mkdir -p $SRC_PATH
rsync --verbose --archive --delete `pwd`/ $BUILD_HOST:$SRC_PATH/
echo "# OK."
echo -e "\n# Copy this script to $BUILD_HOST:$BIN_PATH/ ..."
ssh $BUILD_HOST mkdir -p $BIN_PATH
scp -p $0 $BUILD_HOST:$BIN_PATH/
echo "# OK."
echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for MacOS $BUILD_ARCH APP done."

ssh $BUILD_HOST "cd $SRC_PATH && $BIN_PATH/$COMMAND"
scp -p $BUILD_HOST:$SRC_PATH/00-Binary_Packages/tuxguitar-$TGVERSION-macosx-swt-cocoa-$BUILD_ARCH.app.tar.gz $DIST_DIR

}

function build_tg_for_macos {

install_eclipse_swt

#for GUI_TK in swt jfx; do
for GUI_TK in swt; do
  echo -e "\n### Host: "`hostname -s`" ########### Building MacOS $GUI_TK $BUILD_ARCH APP ..."
  cd build-scripts/tuxguitar-macosx-$GUI_TK-cocoa-$BUILD_ARCH
  mvn --batch-mode -e clean verify

  # Copy locally installed openjdk (from Homebrew) to get it integrated in the APP.TAR.GZ packages
  mkdir target/tuxguitar-$TGVERSION-macosx-$GUI_TK-cocoa-$BUILD_ARCH.app/Contents/MacOS/jre
  cp -ai /usr/local/opt/openjdk/* target/tuxguitar-$TGVERSION-macosx-$GUI_TK-cocoa-$BUILD_ARCH.app/Contents/MacOS/jre

  tar --uname=root --gname=root --directory=target -czf $DIST_DIR/tuxguitar-$TGVERSION-macosx-$GUI_TK-cocoa-$BUILD_ARCH.app.tar.gz tuxguitar-$TGVERSION-macosx-$GUI_TK-cocoa-$BUILD_ARCH.app
  cd - > /dev/null
  echo -e "\n### Host: "`hostname -s`" ########### Building MacOS $GUI_TK $BUILD_ARCH APP done.\n"
done

}

function build_tg_for_android {

# Install Android Studio from https://developer.android.com/studio/
# Android Studio    downloaded to $SW_DIR/android-studio/android-studio-2020.3.1.25-linux.tar.gz   & installed in $SW_DIR/android-studio/android-studio-2020.3.1.25-linux
# Android SDK Tools downloaded to $SW_DIR/android-studio/commandlinetools-linux-7583922_latest.zip & installed in $SW_DIR/android-studio/android-studio-2020.3.1.25-linux/cmdline-tools/latest/
# Initial setup:
#   export ANDROID_HOME=$SW_DIR/android-studio/android-studio-2020.3.1.25-linux
#   cd $ANDROID_HOME/bin && ./studio.sh
# Agree to all licenses:
#   yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
# Create signature key:
#   cd ~/Software/TuxGuitar/
#   pwgen 12 1 > github_helge17_apk-sign.storepass
#   $ANDROID_HOME/jre/bin/keytool -genkeypair -keyalg RSA -dname 'CN=helge17' -v -keystore github_helge17_apk-sign.keystore -storepass:file github_helge17_apk-sign.storepass -validity 36500 -alias tuguitar

echo -e "\n### Host: "`hostname -s`" ########### Building Android APK ...\n"
cd build-scripts/tuxguitar-android
export ANDROID_HOME=$SW_DIR/android-studio/android-studio-2020.3.1.25-linux
./gradlew                  # Hiermit wird die benötigte Gradle-Version und sonstiges Zeug ins Verzeichnis .gradle/ installiert.
./gradlew assembleRelease  # Hier wird das APK gebaut
cp -a apk/build/outputs/apk/release/tuxguitar-android-$TGVERSION-release-unsigned.apk $DIST_DIR
cd $DIST_DIR
cp -a tuxguitar-android-$TGVERSION-release-unsigned.apk tuxguitar-android-$TGVERSION-release-signed.apk
$ANDROID_HOME/build-tools/30.0.3/apksigner sign --ks-key-alias tuguitar --ks $HOME/Software/TuxGuitar/github_helge17_apk-sign.keystore --ks-pass file:$HOME/Software/TuxGuitar/github_helge17_apk-sign.storepass tuxguitar-android-$TGVERSION-release-signed.apk
# Install apk on a phone connected via USB
#$ANDROID_HOME/platform-tools/adb install tuxguitar-android-$TGVERSION-release-signed.apk
echo -e "\n### Host: "`hostname -s`" ########### Building Android APK done.\n"

}

# Prepare source code. This is done on Linux for alle platforms.
[ `uname` == Linux ] && prepare_source

# BSD 64 bit x86_64 build
BUILD_ARCH=x86_64
SWT_VERSION=4.13
SWT_DATE=201909161045
SWT_PLATFORM=gtk-linux
[ `uname` == Linux ] && start_remote_bsd_build
[ `uname` == FreeBSD ] && build_tg_for_bsd

# MacOS 64 bit x86_64 build
BUILD_ARCH=x86_64
SWT_VERSION=4.13
SWT_DATE=201909161045
SWT_PLATFORM=cocoa-macosx
[ `uname` == Linux ] && start_remote_macos_build
[ `uname` == Darwin ] && build_tg_for_macos

# Linux 64 bit x86_64 build
BUILD_ARCH=x86_64
SWT_VERSION=4.13
SWT_DATE=201909161045
SWT_PLATFORM=gtk-linux
[ `uname` == Linux ] && build_tg_for_linux

# Windows 64 bit x86_64 build
BUILD_ARCH=x86_64
SWT_VERSION=4.13
SWT_DATE=201909161045
SWT_PLATFORM=win32-win32
# Get Java for Windows 64 bit from https://portableapps.com/apps/utilities/OpenJDK64
PA_JAVA=OpenJDK64_17.0.1-12.paf
PA_LINK=https://download3.portableapps.com/portableapps/OpenJDK64/$PA_JAVA.exe
[ `uname` == Linux ] && build_tg_for_windows

# Android build
[ `uname` == Linux ] && build_tg_for_android

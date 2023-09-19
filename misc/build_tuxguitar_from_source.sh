#!/usr/bin/env -S bash -l

# Exit on error
#set -e
# Print commands
#set -x

COMMAND=`basename $0`" $@"

function usage {
  echo
  echo "# USE AT YOUR OWN RISK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo "#"
  echo "# TuxGuitar build script for https://github.com/helge17/tuxguitar"
  echo "#"
  echo "# I use this script to build TuxGuitar for Linux, Windows and Android on Debian Bullseye and"
  echo "# on remote FreeBSD and MacOS systems."
  echo "# The script heavily depends on my build environment, so examine it carefully and modify it to"
  echo "# your needs before starting it on your computer!"
  echo "# The script contains ugly hacks and modifies many source files, so only start it on a copy of"
  echo "# your sources!"
  echo "#"
  echo "# Usage: "`basename $0`" [OPTIONS]"
  echo "#"
  echo "# -l     Build for Linux"
  echo "# -w     Build for Windows"
  echo "# -m     Build for MacOS"
  echo "# -b     Build for FreeBSD"
  echo "# -a     Build for Android"
  echo "# -A     Build for all"
  echo "#"
  echo "# -g     Create a new Github release and upload the builds"
  echo "#"
  echo "# By default (without the -r option) the version of the build will be <YYYY-MM-DD>-snapshot,"
  echo "# e.g. 2023-08-25-snapshot. This string will be part of the archive and package file names"
  echo '# and shows up in the TuxGuitar "About" dialog.'
  echo "#"
  echo "# -r <release>"
  echo "#        Set the build version to the given <release> string, e.g. 1.6.0beta1."
  echo '#        If you use "SRC" as <release>, then the build version is extracted from the source'
  echo "#        code, e.g. 1.6.0."
  echo "#"
  echo "# -h     Display this help message and exit."
  echo "#"
  echo "# USE AT YOUR OWN RISK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo
}

# Default build version
TGVERSION=`date +%Y`-`date +%m`-`date +%d`"-snapshot"

# Parse command line options
while getopts "lwmbaAghr:" CMDopt; do
  case $CMDopt in
    l) build_linux=1;;
    w) build_windows=1;;
    m) build_macos=1;;
    b) build_bsd=1;;
    a) build_android=1;;
    A) build_linux=1
       build_windows=1
       build_macos=1
       build_bsd=1
       build_android=1
       ;;
    g) copy_to_github=1;;
    r) TGVERSION="$OPTARG"
       [ $TGVERSION == SRC ] && TGVERSION=`grep 'CURRENT = new TGVersion' TuxGuitar-lib/src/org/herac/tuxguitar/util/TGVersion.java | awk -F '[(,)]' '{ print $2"."$3"."$4 }'`
       ;;
    *) usage
       [ $CMDopt == "h" ] && exit || exit 1
       ;;
  esac
done

if [ "$#" -lt 1 ]; then
  usage
  exit 1
fi

# Check if we are in the TuxGuitar source directory
if [ ! -e pom.xml ] || [ ! -d TuxGuitar ] || [ ! -d build-scripts ]; then
  echo "$COMMAND must be started in the TuxGuitar source directory!"
  echo
  exit 1
fi

# TuxGuitar source directory
SRC_DIR=`pwd`

# Binary packages are placed into this directory
DIST_DIR=`pwd`/00-Binary_Packages

# Software needed to build TuxGuitar is located in this directory
SW_DIR=~/Software/TuxGuitar/tuxguitar_build_dependencies

mkdir -p $SW_DIR

echo
echo "### Host: "`hostname -s`" #################################################"
echo "### Build version: $TGVERSION"
echo "### Building TuxGuitar from source. All Builds will be placed into the dirctory"
echo "### $DIST_DIR"
echo "### Host: "`hostname -s`" #################################################"
mkdir -p $DIST_DIR

function prepare_source {

if [ -e .build-version ]; then
  OLDVERSION=`cat .build-version`
  if [ "$TGVERSION" == "$OLDVERSION" ]; then
    echo -e "\n# Skipping hacks, sources are already prepared to build version $TGVERSION.\n"
    return
  else
    echo -e "\n# Build version was already set to $OLDVERSION in a previous run of `basename $0`."
    echo "# Please confirm that you want to build version $OLDVERSION with the option '-r $OLDVERSION'."
    echo -e "\n### Host: "`hostname -s`" ########### Aborting build.\n"
    exit 1
  fi
fi

echo -e "\n### Host: "`hostname -s`" ########### Hacks ...\n"

if [ ! -e $SW_DIR/VST_SDK/V2/VST_SDK_2.4 ]; then
  echo -e "\n# Download the Steinberg SDK (VST_SDK_2.4) ..."
  mkdir -p $SW_DIR/VST_SDK/V2
  ( cd $SW_DIR/VST_SDK/V2 && git clone https://github.com/R-Tur/VST_SDK_2.4.git )
  echo "# OK."
fi

echo "# Copy header files of the Steinberg SDK (VST_SDK_2.4) in place ..."
  cp -Ta $SW_DIR/VST_SDK/V2/VST_SDK_2.4/pluginterfaces/vst2.x/ build-scripts/native-modules/tuxguitar-synth-vst-linux-x86_64/include/
  cp -Ta $SW_DIR/VST_SDK/V2/VST_SDK_2.4/pluginterfaces/vst2.x/ build-scripts/native-modules/tuxguitar-synth-vst-windows-x86/include/
echo "# OK."

echo -e "\n# Change build version from SNAPSHOT to $TGVERSION in config files..."
  find . \( -name "*.xml" -or -name "*.gradle"  -or -name "*.properties" -or -name control -or -name Info.plist \) -and -type f -exec sed -i "s/SNAPSHOT/$TGVERSION/" '{}' \;
  # Also set the version in the "Help - About" dialog
  sed -i "s/static final String RELEASE_NAME =.*/static final String RELEASE_NAME = (TGApplication.NAME + \" $TGVERSION\");/" TuxGuitar/src/org/herac/tuxguitar/app/view/dialog/about/TGAboutDialog.java
echo "# OK."

echo -e "\n# Remove some files and directories to find out if they are needed to build TuxGuitar..."
  rm -rf TuxGuitar-android-gdrive-gdaa
  rm -rf TuxGuitar-android-midimaster*
  #rm -rf TuxGuitar-AudioUnit
  rm -rf TuxGuitar-abc
  rm -rf TuxGuitar-community
  rm -rf TuxGuitar-CoreAudio
  rm -rf TuxGuitar-midi-input
  rm -rf TuxGuitar-ui-toolkit-qt5
  rm -rf TuxGuitar-viewer
echo "# OK."

echo $TGVERSION > .build-version

echo -e "\n### Host: "`hostname -s`" ########### Hacks done."
echo

}

function install_eclipse_swt {

# The swt-repo https://maven-eclipse.github.io/maven in pom.xml is outdated.
# I could not find any other repo for current swt versions, so swt must be installed manually.
# See https://github.com/pcarmona79/tuxguitar/issues/1

SWT_FILE=swt-$SWT_VERSION-$SWT_PLATFORM-$BUILD_ARCH
SWT_LINK=https://archive.eclipse.org/eclipse/downloads/drops4/R-$SWT_VERSION-$SWT_DATE/$SWT_FILE.zip
SWT_DEST=~/.m2/repository/org/eclipse/swt/org.eclipse.swt.${SWT_PLATFORM//-/.}.$BUILD_ARCH/$SWT_VERSION/org.eclipse.swt.${SWT_PLATFORM//-/.}.$BUILD_ARCH-$SWT_VERSION.jar
if [ -e $SWT_DEST ]; then
  echo "# $SWT_DEST is already installed."
else
  echo "# Installing $SWT_DEST from $SWT_LINK ..."
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
  echo -e "\n### Host: "`hostname -s`" ########### Building Linux $GUI_TK $BUILD_ARCH TAR.GZ & DEB & RPM package ...\n"
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

# Create selfsigned Windows code signing certificate:
#   cd ~/Software/TuxGuitar/
#   openssl genrsa -out github_helge17_win-sign.key 4096
#   openssl req -new -subj '/CN=helge17/' -x509 -days 36500 -key github_helge17_win-sign.key -out github_helge17_win-sign.crt
#   pwgen 12 1 > github_helge17_win-sign.p12pass
#   openssl pkcs12 -export -out github_helge17_win-sign.p12 -passout file:github_helge17_win-sign.p12pass -inkey github_helge17_win-sign.key -in github_helge17_win-sign.crt
# Show content of the certificate and the pkcs12 container
#   openssl x509 -in github_helge17_win-sign.crt -noout -text
#   openssl pkcs12 -in github_helge17_win-sign.p12 -passin file:github_helge17_win-sign.p12pass -passout 'pass:DummyPass'

# Set certificate for InstallBuilder
export WIN_SIGN_P12_FILE=~/Software/TuxGuitar/github_helge17_win-sign.p12
export WIN_SIGN_P12_PASS=`cat ~/Software/TuxGuitar/github_helge17_win-sign.p12pass`

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
echo -e "\n# Copy sources to $BUILD_HOST:$SRC_PATH/ ..."
ssh $BUILD_HOST mkdir -p $SRC_PATH
rsync --verbose --archive --delete `pwd`/ $BUILD_HOST:$SRC_PATH/
echo "# OK."
echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for BSD $BUILD_ARCH TAR.GZ done."

ssh $BUILD_HOST "cd $SRC_PATH && misc/$COMMAND"
scp -p $BUILD_HOST:$SRC_PATH/00-Binary_Packages/tuxguitar-$TGVERSION-freebsd-*-$BUILD_ARCH.tar.gz $DIST_DIR

}

function build_tg_for_bsd {

install_eclipse_swt

#for GUI_TK in swt jfx; do
for GUI_TK in swt; do
  echo -e "\n### Host: "`hostname -s`" ########### Building BSD $GUI_TK $BUILD_ARCH TAR.GZ ..."
  cd build-scripts/tuxguitar-freebsd-$GUI_TK-$BUILD_ARCH
  mvn --batch-mode -e clean verify -P native-modules
  cd - > /dev/null
  rm -f $DIST_DIR/*
  tar --uname=root --gname=root --directory=build-scripts/tuxguitar-freebsd-$GUI_TK-$BUILD_ARCH/target -czf $DIST_DIR/tuxguitar-$TGVERSION-freebsd-$GUI_TK-$BUILD_ARCH.tar.gz tuxguitar-$TGVERSION-freebsd-$GUI_TK-$BUILD_ARCH
  echo -e "\n### Host: "`hostname -s`" ########### Building BSD $GUI_TK $BUILD_ARCH TAR.GZ done.\n"
done

}

function start_remote_macos_build {

BUILD_HOST=$USER@172.16.208.132
echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for MacOS $BUILD_ARCH APP on $BUILD_HOST ..."
SRC_PATH=/Users/$USER/tg-1.x-build-macos
echo -e "\n# Copy sources to $BUILD_HOST:$SRC_PATH/ ..."
ssh $BUILD_HOST mkdir -p $SRC_PATH
rsync --verbose --archive --delete `pwd`/ $BUILD_HOST:$SRC_PATH/
echo "# OK."
echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for MacOS $BUILD_ARCH APP done."

ssh $BUILD_HOST "cd $SRC_PATH && misc/$COMMAND"
scp -p $BUILD_HOST:$SRC_PATH/00-Binary_Packages/tuxguitar-$TGVERSION-macosx-*-cocoa-$BUILD_ARCH.app.tar.gz $DIST_DIR

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

  rm -f $DIST_DIR/*
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

function copy_to_github {

  # To edit the repository on Github with the gh command, you have to authenticate first. See 'man gh-auth' for details.

  echo -e "### Host: "`hostname -s`" ########### Creating a new Github release and upload the builds $TGVERSION to Github ...\n"

  cd $SRC_DIR

  if [ -z "$(gh release list | grep "^$TGVERSION\s")" ]; then
    echo "# Creating Github pre-release draft $TGVERSION ..."
    REL_NOTES=$'The Windows packages include OpenJDK from portableapps.com.\nThe MacOS package includes OpenJDK from brew.sh.'
    gh release create --prerelease --draft --title $TGVERSION --notes "$REL_NOTES" $TGVERSION
    # It may take a few sec until the release is ready
    sleep 5
    echo "# OK."
  else
    echo "# Skipping creation of release $TGVERSION on Github, release already exists."
  fi
  echo

  cd $DIST_DIR

  cat /dev/null > tuxguitar-$TGVERSION.sha256
  shopt -s nullglob
  for TG_FILE in *.tar.gz *.deb *.rpm *.zip *.exe *-signed.apk; do
    echo "# Creating sha256 checksum and uploading file $TG_FILE to release $TGVERSION ..."
    sha256sum $TG_FILE >> tuxguitar-$TGVERSION.sha256
    if gh release upload --clobber $TGVERSION $TG_FILE; then
      echo "# OK."
    else
     echo "# Upload of file $TG_FILE failed!"
    fi
    echo
  done
  echo "# Uploading file tuxguitar-$TGVERSION.sha256 to release $TGVERSION ..."
  if gh release upload --clobber $TGVERSION tuxguitar-$TGVERSION.sha256; then
    echo "# OK."
  else
   echo "# Upload of file tuxguitar-$TGVERSION.sha256 failed!"
  fi
  echo

  echo "# This draft won’t be seen by the public unless it’s published."
  echo "# To published the draft, issue the command:"
  echo "# $ gh release edit $TGVERSION --draft=false"
  echo "# Then also the release tag will be created on Github."
  echo -e "\n### Host: "`hostname -s`" ########### New Github pre-release draft created and uploads to Github done.\n"

}

# Prepare source code. This is done on Linux for all platforms.
[ `uname` == Linux ] && prepare_source

# First, we start the remote builds to avoid copying all locally created binaries to the remote hosts.

# BSD 64 bit x86_64 build
if [ $build_bsd ]; then
  BUILD_ARCH=x86_64
  SWT_VERSION=4.13
  SWT_DATE=201909161045
  SWT_PLATFORM=gtk-linux
  [ `uname` == Linux ] && start_remote_bsd_build
  [ `uname` == FreeBSD ] && build_tg_for_bsd
fi

# MacOS 64 bit x86_64 build
if [ $build_macos ]; then
  BUILD_ARCH=x86_64
  SWT_VERSION=4.13
  SWT_DATE=201909161045
  SWT_PLATFORM=cocoa-macosx
  [ `uname` == Linux ] && start_remote_macos_build
  [ `uname` == Darwin ] && build_tg_for_macos
fi

# Then come the local builds.

# Linux 64 bit x86_64 build
if [ $build_linux ]; then
  BUILD_ARCH=x86_64
  SWT_VERSION=4.13
  SWT_DATE=201909161045
  SWT_PLATFORM=gtk-linux
  [ `uname` == Linux ] && build_tg_for_linux
fi

# Windows 64 bit x86_64 build
if [ $build_windows ]; then
  BUILD_ARCH=x86_64
  SWT_VERSION=4.13
  SWT_DATE=201909161045
  SWT_PLATFORM=win32-win32
  # Get Java for Windows 64 bit from https://portableapps.com/apps/utilities/OpenJDK64
  PA_JAVA=OpenJDK64_17.0.1-12.paf
  PA_LINK=https://download3.portableapps.com/portableapps/OpenJDK64/$PA_JAVA.exe
  [ `uname` == Linux ] && build_tg_for_windows
fi

# Android build
if [ $build_android ]; then
  [ `uname` == Linux ] && build_tg_for_android
fi

# Create a new Github release and upload the builds
if [ $copy_to_github ]; then
  [ `uname` == Linux ] && copy_to_github
fi

#!/usr/bin/env -S bash -l

# Exit on error
#set -e
# Print commands
#set -x

COMMAND=`basename $0`" $@"
SCRIPT_DIR=`dirname $0`
SCRIPT_DIR=`cd "$SCRIPT_DIR"; pwd`

# TuxGuitar source directory
SRC_DIR=$SCRIPT_DIR/..

cd $SRC_DIR

# Current git branch
GIT_BRANCH=`git rev-parse --abbrev-ref HEAD`

function usage {
  echo
  echo "# USE AT YOUR OWN RISK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo "#"
  echo "# TuxGuitar build script for https://github.com/helge17/tuxguitar"
  echo "#"
  echo "# I use this script to build TuxGuitar for Linux, Windows and Android on Debian 12 (Bookworm)"
  echo "# and on remote FreeBSD and MacOS systems."
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
  echo "# -g, -G Creates a new Github release and uploads the builds. The release will be marked as a"
  echo "#        pre-release."
  echo "#        -g keeps the release in draft status after the upload. The draft won’t be seen by the"
  echo "#           public unless it is published manually, either in the Github web interface or with"
  echo "#           the command"
  echo "#           $ gh release edit <release> --draft=false"
  echo "#           Then also the release tag will be created on Github."
  echo "#           After that you also have to sync the website manually."
  echo "#        -G publishes the release immediately and syncs the website folder to the webserver if"
  echo "#           all files were successfully uploaded. If one or more uploads failed, the release"
  echo "#           remains in draft status and the website is not synced (as with -g)."
  echo "#"
  echo "# By default (without the -r option) the version of the build will be <YYYY-MM-DD>-<git-branch>,"
  echo "# currently that would be $TGVERSION. This string will be part of the archive and package"
  echo '# file names and shows up in the TuxGuitar "About" dialog and the help pages.'
  echo "#"
  echo "# -r <release>"
  echo "#        Sets the build version to the given <release> string, e.g. 1.6.0beta1."
  echo "#        If you use REL or the current source version $TGSRCVER as <release>, then a few"
  echo '#        preliminary checks will be done before the build starts.'
  echo "#"
  echo "# -h     Display this help message and exit."
  echo "#"
  echo "# USE AT YOUR OWN RISK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo
}

# Current source version
TGSRCVER=`grep 'CURRENT = new TGVersion' common/TuxGuitar-lib/src/org/herac/tuxguitar/util/TGVersion.java | awk -F '[(,)]' '{ print $2"."$3"."$4 }'`

# Default build version
TGVERSION=`date +%Y`-`date +%m`-`date +%d`"-$GIT_BRANCH"

# Parse command line options
while getopts "lwmbaAgGhr:" CMDopt; do
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
    G) copy_to_github=1
       publish_release=1
       ;;
    r) TGVERSION="$OPTARG"
       if [ $TGVERSION == $TGSRCVER ]; then
         echo -e "\n# The given build version '-r $TGVERSION' is the same as the version in the source code. Asuming '-r REL'."
       elif [ $TGVERSION == "REL" ]; then
         TGVERSION=$TGSRCVER
       fi
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

function release_checks_before_prepare_source {

  echo -e "\n### Host: "`hostname -s`" ########### Building official release $TGVERSION, starting release checks ...\n"

  echo -n "# Checking newest entry in CHANGES file ... "
  if [ -n "$(head -1 CHANGES | grep "^TuxGuitar ${TGVERSION//\./\\\.} ([0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]) changes:$")" ]; then
    echo -e "found:"
    head -1 CHANGES
    echo -e "# OK.\n"
  else
    echo -e "first line not matching \"TuxGuitar $TGVERSION (YYYY-MM-DD) changes:\"."
    echo -e "\n### Aborting build.\n"
    exit 1
  fi

  echo -n "# Checking download and help links on website ... "
  if [ "$(grep "Latest stable version ${TGVERSION//\./\\\.}.*${TGVERSION//\./\\\.}" website/index.html | wc -l)" -eq 2 ]; then
    echo -e "found:"
    grep "Latest stable version ${TGVERSION//\./\\\.}.*${TGVERSION//\./\\\.}" website/index.html
    echo -e "# OK.\n"
  else
    echo -e "not found:"
    grep "Latest stable version ${TGVERSION//\./\\\.}.*${TGVERSION//\./\\\.}" website/index.html
    echo -e "\n### Aborting build.\n"
    exit 1
  fi

  echo -n "# Checking for local source changes ... "
  if [ -n "$(git status --ignored=traditional --porcelain)" ]; then
    echo -e "found:\n"
    git status --ignored=traditional
    echo -e "\n### Aborting build.\n"
    exit 1
  else
    echo -e "none found, OK.\n"
  fi

  echo -n "# Checking for local commits not pushed to origin/master ... "
  if [ -n "$(git log origin/master..HEAD)" ]; then
    echo -e "found:\n"
    git log origin/master..HEAD
    echo -e "\n### Aborting build.\n"
    exit 1
  else
    echo -e "none found, OK.\n"
  fi

  echo -n "# Fetching origin/master to local branch ... "
  git fetch
  echo -e "OK.\n"

  echo -n "# Checking for commits in origin/master not pulled locally ... "
  if [ -n "$(git log HEAD..origin/master)" ]; then
    echo -e "found:\n"
    git log HEAD..origin/master
    echo -e "\n### Aborting build.\n"
    exit 1
  else
    echo -e "none found, OK.\n"
  fi

  echo -e "\n### Host: "`hostname -s`" ########### Successfully finished release checks."

}

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

echo -e "\n# Change build version from 9.99-SNAPSHOT to $TGVERSION in config files ..."
  find . \( -name "*.xml" -or -name "*.gradle"  -or -name "*.properties" -or -name "*.html" -or -name control -or -name Info.plist -or -name CHANGES \) -and -not -path "./website/*" -and -type f -exec sed -i "s/9.99-SNAPSHOT/$TGVERSION/" '{}' \;
  # Also set the version in the "Help - About" dialog
  sed -i "s/static final String RELEASE_NAME =.*/static final String RELEASE_NAME = (TGApplication.NAME + \" $TGVERSION\");/" desktop/TuxGuitar/src/org/herac/tuxguitar/app/view/dialog/about/TGAboutDialog.java
echo "# OK."

echo $TGVERSION > .build-version

echo -e "\n### Host: "`hostname -s`" ########### Hacks done."
echo

}

function release_checks_after_prepare_source {

  echo -n "# Searching differences between help files in package and local website folder ... "
  if `diff -qr desktop/TuxGuitar/share/help/ website/files/$TGVERSION/desktop/help/ > /dev/null 2>&1`; then
    echo -e "none found, OK.\n"
  else
    echo -e "found:\n"
    diff -r desktop/TuxGuitar/share/help/ website/files/$TGVERSION/desktop/help/
    echo -e "\n### Aborting build.\n"
  fi

}

function install_eclipse_swt {

SWT_DEST=~/.m2/repository/org/eclipse/swt/org.eclipse.swt.${SWT_PLATFORM//-/.}/$SWT_VERSION/org.eclipse.swt.${SWT_PLATFORM//-/.}-$SWT_VERSION.jar

if [ -e $SWT_DEST ]; then

  echo "# $SWT_DEST is already installed."

else

  if [ "$SWT_PLATFORM" = 'gtk-linux' ] || [ "$SWT_PLATFORM" = 'cocoa-macosx' ] || [ "$SWT_PLATFORM" = 'win32-win32' ]; then

    # I could not find any repo for current SWT versions, so SWT must be installed manually.
    # See https://github.com/pcarmona79/tuxguitar/issues/1
    SWT_NAME=swt-$SWT_VERSION-$SWT_PLATFORM-`uname -m`
    SWT_LINK=https://archive.eclipse.org/eclipse/downloads/drops4/R-$SWT_VERSION-$SWT_DATE/$SWT_NAME.zip
    SWT_JARF=$SW_DIR/$SWT_NAME/swt.jar

    echo "# Installing $SWT_DEST from $SWT_LINK ..."
    if [ ! -e $SWT_JARF ]; then
      [ ! -e $SW_DIR/$SWT_NAME.zip ] && wget $SWT_LINK -O $SW_DIR/$SWT_NAME.zip
      rm -rf $SW_DIR/$SWT_NAME && mkdir $SW_DIR/$SWT_NAME && unzip $SW_DIR/$SWT_NAME.zip -d $SW_DIR/$SWT_NAME
    fi

  elif [ "$SWT_PLATFORM" = 'gtk-freebsd' ]; then

    # On FreeBSD we use SWT from the OS
    SWT_JARF=/usr/local/share/java/classes/swt.jar

  fi

  mvn install:install-file -Dfile=$SWT_JARF -DgroupId=org.eclipse.swt -DartifactId=org.eclipse.swt.${SWT_PLATFORM//-/.} -Dpackaging=jar -Dversion=$SWT_VERSION
  echo "# OK."

fi

}

function install_openjfx {

# On FreeBSD we also install JFX from the OS
if [ "$SWT_PLATFORM" = 'gtk-freebsd' ]; then

  JFX_DIR=$SW_DIR/OpenJFX

  for JFX_PKG in javafx-base javafx-controls javafx-graphics javafx-web javafx-media; do

    JFX_DEST=~/.m2/repository/org/openjfx/$JFX_PKG/$JFX_VERSION/$JFX_PKG-$JFX_VERSION-freebsd.jar
    JFX_JARF=/usr/local/openjfx14/lib/${JFX_PKG//-/.}.jar

    if [ -e $JFX_DEST ]; then

      echo "# $JFX_DEST is already installed."

    else

      echo "# Installing $JFX_DEST from $JFX_JARF ..."
      mkdir -p $JFX_DIR
      if [ ! -e $JFX_DIR/$JFX_PKG-$JFX_VERSION.pom ]; then
        wget -P $JFX_DIR https://repo.maven.apache.org/maven2/org/openjfx/$JFX_PKG/$JFX_VERSION/$JFX_PKG-$JFX_VERSION.pom
        sed -i '.orig' -e 's/${javafx.platform}/freebsd/' $JFX_DIR/$JFX_PKG-$JFX_VERSION.pom
      fi
      mvn install:install-file -Dfile=$JFX_JARF -DpomFile=$JFX_DIR/$JFX_PKG-$JFX_VERSION.pom -DartifactId=$JFX_PKG -DgroupId=org.openjfx -Dpackaging=jar -Dversion=$JFX_VERSION -Dclassifier=freebsd
      echo "# OK."

    fi

  done

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

BUILD_ARCH=`dpkg --print-architecture`

install_eclipse_swt

# --batch-mode:      Disables output color to avoid problems when you redirect the output to a file
# -e clean verify:   Avoids problems when trying to build for Linux after having built for Windows
# -P native-modules: Build with native modules

for GUI_TK in swt jfx; do
  echo -e "\n### Host: "`hostname -s`" ########### Building Linux $GUI_TK $BUILD_ARCH TAR.GZ & DEB & RPM package ...\n"

  cd desktop/build-scripts/tuxguitar-linux-$GUI_TK-deb
  mvn --batch-mode -e clean verify -P native-modules

  TARGET=tuxguitar-$TGVERSION-linux-$GUI_TK

  cp -a target/$TARGET.deb $DIST_DIR/$TARGET-$BUILD_ARCH.deb
  cd - > /dev/null

  cd desktop/build-scripts/tuxguitar-linux-$GUI_TK
  rm -rf target/$TARGET-$BUILD_ARCH && mv -i target/$TARGET target/$TARGET-$BUILD_ARCH
  tar --owner=root --group=root --directory=target -czf $DIST_DIR/$TARGET-$BUILD_ARCH.tar.gz $TARGET-$BUILD_ARCH
  cd - > /dev/null

  # Create RPM from DEB
  cd $DIST_DIR
  fakeroot alien --verbose --keep-version --scripts --to-rpm $TARGET-$BUILD_ARCH.deb
  cd - > /dev/null

  echo -e "\n### Host: "`hostname -s`" ########### Building Linux $GUI_TK $BUILD_ARCH TAR.GZ & DEB & RPM package done.\n"
done

}

function build_tg_for_windows {

BUILD_ARCH=x86_64

# To build the installer package you must install the VMware InstallBuilder for Linux from https://installbuilder.com/ and link the binary /opt/installbuilder-<version>/bin/builder to /usr/local/bin/builder
# E.g. download and start installbuilder-enterprise-21.9.0-linux-x64-installer.run, then link with "sudo ln -s /opt/installbuilder-21.9.0/bin/builder /usr/local/bin/"

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
rm -rf desktop/build-scripts/common-resources/common-windows/jre
cp -ai $SW_DIR/$PA_JAVA desktop/build-scripts/common-resources/common-windows/jre

for GUI_TK in swt jfx; do
  echo -e "\n### Host: "`hostname -s`" ########### Building Windows $GUI_TK $BUILD_ARCH ZIP & INSTALL (including Java) ..."

  cd desktop/build-scripts/tuxguitar-windows-$GUI_TK-$BUILD_ARCH-installer
  # As we are building the Windows version on Linux, we explicitly deactivate the Linux profile and select the Windows profile manually to avoid confusion.
  mvn --batch-mode -e clean verify -P native-modules -P -platform-linux -P platform-windows

  TARGET=tuxguitar-$TGVERSION-windows-$GUI_TK-$BUILD_ARCH

  cp -a target/$TARGET-installer.exe $DIST_DIR
  cd - > /dev/null

  (
    cd desktop/build-scripts/tuxguitar-windows-$GUI_TK-$BUILD_ARCH/target
    zip -r $DIST_DIR/$TARGET.zip $TARGET
  )

  echo -e "\n### Host: "`hostname -s`" ########### Building Windows $GUI_TK $BUILD_ARCH ZIP & INSTALL done.\n"
done

}

function start_remote_bsd_build {

BUILD_HOST=$USER@172.16.208.131

echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for BSD TAR.GZ on $BUILD_HOST ..."
SRC_PATH=/home/$USER/tg-1.x-build-bsd
echo -e "\n# Copy sources to $BUILD_HOST:$SRC_PATH/ ..."
ssh $BUILD_HOST mkdir -p $SRC_PATH
rsync --verbose --archive --delete --exclude=00-Binary_Packages/* --delete-excluded `pwd`/ $BUILD_HOST:$SRC_PATH/
echo "# OK."
echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for BSD TAR.GZ done."

ssh $BUILD_HOST "cd $SRC_PATH && misc/$COMMAND"
scp -p $BUILD_HOST:$SRC_PATH/00-Binary_Packages/tuxguitar-$TGVERSION-freebsd-*-*.tar.gz $DIST_DIR

}

function build_tg_for_bsd {

BUILD_ARCH=`uname -m`

install_eclipse_swt
install_openjfx

for GUI_TK in swt jfx; do
  echo -e "\n### Host: "`hostname -s`" ########### Building BSD $GUI_TK $BUILD_ARCH TAR.GZ ..."

  cd desktop/build-scripts/tuxguitar-freebsd-$GUI_TK
  mvn --batch-mode -e clean verify -P native-modules

  TARGET=tuxguitar-$TGVERSION-freebsd-$GUI_TK

  # Copy local JFX libs into TAR.GZ package
  [ "$GUI_TK" == "jfx" ] && cp -a /usr/local/openjfx14/lib/*.so target/$TARGET/lib

  rm -rf target/$TARGET-$BUILD_ARCH && mv -i target/$TARGET target/$TARGET-$BUILD_ARCH
  tar --uname=root --gname=root --directory=target -czf $DIST_DIR/$TARGET-$BUILD_ARCH.tar.gz $TARGET-$BUILD_ARCH
  cd - > /dev/null

  echo -e "\n### Host: "`hostname -s`" ########### Building BSD $GUI_TK $BUILD_ARCH TAR.GZ done.\n"
done

}

function start_remote_macos_build {

# 172.16.208.132: MacOS 11 x86_64 (Big Sur)
# 172.16.208.133: MacOS 14 x86_64 (Sonoma)
BUILD_HOST=$USER@172.16.208.132

echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for MacOS APP on $BUILD_HOST ..."
SRC_PATH=/Users/$USER/tg-1.x-build-macos
echo -e "\n# Copy sources to $BUILD_HOST:$SRC_PATH/ ..."
ssh $BUILD_HOST mkdir -p $SRC_PATH
rsync --verbose --archive --delete --exclude=00-Binary_Packages/* --delete-excluded `pwd`/ $BUILD_HOST:$SRC_PATH/
echo "# OK."
echo -e "\n### Host: "`hostname -s`" ########### Preparing the build for MacOS APP done."

ssh $BUILD_HOST "cd $SRC_PATH && misc/$COMMAND"
# On my MacOS 14 VM, the outgoing transfer rate via scp is terribly slow.
# Without -X options:                 ~ 15KB/s  (always!!!)
# With -X nrequests=1 -X buffer=2048: ~ 250KB/s (bridged -> WLAN -> Linux system), 5MB/s (NAT -> local VMware host)
# The problem only exists in the outgoing direction and only for scp, but regardless of whether scp was started on the MacOS system or on the target system.
# Incoming transfers via scp are OK, outgoing transfers via https are also OK.
# Experimenting with the MTU size or other parameters of the network interfaces (NAT, bridged, fixed duplex and speed settings, ...) did not help.
# MacOS 11 is fine and the -X options do not harm.
scp -p -X nrequests=1 -X buffer=2048 $BUILD_HOST:$SRC_PATH/00-Binary_Packages/tuxguitar-$TGVERSION-macosx-*-cocoa-*.app.tar.gz $DIST_DIR

}

function build_tg_for_macos {

BUILD_ARCH=`uname -m`

install_eclipse_swt

for GUI_TK in swt jfx; do
  echo -e "\n### Host: "`hostname -s`" ########### Building MacOS $GUI_TK $BUILD_ARCH APP ..."

  cd desktop/build-scripts/tuxguitar-macosx-$GUI_TK-cocoa
  mvn --batch-mode -e clean verify

  TARGET=tuxguitar-$TGVERSION-macosx-$GUI_TK-cocoa

  # Copy locally installed openjdk (from Homebrew) to get it integrated in the APP.TAR.GZ packages
  mkdir target/$TARGET.app/Contents/MacOS/jre
  cp -ai /usr/local/opt/openjdk/* target/$TARGET.app/Contents/MacOS/jre

  rm -rf target/$TARGET-$BUILD_ARCH.app && mv -i target/$TARGET.app target/$TARGET-$BUILD_ARCH.app
  tar --uname=root --gname=root --directory=target -czf $DIST_DIR/$TARGET-$BUILD_ARCH.app.tar.gz $TARGET-$BUILD_ARCH.app
  cd - > /dev/null

  echo -e "\n### Host: "`hostname -s`" ########### Building MacOS $GUI_TK $BUILD_ARCH APP done.\n"
done

}

function build_tg_for_android {

BUILD_ARCH=`dpkg --print-architecture`

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
cd android/build-scripts/tuxguitar-android
export ANDROID_HOME=$SW_DIR/android-studio/android-studio-2020.3.1.25-linux
# Build did not work with the default Java version 17 from Debian 12 (Bookworm), but with Java 11 from Debian 11 (Bullseye)
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-$BUILD_ARCH ./gradlew                  # Install the required Gradle version and other stuff into the .gradle/ directory
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-$BUILD_ARCH ./gradlew assembleRelease  # Build the APK
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

  echo -e "### Host: "`hostname -s`" ########### Creating a new Github pre-release and upload the builds $TGVERSION to Github ...\n"

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
  UPLOAD_OK=0
  UPLOAD_FAIL=0
  shopt -s nullglob
  for TG_FILE in *.tar.gz *.deb *.rpm *.zip *.exe *-signed.apk; do
    echo "# Creating sha256 checksum and uploading file $TG_FILE to release $TGVERSION ..."
    sha256sum $TG_FILE >> tuxguitar-$TGVERSION.sha256
    if gh release upload --clobber $TGVERSION $TG_FILE; then
      ((UPLOAD_OK++))
      echo "# OK."
    else
      ((UPLOAD_FAIL++))
      echo "# Upload of file $TG_FILE failed!"
    fi
    echo
  done
  echo "# Uploading file tuxguitar-$TGVERSION.sha256 to release $TGVERSION ..."
  if gh release upload --clobber $TGVERSION tuxguitar-$TGVERSION.sha256; then
    ((UPLOAD_OK++))
    echo "# OK."
  else
    ((UPLOAD_FAIL++))
    echo "# Upload of file tuxguitar-$TGVERSION.sha256 failed!"
  fi
  echo

  echo "# $((UPLOAD_OK+UPLOAD_FAIL)) files processed, $UPLOAD_OK files uploaded successfully, $UPLOAD_FAIL uploads failed."
  echo

  if [ $publish_release ] && [ "$UPLOAD_FAIL" -eq 0 ]; then
    echo "# Publishing release $TGVERSION ..."
    if gh release edit $TGVERSION --draft=false; then
      echo "# OK."
      echo -e "\n# Syncing website folder to webserver ...\n"
      $SCRIPT_DIR/sync_website.sh
      echo -e "\n# done."
    else
      echo "# Publishing release $TGVERSION failed!"
    fi
  else
    echo "# Keeping the release in draft status."
  fi

  echo -e "\n### Host: "`hostname -s`" ########### New Github pre-release created and uploads to Github done.\n"

}

# Check and prepare source code. This is done on Linux for all platforms.
if [ `uname` == Linux ]; then
  [ $TGVERSION == $TGSRCVER ] && release_checks_before_prepare_source
  prepare_source
  [ $TGVERSION == $TGSRCVER ] && release_checks_after_prepare_source
fi

# First, we start the remote builds to avoid copying all locally created binaries to the remote hosts.

# BSD
if [ $build_bsd ]; then
  # SWT & JFX versions in FreeBSD 13.2
  SWT_VERSION=4.21
  SWT_PLATFORM=gtk-freebsd
  JFX_VERSION=14.0.2.1
  [ `uname` == Linux ] && start_remote_bsd_build
  [ `uname` == FreeBSD ] && build_tg_for_bsd
fi

# MacOS
if [ $build_macos ]; then
  SWT_VERSION=4.14
  SWT_DATE=201912100610
  SWT_PLATFORM=cocoa-macosx
  [ `uname` == Linux ] && start_remote_macos_build
  [ `uname` == Darwin ] && build_tg_for_macos
fi

# Then come the local builds.

# Linux
if [ $build_linux ]; then
  # SWT version in Debian 12
  SWT_VERSION=4.26
  SWT_DATE=202211231800
  SWT_PLATFORM=gtk-linux
  [ `uname` == Linux ] && build_tg_for_linux
fi

# Windows 64 bit x86_64 build
if [ $build_windows ]; then
  SWT_VERSION=4.26
  SWT_DATE=202211231800
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

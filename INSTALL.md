# Install TuxGuitar

You can find ready to use installation packages for Linux, Windows, MacOS FreeBDS and Android on

[https://github.com/helge17/tuxguitar/releases/](https://github.com/helge17/tuxguitar/releases/)

# TuxGuitar Build Instructions

## Warning

The following instructions may not be complete. For hints and workarounds needed to build TuxGuitar, see the script

```sh
misc/build_tuxguitar_from_source.sh
```

## Prerequisites

- JDK 7 or higher
- Maven 3.3 or higher
- Fluidsynth (optional)
- JACK (optional)
- Eclipse SWT 4.13

## Build on Debian/Ubuntu Linux

### Install Prerequisites

```sh
$ sudo apt install wget unzip git build-essential default-jdk maven libfluidsynth-dev libjack-jackd2-dev libasound2-dev libgtk-3-dev liblilv-dev libsuil-dev qtbase5-dev
```

### Download and install SWT for Linux

```sh
$ wget https://archive.eclipse.org/eclipse/downloads/drops4/R-4.13-201909161045/swt-4.13-gtk-linux-x86_64.zip
$ mkdir swt-4.13-gtk-linux-x86_64
$ cd swt-4.13-gtk-linux-x86_64
$ unzip ../swt-4.13-gtk-linux-x86_64.zip
$ mvn install:install-file -Dfile=swt.jar -DgroupId=org.eclipse.swt -DartifactId=org.eclipse.swt.gtk.linux.x86_64 -Dpackaging=jar -Dversion=4.13
$ cd ..
```

### Get the TuxGuitar sources

```sh
$ git clone https://github.com/helge17/tuxguitar.git
$ cd tuxguitar
```

### Hacks

The Debian version must start with a number:

```sh
$ sed -i "s/SNAPSHOT/9.99-snapshot/" build-scripts/tuxguitar-linux-swt-x86_64-deb/src/resources/DEBIAN/control
```

For the (outdated) VST plugin you need some additional header files:

```sh
$ mkdir build-scripts/native-modules/tuxguitar-synth-vst-linux-x86_64/include
$ cd build-scripts/native-modules/tuxguitar-synth-vst-linux-x86_64/include
$ for hfile in aeffect.h aeffectx.h vstfxstore.h; do
    wget https://raw.githubusercontent.com/R-Tur/VST_SDK_2.4/master/pluginterfaces/vst2.x/$hfile
  done
$ cd -
```

### Build and install

```sh
$ cd build-scripts/tuxguitar-linux-swt-x86_64-deb
$ mvn -e clean verify -P native-modules
$ sudo dpkg -i target/tuxguitar-*.deb
```

### Start TuxGuitar

Now you can start TuxGuitar from your Desktop menu or on the command line with

```sh
$ tuxguitar
```

## Generic GNU/Linux

On Non-Debian-based systems install the prerequisites using your package manager. Then download and install SWT, the TuxGuitar sources and the VST header files as described for Debian above.

### Build and install

```sh
$ cd build-scripts/tuxguitar-linux-swt-x86_64
$ mvn -e clean verify -P native-modules
```

### Start TuxGuitar

```sh
$ cd target/tuxguitar-*
$ ./tuxguitar.sh
```

## Build for Windows on Linux

The Windows version is cross compiled on Ubuntu/Debian with [Mingw-w64](https://mingw-w64.org/).

### Install Prerequisites

```sh
$ sudo apt install default-jdk maven gcc-mingw-w64-x86-64 g++-mingw-w64-i686-win32
```

### Download and install SWT for Windows

```sh
$ wget https://archive.eclipse.org/eclipse/downloads/drops4/R-4.13-201909161045/swt-4.13-win32-win32-x86_64.zip
$ mkdir swt-4.13-win32-win32-x86_64
$ cd swt-4.13-win32-win32-x86_64
$ unzip ../swt-4.13-win32-win32-x86_64.zip
$ mvn install:install-file -Dfile=swt.jar -DgroupId=org.eclipse.swt -DartifactId=org.eclipse.swt.win32.win32.x86_64 -Dpackaging=jar -Dversion=4.13
$ cd ..
```

### Get the TuxGuitar sources

Same as for Debian (see above).

### Hacks

Download the VST header files:

```sh
$ mkdir build-scripts/native-modules/tuxguitar-synth-vst-windows-x86/include
$ cd build-scripts/native-modules/tuxguitar-synth-vst-windows-x86/include
$ for hfile in aeffect.h aeffectx.h vstfxstore.h; do
    wget https://raw.githubusercontent.com/R-Tur/VST_SDK_2.4/master/pluginterfaces/vst2.x/$hfile
  done
$ cd -
```

### Build and install

```sh
$ cd build-scripts/tuxguitar-windows-swt-x86_64
$ mvn -e clean verify -P native-modules -D tuxguitar.jni.cc=x86_64-w64-mingw32-gcc -D tuxguitar.jni.cxx=i686-w64-mingw32-g++-win32
```

The Windows application is now located in the `build-scripts/tuxguitar-windows-swt-x86_64/target/tuxguitar-SNAPSHOT-windows-swt-x86_64` folder. Copy it to your Windows machine.

To start TuxGuitar you need a Java Runtime Environment. You can get the one from [portableapps.com](https://portableapps.com/apps/utilities/OpenJDK64) and extract it to a subfolder named `jre`. Then you should be able to start TuxGuitar by double-clicking on `tuxguitar.exe` or `tuxguitar.bat`.

## Build on MacOS

On MacOS you need [Homebrew](https://brew.sh) to build TuxGuitar. The basic steps are:

```sh
$ brew install oracle-jdk maven
$ cd build-scripts/tuxguitar-macosx-swt-cocoa-x86_64
$ /usr/local/bin/mvn -e clean verify
```
The application will be located in the `build-scripts/tuxguitar-macosx-swt-cocoa-x86_64/target/tuxguitar-SNAPSHOT-macosx-swt-cocoa-x86_64.app` folder.

## Build on FreeBSD

On FreeBSD you don't need to download SWT mnaually. The basic steps to build TuxGuitar are:

```sh
$ pkg install openjdk11 alsa-plugins maven swt gcc gmake fluidsynth
$ cd build-scripts/tuxguitar-freebsd-swt-x86_64
$ mvn -e clean verify -P native-modules
```

The application will be located in the `build-scripts/tuxguitar-freebsd-swt-x86_64/target/tuxguitar-SNAPSHOT-freebsd-swt-x86_64` folder.

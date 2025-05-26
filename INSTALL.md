# Install TuxGuitar

You can find ready to use installation packages for Linux, Windows, macOS, FreeBSD and Android on

[https://github.com/helge17/tuxguitar/releases/](https://github.com/helge17/tuxguitar/releases/)

# TuxGuitar Build Instructions

## Warning

The following instructions have been roughly tested on the x86_64/amd64 architecture. They may also work on some other hardware platforms like aarch64/arm64 or ppc64le/ppc64el, but this depends heavily on the availability of SWT and other prerequisites on these platforms.

For hints and workarounds needed to build TuxGuitar, see the script

```sh
misc/build_tuxguitar_from_source.sh
```

You can also try to build TuxGuitar using the [GitHub Actions](https://docs.github.com/en/actions) defined in the `.github` folder.

## Prerequisites

- JDK 9 or higher
- Maven 3.3 or higher
- FluidSynth (optional)
- Jack (optional)
- Eclipse SWT 4

## Build on Debian/Ubuntu Linux

### Install Prerequisites

```sh
$ sudo apt install wget unzip git build-essential default-jdk maven libwebkit2gtk-4.1-0 libfluidsynth-dev libjack-jackd2-dev libasound2-dev liblilv-dev libsuil-dev qtbase5-dev
```

In order for Asian characters to be displayed correctly, you may also need to install the `fonts-wqy-zenhei` font package.

### Download and install SWT for Linux

```sh
$ wget https://archive.eclipse.org/eclipse/downloads/drops4/R-4.26-202211231800/swt-4.26-gtk-linux-`uname -m`.zip
$ mkdir swt-4.26-gtk-linux-`uname -m`
$ cd swt-4.26-gtk-linux-`uname -m`
$ unzip ../swt-4.26-gtk-linux-`uname -m`.zip
$ mvn install:install-file -Dfile=swt.jar -DgroupId=org.eclipse.swt -DartifactId=org.eclipse.swt.gtk.linux -Dpackaging=jar -Dversion=4.26
$ cd ..
```

### Get the TuxGuitar sources

```sh
$ git clone https://github.com/helge17/tuxguitar.git
$ cd tuxguitar
```

### Build and install

```sh
$ cd desktop/build-scripts/tuxguitar-linux-swt-deb
$ mvn -e clean verify -P native-modules
$ sudo dpkg -i target/tuxguitar-*.deb
```

### Start TuxGuitar

Now you can start TuxGuitar from your Desktop menu or on the command line with

```sh
$ tuxguitar
```

## Generic GNU/Linux

On Non-Debian-based systems install the prerequisites and git using your package manager. Then download and install SWT and download the TuxGuitar sources as described for Debian above.

### Build and Start TuxGuitar

```sh
$ cd desktop/build-scripts/tuxguitar-linux-swt
$ mvn -e clean verify -P native-modules
```

```sh
$ cd target/tuxguitar-*
$ ./tuxguitar.sh
```

## Build for Windows on Linux

The Windows version is cross compiled on Ubuntu/Debian with [Mingw-w64](https://mingw-w64.org/).

### Install Prerequisites

```sh
$ sudo apt install wget unzip git default-jdk maven gcc-mingw-w64-x86-64 g++-mingw-w64-i686-win32
```

### Download and install SWT for Windows

```sh
$ wget https://archive.eclipse.org/eclipse/downloads/drops4/R-4.26-202211231800/swt-4.26-win32-win32-x86_64.zip
$ mkdir swt-4.26-win32-win32-x86_64
$ cd swt-4.26-win32-win32-x86_64
$ unzip ../swt-4.26-win32-win32-x86_64.zip
$ mvn install:install-file -Dfile=swt.jar -DgroupId=org.eclipse.swt -DartifactId=org.eclipse.swt.win32.win32 -Dpackaging=jar -Dversion=4.26
$ cd ..
```

### Get the TuxGuitar sources

Same as for Debian (see above).

### Build and Start TuxGuitar

As we are building the Windows version on Linux, we explicitly deactivate the Linux profile and select the Windows profile manually to avoid confusion.

```sh
$ cd desktop/build-scripts/tuxguitar-windows-swt-x86_64
$ mvn -e clean verify -P native-modules -P -platform-linux -P platform-windows
$ cd -
```

The Windows application is now located in the `desktop/build-scripts/tuxguitar-windows-swt-x86_64/target/tuxguitar-9.99-SNAPSHOT-windows-swt-x86_64` folder. Copy it to your Windows machine.

To start TuxGuitar you need a Java Runtime Environment. You can get the one from [portableapps.com](https://portableapps.com/apps/utilities/OpenJDK64) and extract it to a subfolder named `jre`. Then you should be able to start TuxGuitar by double-clicking on `tuxguitar.exe` or `tuxguitar.bat`.

## Build on macOS

On macOS you need to download and install [Homebrew](https://brew.sh) to build TuxGuitar.

### Install Prerequisites

```sh
$ brew install openjdk maven wget
```

### Download and install SWT for macOS

```sh
$ TUX_ARCH=`uname -m | sed 's/arm64/aarch64/'`
$ wget https://archive.eclipse.org/eclipse/downloads/drops4/R-4.33-202409030240/swt-4.33-cocoa-macosx-${TUX_ARCH}.zip
$ mkdir swt-4.33-cocoa-macosx-${TUX_ARCH}
$ cd swt-4.33-cocoa-macosx-${TUX_ARCH}
$ unzip ../swt-4.33-cocoa-macosx-${TUX_ARCH}.zip
$ mvn install:install-file -Dfile=swt.jar -DgroupId=org.eclipse.swt -DartifactId=org.eclipse.swt.cocoa.macosx -Dpackaging=jar -Dversion=4.33
$ cd ..
```

### Get the TuxGuitar sources

Same as for Debian (see above).

### Build and Start TuxGuitar

```sh
$ cd desktop/build-scripts/tuxguitar-macosx-swt-cocoa
$ mvn -e clean verify -P native-modules
$ cd -
```

The application is now located in the `desktop/build-scripts/tuxguitar-macosx-swt-cocoa/target/tuxguitar-9.99-SNAPSHOT-macosx-swt-cocoa.app` folder. Start TuxGuitar by double-clicking on the folder.

## Build on FreeBSD

### Install Prerequisites

```sh
$ sudo pkg install openjdk11 alsa-plugins maven swt gcc gmake fluidsynth wget git
```

In order for Asian characters to be displayed correctly, you may also need to install the `wqy-fonts-20100803_10,1` font package.

### Install SWT for FreeBSD

On FreeBSD we use SWT from the OS to build and run TuxGuitar. FreeBSD 14.2 comes with SWT version 4.21.

```sh
mvn install:install-file -Dfile=/usr/local/share/java/classes/swt.jar -DgroupId=org.eclipse.swt -DartifactId=org.eclipse.swt.gtk.freebsd -Dpackaging=jar -Dversion=4.21
```

### Get the TuxGuitar sources

Same as for Debian (see above).

### Build and Start TuxGuitar

```sh
$ cd desktop/build-scripts/tuxguitar-freebsd-swt
$ mvn -e clean verify -P native-modules
```

```sh
$ cd target/tuxguitar-*
$ ./tuxguitar.sh
```

## Build the Windows version on Windows

### How to install WSL2 and Ubuntu

You must be running Windows 10 version 2004 and higher (Build 19041 and higher) or Windows 11 to use the command below. Open PowerShell as Administrator and run:

```ps

wsl --install

```

This installs WSL2, the Ubuntu distro by default, and sets everything up. If it says you need a restart, do it. After reboot, open "Ubuntu" from the Start Menu. Let it finish installing and set up a UNIX username/password. Since WSL2 is seperate from your main machine, in order to connect to GitHub, you will need to generate an SSH key if you don’t have one.

### Installing Prerequisites

At this point, you can proceed in the WSL2 window with the "Install Prerequisites" instructions from the Build for Windows on Linux including "Download and install SWT for Windows"

### Get the TuxGuitar sources

Same as for Debian (see above).

### Build and Start TuxGuitar

From the tuxguitar folder, run the following commands as above:

```sh
$ cd desktop/build-scripts/tuxguitar-windows-swt-x86_64
$ mvn -e clean verify -P native-modules -P -platform-linux -P platform-windows
$ cd -
```

After a successful build, copy the resulting folder to your Windows filesystem:

```sh
$ cp -r desktop/build-scripts/tuxguitar-windows-swt-x86_64/target/tuxguitar-9.99-SNAPSHOT-windows-swt-x86_64 /mnt/c/tuxguitar
```

This will copy the files to c:\tuxguitar on your Windows machine, adjust the /mnt/ folder as you wish. From there, you can simply launch TuxGuitar from your Windows machine by launching the exe in this folder.
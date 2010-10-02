#! /usr/bin/make -f
# -*- makefile -*-
#ident "$Id: Makefile,v 1.19 2008/04/17 20:06:22 rzr Exp $"
#@author: created by www.philippe.coval.online.fr -- revision: $Author: rzr $
#licence: LGPL-2.1
#------------------------------------------------------------------------------
default: build

PACKAGE?=tuxguitar

JNI_OS?=linux
JAVA_HOME?=/usr/lib/jvm/java-6-sun/
JAVA_VERS?=1.4

ITEXT_JAR?=/usr/share/java/itext.jar

SWT_JAR?=$(shell echo ` \
    ( t=/usr/share/java/swt.jar && test -r "$$t" && echo $$t ) \
 || ( t=/usr/lib/java/swt.jar && test -r "$$t" && echo $$t ) \
 || ( t=/usr/lib/java/swt-gtk-3.5.1.jar && test -r "$$t" && echo $$t ) \
 || ( t=/usr/lib/java/swt-gtk-3.5.jar && test -r "$$t" && echo $$t ) \
 || ( t=/etc/alternatives/swt.jar && test -r "$$t" && echo $$t ) \
` )
SWT_PATH?=${SWT_JAR}

PACKAGE_JAR?=${CURDIR}/TuxGuitar/${PACKAGE}.jar
TUXGUITAR_PATH?=${PACKAGE_JAR}

PACKAGE_EXEC?=${CURDIR}/misc/${PACKAGE}.sh

JNILIB_SUFFIX?=.so
JNILIB_PREFIX?=lib

subdirs?=\
 TuxGuitar \
 TuxGuitar-compat \
 TuxGuitar-ascii \
 TuxGuitar-gtp \
 TuxGuitar-musicxml \
 TuxGuitar-ptb \
 TuxGuitar-tef \
\
 TuxGuitar-pdf \
 TuxGuitar-midi \
 TuxGuitar-tray \
 TuxGuitar-lilypond \
\
 TuxGuitar-jsa \
\
 TuxGuitar-converter \
 TuxGuitar-community \
 TuxGuitar-tuner \
#}subdirs

subdirs_jni?=\
 TuxGuitar-alsa \
 TuxGuitar-oss \
 TuxGuitar-fluidsynth \
 TuxGuitar-jack \
 #}subdirs_jni

#
export PACKAGE_JAR
###

out_java?=\
 ./TuxGuitar/tuxguitar.jar \
 ./TuxGuitar-compat/tuxguitar-compat.jar \
 ./TuxGuitar-lilypond/tuxguitar-lilypond.jar \
 ./TuxGuitar-midi/tuxguitar-midi.jar \
 ./TuxGuitar-gtp/tuxguitar-gtp.jar \
 ./TuxGuitar-pdf/tuxguitar-pdf.jar \
 ./TuxGuitar-tef/tuxguitar-tef.jar \
 ./TuxGuitar-ascii/tuxguitar-ascii.jar \
 ./TuxGuitar-tray/tuxguitar-tray.jar \
 ./TuxGuitar-ptb/tuxguitar-ptb.jar \
 ./TuxGuitar-musicxml/tuxguitar-musicxml.jar \
 ./TuxGuitar-converter/tuxguitar-converter.jar \
 ./TuxGuitar-community/tuxguitar-community.jar \
 ./TuxGuitar-tuner/tuxguitar-tuner.jar \
 #}out_java

out_jsa?=TuxGuitar-jsa/tuxguitar-jsa.jar

out_sun?=\
 TuxGuitar-library-ftp/tuxguitar-browser-ftp.jar \
 TuxGuitar-library-http/tuxguitar-library-http.jar \
 #}out_sun

out_linux?=\
 ./TuxGuitar-alsa/tuxguitar-alsa.jar \
 ./TuxGuitar-alsa/jni/libtuxguitar-alsa-jni.so \
 ./TuxGuitar-oss/tuxguitar-oss.jar \
 ./TuxGuitar-oss/jni/libtuxguitar-oss-jni.so \
 ./TuxGuitar-fluidsynth/tuxguitar-fluidsynth.jar \
 ./TuxGuitar-fluidsynth/jni/libtuxguitar-fluidsynth-jni.so \
 ./TuxGuitar-jack/tuxguitar-jack.jar \
 ./TuxGuitar-jack/jni/libtuxguitar-jack-jni.so \
 #}out_linux

out_windows?=TuxGuitar-winmm/tuxguitar-winmm.jar

out_macos?=TuxGuitar-CoreAudio/tuxguitar-coreaudio.jar

#all?=${out_java} out_${JNI_OS} ${out_jsa} ${out_sun}  # TODO
all?=${out_java} ${out_${JNI_OS}} ${out_jsa}

###

ANT_FLAGS?=\
 -Dpath.tuxguitar="${PACKAGE_JAR}" \
 -Dpath.itext="${ITEXT_JAR}" \
 -Dpath.swt="${SWT_JAR}" \
 -Dlib.swt.jar="${SWT_JAR}" \
 -Ddist.version="java" \
 -Ddist.file="NOT_EXISTENT_FILE" \
 -Dant.build.javac.source=${JAVA_VERS} \
 -Dant.build.javac.target=${JAVA_VERS} \
 -Dbuild.jni.library.dir=. \
 -Dbuild.jni.library.extension=${JNILIB_SUFFIX} \
 -Dbuild.jni.library.prefix=${JNILIB_PREFIX} \
 -lib ${CURDIR}/TuxGuitar \
 #}ANT_FLAGS

CFLAGS?=\
 -I$(shell gcj -print-file-name=include/)

CFLAGS+= \
-I${JAVA_HOME}/include/ \
-I${JAVA_HOME}/include/${JNI_OS}

MAKE_FLAGS+=\
 SWT_PATH=${SWT_PATH} \
 JAVA_HOME=${JAVA_HOME} \
 JNILIB_SUFFIX=${JNILIB_SUFFIX} \
 JNILIB_PREFIX=${JNILIB_PREFIX} \
 CFLAGS=${CFLAGS}
 #} MAKE_FLAGS

PREFIX?=${DESTDIR}/usr
INSTALL_BIN_DIR?=${PREFIX}/bin/
INSTALL_LIB_DIR?=${PREFIX}/lib/jni/
INSTALL_DOC_DIR?=${PREFIX}/share/${PACKAGE}/doc/
INSTALL_SHARE_DIR?=${PREFIX}/share/${PACKAGE}/
INSTALL_JAR_DIR?=${INSTALL_SHARE_DIR}/

build: help all

rebuild: clean fix build

all: ${all}

all-java: ${out_java}

all-linux: ${out_linux}

all-sun: ${out_sun}

%.jar:
	cd ${@D} && ant -v -d ${ANT_FLAGS} all

%.so:
	${MAKE} -C ${@D}/../jni/ ${@F}

#%.native:
#	 make -C $$t  ${MAKE_FLAGS} library_jni

subdirs: ${subdirs}
	for t in $^ ; do \
	 cd "${CURDIR}/$$t" && ant -v -d ${ANT_FLAGS} build ; \
	done

fix: overide

overide: COPYING

COPYING: TuxGuitar
	-@cp -a $</doc/LICENSE ./COPYING
	-@cp -a $</doc/LICENSE ./
	-@cp -a $</doc/CHANGES ChangeLog
	-@cp -a $</doc/README ./
	-@cp -a $</doc/AUTHORS ./

install: ${all}
	-install -d ${PREFIX}
	-install -d ${INSTALL_BIN_DIR}
	-install -d ${INSTALL_JAR_DIR}
	-install -d ${INSTALL_SHARE_DIR}
	-install -d ${INSTALL_DOC_DIR}
#	-install -m 755 ./TuxGuitar/${PACKAGE} ${INSTALL_BIN_DIR}/${PACKAGE}
	-install -m 644 ${PACKAGE_JAR} ${INSTALL_JAR_DIR}/${PACKAGE}.jar
	install -d ${INSTALL_BIN_DIR}
	mkdir -p  ./TuxGuitar/share/plugins/
	for t in ${subdirs} ; do cp $$t/*.jar ./TuxGuitar/share/plugins/ ; done
#	-find . -iname "*.jar" -exec cp {} ./TuxGuitar/share/plugins/ \;
	cp -rfa ./TuxGuitar/doc/* ${INSTALL_DOC_DIR}/
#	cp -rfa ./TuxGuitar/share/* ${INSTALL_SHARE_DIR} # TODO
	cp -rfa ./TuxGuitar/share/help ${INSTALL_SHARE_DIR} # TODO
	cp -rfa ./TuxGuitar/share/lang ${INSTALL_SHARE_DIR} # TODO
	cp -rfa ./TuxGuitar/share/scales ${INSTALL_SHARE_DIR} # TODO
	cp -rfa ./TuxGuitar/share/skins ${INSTALL_SHARE_DIR} # TODO
	-cp -a misc/${PACKAGE}.tg ${INSTALL_SHARE_DIR} # TODO
	-install -T ${PACKAGE_EXEC} ${INSTALL_BIN_DIR}/${PACKAGE}
#	-${MAKE} install-jni

install-linux:
	install -d ${INSTALL_LIB_DIR}
	install -s TuxGuitar*/jni/lib*.so ${INSTALL_LIB_DIR}

clean:
	find . -iname "*.class" -exec rm -fv "{}" \;
	find . -iname "*.jar" -exec rm -fv "{}" \;
	find . -iname "*.jnilib" -exec rm -fv "{}" \;
	find . -iname "*.o" -exec rm -fv "{}" \;
	find . -iname "*.so" -exec rm -fv "{}" \;
	find . -type l -exec rm -fv "{}" \;
	rm -rf TuxGuitar/tmp

run: ${PACKAGE_EXEC}
	cd ./TuxGuitar && APP_HOME=. ; ${SHELL} $<

test: ${PACKAGE_EXEC}
	cd TuxGuitar && APP_HOME=. ; \
	 export PACKAGE_CLASSPATH="${PACKAGE_JAR}:." ;\
	 DEBUG=1 ${SHELL} $< --version

help:
	@echo "# JAVA_HOME=${JAVA_HOME}"
	@echo "# JAVA=${JAVA}"
	@echo "# JAVAC=${JAVAC}"
	@echo "# SWT_JAR=${SWT_JAR}"
	-which javac
	-which java
#	java -version

version?=$(shell date +%Y%m%d)
scmroot?=https://${PACKAGE}.svn.sourceforge.net/svnroot/${PACKAGE}/trunk

package_version?=0.0.${version}
snapshot_dir?=${PACKAGE}-snapshot-${package_version}
release_dir?=${PACKAGE}-${package_version}

dist tarball: snapshot


snapshot: ../${snapshot_dir}.tar.gz

../${snapshot_dir}.tar.gz: ../${snapshot_dir}
	cd ${@D} && tar czf ${@F}.tar.gz \
	 --exclude="debian" \
	 ${@D}

release:  ../${release_dir}.tar.gz

../${release_dir}.tar.gz: ../${release_dir}
	cd ${@D} && tar czf ${@F}.tar.gz \
	 --exclude="debian" --exclude "CVS" --exclude ".svn"\
	 ${@D}

../${snapshot_dir} ../${release_dir}:
	svn co ${scmroot} $@

#eof "$Id: Makefile,v 1.19 2008/04/17 20:06:22 rzr Exp $"

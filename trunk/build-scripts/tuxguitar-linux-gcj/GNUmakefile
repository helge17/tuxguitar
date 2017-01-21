#! /usr/bin/make -f
# -*- makefile -*-
#ident "$Id: GNUmakefile,v 1.4 2008/02/26 15:25:26 akdmia Exp $"
#@author: created by www.philippe.coval.online.fr -- revision: $Author: akdmia $
#licence: LGPL
#------------------------------------------------------------------------------
PREFIX?=./
PACKAGE?=tuxguitar-SNAPSHOT-linux-gcj

TG_ROOT_PATH?=../..
TG_BUILD_PATH?=./target
TG_BUILD_PREFIX?=$(PREFIX)
TG_BUILD_RESOURCES?=$(TG_BUILD_PATH)/resources
TG_BUILD_DESTDIR?=$(TG_BUILD_PATH)/$(PACKAGE)/
TG_BUILD_ARCH?=64
TG_BUILD_JAVA_VERSION?=1.5

ENVIRONMENT_PROPERTIES?=\
	PREFIX=$(TG_BUILD_PREFIX)\
	DESTDIR=$(TG_BUILD_DESTDIR)\
	TG_BUILD_ARCH=$(TG_BUILD_ARCH)\
	TG_BUILD_JAVA_VERSION=$(TG_BUILD_JAVA_VERSION)\
	TG_BUILD_RESOURCES=$(TG_BUILD_RESOURCES)\
	TG_ROOT_PATH=$(TG_ROOT_PATH)\
	TG_SOURCE_PATH=$(TG_ROOT_PATH)/TuxGuitar/src\
	TG_LIB_SOURCE_PATH=$(TG_ROOT_PATH)/TuxGuitar-lib/src\
	TG_LIB_LIBRARY_PATH=$(TG_BUILD_DESTDIR)$(TG_BUILD_PREFIX)/lib\
	TG_UI_TOOLKIT_SOURCE_PATH=$(TG_ROOT_PATH)/TuxGuitar-ui-toolkit/src\
	TG_UI_TOOLKIT_SWT_SOURCE_PATH=$(TG_ROOT_PATH)/TuxGuitar-ui-toolkit-swt/src\
	TG_UI_TOOLKIT_SWT_RESOURCE_PATH=$(TG_ROOT_PATH)/TuxGuitar-ui-toolkit-swt/resources\
	TG_EDITOR_UTILS_SOURCE_PATH=$(TG_ROOT_PATH)/TuxGuitar-editor-utils/src\
	TG_GM_UTILS_SOURCE_PATH=$(TG_ROOT_PATH)/TuxGuitar-gm-utils/src\
	TG_JACK_SOURCE_PATH=$(TG_ROOT_PATH)/TuxGuitar-jack/src\
	TG_GTP_SOURCE_PATH=$(TG_ROOT_PATH)/TuxGuitar-gtp/src\

# build paths
MAKEFILE_PATH?=./makefiles
MAKEFILE_SCRIPTS?=\
	$(MAKEFILE_PATH)/tuxguitar-lib.mf\
	$(MAKEFILE_PATH)/tuxguitar.mf\
	$(MAKEFILE_PATH)/tuxguitar-gm-utils.mf\
	$(MAKEFILE_PATH)/tuxguitar-jack.mf\
	$(MAKEFILE_PATH)/tuxguitar-compat.mf\
	$(MAKEFILE_PATH)/tuxguitar-gtp.mf\
	$(MAKEFILE_PATH)/tuxguitar-gtp-ui.mf\
	$(MAKEFILE_PATH)/tuxguitar-gpx.mf\
	$(MAKEFILE_PATH)/tuxguitar-ptb.mf\
	$(MAKEFILE_PATH)/tuxguitar-tef.mf\
	$(MAKEFILE_PATH)/tuxguitar-lilypond.mf\
	$(MAKEFILE_PATH)/tuxguitar-lilypond-ui.mf\
	$(MAKEFILE_PATH)/tuxguitar-musicxml.mf\
	$(MAKEFILE_PATH)/tuxguitar-ascii.mf\
	$(MAKEFILE_PATH)/tuxguitar-svg.mf\
	$(MAKEFILE_PATH)/tuxguitar-midi.mf\
	$(MAKEFILE_PATH)/tuxguitar-midi-ui.mf\
	$(MAKEFILE_PATH)/tuxguitar-image.mf\
	$(MAKEFILE_PATH)/tuxguitar-alsa.mf\
	$(MAKEFILE_PATH)/tuxguitar-fluidsynth.mf\
	$(MAKEFILE_PATH)/tuxguitar-oss.mf\
	$(MAKEFILE_PATH)/tuxguitar-jack.mf\
	$(MAKEFILE_PATH)/tuxguitar-jack-ui.mf\
	$(MAKEFILE_PATH)/tuxguitar-gm-settings.mf\
	$(MAKEFILE_PATH)/tuxguitar-community.mf\
	$(MAKEFILE_PATH)/tuxguitar-converter.mf\
	$(MAKEFILE_PATH)/tuxguitar-browser-ftp.mf\

MAKEFILE_OBJECTS?=$(MAKEFILE_SCRIPTS:%.mf=%.make)

default: all

all: create_tmp_files $(MAKEFILE_OBJECTS) resources clean_tmp_files

create_tmp_files:
	-install -d $(TG_BUILD_RESOURCES)
	-cp -rf $(TG_ROOT_PATH)/TuxGuitar/dist/* $(TG_BUILD_RESOURCES)
	-cp -rf src/resources/dist/* $(TG_BUILD_RESOURCES)

clean_tmp_files: 
	-rm $(TG_BUILD_RESOURCES) -rf

resources:
	-install -d $(TG_BUILD_DESTDIR)$(TG_BUILD_PREFIX)
	-install -d $(TG_BUILD_DESTDIR)$(TG_BUILD_PREFIX)/share/tuxguitar/soundfont
	-install src/resources/tuxguitar.sh $(TG_BUILD_DESTDIR)$(TG_BUILD_PREFIX)/tuxguitar.sh
	-install $(TG_ROOT_PATH)/TuxGuitar-resources/resources/soundfont/* $(TG_BUILD_DESTDIR)$(TG_BUILD_PREFIX)/share/tuxguitar/soundfont

%.make: %.mf
	$(ENVIRONMENT_PROPERTIES) make clean -f $^
	$(ENVIRONMENT_PROPERTIES) make -f $^
	$(ENVIRONMENT_PROPERTIES) make install -f $^
	$(ENVIRONMENT_PROPERTIES) make clean -f $^

clean:
	-rm $(DESTDIR)$(TG_BUILD_PATH) -rf

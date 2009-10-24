#! /usr/bin/make -f
# -*- makefile -*-
#ident "$Id: GNUmakefile,v 1.10 2008/04/17 20:06:34 rzr Exp $"
#@author: created by www.philippe.coval.online.fr -- revision: $Author: rzr $
#licence: LGPL
#------------------------------------------------------------------------------
PACKAGE?=tuxguitar-alsa
LIBRARY?=lib$(PACKAGE).so
LIBRARY_JNI?=lib$(PACKAGE)-jni.so
LIBRARY_PROVIDER?=org.herac.tuxguitar.player.impl.midiport.alsa.MidiOutputPortProviderPlugin

PREFIX?=/usr

# dependencies
SWT_PATH?=/usr/share/java/swt.jar

# tuxguitar paths
TG_SERVICE_CLASS?=org.herac.tuxguitar.gui.system.plugins.TGPlugin
TG_PREFIX?=$(PREFIX)
TG_SOURCE_PATH?=$(TG_PREFIX)/src
TG_LIBRARY_PATH?=$(TG_PREFIX)/lib
TG_LIBRARY_PATH_JNI?=$(TG_PREFIX)/lib/jni
TG_NATIVE_PLUGINS_PATH?=$(TG_PREFIX)/share/tuxguitar/plugins

# install paths
INSTALL_LIBRARY_JNI_DIR?=$(TG_LIBRARY_PATH_JNI)
INSTALL_LIBRARY_DIR?=$(TG_NATIVE_PLUGINS_PATH)
INSTALL_SERVICE_DIR?=$(TG_NATIVE_PLUGINS_PATH)/$(PACKAGE)/META-INF/services

# build paths
JAVA_SOURCE_PATH?=./src/
JAVA_SOURCES?=$(shell find $(JAVA_SOURCE_PATH) -name "*.java")
JAVA_OBJECTS?=$(PACKAGE).o
JNI_SOURCE_PATH?=./jni/
JNI_SOURCES?=$(shell find $(JNI_SOURCE_PATH) -name "*.c")
JNI_OBJECTS?=$(JNI_SOURCES:%.c=%.o)

GCJ?=gcj
GCJFLAGS+=-fjni -I$(JAVA_SOURCE_PATH) -I$(TG_SOURCE_PATH) -I$(SWT_PATH) 

CC=gcc
CFLAGS=-fPIC -I $(shell $(GCJ) -print-file-name=include/)

default: all

all: objects library library_jni meta-inf

objects: tmp.classes.tmp

tmp.classes.tmp: $(JAVA_OBJECTS)
	@touch $@

library: $(LIBRARY)

library_jni: $(LIBRARY_JNI)

install:
	-install -d $(DESTDIR)$(PREFIX)/
	-install -d $(DESTDIR)$(INSTALL_LIBRARY_DIR)/
	-install -d $(DESTDIR)$(INSTALL_LIBRARY_JNI_DIR)/
	-install -d $(DESTDIR)$(INSTALL_SERVICE_DIR)/
	-install $(LIBRARY) $(DESTDIR)$(INSTALL_LIBRARY_DIR)/$(LIBRARY)
	-install $(LIBRARY_JNI) $(DESTDIR)$(INSTALL_LIBRARY_JNI_DIR)/$(LIBRARY_JNI)
	-install $(TG_SERVICE_CLASS) $(DESTDIR)$(INSTALL_SERVICE_DIR)/$(TG_SERVICE_CLASS)

uninstall:
	-rm $(DESTDIR)$(INSTALL_LIBRARY_DIR)/$(LIBRARY)
	-rm $(DESTDIR)$(INSTALL_LIBRARY_JNI_DIR)/$(LIBRARY_JNI)
	-rm $(DESTDIR)$(INSTALL_SERVICE_DIR)/$(TG_SERVICE_CLASS)

clean:
	-@find . -name "*.o" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.so" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.tmp" -exec rm -f {} \; > /dev/null 2>&1
	-@rm $(TG_SERVICE_CLASS)

%.o: %.c
	$(CC) $(CFLAGS) -c -o $(@F) $<

$(PACKAGE).o: $(JAVA_SOURCES)
	$(GCJ) $(GCJFLAGS) -c -o $(@F) $^

$(LIBRARY): $(JAVA_OBJECTS)
	${GCJ} -shared -o $@ -ltuxguitar -L$(TG_LIBRARY_PATH) $(^F)

$(LIBRARY_JNI): $(JNI_OBJECTS)
	$(GCJ) -shared -o $@ -lasound -lpthread $(^F)

meta-inf:
	echo $(LIBRARY_PROVIDER) > $(TG_SERVICE_CLASS)

#eof "$Id: GNUmakefile,v 1.10 2008/04/17 20:06:34 rzr Exp $"

#! /usr/bin/make -f
# -*- makefile -*-
#ident "$Id: GNUmakefile,v 1.7 2008/02/29 12:55:24 akdmia Exp $"
#@author: created by www.philippe.coval.online.fr -- revision: $Author: akdmia $
#licence: LGPL
#------------------------------------------------------------------------------
OS_LIBRARY_PREFIX?=lib
OS_LIBRARY_EXTENSION?=.so

PACKAGE?=tuxguitar
BINARY?=$(PACKAGE).bin
LIBRARY?=$(OS_LIBRARY_PREFIX)$(PACKAGE)$(OS_LIBRARY_EXTENSION)

# constants
SHARE_DIR?=./share/
DOC_DIR?=./doc/

# dependencies
SWT_PATH?=/usr/share/java/swt.jar

# install
PREFIX?=/usr
TG_BINARY_PATH?=$(PREFIX)/bin
TG_LIBRARY_PATH?=$(PREFIX)/lib
TG_LIBRARY_PATH_JNI?=$(TG_LIBRARY_PATH)/jni
TG_SHARE_PATH?=$(PREFIX)/share/$(PACKAGE)
TG_DOC_PATH?=$(PREFIX)/share/doc/$(PACKAGE)
TG_NATIVE_PLUGINS_PATH?=$(TG_SHARE_PATH)/plugins
TG_NATIVE_PLUGINS_PREFIX?=$(OS_LIBRARY_PREFIX)
TG_NATIVE_PLUGINS_EXTENSION?=$(OS_LIBRARY_EXTENSION)

# dependencies
SWT_JAR?=$(SWT_PATH)

# sources
SOURCE_PATH?=./src/
SOURCES?=$(shell find $(SOURCE_PATH) -name "*.java")
RESOURCE_PATH?=./dist/
RESOURCES_XML?=$(shell find $(RESOURCE_PATH) -name "*.xml")
RESOURCES_DIST?=$(shell find $(RESOURCE_PATH) -name "*.dist")
DEPENDENCIES?=$(SWT_JAR)
OBJECTS?=\
 $(PACKAGE).o\
 $(RESOURCES_DIST:%.dist=%.o)\
 $(RESOURCES_XML:%.xml=%.o)\
 $(DEPENDENCIES:%.jar=%.o)\

# runtime
MAINCLASS?=org.herac.tuxguitar.gui.TGMain
PROPERTIES?=\
 -Djava.library.path=$(TG_LIBRARY_PATH_JNI) \
 -Djava.class.path=$(TG_SHARE_PATH) \
 -Dtuxguitar.share.path=$(TG_SHARE_PATH) \
 -Dtuxguitar.library.path=$(TG_NATIVE_PLUGINS_PATH) \
 -Dtuxguitar.library.prefix=$(TG_NATIVE_PLUGINS_PREFIX) \
 -Dtuxguitar.library.extension=$(TG_NATIVE_PLUGINS_EXTENSION) \

GCJ?=gcj
GCJFLAGS+=-fjni -I$(SOURCE_PATH) -I$(SWT_PATH)
LDFLAGS?=

default: all

all: objects library binary

objects: $(OBJECTS)

library: $(LIBRARY)

binary: $(BINARY)

install:
	-install -d $(DESTDIR)$(PREFIX)/
	-install -d $(DESTDIR)$(TG_BINARY_PATH)/
	-install -d $(DESTDIR)$(TG_LIBRARY_PATH)/
	-install -d $(DESTDIR)$(TG_SHARE_PATH)/
	-install -d $(DESTDIR)$(TG_DOC_PATH)/
	-install $(BINARY) $(DESTDIR)$(TG_BINARY_PATH)/$(BINARY)
	-install $(LIBRARY) $(DESTDIR)$(TG_LIBRARY_PATH)/$(LIBRARY)
	cp -rfa $(SHARE_DIR)/* $(DESTDIR)$(TG_SHARE_PATH)/
	cp -rfa $(DOC_DIR)/* $(DESTDIR)$(TG_DOC_PATH)/

uninstall:
	-rm $(DESTDIR)$(TG_BINARY_PATH)/$(BINARY)
	-rm $(DESTDIR)$(TG_LIBRARY_PATH)/$(LIBRARY)

clean:
	-@find . -name "*.o" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.so" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.bin" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.tmp" -exec rm -f {} \; > /dev/null 2>&1

%.o: %.jar
	$(GCJ) $(GCJFLAGS) -c -o $(@F) $<

%.o: %.dist
	$(GCJ) $(GCJFLAGS) -c -o $(@F) --resource $(<F) $<

%.o: %.xml
	$(GCJ) $(GCJFLAGS) -c -o $(@F) --resource $(<F) $<

$(PACKAGE).o: $(SOURCES)
	$(GCJ) $(GCJFLAGS) -c -o $(@F) $^

$(LIBRARY): $(OBJECTS)
	$(GCJ) $(LDFLAGS) -shared -o $@ $(^F)

$(BINARY):
	$(GCJ) -o $@ --main=$(MAINCLASS) $(PROPERTIES) -l$(PACKAGE) -L$(TG_LIBRARY_PATH) -L./

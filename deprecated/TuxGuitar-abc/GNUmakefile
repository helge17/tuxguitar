#! /usr/bin/make -f
# -*- makefile -*-
#ident "$Id: GNUmakefile,v 1.4 2008/02/26 15:24:45 akdmia Exp $"
#@author: created by www.philippe.coval.online.fr -- revision: $Author: akdmia $
#licence: LGPL
#------------------------------------------------------------------------------
PACKAGE?=tuxguitar-abc
LIBRARY?=lib$(PACKAGE).so

PREFIX?=/usr

# dependencies
SWT_PATH?=/usr/share/java/swt.jar

# tuxguitar paths
TG_PREFIX?=$(PREFIX)
TG_SOURCE_PATH?=$(TG_PREFIX)/src
TG_LIBRARY_PATH?=$(TG_PREFIX)/lib
TG_NATIVE_PLUGINS_PATH?=$(TG_PREFIX)/share/tuxguitar/plugins

# install paths
INSTALL_LIBRARY_DIR?=$(TG_NATIVE_PLUGINS_PATH)
INSTALL_SHARED_DIR?=$(TG_NATIVE_PLUGINS_PATH)/$(PACKAGE)

# resources
SHARE_PATH?=./share/

# build paths
SOURCE_PATH?=./src/
SOURCES?=$(shell find $(SOURCE_PATH) -name "*.java")
OBJECTS?=$(PACKAGE).o

GCJ?=gcj
GCJFLAGS+=-fjni -I$(SOURCE_PATH) -I$(TG_SOURCE_PATH) -I$(SWT_PATH)

default: all

all: objects library

objects: tmp.classes.tmp

tmp.classes.tmp: $(OBJECTS)
	@touch $@

library: $(LIBRARY)

install:
	-install -d $(DESTDIR)$(PREFIX)/
	-install -d $(DESTDIR)$(INSTALL_LIBRARY_DIR)/
	-install -d $(DESTDIR)$(INSTALL_SHARED_DIR)/
	-install $(LIBRARY) $(DESTDIR)$(INSTALL_LIBRARY_DIR)/$(LIBRARY)
	-cp -rfa $(SHARE_PATH)/* $(DESTDIR)$(INSTALL_SHARED_DIR)

uninstall:
	-rm -rf $(DESTDIR)$(INSTALL_SHARED_DIR)/
	-rm $(DESTDIR)$(INSTALL_LIBRARY_DIR)/$(LIBRARY)

clean:
	-@find . -name "*.o" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.so" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.tmp" -exec rm -f {} \; > /dev/null 2>&1

$(PACKAGE).o: $(SOURCES)
	$(GCJ) $(GCJFLAGS) -c -o $(@F) $^

$(LIBRARY): $(OBJECTS)
	$(GCJ) -shared -o $@ -ltuxguitar -L$(TG_LIBRARY_PATH) $(^F)


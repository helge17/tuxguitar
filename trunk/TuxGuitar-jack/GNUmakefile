#------------------------------------------------------------------------------
PACKAGE?=tuxguitar-jack
LIBRARY?=lib$(PACKAGE).so
LIBRARY_JNI?=lib$(PACKAGE)-jni.so

PREFIX?=/usr

# dependencies
SWT_PATH?=/usr/share/java/swt.jar

# tuxguitar paths
TG_PREFIX?=$(PREFIX)
TG_SOURCE_PATH?=$(TG_PREFIX)/src
TG_LIBRARY_PATH?=$(TG_PREFIX)/lib
TG_LIBRARY_PATH_JNI?=$(TG_PREFIX)/lib/jni
TG_NATIVE_PLUGINS_PATH?=$(TG_PREFIX)/share/tuxguitar/plugins

# install paths
INSTALL_LIBRARY_JNI_DIR?=$(TG_LIBRARY_PATH_JNI)
INSTALL_LIBRARY_DIR?=$(TG_NATIVE_PLUGINS_PATH)
INSTALL_SHARED_DIR?=$(TG_NATIVE_PLUGINS_PATH)/$(PACKAGE)

# resources
SHARE_PATH?=./share/

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

all: objects library library_jni

objects: tmp.classes.tmp

tmp.classes.tmp: $(JAVA_OBJECTS)
	@touch $@

library: $(LIBRARY)

library_jni: $(LIBRARY_JNI)

install:
	-install -d $(DESTDIR)$(PREFIX)/
	-install -d $(DESTDIR)$(INSTALL_LIBRARY_DIR)/
	-install -d $(DESTDIR)$(INSTALL_LIBRARY_JNI_DIR)/
	-install -d $(DESTDIR)$(INSTALL_SHARED_DIR)/
	-install $(LIBRARY) $(DESTDIR)$(INSTALL_LIBRARY_DIR)/$(LIBRARY)
	-install $(LIBRARY_JNI) $(DESTDIR)$(INSTALL_LIBRARY_JNI_DIR)/$(LIBRARY_JNI)
	-cp -rfa $(SHARE_PATH)/* $(DESTDIR)$(INSTALL_SHARED_DIR)

uninstall:
	-rm -rf $(DESTDIR)$(INSTALL_SHARED_DIR)/
	-rm $(DESTDIR)$(INSTALL_LIBRARY_DIR)/$(LIBRARY)
	-rm $(DESTDIR)$(INSTALL_LIBRARY_JNI_DIR)/$(LIBRARY_JNI)

clean:
	-@find . -name "*.o" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.so" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.tmp" -exec rm -f {} \; > /dev/null 2>&1

%.o: %.c
	$(CC) $(CFLAGS) -c -o $(@F) $<

$(PACKAGE).o: $(JAVA_SOURCES)
	$(GCJ) $(GCJFLAGS) -c -o $(@F) $^

$(LIBRARY): $(JAVA_OBJECTS)
	$(GCJ) -shared -o $@ $(^F) -ltuxguitar -L$(TG_LIBRARY_PATH)

$(LIBRARY_JNI): $(JNI_OBJECTS)
	$(GCJ) -shared -o $@ $(^F) `pkg-config --libs jack`

#------------------------------------------------------------------------------

#------------------------------------------------------------------------------
PACKAGE?=tuxguitar-community
LIBRARY?=lib$(PACKAGE).so
LIBRARY_PROVIDER?=org.herac.tuxguitar.community.TGCommunityPluginImpl

PREFIX?=/usr

# dependencies
SWT_PATH?=/usr/share/java/swt.jar

# tuxguitar paths
TG_SERVICE_CLASS?=org.herac.tuxguitar.gui.system.plugins.TGPlugin
TG_PREFIX?=$(PREFIX)
TG_SOURCE_PATH?=$(TG_PREFIX)/src
TG_LIBRARY_PATH?=$(TG_PREFIX)/lib
TG_NATIVE_PLUGINS_PATH?=$(TG_PREFIX)/share/tuxguitar/plugins

# install paths
INSTALL_LIBRARY_DIR?=$(TG_NATIVE_PLUGINS_PATH)
INSTALL_SETTINGS_DIR?=$(TG_NATIVE_PLUGINS_PATH)/$(PACKAGE)/
INSTALL_LANGUAGE_DIR?=$(TG_NATIVE_PLUGINS_PATH)/$(PACKAGE)/lang
INSTALL_SERVICE_DIR?=$(TG_NATIVE_PLUGINS_PATH)/$(PACKAGE)/META-INF/services

# resources
LANGUAGE?=./share/lang
SETTINGS=$(PACKAGE).cfg
DEFAULT_SETTINGS_URL?="community.url=http://community.tuxguitar.com.ar\n"
DEFAULT_SETTINGS_BROWSER?="community.browser=;x-www-browser %s;firefox %s;mozilla %s;mozilla-firefox %s;epiphany-browser %s;epiphany-gecko %s\n"

# build paths
SOURCE_PATH?=./src/
SOURCES?=$(shell find $(SOURCE_PATH) -name "*.java")
OBJECTS?=$(PACKAGE).o

GCJ?=gcj
GCJFLAGS+=-fjni -I$(SOURCE_PATH) -I$(TG_SOURCE_PATH) -I$(SWT_PATH)

default: all

all: objects library meta-inf settings

objects: tmp.classes.tmp

tmp.classes.tmp: $(OBJECTS)
	@touch $@

library: $(LIBRARY)

install:
	-install -d $(DESTDIR)$(PREFIX)/
	-install -d $(DESTDIR)$(INSTALL_LIBRARY_DIR)/
	-install -d $(DESTDIR)$(INSTALL_SERVICE_DIR)/
	-install -d $(DESTDIR)$(INSTALL_SETTINGS_DIR)/
	-install -d $(DESTDIR)$(INSTALL_LANGUAGE_DIR)/
	-install $(LIBRARY) $(DESTDIR)$(INSTALL_LIBRARY_DIR)/$(LIBRARY)
	-install $(LANGUAGE)/* $(DESTDIR)$(INSTALL_LANGUAGE_DIR)
	-install $(SETTINGS) $(DESTDIR)$(INSTALL_SETTINGS_DIR)/$(SETTINGS)
	-install $(TG_SERVICE_CLASS) $(DESTDIR)$(INSTALL_SERVICE_DIR)/$(TG_SERVICE_CLASS)

uninstall:
	-rm $(DESTDIR)$(INSTALL_LIBRARY_DIR)/$(LIBRARY)
	-rm $(DESTDIR)$(INSTALL_SERVICE_DIR)/$(TG_SERVICE_CLASS)
	-rm $(DESTDIR)$(INSTALL_SETTINGS_DIR)/$(SETTINGS)
	-rm $(DESTDIR)$(INSTALL_LANGUAGE_DIR)/*

clean:
	-@find . -name "*.o" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.so" -exec rm -f {} \; > /dev/null 2>&1
	-@find . -name "*.tmp" -exec rm -f {} \; > /dev/null 2>&1
	-@rm $(TG_SERVICE_CLASS)
	-@rm $(SETTINGS)

$(PACKAGE).o: $(SOURCES)
	$(GCJ) $(GCJFLAGS) -c -o $(@F) $^

$(LIBRARY): $(OBJECTS)
	${GCJ} -shared -o $@ -ltuxguitar -L$(TG_LIBRARY_PATH) $(^F)

meta-inf:
	echo $(LIBRARY_PROVIDER) > $(TG_SERVICE_CLASS)

settings:
	echo $(DEFAULT_SETTINGS_URL) > $(SETTINGS)
	echo $(DEFAULT_SETTINGS_BROWSER) >> $(SETTINGS)

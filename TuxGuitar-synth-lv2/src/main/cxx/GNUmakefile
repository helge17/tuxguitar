LILV_INCLUDE_PATH?=/usr/include/lilv-0/lilv
SUIl_INCLUDE_PATH?=/usr/include/suil-0/suil
QT5_INCLUDE_PATH?=`pkg-config Qt5Core --cflags` `pkg-config Qt5Widgets --cflags`

###############################################
### GNULinux flags ############################
###############################################
CXX?=g++
RM?=rm -f
CFLAGS?=-I$(LILV_INCLUDE_PATH) -I$(SUIl_INCLUDE_PATH) -I$(QT5_INCLUDE_PATH)  -fPIC -fpermissive 
LDFLAGS?=
LDLIBS?=-llilv-0 -lsuil-0 -lpthread `pkg-config Qt5Core --libs` `pkg-config Qt5Widgets --libs`
LDPATH?=-L/usr/lib
BINARY_NAME?=tuxguitar-synth-lv2-ui
BINARY_SUFFIX?=
BINARY_OS?=_GNULinux
###############################################

###############################################
BINARY=$(BINARY_PREFIX)$(BINARY_NAME)$(BINARY_SUFFIX)
OBJECTS=\
	LV2Logger.o\
	LV2Client.o\
	LV2World.o\
	LV2Plugin.o\
	LV2Instance.o\
	LV2UIQt5.o\
	LV2Socket_GNULinux.o\

%.o: %.c
	$(CXX) $(CFLAGS) -c -o $@ $<

all:    $(BINARY)

$(BINARY):	$(OBJECTS)
	$(CXX) $(LDFLAGS) -o $(BINARY) $(OBJECTS) $(LDPATH) $(LDLIBS)
	
clean: 
	$(RM) $(OBJECTS) $(BINARY)

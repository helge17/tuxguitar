VST_SDK_HOME?=/home/julian/Documents/vst/VST_SDK/VST3_SDK/pluginterfaces/vst2.x/

###############################################
### GNULinux flags ############################
###############################################
#CXX?=g++
#RM?=rm -f
#CFLAGS?=-I$(VST_SDK_HOME) -fPIC -fpermissive 
#LDFLAGS?=
#LDLIBS?=-ldl -lX11 -lrt -lpthread
#LDPATH?=-L/usr/lib
#BINARY_NAME?=tuxguitar-synth-vst
#BINARY_SUFFIX?=.bin
#BINARY_OS?=_GNULinux
###############################################

###############################################
### Windows flags #############################
###############################################
#CXX=/usr/bin/i686-w64-mingw32-g++-win32
#RM?=rm -f
#CFLAGS?=-I$(VST_SDK_HOME) -fPIC -fpermissive 
#LDFLAGS?=-static-libgcc -static-libstdc++
#LDLIBS?=-static -lws2_32 -lpthread
#LDPATH?=-L/usr/lib
#BINARY_NAME?=tuxguitar-synth-vst
#BINARY_SUFFIX?=.exe
#BINARY_OS?=_Win32
###############################################

BINARY=$(BINARY_PREFIX)$(BINARY_NAME)$(BINARY_SUFFIX)
OBJECTS=\
	VSTClient.o\
	VST.o\
	VSTLogger.o\
	VSTPlugin.o\
	VSTPluginLoader$(BINARY_OS).o\
	VSTSocket$(BINARY_OS).o\
	VSTEffect.o\
	VSTEffectUI$(BINARY_OS).o\

%.o: %.c
	$(CXX) $(CFLAGS) -c -o $@ $<

all:    $(BINARY)

$(BINARY):	$(OBJECTS)
	$(CXX) $(LDFLAGS) -o $(BINARY) $(OBJECTS) $(LDPATH) $(LDLIBS)
	
clean: 
	$(RM) $(OBJECTS) $(BINARY)

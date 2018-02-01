VST_SDK_HOME?=/home/julian/Documents/vst/VST_SDK/VST3_SDK/pluginterfaces/vst2.x/

CXX=/usr/bin/i686-w64-mingw32-g++-win32
#CXX=wineg++

RM?=rm -f
CFLAGS?=-I$(VST_SDK_HOME) -fPIC -fpermissive
LDFLAGS?=
#LDLIBS?=-ldl -lX11 -lrt -lpthread
LDLIBS?=-lws2_32 -lpthread
LDPATH?=-L/usr/lib
BINARY_NAME?=tuxguitar-synth-vst-jni
BINARY_SUFFIX?=.exe
#BINARY_OS?=_GNULinux
BINARY_OS?=_Win32

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

LILV_INCLUDE_PATH?=/usr/include/lilv-0/lilv
JNI_INCLUDE_PATH?=../../../../build-scripts/native-modules/common-include

RM?=rm -f
CFLAGS?=-I$(JNI_INCLUDE_PATH) -I$(LILV_INCLUDE_PATH) -fPIC
LDFLAGS?=
LDLIBS?=-llilv-0 -lsuil-0
LDPATH?=
LIBRARY_NAME?=tuxguitar-synth-lv2-jni
LIBRARY_PREFIX?=lib
LIBRARY_SUFFIX?=.so

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_SUFFIX)
OBJECTS=\
	../cxx/LV2Logger.o\
	../cxx/LV2World.o\
	../cxx/LV2Plugin.o\
	org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World.o\
	org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin.o

%.o: %.c
	$(CXX) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CXX) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)
	
clean: 
	$(RM) $(OBJECTS) $(LIBRARY)

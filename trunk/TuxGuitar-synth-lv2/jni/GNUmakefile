LILV_INCLUDE_PATH?=/usr/include/lilv-0/lilv
JNI_INCLUDE_PATH?=$(shell gcj -print-file-name=include/)

RM?=rm -f
CFLAGS?=-I$(JNI_INCLUDE_PATH) -I$(LILV_INCLUDE_PATH) -fPIC
LDFLAGS?=
LDLIBS?=-llilv-0
LDPATH?=-L/usr/lib
LIBRARY_NAME?=tuxguitar-synth-lv2-jni
LIBRARY_PREFIX?=lib
LIBRARY_SUFFIX?=.so

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_SUFFIX)
OBJECTS=\
	org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World.o\
	org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin.o\
	org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance.o\

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)
	
clean: 
	$(RM) $(OBJECTS) $(LIBRARY)

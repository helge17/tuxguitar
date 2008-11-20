CFLAGS?=-mno-cygwin -I./include -I$(shell gcj -print-file-name=include/)
LDFLAGS?=-mno-cygwin -Wl,--kill-at
LDLIBS?=-llibfluidsynth-1
LDPATH?=-L./
LIBRARY_PREFIX?=
LIBRARY_NAME?=tuxguitar-fluidsynth-jni
LIBRARY_EXTENSION?=.dll

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_EXTENSION)
OBJECTS=../jni/org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean: 
	rm -f $(OBJECTS) $(LIBRARY)

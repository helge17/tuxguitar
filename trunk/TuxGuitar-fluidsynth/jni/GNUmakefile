CFLAGS?=-I$(shell gcj -print-file-name=include/)
CFLAGS+=-fPIC
LDFLAGS?=
LDLIBS?=-lfluidsynth
LDPATH?=-L/usr/lib
LIBRARY_PREFIX?=lib
LIBRARY_NAME?=tuxguitar-fluidsynth-jni
LIBRARY_EXTENSION?=.so

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_EXTENSION)
OBJECTS=org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean: 
	rm -f $(OBJECTS) $(LIBRARY)

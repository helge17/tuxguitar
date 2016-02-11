RM?=rm -f
CFLAGS?=-I$(shell gcj -print-file-name=include/) -fPIC
LDFLAGS?=
LDLIBS?=-lfluidsynth
LDPATH?=-L/usr/lib
LIBRARY_NAME?=tuxguitar-fluidsynth-jni
LIBRARY_PREFIX?=lib
LIBRARY_SUFFIX?=.so

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_SUFFIX)
OBJECTS=org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean: 
	$(RM) $(OBJECTS) $(LIBRARY)

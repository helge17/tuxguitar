# See pom.xml for variable definitions

OBJECTS=app_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean:
	$(RM) $(OBJECTS) $(LIBRARY)

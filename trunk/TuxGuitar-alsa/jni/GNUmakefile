CC?=gcc
CFLAGS?=-fPIC -I$(shell gcj -print-file-name=include/)
LDFLAGS?=
LDLIBS?=-lasound
LDPATH?=-L/usr/lib
LIBRARY_PREFIX?=lib
LIBRARY_NAME?=tuxguitar-alsa-jni
LIBRARY_EXTENSION?=.so

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_EXTENSION)
OBJECTS=org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean: 
	rm -f $(OBJECTS) $(LIBRARY)

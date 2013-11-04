CFLAGS?=-I$(shell gcj -print-file-name=include/) -fPIC
LDFLAGS?=
LDLIBS?=-lasound
LDPATH?=-L/usr/lib
LIBRARY_NAME?=tuxguitar-alsa-jni
LIBRARY_PREFIX?=lib
LIBRARY_SUFFIX?=.so

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_SUFFIX)
OBJECTS=org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean: 
	rm -f $(OBJECTS) $(LIBRARY)

#eof "$Id: GNUmakefile,v 1.10 2008/04/17 20:06:34 rzr Exp $"

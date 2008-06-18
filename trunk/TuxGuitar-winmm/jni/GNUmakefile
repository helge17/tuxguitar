CFLAGS?=-mno-cygwin -I$(shell gcj -print-file-name=include/)
LDFLAGS?=-mno-cygwin -Wl,--kill-at
LDLIBS?=-lwinmm
LDPATH?=
LIBRARY_PREFIX?=
LIBRARY_NAME?=tuxguitar-winmm-jni
LIBRARY_EXTENSION?=.dll

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_EXTENSION)
OBJECTS=org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean: 
	rm -f $(OBJECTS) $(LIBRARY)

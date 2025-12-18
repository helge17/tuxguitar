# See pom.xml for variable definitions

OBJECTS=app_tuxguitar_jack_JackClient.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean:
	$(RM) $(OBJECTS) $(LIBRARY)
